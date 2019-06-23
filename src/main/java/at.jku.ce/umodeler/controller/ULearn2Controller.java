package at.jku.ce.umodeler.controller;

import at.jku.ce.umodeler.LoggingRequestInterceptor;
import at.jku.ce.umodeler.Pair;
import at.jku.ce.umodeler.dto.*;
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

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO this controller is work in progress.
 * intended to provide a workspace/submission selection to the user
 */
@RestController
public class ULearn2Controller {
    private final static String ULEARN_BASE_PATH = "https://dev.ce.jku.at/api/service/";
    private final static String ULEARN_LOGIN = ULEARN_BASE_PATH + "login";

    private final static String ULEARN_WORKSPACE_LIST = ULEARN_BASE_PATH + "api/workspace/list";

    public ULearn2Controller() {
        System.out.println("ULearnController 2 initialized");
    }

    /**
     * returns the Bearer Auth token
     *
     * @return
     * @throws Exception
     */
    @GetMapping("ulearn2/login")
    public ClientLoginResponse loginToUlearnAndFetchWorkspaces(@RequestParam("username") String username,
                                                               @RequestParam("password") String password)

    /*(HttpServletRequest request, HttpEntity<String> httpEntity, HttpServletResponse response) */ {
        System.out.println("Start login");

        // split all request parameters into a map
//        Map<String, String> requestParameters = Arrays.stream(Objects.requireNonNull(httpEntity.getBody())
//                .split("&")).map(entity -> {
//            String[] result = entity.split("=");
//            try {
//                return new Pair<>(result[0], URLDecoder.decode(result.length == 1 ? "NaN" : result[1], StandardCharsets.UTF_8.toString()));
//            } catch (UnsupportedEncodingException e) {
//                return null;
//            }
//        }).filter(Objects::nonNull).collect(Collectors.toMap(Pair::getFst, Pair::getSnd));
//        String decodedUsername = requestParameters.get("username");
//        String decodedPassword = requestParameters.get("password");

        String decodedUsername = URLDecoder.decode(username);
        String decodedPassword = URLDecoder.decode(password);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<AuthDto> entity = new HttpEntity<>(new AuthDto(decodedUsername, decodedPassword), headers);
        ResponseEntity<ULearnLoginResponseDto> result = restTemplate.exchange(ULEARN_LOGIN, HttpMethod.POST, entity, ULearnLoginResponseDto.class);

        System.out.println("Login Successful! Token=" + result.getHeaders().get("Authorization").get(0));

        ClientLoginResponse clientLoginResponse = new ClientLoginResponse();
        clientLoginResponse.setBearerToken(result.getHeaders().get("Authorization").get(0));

        ListResponseDto workspaces = fetchWorkspace(clientLoginResponse.getBearerToken());
        clientLoginResponse.setWorkspaces(workspaces.getData().stream()
                .map(x -> new Pair<>(x.getName(), x.getId().getCreated())).collect(Collectors.toList()));

        return clientLoginResponse;
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

    @GetMapping("ulearn2/submissions")
    public SubmissionsResponseDto fetchSubmissionsForWorkspace(@RequestParam("token") String bearerToken,
                                                               @RequestParam("workspace") long workspaceId) throws Exception {
        System.out.println("Start submissions");
        String decodedBearerToken = URLDecoder.decode(bearerToken);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", decodedBearerToken);
        HttpEntity<String> entity = new HttpEntity<String>("", headers);
        ResponseEntity<SubmissionsResponseDto> result = restTemplate.exchange(ULEARN_BASE_PATH + "api/submission-specification-group/workspace:" + workspaceId + " ?global=false", HttpMethod.GET, entity, SubmissionsResponseDto.class);
        return result.getBody();
    }

    @PostMapping("ulearn2/save")
    public void postModelToUlearn(@RequestParam("token") String bearerToken,
                                  @RequestParam("groupId") long submissionGroupId,
                                  @RequestParam("submissionId") long submissionSpecId,
                                  @RequestParam("xml") String xml,
                                  @RequestParam("filename") String fileName) {
        String decodedBearerToken = URLDecoder.decode(bearerToken);
        String decodedXML = URLDecoder.decode(xml);

        // this interceptor is added to log the sent requests onto the command line --> very useful for debugging!
        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new LoggingRequestInterceptor());
        restTemplate.setInterceptors(interceptors);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", decodedBearerToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> bodyData = new LinkedMultiValueMap<>();
        bodyData.add("fileName", fileName);

        Resource xmlFile = new ByteArrayResource(decodedXML.getBytes(StandardCharsets.UTF_8)) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };

        HttpHeaders xmlHeaders = new HttpHeaders();
        xmlHeaders.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<Resource> xmlEntity = new HttpEntity<>(xmlFile, xmlHeaders);

        bodyData.add("file", xmlEntity);
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(bodyData, headers);
        String url = ULEARN_BASE_PATH + "api/submission/group:" + submissionGroupId + "/submission-specification:" + submissionSpecId;
        System.out.println("url: " + url);

        ResponseEntity<Object> result = restTemplate.exchange(url, HttpMethod.PUT, entity, Object.class);
    }
}
