package at.jku.ce.umodeler.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    public IndexController(){

    }

    @GetMapping ("/")
    public String index() {
        return "../index.html";
    }

//    @GetMapping ("/favicon.ico")
//    public String favicon() {
//        return "../favicon.ico";
//    }
}
