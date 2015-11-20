package nl.politie.speeltuin.grumpyOldMen.analyzer.sentiment;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Properties {

    private java.util.Properties properties = new java.util.Properties();

    private Properties() {
        try {
            Path defaultPropertyPath = Paths
                    .get(Properties.class.getClassLoader().getResource("application.properties").toURI());
            properties.load(Files.newBufferedReader(defaultPropertyPath));
        } catch (IOException | URISyntaxException e) {
            System.err.println("Failed to read properties");
            System.exit(-1);
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public Integer getInt(String key) {
        return Integer.valueOf(properties.getProperty(key));
    }

    public static Properties getInstance() {
        return PropertiesHolder.INSTANCE;
    }

    private static class PropertiesHolder {

        private static final Properties INSTANCE = new Properties();
    }

}
