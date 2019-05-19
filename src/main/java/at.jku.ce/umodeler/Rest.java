package at.jku.ce.umodeler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
public class Rest {
    ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, Boolean> canContinue = new HashMap<>();

    public Rest() {
        System.out.println("Rest controller initialized");
    }

//    @RequestMapping("/")
//    public ModelAndView index () {
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("index");
//        return modelAndView;
//    }

    @CrossOrigin
    @RequestMapping(value = "/open", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void open(HttpServletRequest request) {
        System.out.println("Post was called with ");

    }


    @CrossOrigin
    @RequestMapping(value = "/open", method = RequestMethod.GET)
    public void openGET(HttpServletRequest request, @RequestBody Object data) {
        System.out.println("open get called");
    }

    @CrossOrigin
    @RequestMapping(value = "/save", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void save(HttpServletRequest request, HttpEntity<String> httpEntity) {
        System.out.println(request.getQueryString());
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
            System.out.println("save was called with " + objectMapper.writeValueAsString(httpEntity));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/save", method = RequestMethod.GET)
    public void saveGET(HttpServletRequest request, @RequestBody Object data) {
        System.out.println("save get called");
    }

    @CrossOrigin
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public void dImport(HttpServletRequest request, @RequestBody Object data) {
        try {
            System.out.println("import post was called with " + objectMapper.writeValueAsString(data));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/import", method = RequestMethod.GET)
    public void dImportGET(HttpServletRequest request) {
        System.out.println("import get called");
    }


}
