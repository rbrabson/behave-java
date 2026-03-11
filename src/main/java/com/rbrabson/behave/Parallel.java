package com.rbrabson.behave;

import java.util.List;
import java.util.Arrays;

/**
 * The Parallel class is a composite node in a behavior tree that ticks all of
 * its child nodes simultaneously. It returns SUCCESS if at least a specified
 * number of child nodes return SUCCESS, FAILURE if it's impossible for enough
 * to succeed, and RUNNING otherwise. This node is useful for creating behaviors
 * that require multiple conditions to be met or multiple actions to be
 * performed in parallel. The minimum number of successes required can be
 * configured, allowing for flexible behavior definitions.
 */
public class Parallel implements Node {
    private final List<? extends Node> children;
    private int minSuccessCount;
    private Status status = Status.READY;

    /**
     * Constructor takes a list of child nodes and the minimum number of successes
     * required for this node to succeed.
     *
     * @param children The list of child nodes.
     */
    public Parallel(List<? extends Node> children) {
        this.children = children;
        this.minSuccessCount = children.size();
        if (minSuccessCount <= 0) {
            this.minSuccessCount = 1;
        }
        if (minSuccessCount > children.size()) {
            this.minSuccessCount = children.size();
        }
    }

    /**
     * Constructor takes an array of child nodes and the minimum number of successes
     * required for this node to succeed.
     * 
     * @param children The array of child nodes.
     */
    public Parallel(Node... children) {
        this(Arrays.asList(children));
    }

    public Parallel withMinSuccess(int minSuccessCount) {
        if (minSuccessCount <= 0) {
            this.minSuccessCount = 1;
        } else if (minSuccessCount > children.size()) {
            this.minSuccessCount = children.size();
        } else {
            this.minSuccessCount = minSuccessCount;
        }
        return this;
    }

    /**
     * Resets the status to READY and resets all child nodes.
     *
     * @return The status of this node after resetting (which will be READY).
     */
    @Override
    public Status reset() {
        for (Node child : children) {
            child.reset();
        }
        status = Status.READY;
        return status;
    }

    /**
     * Ticks all child nodes and updates the status of this node based on the
     * results, following the logic described above.
     */
    @Override
    public Status tick() {
        if (children.isEmpty()) {
            status = Status.SUCCESS;
            return status;
        }

        int successCount = 0, runningCount = 0;
        for (Node child : children) {
            Status s = child.tick();
            if (s == null) {
                // treat null as failure
                continue;
            }
            switch (s) {
            case SUCCESS:
                successCount++;
                break;
            case RUNNING:
                runningCount++;
                break;
            case FAILURE:
            case READY:
                break;
            }
        }
        if (successCount >= minSuccessCount) {
            status = Status.SUCCESS;
            return status;
        }
        int maxPossibleSuccesses = successCount + runningCount;
        if (maxPossibleSuccesses < minSuccessCount) {
            status = Status.FAILURE;
            return status;
        }
        if (runningCount > 0) {
            status = Status.RUNNING;
            return status;
        }
        status = Status.FAILURE;
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
     * Provides a string representation of the node, including its current status
     * and the string representations of its children.
     *
     * @return A string representation of this node.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Parallel (" + status + ")");
        for (Node child : children) {
            String[] lines = child.toString().split("\n");
            for (String line : lines) {
                builder.append("\n  ").append(line);
            }
        }
        return builder.toString();
    }
}
