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
 * Provides methods for login to uLearn, fetching submissions and sending the file to the selected submission
 */
@RestController
public class ULearnController {
    private final static String ULEARN_BASE_PATH = "https://dev.ce.jku.at/api/service/";
    private final static String ULEARN_LOGIN = ULEARN_BASE_PATH + "login";
    private final static String ULEARN_WORKSPACE_LIST = ULEARN_BASE_PATH + "api/workspace/list";

    public ULearnController() {
        System.out.println("ULearnController initialized");
    }

    /**
     * performs the login and returns the Bearer Authentication token and a list of workspaces if successful
     *
     * @return ClientLoginResponse
     */
    @GetMapping("ulearn/login")
    public ClientLoginResponse loginToUlearnAndFetchWorkspaces(@RequestParam("username") String username,
                                                               @RequestParam("password") String password) {

        String decodedUsername = URLDecoder.decode(username);
        String decodedPassword = URLDecoder.decode(password);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<AuthDto> entity = new HttpEntity<>(new AuthDto(decodedUsername, decodedPassword), headers);
        ResponseEntity<ULearnLoginResponseDto> result = restTemplate.exchange(ULEARN_LOGIN, HttpMethod.POST, entity, ULearnLoginResponseDto.class);

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

    /**
     * fetches all submission groups and submissionSpecifications for a selected workspace
     *
     * @param bearerToken users authentication token
     * @param workspaceId select workspace
     * @return SubmissionsResponseDto
     */
    @GetMapping("ulearn/submissions")
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

    /**
     * posts the mxGraph model to the specified submission in uLearn
     *
     * @param bearerToken       users authentication token
     * @param submissionGroupId group id of selected submission
     * @param submissionSpecId  submission specification id
     * @param xml               the actual graph
     * @param fileName          name of the graph
     */
    @PostMapping("ulearn/save")
    public void postModelToUlearn(@RequestParam("token") String bearerToken,
                                  @RequestParam("groupId") String submissionGroupId,
                                  @RequestParam("submissionId") String submissionSpecId,
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

        ResponseEntity<Object> result = restTemplate.exchange(url, HttpMethod.PUT, entity, Object.class);
    }
}
