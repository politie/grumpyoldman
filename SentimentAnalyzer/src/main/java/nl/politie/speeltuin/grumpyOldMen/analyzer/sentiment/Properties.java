package nl.politie.speeltuin.grumpyOldMen.analyzer.sentiment;

import java.io.IOException;

public class Properties {

    private java.util.Properties properties = new java.util.Properties();

    private Properties() {
        try {
            properties.load(ClassLoader.getSystemResourceAsStream("application.properties"));
        } catch (IOException e) {
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
