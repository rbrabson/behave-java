package behave;

/**
 * The Status enum represents the possible states of a node in a behavior tree.
 * Each node can be in one of four states: READY, RUNNING, SUCCESS, or FAILURE.
 * This enum is used to track the state of nodes as they are ticked and reset
 * during the execution of the behavior tree.
 */
public enum Status {
    /** The node is ready to be ticked. */
    READY,
    /** The node is currently running. */
    RUNNING,
    /** The node has successfully completed its task. */
    SUCCESS,
    /** The node has failed to complete its task. */
    FAILURE;

    /**
     * Provides a string representation of the status for easier debugging and
     * visualization.
     *
     * @return A string representation of the status.
     */
    @Override
    public String toString() {
        switch (this) {
        case READY:
            return "Ready";
        case RUNNING:
            return "Running";
        case SUCCESS:
            return "Success";
        case FAILURE:
            return "Failure";
        default:
            return "Unknown";
        }
    }
}
