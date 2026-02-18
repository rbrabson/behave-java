package behave;

// Retry node that ticks its child node and resets it to RUNNING if it fails, keeps it RUNNING if it's still
public class Retry implements Node {
    private final Node child;
    private Status status = Status.READY;

    // Constructor takes a child node to decorate.
    public Retry(Node child) {
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
        switch (childStatus) {
        case SUCCESS:
            status = Status.SUCCESS;
            return status;
        case RUNNING:
            status = Status.RUNNING;
            return status;
        case FAILURE:
            child.reset();
            status = Status.RUNNING;
            return status;
        default:
            status = Status.RUNNING;
            return status;
        }
    }

    // Resets the status of this node, and resets the child node as well.
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

    // Provides a string representation of this node, including its current status,
    // and
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Retry (" + status + ")");
        if (child != null) {
            String[] lines = child.toString().split("\n");
            for (String line : lines) {
                builder.append("\n  ").append(line);
            }
        }
        return builder.toString();
    }
}
