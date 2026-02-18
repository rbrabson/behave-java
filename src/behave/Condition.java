package behave;

public class Condition implements Node {
    private final ConditionFunction check;
    private Status status = Status.READY;

    public interface ConditionFunction {
        Status check();
    }

    public Condition(ConditionFunction check) {
        this.check = check;
    }

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

    @Override
    public Status reset() {
        status = Status.READY;
        return status;
    }

    @Override
    public Status status() {
        return status;
    }

    @Override
    public String toString() {
        return "Condition (" + status + ")";
    }
}
