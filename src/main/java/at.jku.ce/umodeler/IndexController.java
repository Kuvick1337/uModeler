package at.jku.ce.umodeler;

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

}
