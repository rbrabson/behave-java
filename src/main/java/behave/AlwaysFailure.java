package behave;

/**
 * Decorator node that always returns FAILURE, regardless of the child's status.
 */
public class AlwaysFailure implements Node {
    private final Node child;
    private Status status = Status.READY;

    /**
     * Constructor takes a child node to decorate.
     *
     * @param child The child node to decorate.
     */
    public AlwaysFailure(Node child) {
        this.child = child;
    }

    /**
     * Ticks the child node and always returns RUNNING or FAILURE.
     *
     * @return The current status of this node (which will always be FAILURE).
     */
    @Override
    public Status tick() {
        if (child == null) {
            status = Status.FAILURE;
        } else {
            status = child.tick();
            if (status != Status.RUNNING) {
                status = Status.FAILURE;
            }
        }
        return status;
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
     * Provides a string representation of the node, including its current status
     * and the child's representation.
     *
     * @return A string representation of this node.
     */
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
