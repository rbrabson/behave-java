package behave;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log implements Node {
    private static final Logger logger = Logger.getLogger(Log.class.getName());
    private final Node child;
    private final String message;
    private final Level logLevel;
    private Status status = Status.READY;

    public Log(Node child, String message, Level logLevel) {
        this.child = child;
        this.message = message;
        this.logLevel = logLevel;
    }

    public Log(Node child, String message) {
        this(child, message, null);
    }

    public Log(Node child) {
        this(child, null, null);
    }

    @Override
    public Status tick() {
        if (child == null) {
            status = Status.FAILURE;
            log(Level.WARNING, "Log node has no child", status);
            return status;
        }
        Status childStatus = child.tick();
        status = childStatus;
        String msg = (message != null) ? message : "Log node executed";
        Level level = (logLevel != null) ? logLevel : defaultLevel(childStatus);
        log(level, msg, childStatus);
        return status;
    }

    private void log(Level level, String msg, Status childStatus) {
        logger.log(level, msg + " | child_status=" + childStatus + ", child_type=" + getChildType());
    }

    private Level defaultLevel(Status s) {
        switch (s) {
        case SUCCESS:
            return Level.INFO;
        case FAILURE:
            return Level.WARNING;
        case RUNNING:
        case READY:
            return Level.FINE;
        default:
            return Level.FINE;
        }
    }

    private String getChildType() {
        return (child != null) ? child.getClass().getSimpleName() : "null";
    }

    @Override
    public Status reset() {
        status = Status.READY;
        if (child != null)
            child.reset();
        return status;
    }

    @Override
    public Status status() {
        return status;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Log (" + status + ")");
        if (child != null) {
            String[] lines = child.toString().split("\n");
            builder.append("\n  " + lines[0]);
            for (int i = 1; i < lines.length; i++) {
                builder.append("\n  " + lines[i]);
            }
        }
        return builder.toString();
    }
}// ...existing code from src/behave/Log.java...
