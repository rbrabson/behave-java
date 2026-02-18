package behave;

/**
 * The RepeatN class is a decorator node in a behavior tree that continuously
 * ticks its child node until it has been ticked a specified number of times. If
 * the child node returns SUCCESS or FAILURE, the RepeatN node resets the child
 * and continues ticking until the maximum count is reached. Once the maximum
 * count is reached, the RepeatN node returns the last status of the child node.
 * This node is useful for creating behaviors that should repeat a certain
 * number of times, such as attacking an enemy three times before retreating.
 */
public class RepeatN implements Node {
    private final Node child;
    private final int maxCount;
    private int count = 0;
    private Status status = Status.READY;

    /**
     * Constructor takes a child node to decorate and the maximum number of times to
     * tick it.
     *
     * @param child    The child node to decorate.
     * @param maxCount The maximum number of times to tick the child node.
     */
    public RepeatN(Node child, int maxCount) {
        this.child = child;
        this.maxCount = maxCount;
    }

    /**
     * Ticks the child node and updates the status and count of this node based on
     * the results, following the logic described above.
     * 
     * @return The current status of this node after ticking.
     */
    @Override
    public Status tick() {
        if (child == null) {
            status = Status.FAILURE;
            count = maxCount;
            return status;
        }
        if (maxCount <= 0 || count < maxCount) {
            Status childStatus = child.tick();
            if (childStatus == Status.RUNNING) {
                status = Status.RUNNING;
                return status;
            }
            count++;
            if (maxCount > 0 && count >= maxCount) {
                status = childStatus;
                return status;
            }
            child.reset();
            status = Status.RUNNING;
            return status;
        }
        return status;
    }

    /**
     * Resets the status to READY, resets the count to 0, and resets the child node
     * if it exists.
     *
     * @return The status of this node after resetting (which will be READY).
     */
    @Override
    public Status reset() {
        status = Status.READY;
        count = 0;
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
     * Provides a string representation of this node, including its current status,
     * count, and max count, as well as the string representation of its child.
     *
     * @return A string representation of this node.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RepeatN (" + status + ", " + count + "/" + maxCount + ")");
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
