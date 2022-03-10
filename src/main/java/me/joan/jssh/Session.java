package me.joan.jssh;

import com.jcraft.jsch.*;

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

}
