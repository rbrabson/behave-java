package behave;

// Invert node that inverts the result of its child node.
public class Invert implements Node {
    private final Node child;
    private Status status = Status.READY;

    // Constructor takes a child node to decorate.
    public Invert(Node child) {
        this.child = child;
    }

    // Ticks the child node and inverts its status, updating the status of this node
    // accordingly.
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
}
