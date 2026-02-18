package behave;

// Decorator node that always returns FAILURE, regardless of the child's status.
public class AlwaysFailure implements Node {
    private final Node child;
    private Status status = Status.READY;

    // Constructor takes a child node to decorate.
    public AlwaysFailure(Node child) {
        this.child = child;
    }

    // Ticks the child node and always returns FAILURE.
    @Override
    public Status tick() {
        if (child != null) {
            child.tick();
        }
        status = Status.FAILURE;
        return status;
    }

    // Resets the status to READY and resets the child node if it exists.
    @Override
    public Status reset() {
        status = Status.READY;
        if (child != null)
            child.reset();
        return status;
    }

    // Returns the current status of this node.
    @Override
    public Status status() {
        return status;
    }

    // Provides a string representation of the node, including its current status
    // and the child's representation.
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
