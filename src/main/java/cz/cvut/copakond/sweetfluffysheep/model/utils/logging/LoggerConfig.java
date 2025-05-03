package cz.cvut.copakond.sweetfluffysheep.model.utils.logging;

import java.util.logging.*;

/**
 * LoggerConfig is a utility class that configures the logging settings for the application.
 * It allows enabling or disabling logging based on the provided boolean parameter.
 */
public class LoggerConfig {

    /**
     * Configures the logging settings for the application, used for connecting to stdout and stderr.
     *
     * @param enableLogging If true, enables logging; if false, disables logging.
     */
    public static void configureLoggers(boolean enableLogging) {
        Level logLevel = enableLogging ? Level.ALL : Level.OFF;

        Logger rootLogger = Logger.getLogger("");
        rootLogger.removeHandler(rootLogger.getHandlers()[0]);
        LoggerStdOutErrHandler handler = new LoggerStdOutErrHandler();
        rootLogger.addHandler(handler);
        rootLogger.setLevel(logLevel);
    }
}