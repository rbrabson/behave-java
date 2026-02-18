package behave;

/**
 * The Action node is a leaf node in the behavior tree that performs a specific
 * action defined by the provided function. The function should return a Status
 * indicating the result of the action (READY, RUNNING, SUCCESS, or FAILURE).
 * The Action node updates its own status based on the function's return value
 * and can be reset to READY when needed.
 */
public class Action implements Node {
    private final ActionFunction run;
    private Status status = Status.READY;

    /**
     * Functional interface for the action's behavior. The run method should return
     * a Status indicating the result of the action.
     */
    public interface ActionFunction {
        Status run();
    }

    /**
     * Constructor takes an ActionFunction that defines the behavior of this action
     * node.
     *
     * @param run The function to execute when this action is ticked.
     */
    public Action(ActionFunction run) {
        this.run = run;
    }

    /**
     * Ticks the action node by executing the provided function and updating the
     * status based on the function's return value. If the function is null or
     * returns an invalid status, the action will return FAILURE.
     *
     * @return The current status of the action after ticking.
     */
    // value.
    @Override
    public Status tick() {
        if (run == null) {
            status = Status.FAILURE;
            return status;
        }
        Status s = run.run();
        if (s == null) {
            status = Status.FAILURE;
            return status;
        }
        switch (s) {
        case READY:
        case RUNNING:
        case SUCCESS:
        case FAILURE:
            status = s;
            return status;
        default:
            status = Status.FAILURE;
            return status;
        }
    }

    /**
     * Resets the action node's status to READY. This can be called when the
     * behavior tree is reset or when this node needs to be re-evaluated from the
     * beginning.
     *
     * @return The status of the action after resetting (which will be READY).
     */
    @Override
    public Status reset() {
        status = Status.READY;
        return status;
    }

    /**
     * Returns the current status of the action node. This can be used to check the
     * result of the last tick or to monitor the state of the action.
     *
     * @return The current status of the action node.
     */
    @Override
    public Status status() {
        return status;
    }

    /**
     * Provides a string representation of the action node, including its current
     * status. This can be useful for debugging and visualization of the behavior
     * tree.
     *
     * @return A string representation of the action node.
     */
    @Override
    public String toString() {
        return "Action (" + status + ")";
    }
}
