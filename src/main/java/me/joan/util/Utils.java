package me.joan.util;

import org.apache.commons.lang3.SystemUtils;

public class Utils {
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
}
