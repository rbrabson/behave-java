package behave;

// RepeatN node that ticks its child node up to a specified number of times, returning the child's status on the final tick.
public class RepeatN implements Node {
    private final Node child;
    private final int maxCount;
    private int count = 0;
    private Status status = Status.READY;

    // Constructor takes a child node to decorate and the maximum number of times to
    // tick it.
    public RepeatN(Node child, int maxCount) {
        this.child = child;
        this.maxCount = maxCount;
    }

    // Ticks the child node and updates the status of this node based on the child's
    // status and the count, following the logic described above.
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

    // Resets the count and status of this node, and resets the child node as well.
    @Override
    public Status reset() {
        status = Status.READY;
        count = 0;
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
    // count, and max count,
    // as well as the string representation of its child.
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
