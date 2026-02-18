package behave;

public enum Status {
    READY, RUNNING, SUCCESS, FAILURE;

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
}// ...existing code from src/behave/Status.java...
