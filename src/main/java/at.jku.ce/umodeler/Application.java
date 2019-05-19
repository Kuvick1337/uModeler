package at.jku.ce.umodeler;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.logging.Logger;

@SpringBootApplication
public class Application implements ApplicationRunner {
    public static void main(String args[]) {
        SpringApplication.run(Application.class, "--security.require-ssl=true");
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("Application started with command-line arguments: " + Arrays.toString(args.getSourceArgs()));

        for (String name : args.getOptionNames()){
            System.out.println("arg-" + name + "=" + args.getOptionValues(name));
        }

        boolean containsOption = args.containsOption("security.require-ssl");
        System.out.println("Contains person.name: " + containsOption);
    }
}
