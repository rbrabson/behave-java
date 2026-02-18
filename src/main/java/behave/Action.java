package behave;

/// Action node that executes a provided function and returns its status.
public class Action implements Node {
    private final ActionFunction run;
    private Status status = Status.READY;

    // Functional interface for the action's behavior.
    public interface ActionFunction {
        Status run();
    }

    // Constructor takes a function that defines the action's behavior.
    public Action(ActionFunction run) {
        this.run = run;
    }

    // Executes the action and updates its status based on the function's return
    // value.
    @Override
    public Status tick() {
        if (run == null) {
            status = Status.FAILURE;
            return status;
        }
        Status s = run.run();
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

    // Resets the action's status to READY.
    @Override
    public Status reset() {
        status = Status.READY;
        return status;
    }

    // Returns the current status of the action.
    @Override
    public Status status() {
        return status;
    }

    // Provides a string representation of the action, including its current status.
    @Override
    public String toString() {
        return "Action (" + status + ")";
    }
}
