package cz.cvut.copakond.sweetfluffysheep.model.utils.logging;

import java.util.logging.*;

public class LoggerConfig {
    public static void configureLoggers(boolean enableLogging) {
        Level logLevel = enableLogging ? Level.ALL : Level.OFF;

        Logger rootLogger = Logger.getLogger("");
        rootLogger.removeHandler(rootLogger.getHandlers()[0]);
        LoggerStdOutErrHandler handler = new LoggerStdOutErrHandler();
        rootLogger.addHandler(handler);
        rootLogger.setLevel(logLevel);
    }
}
