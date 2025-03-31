package client.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Config {
    private HashMap<String, String> config;

    public Config() {
        this.config = new HashMap<>();
    }

    public void readConfigFile(){
        try (BufferedReader reader = new BufferedReader(new FileReader(".cfg"))) {
            while (reader.ready()) {
                String line = reader.readLine();

                System.out.println(line);
                config.put(line.split("=")[0].trim(), line.split("=")[1].trim());



            }
            System.out.println(config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getValue(String key){
        return config.get(key);
    }
}
