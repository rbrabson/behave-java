package behave;

import java.util.logging.Level;
import java.util.logging.Logger;

// Log node that logs the status of its child node when ticked.
public class Log implements Node {
    private static final Logger logger = Logger.getLogger(Log.class.getName());
    private final Node child;
    private final String message;
    private final Level logLevel;
    private Status status = Status.READY;

    // Constructor takes a child node to decorate, an optional message to log, and
    // an optional log level.
    public Log(Node child, String message, Level logLevel) {
        this.child = child;
        this.message = message;
        this.logLevel = logLevel;
    }

    // Overloaded constructors for convenience when message or log level are not
    // provided.
    public Log(Node child, String message) {
        this(child, message, null);
    }

    // Overloaded constructor for when only the child node is provided.
    public Log(Node child) {
        this(child, null, null);
    }

    // Ticks the child node and logs its status, updating the status of this node
    // accordingly.
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

    // Helper method to log messages with the appropriate level and child status
    // information.
    private void log(Level level, String msg, Status childStatus) {
        logger.log(level, msg + " | child_status=" + childStatus + ", child_type=" + getChildType());
    }

    // Determines the default log level based on the child's status.
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

    // Helper method to get the type of the child node for logging purposes.
    private String getChildType() {
        return (child != null) ? child.getClass().getSimpleName() : "null";
    }

    // Resets the status to READY and resets the child node if it exists.
    @Override
    public Status reset() {
        status = Status.READY;
        if (child != null)
            child.reset();
        return status;
    }

    // Returns the current status of this node.
    @Override
    public Status status() {
        return status;
    }

    // Provides a string representation of the node, including its current status
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
}
