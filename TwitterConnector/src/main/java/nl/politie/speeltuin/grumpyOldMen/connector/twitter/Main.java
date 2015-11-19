package nl.politie.speeltuin.grumpyOldMen.connector.twitter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Bootstraps the Spring application
 */
@SpringBootApplication
@EnableScheduling
public class Main {

    public static void main(String... args) {
        SpringApplication app = new SpringApplication(new Object[]{Main.class});
        app.addListeners( new ClientStarter());
        app.run(args);
    }

}
