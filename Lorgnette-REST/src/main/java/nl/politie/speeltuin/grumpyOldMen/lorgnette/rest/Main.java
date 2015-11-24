package nl.politie.speeltuin.grumpyOldMen.lorgnette.rest;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Bootstraps the spring application
 */
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(new Object[] { Main.class, KafkaWebSocketConfig.class },args);
    }
}

