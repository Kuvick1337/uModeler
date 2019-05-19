package at.jku.ce.umodeler;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * This class holds the main application entry point, which starts the Spring application.
 */
@SpringBootApplication
public class Application {
    public static void main(String args[]) {
        SpringApplication.run(Application.class);
    }
}
