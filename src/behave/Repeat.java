package behave;

public class Repeat implements Node {
    private final Node child;
    private Status status = Status.READY;

    public Repeat(Node child) {
        this.child = child;
    }

    @Override
    public Status tick() {
        if (child == null) {
            status = Status.FAILURE;
            return status;
        }
        Status childStatus = child.tick();
        switch (childStatus) {
        case SUCCESS:
            child.reset();
            status = Status.RUNNING;
            return status;
        case RUNNING:
            status = Status.RUNNING;
            return status;
        case FAILURE:
            status = Status.FAILURE;
            return status;
        default:
            status = Status.FAILURE;
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
        builder.append("Repeat (" + status + ")");
        if (child != null) {
            String[] lines = child.toString().split("\n");
            for (String line : lines) {
                builder.append("\n  ").append(line);
            }
        }
        return builder.toString();
    }
}
