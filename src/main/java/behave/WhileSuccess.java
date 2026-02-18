package behave;

// WhileSuccess node that ticks its child node and resets it to RUNNING if it succeeds, keeps it RUNNING if it's still running.
public class WhileSuccess implements Node {
    private final Node child;
    private Status status = Status.READY;

    // Constructor takes a child node to decorate.
    public WhileSuccess(Node child) {
        this.child = child;
    }

    // Ticks the child node and updates the status of this node based on the child's
    @Override
    public Status tick() {
        if (child == null) {
            status = Status.FAILURE;
            return status;
        }
        Status childStatus = child.tick();
        if (childStatus == Status.RUNNING || childStatus == Status.SUCCESS) {
            if (childStatus == Status.SUCCESS) {
                child.reset();
            }
            status = Status.RUNNING;
            return status;
        }
        status = Status.FAILURE;
        return status;
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
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WhileSuccess (" + status + ")");
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
