package at.jku.ce.umodeler.controller;

import at.jku.ce.umodeler.LoggingRequestInterceptor;
import at.jku.ce.umodeler.Pair;
import at.jku.ce.umodeler.dto.AuthDto;
import at.jku.ce.umodeler.dto.ListResponseDto;
import at.jku.ce.umodeler.dto.LoginResponseDto;
import at.jku.ce.umodeler.dto.SubmissionsResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ULearnController {
    private final static String ULEARN_BASE_PATH = "https://dev.ce.jku.at/api/service/";
    private final static String ULEARN_LOGIN = ULEARN_BASE_PATH + "login";

    private final static String ULEARN_WORKSPACE_LIST = ULEARN_BASE_PATH + "api/workspace/list";

    public ULearnController() {
        System.out.println("ULearnController initialized");
    }

    @PostMapping("ulearn/save")
    public void postModelToUlearn(HttpServletRequest request, HttpEntity<String> httpEntity, HttpServletResponse response) throws Exception {

        // split all request parameters into a map
        Map<String, String> requestParameters = Arrays.stream(Objects.requireNonNull(httpEntity.getBody())
                .split("&")).map(entity -> {
            String[] result = entity.split("=");
            try {
                return new Pair<>(result[0], URLDecoder.decode(result.length == 1 ? "NaN" : result[1], StandardCharsets.UTF_8.toString()));
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toMap(Pair::getFst, Pair::getSnd));

        String fileName = requestParameters.getOrDefault("filename", "newFile.xml");


        // Step 1: perform login to uLearn
        Pair<List<String>, LoginResponseDto> loginResponse = login(requestParameters);
        loginResponse.getFst().forEach(System.out::println);
        String bearerToken = loginResponse.getFst().get(0);

        // Step 2: fetch workspaces and find ID of correct one
        ListResponseDto workspaces = fetchWorkspace(bearerToken);

        // TODO filter through and find right workspace
        long workspaceId = workspaces.getData().get(0).getId().getCreated();

        // Step 3: fetch all submissions and find ID of correct one
        SubmissionsResponseDto submissionResponse = fetchSubmissions(bearerToken, workspaceId);
        // TODO filter through and find right submission

        // Step 4: perform submission
        doSubmission(bearerToken, submissionResponse.getData().get(0).getId().getCreated(),
                submissionResponse.getData().get(0).getMembers().get(0).getId().getCreated(), requestParameters.get("xml"), fileName);

        response.setStatus(200);
        ControllerUtil.handleRequest(request, response);
    }

    private Pair<List<String>, LoginResponseDto> login(Map<String, String> values) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<AuthDto> entity = new HttpEntity<>(new AuthDto(values.get("username"), values.get("password")), headers);
        ResponseEntity<LoginResponseDto> result = restTemplate.exchange(ULEARN_LOGIN, HttpMethod.POST, entity, LoginResponseDto.class);

        return new Pair<>(result.getHeaders().get("Authorization"), result.getBody());
    }

    private ListResponseDto fetchWorkspace(String bearerToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", bearerToken);
        HttpEntity<String> entity = new HttpEntity<String>("", headers);
        ResponseEntity<ListResponseDto> result = restTemplate.exchange(ULEARN_WORKSPACE_LIST, HttpMethod.GET, entity, ListResponseDto.class);

        return result.getBody();
    }

    private SubmissionsResponseDto fetchSubmissions(String bearerToken, long workspaceId) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", bearerToken);
        HttpEntity<String> entity = new HttpEntity<String>("", headers);
        ResponseEntity<SubmissionsResponseDto> result = restTemplate.exchange(ULEARN_BASE_PATH + "api/submission-specification-group/workspace:" + workspaceId + " ?global=false", HttpMethod.GET, entity, SubmissionsResponseDto.class);
        return result.getBody();
    }

    private void doSubmission(String bearerToken, long groupId, long submissionSpecId, String xml, String fileName) {
        // this interceptor is added to log the sent requests onto the command line --> very useful for debugging!
        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new LoggingRequestInterceptor());
        restTemplate.setInterceptors(interceptors);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", bearerToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> bodyData = new LinkedMultiValueMap<>();
        bodyData.add("fileName", fileName);


        Resource xmlFile = new ByteArrayResource(xml.getBytes(StandardCharsets.UTF_8)) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };

        HttpHeaders xmlHeaders = new HttpHeaders();
        xmlHeaders.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<Resource> xmlEntity = new HttpEntity<>(xmlFile, xmlHeaders);

        bodyData.add("file", xmlEntity);
        System.out.println("reloaded");
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(bodyData, headers);
        String url = ULEARN_BASE_PATH + "api/submission/group:" + groupId + "/submission-specification:" + submissionSpecId;
        System.out.println("url: " + url);

        ResponseEntity<Object> result = restTemplate.exchange(url, HttpMethod.PUT, entity, Object.class);
        //return result.getBody();
    }

    @GetMapping("/ulearn/load")
    public void loadModelFromULearn(@RequestParam(name = "filename") String filename,
                                    @RequestParam(name = "username") String username,
                                    @RequestParam(name = "password") String password) {
        System.out.println("GET /ulearn/load got called with filename=" + filename + ", username=" + username + ", password=" + password);
    }
}
