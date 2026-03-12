package com.rbrabson.behave;

/**
 * The Forever class is a decorator node in a behavior tree that continuously
 * ticks its child node and always returns RUNNING. This node is useful for
 * creating behaviors that should run indefinitely until interrupted. The status
 * of the Forever node is always RUNNING, regardless of the status of its child
 * node.
 */
public class Forever implements Node {
    private final Node child;
    private Status status = Status.READY;

    /**
     * Constructor takes a child node to decorate.
     *
     * @param child The child node to decorate.
     */
    public Forever(Node child) {
        this.child = child;
    }

    /**
     * Ticks the child node and always returns RUNNING.
     *
     * @return The current status of this node after ticking (which will be
     *         RUNNING).
     */
    @Override
    public Status tick() {
        if (child != null) {
            child.tick();
        }
        status = Status.RUNNING;
        return status;
    }

    /**
     * Resets the status to READY and resets the child node if it exists.
     *
     * @return The status of this node after resetting (which will be READY).
     */
    @Override
    public Status reset() {
        status = Status.READY;
        if (child != null)
            child.reset();
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
        builder.append("Forever (").append(status).append(")");
        if (child != null) {
            String[] lines = child.toString().split("\n");
            for (String line : lines) {
                builder.append("\n  ").append(line);
            }
        }
        return builder.toString();
    }
}
