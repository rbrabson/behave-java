package behave;

/**
 * The WhileSuccess class is a decorator node in a behavior tree that
 * continuously ticks its child node until the child returns FAILURE. If the
 * child returns SUCCESS, the WhileSuccess node resets the child and continues
 * ticking it on the next tick. If the child returns RUNNING, the WhileSuccess
 * node returns RUNNING and will continue ticking that child on the next tick.
 * This node is useful for creating behaviors that should keep trying an action
 * until it fails, such as attempting to pick up an item until it finally fails
 * (e.g., because the item is no longer there).
 */
public class WhileSuccess implements Node {
    private final Node child;
    private Status status = Status.READY;

    /**
     * Constructor takes a child node to decorate.
     *
     * @param child The child node to decorate.
     */
    public WhileSuccess(Node child) {
        this.child = child;
    }

    /**
     * Ticks the child node and updates the status of this node based on the child's
     * status.
     *
     * @return The status of this node after ticking.
     */
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

    /**
     * Resets this node and its child node, setting the status to READY.
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
     * Provides a string representation of this node, including its current status.
     *
     * @return A string representation of this node.
     */
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
