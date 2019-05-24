package at.jku.ce.umodeler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class SaveController {

    private ObjectMapper objectMapper = new ObjectMapper();

    public SaveController() {
        System.out.println("Save controller initialized");
    }

//    @CrossOrigin
//    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//    public void save(HttpServletRequest request, HttpEntity<String> httpEntity) {
//        System.out.println("Request query string: " + request.getQueryString());
//        Arrays.stream(httpEntity.getBody().split("&")).map(entity -> {
//            String[] result = entity.split("=");
//            try {
//                return new Pair(result[0], URLDecoder.decode(result[1], StandardCharsets.UTF_8.toString()));
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//                return null;
//            }
//        }).filter(Objects::nonNull).forEach(entity -> {
//            try {
//                System.out.println("Splitted: " + objectMapper.writeValueAsString(entity));
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }
//        });
//
//        try {
//            System.out.println("POST save was called with " + objectMapper.writeValueAsString(httpEntity));
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//    }

    @PostMapping("/save")
    public void postExport(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ControllerUtil.handleRequest(request, response);
    }

    @GetMapping("/save")
    public void saveGET(HttpServletRequest request, @RequestBody Object data) {
        System.out.println("GET save get called");
    }
}


