package me.joan;

import java.io.File;
import me.joan.jssh.Session;
import me.joan.util.PropertiesManager;
import me.joan.util.Utils;
import org.apache.logging.log4j.*;

public class Main {
    public static Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        LOGGER.info("JSSH Client starting...");
        PropertiesManager propertiesManager = new PropertiesManager(LOGGER);
        propertiesManager.load(new File("./session.properties"));

        Session JSSH = new Session(propertiesManager.getKeys(), LOGGER);
        JSSH.connect();
        JSSH.logExecuteCommand("echo Hello from the other side!");
    }

    public Main() {}
}
