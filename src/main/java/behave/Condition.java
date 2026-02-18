package behave;

// Condition node that evaluates a condition function and returns its status.
public class Condition implements Node {
    private final ConditionFunction check;
    private Status status = Status.READY;

    // Functional interface for the condition function that returns a Status.
    public interface ConditionFunction {
        Status check();
    }

    // Constructor takes a condition function to evaluate.
    public Condition(ConditionFunction check) {
        this.check = check;
    }

    // Ticks the condition by evaluating the condition function and updating the
    // status based on its result.
    @Override
    public Status tick() {
        if (check == null) {
            status = Status.FAILURE;
            return status;
        }
        Status s = check.check();
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

    // Resets the status to READY.
    @Override
    public Status reset() {
        status = Status.READY;
        return status;
    }

    // Returns the current status of this node.
    @Override
    public Status status() {
        return status;
    }

    // Provides a string representation of the node, including its current status
    @Override
    public String toString() {
        return "Condition (" + status + ")";
    }
}
