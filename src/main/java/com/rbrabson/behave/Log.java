package com.rbrabson.behave;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Log class is a decorator node in a behavior tree that logs the status of
 * its child node when ticked. It can optionally take a custom message and log
 * level. If no message is provided, it defaults to "Log node executed". If no
 * log level is provided, it defaults to INFO for SUCCESS, WARNING for FAILURE,
 * and FINE for RUNNING and READY. The Log node updates its status based on the
 * status of its child node and logs the relevant information each time it is
 * ticked.
 */
public class Log implements Node {
    private static final Logger logger = Logger.getLogger(Log.class.getName());
    private final Node child;
    private final String message;
    private final Level logLevel;
    private Status status = Status.READY;

    /**
     * Constructor takes a child node to decorate, an optional message to log, and
     * an optional log level.
     *
     * @param child    The child node to decorate.
     * @param message  The message to log (optional).
     * @param logLevel The log level to use (optional).
     */
    public Log(Node child, String message, Level logLevel) {
        this.child = child;
        this.message = message;
        this.logLevel = logLevel;
    }

    /**
     * Overloaded constructor for when only the child node and message are provided.
     *
     * @param child   The child node to decorate.
     * @param message The message to log (optional).
     */
    // provided.
    public Log(Node child, String message) {
        this(child, message, null);
    }

    /**
     * Overloaded constructor for when only the child node is provided.
     *
     * @param child The child node to decorate.
     */
    public Log(Node child) {
        this(child, null, null);
    }

    /**
     * Ticks the child node and logs the status, message, and child type. The log
     * level is determined based on the child's status if not explicitly provided.
     *
     * @return The current status of this node after ticking.
     */
    // accordingly.
    @Override
    public Status tick() {
        if (child == null) {
            status = Status.FAILURE;
            log(Level.WARNING, "Log node has no child", status);
            return status;
        }
        Status childStatus = child.tick();
        if (childStatus == null) {
            status = Status.FAILURE;
            log(Level.WARNING, "Log node child returned null status", status);
            return status;
        }
        status = childStatus;
        String msg = (message != null) ? message : "Log node executed";
        Level level = (logLevel != null) ? logLevel : defaultLevel(childStatus);
        log(level, msg, childStatus);
        return status;
    }

    /**
     * Helper method to log the message along with the child's status and type.
     *
     * @param level       The log level to use for logging.
     * @param msg         The message to log.
     * @param childStatus The status of the child node to include in the log.
     */
    // information.
    private void log(Level level, String msg, Status childStatus) {
        logger.log(level, msg + " | child_status=" + childStatus + ", child_type=" + getChildType());
    }

    /**
     * Helper method to determine the default log level based on the child's status.
     *
     * @param s The status of the child node.
     * @return The default log level for the given status.
     */
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

    /**
     * Helper method to get the type of the child node for logging purposes.
     *
     * @return The simple class name of the child node, or "null" if there is no
     *         child.
     */
    private String getChildType() {
        return (child != null) ? child.getClass().getSimpleName() : "null";
    }

    /**
     * Resets the status of this node and its child node (if any) to READY.
     *
     * @return The status of this node after reset. This is READY if there is no
     *         child, otherwise it is the status of the child after reset.
     */
    @Override
    public Status reset() {
        status = Status.READY;
        if (child != null) {
            status = child.reset();
        }
        return status;
    }

    /**
     * Returns the current status of this node.
     *
     * @return The current status of this node.
     */
    @Override
    public Status status() {
        return status;
    }

    /**
     * Provides a string representation of the node, including its current status.
     *
     * @return A string representation of this node.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Log (").append(status).append(")");
        if (child != null) {
            String[] lines = child.toString().split("\n");
            for (String line : lines) {
                builder.append("\n  ").append(line);
            }
        }
        return builder.toString();
    }
}
