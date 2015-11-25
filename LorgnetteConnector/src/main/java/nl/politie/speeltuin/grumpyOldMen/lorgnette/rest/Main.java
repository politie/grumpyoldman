package nl.politie.speeltuin.grumpyOldMen.lorgnette.rest;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * Bootstraps the spring application
 */
@Configuration
@EnableAutoConfiguration
public class Main {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext ctx = SpringApplication.run(new Object[] { Main.class, KafkaWebSocketConfig
                .class }, args);
        System.out.println("Hit 'Enter' to terminate");
        System.in.read();
        ctx.close();
    }

}