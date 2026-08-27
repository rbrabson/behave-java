package com.rbrabson.behave;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The Selector class is a composite node in a behavior tree that evaluates its
 * child nodes in order until one of them returns SUCCESS. Null child references
 * are skipped. If a child node
 * returns FAILURE, the Selector moves on to the next child. If a child node
 * returns RUNNING, the Selector returns RUNNING and will continue ticking that
 * child on the next tick. If all child nodes return FAILURE, the Selector
 * returns FAILURE. This node is useful for creating behaviors that should try
 * multiple options until one succeeds, such as trying different paths to reach
 * a target.
 */
public class Selector implements Node {
    private final List<? extends Node> children;
    private Status status = Status.READY;

    /**
     * Constructor takes a list of child nodes to evaluate in order.
     *
     * @param children The list of child nodes to evaluate.
     */
    public Selector(List<? extends Node> children) {
        this.children = children == null ? Collections.<Node>emptyList() : children;
    }

    /**
     * Constructor takes an array of child nodes to evaluate in order.
     * 
     * @param children The array of child nodes to evaluate.
     */
    public Selector(Node... children) {
        this(children == null ? Collections.<Node>emptyList() : Arrays.asList(children));
    }

    /**
     * Resets all child nodes and sets this node's status to READY.
     *
     * @return The status of this node after resetting (which will be READY).
     */
    @Override
    public Status reset() {
        for (Node child : children) {
            if (child == null) {
                continue;
            }
            child.reset();
        }
        status = Status.READY;
        return status;
    }

    /**
     * Ticks each child node in order until one succeeds, returning the first
     * success or the last failure.
     *
     * @return The status of this node after ticking.
     */
    @Override
    public Status tick() {
        for (Node child : children) {
            if (child == null) {
                continue;
            }
            Status s = child.tick();
            if (s == null) {
                status = Status.FAILURE;
                return status;
            }
            switch (s) {
            case FAILURE:
                break;
            case READY:
            case RUNNING:
            case SUCCESS:
                status = s;
                return status;
            default:
                status = Status.FAILURE;
                return status;
            }
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
     * Provides a string representation of this node, including its current status,
     * and the string representation of its child nodes.
     *
     * @return A string representation of this node.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Selector (").append(status).append(")");
        for (Node child : children) {
            if (child == null) {
                continue;
            }
            String[] lines = child.toString().split("\n");
            for (String line : lines) {
                builder.append("\n  ").append(line);
            }
        }
        return builder.toString();
    }
}
