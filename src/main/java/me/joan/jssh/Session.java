package me.joan.jssh;

import com.jcraft.jsch.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
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


    private String readChannelOutput(ChannelExec theChannel, int bufferSize) {
        String output = null;

        byte[] buffer = new byte[bufferSize];

        try {
            InputStream in = theChannel.getInputStream();
            String line = "";
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(buffer, 0, bufferSize);

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

    private String readChannelOutput(ChannelExec theChannel) {
        return readChannelOutput(theChannel, 1024);
    }

    /* To execute a command over JSch, it's necessary to connect using a "Session"
     * Each session can have multiple "Channels". Each Channel has a type. For automating, the recommended type is
     * "ChannelExec", which executes commands over SSH. Multiple channels can run in parallel or in series.
     * One can't just "ADD" commands to a ChannelExec, each ChannelExec executes one command and returns it's output
     * through an InputStream, that can be read using a delimited-size byte array buffer. (See readChannelOutput)
     *
     * So, to execute multiple commands, we create as much ChannelExec instances as we need, and read each one of
     * their individual outputs. We can pass the output as a String, or we can print it to a PrintStream.
     *
     */

    public String executeCommand(String command) {

        String output = null;

        try {
            ChannelExec theChannel = (ChannelExec) this.session.openChannel("exec");
            theChannel.setOutputStream(System.out);
            theChannel.setInputStream(null);
            theChannel.setErrStream(System.err);
            theChannel.setCommand(command);
            theChannel.connect();
            output = readChannelOutput(theChannel);
            theChannel.disconnect();
        } catch (JSchException jSchException) {
            logger.error("An error occurred while sending a command.", jSchException);
            logger.error("Command: "+command);
        }

        return output;
    }

    public void executeCommand(String command, PrintStream out) {
        String output = executeCommand(command);
        out.print(output);
    }

    public void executeCommands(ArrayList<String> commandList) {
        for (String theCommand : commandList) {
            executeCommand(theCommand);
        }
    }

    public void executeCommands(ArrayList<String> commandList, PrintStream out) {
        for(String theCommand : commandList)
        {
            executeCommand(theCommand, out);
        }
    }

    // Grab the returned String by executeCommand() and send it through the Logger
    public void logExecuteCommand(String command) {
        this.logger.info(this.executeCommand(command));
    }
}
