package behave;

public class WhileFailure implements Node {
    private final Node child;
    private Status status = Status.READY;

    public WhileFailure(Node child) {
        this.child = child;
    }

    @Override
    public Status tick() {
        if (child == null) {
            status = Status.SUCCESS;
            return status;
        }
        Status childStatus = child.tick();
        switch (childStatus) {
        case RUNNING:
        case FAILURE:
            status = Status.RUNNING;
            if (childStatus == Status.FAILURE) {
                child.reset();
            }
            return status;
        case SUCCESS:
            status = Status.SUCCESS;
            return status;
        default:
            status = Status.RUNNING;
            return status;
        }
    }

    @Override
    public Status reset() {
        status = Status.READY;
        if (child != null)
            child.reset();
        return status;
    }

    @Override
    public Status status() {
        return status;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WhileFailure (" + status + ")");
        if (child != null) {
            String[] lines = child.toString().split("\n");
            builder.append("\n  " + lines[0]);
            for (int i = 1; i < lines.length; i++) {
                builder.append("\n  " + lines[i]);
            }
        }
        return builder.toString();
    }
}
