package behave;

// WhileFailure node that ticks its child node and resets it to RUNNING if it fails, keeps it RUNNING if it's still
public class WhileFailure implements Node {
    private final Node child;
    private Status status = Status.READY;

    // Constructor takes a child node to decorate.
    public WhileFailure(Node child) {
        this.child = child;
    }

    // Ticks the child node and updates the status of this node based on the child's
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

    // Resets this node and its child node to READY.
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

    // Provides a string representation of this node, including its current status
    // and the string representation of its child node.
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
