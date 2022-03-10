package me.joan.jssh;

import com.jcraft.jsch.*;

import java.util.Properties;

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
    private Logger logger;

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

            System.out.println("Connecting SSH to " + this.username + "@" + this.hostname + " - Please wait for few seconds...");
            this.session.connect();
            System.out.println("Connected successfully!");
        } catch (JSchException jSchException) {
            System.out.println("There has been an error while connecting to " + this.username +"@"+this.hostname);
            jSchException.printStackTrace();
        }
    }

}
