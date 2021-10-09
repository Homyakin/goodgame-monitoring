package ru.homyakin.goodgame.monitoring.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CommonUtils {
    public static String getStringStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}