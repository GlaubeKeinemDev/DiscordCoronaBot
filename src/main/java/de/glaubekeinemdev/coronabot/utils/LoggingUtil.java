package de.glaubekeinemdev.coronabot.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class LoggingUtil {

    public static void sendInfo(String text) {
        System.out.println("[" + getTimestamp(System.currentTimeMillis()) +" | INFO] " + text);
    }

    public static void sendWarning(String text) {
        System.out.println("[" + getTimestamp(System.currentTimeMillis()) +" | WARNING] " + text);
    }

    public static void sendError(String text) {
        System.out.println("[" + getTimestamp(System.currentTimeMillis()) +" | ERROR] " + text);
    }

    private static String getTimestamp(Long timemillies) {
        Date date = new Date(timemillies);
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy - kk:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        String formatted = format.format(date);
        return formatted;
    }

}
