package at.jku.ce.umodeler;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.net.ssl.SSLContext;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * This class holds the main application entry point, which starts the Spring application.
 */
@SpringBootApplication
public class Application {
    public static void main(String args[]) {

        System.setProperty("https.protocols", "SSLv3,TLSv1,TLSv1.1,TLSv1.2");
        try {
            SSLContext ctx = SSLContext.getInstance("TLSv1.2");
            ctx.init(null, null, null);
            SSLContext.setDefault(ctx);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        SpringApplication.run(Application.class);
    }
}
