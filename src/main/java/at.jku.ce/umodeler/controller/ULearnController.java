package at.jku.ce.umodeler.controller;

import at.jku.ce.umodeler.LoggingRequestInterceptor;
import at.jku.ce.umodeler.Pair;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.print.attribute.HashAttributeSet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Response;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ULearnController {
    private final static String BASE_PATH = "https://dev.ce.jku.at/api/service/";
    private ObjectMapper objectMapper = new ObjectMapper();

    public ULearnController() {
        System.out.println("controller initialized");
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Auth {
        private String password;
        private String userName;

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class ResponseId {
        private long created;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class LoginResponse {
        @Data
        public static class LoginData {
            private ResponseId id;
        }
        private LoginData data;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class ListResponse {
        @Data
        public static class ListData {
            private ResponseId id;
            private String name;
        }

        private List<ListData> data;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class SubmissionsResponse {
        @Data
        public static class SubmissionData {
            @Data
            public static class SubmissionMember {
                private ResponseId id;
            }
            ResponseId id;
            List<SubmissionMember> members;
        }

        List<SubmissionData> data;
    }


    @PostMapping("ulearn/save")
    public void postModelToUlearn(HttpServletRequest request, HttpEntity<String> httpEntity, HttpServletResponse response) throws Exception {
        System.out.println("Post ulearn/save called!");


        Map<String, String> values = Arrays.stream(httpEntity.getBody().split("&")).map(entity -> {
            String[] result = entity.split("=");
            try {
                return new Pair<>(result[0], URLDecoder.decode(result.length == 1 ? "NaN" : result[1], StandardCharsets.UTF_8.toString()));
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toMap(Pair::getFst, Pair::getSnd));

        URL url = new URL(BASE_PATH + " /login");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        String message = con.getResponseMessage();

        Pair<List<String>, LoginResponse> loginResponse = login(values);
        loginResponse.getFst().forEach(System.out::println);
        String bearerToken = loginResponse.getFst().get(0);
        long userId = loginResponse.getSnd().data.id.created;


        ListResponse workspaces = fetchWorkspace(bearerToken);
        long workspaceId = workspaces.data.get(0).id.created;

        SubmissionsResponse submissionResponse = fetchSubmissions(bearerToken, workspaceId);

        doSubmission(bearerToken, submissionResponse.data.get(0).id.created, submissionResponse.data.get(0).members.get(0).id.created, values.get("xml"));

        ControllerUtil.handleRequest(request, response);
    }



    private Pair<List<String>, LoginResponse> login(Map<String, String> values) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<Auth> entity = new HttpEntity<Auth>(new Auth(values.get("password"), values.get("username")), headers);
        ResponseEntity<LoginResponse> result = restTemplate.exchange(BASE_PATH + "login", HttpMethod.POST, entity, LoginResponse.class);


        return new Pair<>(result.getHeaders().get("Authorization"), result.getBody());
    }


    private ListResponse fetchWorkspace(String bearerToken) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", bearerToken);
        HttpEntity<String> entity = new HttpEntity<String>("", headers);
        ResponseEntity<ListResponse> result = restTemplate.exchange(BASE_PATH + "api/workspace/list", HttpMethod.GET, entity, ListResponse.class);


        return result.getBody();
    }




    private SubmissionsResponse fetchSubmissions(String bearerToken, long workspaceId) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", bearerToken);
        HttpEntity<String> entity = new HttpEntity<String>("", headers);
        ResponseEntity<SubmissionsResponse> result = restTemplate.exchange(BASE_PATH + "api/submission-specification-group/workspace:" + workspaceId + " ?global=false", HttpMethod.GET, entity, SubmissionsResponse.class);
        return result.getBody();
    }

    //api/submission-specification-group/workspace:1560323415584?global=false



    private void doSubmission(String bearerToken, long groupId, long submissionSpecId, String xml) throws UnsupportedEncodingException {
        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new LoggingRequestInterceptor());
        restTemplate.setInterceptors(interceptors);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", bearerToken);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        //headers.setContentDispositionFormData("file", "newFile.xml");
        MultiValueMap<String, Object> bodyData = new LinkedMultiValueMap<>();
        bodyData.add("fileName", "newFile.xml");


        Resource xmlFile = new ByteArrayResource(xml.getBytes("UTF-8")){
            @Override
            public String getFilename(){
                return "newFile.xml";
            }
        };
        HttpHeaders xmlHeaders = new HttpHeaders();
        xmlHeaders.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<Resource> xmlEntity = new HttpEntity<Resource>(xmlFile, xmlHeaders);

        bodyData.add("file", xmlEntity);
        System.out.println("reloaded");
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<MultiValueMap<String, Object>>(bodyData, headers);
        groupId = 1560362317642L;
        String url = BASE_PATH + "api/submission/group:" + groupId + "/submission-specification:" + submissionSpecId;
        System.out.println("url: " + url);

        ResponseEntity<Object> result = restTemplate.exchange(url, HttpMethod.PUT, entity, Object.class);
        //return result.getBody();
    }





    //https://dev.ce.jku.at/api/service/api/submission/group:1560362317642/submission-specification:1560610097235



    @GetMapping("/ulearn/load")
    public void loadModelFromULearn(@RequestParam(name = "filename") String filename,
                                    @RequestParam(name = "username") String username,
                                    @RequestParam(name = "password") String password) {
        System.out.println("GET /ulearn/load got called with filename=" + filename + ", username=" + username + ", password=" + password);
    }
}
