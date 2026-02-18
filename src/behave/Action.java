package behave;

public class Action implements Node {
    private final ActionFunction run;
    private Status status = Status.READY;

    public interface ActionFunction {
        Status run();
    }

    public Action(ActionFunction run) {
        this.run = run;
    }

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
        return "Action (" + status + ")";
    }
}
