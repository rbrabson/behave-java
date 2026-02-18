package behave;

public class AlwaysFailure implements Node {
    private final Node child;
    private Status status = Status.READY;

    public AlwaysFailure(Node child) {
        this.child = child;
    }

    @Override
    public Status tick() {
        if (child != null) {
            child.tick();
        }
        status = Status.FAILURE;
        return status;
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
        builder.append("AlwaysFailure (" + status + ")");
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
