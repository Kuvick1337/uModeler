package at.jku.ce.umodeler.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class OpenController {
    private ObjectMapper objectMapper = new ObjectMapper();

    public OpenController() {
        System.out.println("Open controller initialized");
    }

    @CrossOrigin
    @RequestMapping(value = "/open", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void open(HttpServletRequest request) {
        System.out.println("Post was called with ");

    }

    @CrossOrigin
    @GetMapping("/open")
    public void openGET(HttpServletRequest request, @RequestBody Object data) {
        System.out.println("open get called");
    }





}
