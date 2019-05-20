package at.jku.ce.umodeler.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ExportController {
    private ObjectMapper objectMapper = new ObjectMapper();

    public ExportController() {
        System.out.println("Export controller initialized");
    }

    @CrossOrigin
    @PostMapping("/export")
    public void dImport(HttpServletRequest request, @RequestBody Object data) {
        try {
            System.out.println("POST import was called with " + objectMapper.writeValueAsString(data));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @CrossOrigin
    @GetMapping(value = "/export")
    public void dImportGET(HttpServletRequest request) {
        System.out.println("GET export get called");
    }
}
