package behave;

/**
 * Decorator node that always returns SUCCESS, regardless of the child's status.
 */
public class AlwaysSuccess implements Node {
    private final Node child;
    private Status status = Status.READY;

    /**
     * Constructor takes a child node to decorate.
     *
     * @param child The child node to decorate.
     */
    public AlwaysSuccess(Node child) {
        this.child = child;
    }

    /**
     * Ticks the child node and always returns SUCCESS.
     *
     * @return The current status of this node (which will always be SUCCESS).
     */
    @Override
    public Status tick() {
        if (child != null) {
            child.tick();
        }
        status = Status.SUCCESS;
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
        builder.append("AlwaysSuccess (" + status + ")");
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
