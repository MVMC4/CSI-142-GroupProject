package com.ub.project.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logging {

    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public void postUpdate(String message) {
        log("UPDATE", message);
    }

    public void postUpdate(Object source, String message) {
        log("UPDATE", "[" + source.getClass().getSimpleName() + "] " + message);
    }

    public void postWarning(String message) {
        log("WARNING", message);
    }

    public void postError(String message) {
        log("ERROR", message);
    }

    public void postDebug(String message) {
        log("DEBUG", message);
    }

    private void log(String level, String message) {
        System.out.println(getTime() + " [" + level + "]: " + message);
    }

    private String getTime() {
        return LocalDateTime.now().format(FORMATTER);
    }
}