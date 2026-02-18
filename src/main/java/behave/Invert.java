package behave;

/**
 * The Invert class is a decorator node in a behavior tree that inverts the
 * status of its child node. If the child node returns SUCCESS, the Invert node
 * returns FAILURE, and if the child node returns FAILURE, the Invert node
 * returns SUCCESS. If the child node returns RUNNING or READY, the Invert node
 * returns the same status. The Invert node is useful for creating behaviors
 * that should succeed when a certain condition fails, and vice versa.
 */
public class Invert implements Node {
    private final Node child;
    private Status status = Status.READY;

    /**
     * Constructor takes a child node to decorate.
     *
     * @param child The child node to decorate.
     */
    public Invert(Node child) {
        this.child = child;
    }

    /**
     * Ticks the child node and inverts its status, updating the status of this node
     * accordingly.
     */
    // accordingly.
    @Override
    public Status tick() {
        if (child == null) {
            status = Status.FAILURE;
            return status;
        }
        Status childStatus = child.tick();
        if (childStatus == null) {
            status = Status.FAILURE;
            return status;
        }
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

    /**
     * Resets the status to READY and resets the child node if it exists.
     *
     * @return The status of this node after resetting (which will be READY).
     */
    @Override
    public Status reset() {
        status = Status.READY;
        if (child != null)
            child.reset();
        return status;
    }

    /**
     * Returns the current status of this node.
     *
     * @return The current status of this node.
     */
    @Override
    public Status status() {
        return status;
    }

    /**
     * Provides a string representation of the node, including its current status.
     *
     * @return A string representation of this node.
     */
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
