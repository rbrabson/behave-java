package behave;

public class Invert implements Node {
    private final Node child;
    private Status status = Status.READY;

    public Invert(Node child) {
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
            status = Status.FAILURE;
            return status;
        case FAILURE:
            status = Status.SUCCESS;
            return status;
        case RUNNING:
            status = Status.RUNNING;
            return status;
        case READY:
            status = Status.READY;
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
        builder.append("Invert (" + status + ")");
        if (child != null) {
            String[] lines = child.toString().split("\n");
            builder.append("\n  " + lines[0]);
            for (int i = 1; i < lines.length; i++) {
                builder.append("\n  " + lines[i]);
            }
        }
        return builder.toString();
    }
}// ...existing code from src/behave/Invert.java...
