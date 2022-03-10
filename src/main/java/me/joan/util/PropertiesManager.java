package me.joan.util;

import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;

// Custom properties file manager
public class PropertiesManager {
    HashMap<String, String> propertiesMap;
    Logger logger;

    public PropertiesManager(Logger logger) {
        this.propertiesMap = new HashMap<>();
        this.logger = logger;
    }

    // Create the properties file where the JAR is located
    public void createProperties(File propertiesFile) {
        Properties properties = new Properties();
        OutputStream output = null;

        try {
            output = new FileOutputStream(propertiesFile);

            properties.setProperty("port","22");
            properties.setProperty("username","username");
            properties.setProperty("hostname","123.456.7.89");
            properties.setProperty("password","password");
            properties.setProperty("pubkey",".ssh\\pubkey.pub");
            properties.setProperty("privkey",".ssh\\privkey");
            properties.setProperty("passphrase","passphrase");

            properties.store(output, null);
        } catch(IOException ioException) {
            this.logger.error("An error has ocurred while creating a new file: ", ioException);
        }
    }

    // Try to load the properties file. If file does not exist, create a new one.
    public void load(File propertiesFile) {
        Properties properties = new Properties();

        InputStream input = null;

        try { input = new FileInputStream(propertiesFile); }
        catch(FileNotFoundException fileNotFoundException) {
            this.logger.warn("Properties file missing, creating a new one...");
            this.createProperties(propertiesFile);
            this.logger.info(propertiesFile.getName()+" created succesfully.");
        }
        finally {
            if(input != null) {
                try { properties.load(input); }
                catch (IOException ioException) {
                    this.logger.error("There was an error while loading the properties file: ", ioException);
                }

                try { input.close(); }
                catch (IOException ioException) {
                    this.logger.error("There was an error closing the file: ", ioException);
                }

                properties.stringPropertyNames().forEach(key -> {
                    String value = properties.getProperty(key);
                    this.propertiesMap.put(key, value);
                });
                this.logger.info(propertiesFile.getName()+" properly loaded.");
            }
        }
    }
}
