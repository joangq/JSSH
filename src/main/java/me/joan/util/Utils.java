package me.joan.util;

import org.apache.commons.lang3.SystemUtils;

import java.nio.charset.StandardCharsets;

public class Utils {
    // Grab the filename of the key, and append it to the standard location /Users/User/.ssh/
    public static String formatKeypath(String string) {
        if (!string.startsWith(SystemUtils.getUserHome().toString())) {
            if(string.startsWith(".ssh")) {
                string = SystemUtils.getUserHome()+"\\"+ string;
            } else {
                string = SystemUtils.getUserHome()+"\\.ssh\\"+ string;
            }
        }
        return string;
    }

    // Force UTF-8 encoding
    public static String toUTF(String string) {
        return new String(string.getBytes(), StandardCharsets.UTF_8);
    }
}
