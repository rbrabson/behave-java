package com.rbrabson.behave;

/**
 * The Condition class represents a leaf node in a behavior tree that evaluates
 * a condition function. The condition function is a functional interface that
 * returns a Status. The Condition node updates its status based on the result
 * of the condition function when ticked.
 */
public class Condition implements Node {
    private final ConditionFunction check;

    /**
     * Functional interface for the condition function that returns a Status.
     */
    public interface ConditionFunction {
        boolean check();
    }

    /**
     * Constructor takes a condition function to evaluate.
     *
     * @param check The condition function to evaluate.
     */
    public Condition(ConditionFunction check) {
        this.check = check;
    }

    /**
     * Ticks the condition function and updates the status based on its result.
     *
     * @return The current status of this node after ticking.
     */
    // status based on its result.
    @Override
    public Status tick() {
        return status();
    }

    /**
     * Resets the status to READY.
     *
     * @return The status of this node after resetting (which will be READY).
     */
    @Override
    public Status reset() {
        return Status.READY;
    }

    /**
     * Returns the current status of this node.
     *
     * @return The current status of this node.
     */
    @Override
    public Status status() {
        if (check == null) {
            return Status.FAILURE;
        }
        return check.check() ? Status.SUCCESS : Status.FAILURE;
    }

    /**
     * Provides a string representation of the node, including its current status.
     *
     * @return A string representation of this node.
     */
    @Override
    public String toString() {
        return "Condition (" + status() + ")";
    }
}
