package nl.politie.speeltuin.grumpyOldMen.lorgnette;


import nl.politie.speeltuin.grumpyOldMen.lorgnette.socket.KafkaWebSocketConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * Bootstraps the spring application
 */
@SpringBootApplication
public class Main {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext ctx = SpringApplication.run(new Object[] { Main.class, KafkaWebSocketConfig
                .class }, args);
    }

}
