package at.jku.ce.umodeler.controller;

import at.jku.ce.umodeler.Pair;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

@RestController
public class ULearnController {
    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/ulearn/save")
    public void postModelToUlearn(HttpServletRequest request, HttpEntity<String> httpEntity) throws Exception {
        System.out.println("Post ulearn/save called!");

        Arrays.stream(httpEntity.getBody().split("&")).map(entity -> {
            String[] result = entity.split("=");
            try {
                return new Pair(result[0], URLDecoder.decode(result[1], StandardCharsets.UTF_8.toString()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
        }).filter(Objects::nonNull).forEach(entity -> {
            try {
                System.out.println("Splitted: " + objectMapper.writeValueAsString(entity));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }


    @GetMapping("/ulearn/load")
    public void loadModelFromULearn(@RequestParam(name = "filename") String filename,
                                    @RequestParam(name = "username") String username,
                                    @RequestParam(name = "password") String password) {
        System.out.println("GET /ulearn/load got called with filename=" + filename + ", username=" + username + ", password=" + password);
    }
}
