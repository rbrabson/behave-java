package behave;

public class RepeatN implements Node {
    private final Node child;
    private final int maxCount;
    private int count = 0;
    private Status status = Status.READY;

    public RepeatN(Node child, int maxCount) {
        this.child = child;
        this.maxCount = maxCount;
    }

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

    @Override
    public Status reset() {
        status = Status.READY;
        count = 0;
        if (child != null)
            child.reset();
        return status;
    }

    @Override
    public Status status() {
        return status;
    }

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
