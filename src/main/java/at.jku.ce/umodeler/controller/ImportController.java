package at.jku.ce.umodeler.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ImportController {
    private ObjectMapper objectMapper = new ObjectMapper();

    public ImportController() {
        System.out.println("Import controller initialized");
    }

    @CrossOrigin
    @PostMapping("/import")
    public void dImport(HttpServletRequest request, @RequestBody Object data) {
        try {
            System.out.println("import post was called with " + objectMapper.writeValueAsString(data));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @CrossOrigin
    @GetMapping(value = "/import")
    public void dImportGET(HttpServletRequest request) {
        System.out.println("import get called");
    }
}
