package com.rbrabson.behave;

/**
 * The Repeat class is a decorator node in a behavior tree that continuously
 * ticks its child node until it fails. If the child node returns SUCCESS, the
 * Repeat node resets the child and continues ticking, returning RUNNING. If the
 * child node returns RUNNING, the Repeat node also returns RUNNING. If the
 * child node returns FAILURE, the Repeat node returns FAILURE. This node is
 * useful for creating behaviors that should repeat until a certain condition is
 * no longer met, such as patrolling an area until an enemy is detected.
 */
public class Repeat implements Node {
    private final Node child;
    private Status status = Status.READY;

    /**
     * Constructor takes a child node to decorate.
     *
     * @param child The child node to decorate.
     */
    public Repeat(Node child) {
        this.child = child;
    }

    /**
     * Ticks the child node and updates the status of this node based on the
     * results, following the logic described above.
     * 
     * @return The current status of this node after ticking.
     */
    @Override
    public Status tick() {
        if (child == null) {
            status = Status.FAILURE;
            return status;
        }
        Status childStatus = child.tick();
        if (childStatus == null) {
            status = Status.FAILURE;
            return status;
        }
        switch (childStatus) {
        case SUCCESS:
            child.reset();
            status = Status.RUNNING;
            return status;
        case RUNNING:
            status = Status.RUNNING;
            return status;
        case FAILURE:
            status = Status.FAILURE;
            return status;
        default:
            status = Status.FAILURE;
            return status;
        }
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
     * Provides a string representation of this node, including its current status
     * and the string representation of its child node.
     *
     * @return A string representation of this node.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Repeat (").append(status).append(")");
        if (child != null) {
            String[] lines = child.toString().split("\n");
            for (String line : lines) {
                builder.append("\n  ").append(line);
            }
        }
        return builder.toString();
    }
}
