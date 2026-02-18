package behave;

// Enum representing the possible statuses of a behavior tree node.
public enum Status {
    READY, RUNNING, SUCCESS, FAILURE;

    // Provides a string representation of the status for easier debugging and
    // visualization.
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
