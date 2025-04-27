package cz.cvut.copakond.pinkfluffyunicorn.model.utils.logging;

import java.util.logging.*;

// Purely based out of a template class from GitHub:
public class LoggerStdOutErrHandler extends Handler {

    private final StreamHandler stdoutHandler;
    private final StreamHandler stderrHandler;

    public LoggerStdOutErrHandler() {
        stdoutHandler = new StreamHandler(System.out, new SimpleFormatter()) {
            @Override
            public synchronized void publish(LogRecord record) {
                super.publish(record);
                flush();
            }
        };

        stderrHandler = new StreamHandler(System.err, new SimpleFormatter()) {
            @Override
            public synchronized void publish(LogRecord record) {
                super.publish(record);
                flush();
            }
        };
    }

    @Override
    public synchronized void publish(LogRecord record) {
        if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
            stderrHandler.publish(record);
        } else {
            stdoutHandler.publish(record);
        }
    }

    @Override
    public synchronized void flush() {
        stdoutHandler.flush();
        stderrHandler.flush();
    }

    @Override
    public synchronized void close() throws SecurityException {
        stdoutHandler.close();
        stderrHandler.close();
    }
}