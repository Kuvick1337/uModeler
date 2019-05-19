package at.jku.ce.umodeler;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This Spring Configuration file enables the ressources folder to be served statically.
 * The client-side modelling web application can request all neccessary libraries on demand.
 */
@Configuration
@EnableWebMvc
public class ModelerConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        System.out.println("Resource handler added");
        registry
                .addResourceHandler("/*.html")
                .addResourceLocations("classpath:");
        registry
                .addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/assets/");
        registry
                .addResourceHandler("/resources/**")
                .addResourceLocations("classpath:/resources/");
    }

}
