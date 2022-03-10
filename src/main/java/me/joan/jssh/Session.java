package me.joan.jssh;

import com.jcraft.jsch.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.Logger;

public class Session {

    // All the variables for the JSch session to connect
    private int port;
    private com.jcraft.jsch.Session session;
    private String username;
    private String password;
    private String hostname;
    private String privkey;
    private String pubkey;
    private byte[] passphrase;
    private org.apache.logging.log4j.Logger logger;

    public Session(int port, String hostname, String username, String password, String privkey, String pubkey, byte[] passphrase, Logger logger) {
        this.port = port;
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.privkey = privkey;
        this.pubkey = pubkey;
        this.passphrase = passphrase;
        this.logger = logger;
    }

    // Main method to connect
    /* TODO: This is only a method for connecting using a keypair. There should be multiple methods for
     *   connecting using different security configurations! */

    /* Note: JSch can only use old PEM formatted keys. If you don't use such formatted key, it's possible that JSch
     * throws an "Invalid key" error.
     * To format old-existing keys it's recommended to use PuTTYgen's "Import/Export key" options.
     */
    public void connect() {
        JSch jSch = new JSch();

        try {
            jSch.addIdentity(this.privkey, this.pubkey, this.passphrase);

            this.session = jSch.getSession(this.username, this.hostname, this.port);
            this.session.setConfig("PreferredAuthentication", "publickey,keyboard-interactive,password");

            Properties config = new Properties();
            config.put("StrictHostKeyChecking","no");

            this.session.setConfig(config);
            this.session.setPassword(this.password);

            logger.info("Connecting SSH to " + this.username + "@" + this.hostname + " - Please wait for few seconds...");
            this.session.connect();
            logger.info("Connected successfully!");
        } catch (JSchException jSchException) {
            logger.error("There has been an error while connecting to " + this.username +"@"+this.hostname,
                    jSchException);
        }
    }


    private String readChannelOutput(ChannelExec theChannel) {
        String output = null;

        byte[] buffer = new byte[1024];

        try {
            InputStream in = theChannel.getInputStream();
            String line = "";
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(buffer, 0, 1024);

                    if (i < 0) break;

                    line = new String(buffer, 0, i);
                    output = line;
                }


                if (theChannel.isClosed()) {
                    break;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException interruptedException) {
                    logger.error("Error while closing channel output: ", interruptedException);
                }
            }
        } catch (IOException ioException) {
            logger.error("Error while reading channel output: ", ioException);
        }

        return output;
    }

}
