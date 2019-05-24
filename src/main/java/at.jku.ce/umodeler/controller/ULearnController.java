package at.jku.ce.umodeler.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class ULearnController {

    @PostMapping("/Ulearn_save")
    public void postModelToUlearn(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("Post Ulearn_save get called");
    }

    @GetMapping("/Ulearn_load")
    public void loadModelFromULearn(HttpServletRequest request) {
        System.out.println("GET Ulearn_load get called");
    }
}
