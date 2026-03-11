package com.rbrabson.behave;

/**
 * The BehaviorTree class represents the root of a behavior tree structure. It
 * contains a reference to the root node of the tree and manages the overall
 * status of the tree. The BehaviorTree can be ticked to update its status based
 * on the status of its root node, and it can be reset to prepare for a new
 * execution cycle.
 */
public class BehaviorTree implements Node {
    private Node root;
    private Status status;

    /**
     * Constructor takes a root node for the behavior tree and initializes the
     * status to READY.
     *
     * @param root The root node of the behavior tree.
     */
    public BehaviorTree(Node root) {
        this.root = root;
        this.status = Status.READY;
    }

    /**
     * Ticks the root node of the behavior tree and updates the status accordingly.
     *
     * @return The current status of the behavior tree after ticking.
     */
    @Override
    public Status tick() {
        if (root == null) {
            status = Status.FAILURE;
            return status;
        }
        status = root.tick();
        return status;
    }

    /**
     * Resets the behavior tree by resetting the root node and setting the status
     * back to READY.
     *
     * @return The status of the behavior tree after resetting (which will be
     *         READY).
     */
    @Override
    public Status reset() {
        if (root != null) {
            root.reset();
        }
        status = Status.READY;
        return status;
    }

    /**
     * Returns the current status of the behavior tree.
     *
     * @return The current status of the behavior tree.
     */
    @Override
    public Status status() {
        return status;
    }

    /**
     * Provides a string representation of the behavior tree, including its current
     * status and the root node's representation.
     *
     * @return A string representation of the behavior tree.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("BehaviorTree (" + status() + ")");
        if (root != null) {
            String[] lines = root.toString().split("\n");
            for (String line : lines) {
                builder.append("\n  ").append(line);
            }
        }
        return builder.toString();
    }
}
