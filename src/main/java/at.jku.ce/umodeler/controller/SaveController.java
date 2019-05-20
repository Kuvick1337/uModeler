package at.jku.ce.umodeler.controller;

import at.jku.ce.umodeler.Pair;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

@RestController
public class SaveController {

    private ObjectMapper objectMapper = new ObjectMapper();

    public SaveController() {
        System.out.println("Save controller initialized");
    }

    @CrossOrigin
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void save(HttpServletRequest request, HttpEntity<String> httpEntity) {
        System.out.println("Request query string: " + request.getQueryString());
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

        try {
            System.out.println("POST save was called with " + objectMapper.writeValueAsString(httpEntity));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @CrossOrigin
    @GetMapping("/save")
    public void saveGET(HttpServletRequest request, @RequestBody Object data) {
        System.out.println("GET save get called");
    }
}


