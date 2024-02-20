package xyz.zeppelin.casino.common;

public class Environment {

    public static boolean isDevelopmentMode() {
        String developmentEnv = System.getenv("xyz.zeppelin.development");
        return developmentEnv != null && developmentEnv.equals("true");
    }
}
