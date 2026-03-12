package com.rbrabson.behave;

import java.time.Duration;
import java.time.Instant;

/**
 * The WithTimeout class is a decorator node in a behavior tree that adds a
 * timeout to its child node. It continuously ticks the child node until the
 * child returns SUCCESS or FAILURE, or until the specified duration has
 * elapsed. If the child returns RUNNING and the duration has not yet elapsed,
 * the WithTimeout node returns RUNNING. If the duration elapses while the child
 * is still RUNNING, the WithTimeout node returns FAILURE. This node is useful
 * for creating behaviors that should fail if they take too long to complete,
 * such as waiting for an event to occur within a certain time frame.
 */
// keeps it RUNNING if it's still running.
public class WithTimeout implements Node {
    private final Node child;
    private final Duration duration;
    private Instant startTime;
    private Status status = Status.READY;

    /**
     * Constructor takes a child node to decorate and a duration for the timeout.
     *
     * @param child    The child node to decorate.
     * @param duration The duration for the timeout.
     */
    public WithTimeout(Node child, Duration duration) {
        this.child = child;
        this.duration = duration;
    }

    /**
     * Ticks the child node and updates the status of this node based on the child's
     * status and the timeout.
     *
     * @return The status of this node after ticking.
     */
    @Override
    public Status tick() {
        if (child == null) {
            status = Status.FAILURE;
            return status;
        }
        if (startTime == null) {
            startTime = Instant.now();
        }
        Status childStatus = child.tick();
        switch (childStatus) {
        case SUCCESS:
        case FAILURE:
            status = childStatus;
            return status;
        case RUNNING:
            if (Duration.between(startTime, Instant.now()).compareTo(duration) >= 0) {
                status = Status.FAILURE;
                return status;
            }
            status = Status.RUNNING;
            return status;
        default:
            status = Status.FAILURE;
            return status;
        }
    }

    /**
     * Resets this node and its child node, setting the status to READY and clearing
     * the start time.
     *
     * @return The status of this node after resetting (which will be READY).
     */
    @Override
    public Status reset() {
        status = Status.READY;
        startTime = null;
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
     * Provides a string representation of this node, including its current status.
     *
     * @return A string representation of this node.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WithTimeout (").append(status).append(")");
        if (child != null) {
            String[] lines = child.toString().split("\n");
            for (String line : lines) {
                builder.append("\n  ").append(line);
            }
        }
        return builder.toString();
    }
}
