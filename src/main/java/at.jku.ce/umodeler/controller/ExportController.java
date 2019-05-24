package at.jku.ce.umodeler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class ExportController {
    private ObjectMapper objectMapper = new ObjectMapper();

    public ExportController() {
        System.out.println("Export controller initialized");
    }

    // erste Variante nach Setup
//    @CrossOrigin
//    @PostMapping("/export")
//    public void postExport(HttpServletRequest request, @RequestBody Object data) {
//        // from https://jgraph.github.io/mxgraph/docs/js-api/files/editor/mxEditor-js.html
////        URLDecoder.decode(request.getParameter("xml"), "UTF-8").replace("\n", "&#xa;");
//
//        try {
//            System.out.println("POST export was called with " + objectMapper.writeValueAsString(data));
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//    }

    @PostMapping("/export")
    public void postExport(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ControllerUtil.handleRequest(request, response);
    }

    @GetMapping(value = "/export")
    public void getExport(HttpServletRequest request) {
        System.out.println("GET export get called");
    }


}
