package xyz.zeppelin.casino.common;

public class Environment {

    public static boolean isDevelopmentMode() {
        return System.getenv("xyz.zeppelin.development").equalsIgnoreCase("true");
    }
}
