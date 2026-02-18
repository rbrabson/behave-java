package behave;

import java.util.List;

public class Parallel implements Node {
    private final List<Node> children;
    private int minSuccessCount;
    private Status status = Status.READY;

    public Parallel(List<Node> children, int minSuccessCount) {
        this.children = children;
        this.minSuccessCount = minSuccessCount;
    }

    @Override
    public Status reset() {
        for (Node child : children) {
            child.reset();
        }
        status = Status.READY;
        return status;
    }

    @Override
    public Status tick() {
        if (children.isEmpty()) {
            status = Status.SUCCESS;
            return status;
        }
        if (minSuccessCount <= 0)
            minSuccessCount = 1;
        if (minSuccessCount > children.size())
            minSuccessCount = children.size();
        int successCount = 0, runningCount = 0;
        for (Node child : children) {
            Status s = child.tick();
            switch (s) {
            case SUCCESS:
                successCount++;
                break;
            case FAILURE:
                break;
            case RUNNING:
            case READY:
                runningCount++;
                break;
            }
        }
        if (successCount >= minSuccessCount) {
            status = Status.SUCCESS;
            return status;
        }
        int maxPossibleSuccesses = successCount + runningCount;
        if (maxPossibleSuccesses < minSuccessCount) {
            status = Status.FAILURE;
            return status;
        }
        if (runningCount > 0) {
            status = Status.RUNNING;
            return status;
        }
        status = Status.FAILURE;
        return status;
    }

    @Override
    public Status status() {
        return status;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Parallel (" + status + ")");
        for (Node child : children) {
            String[] lines = child.toString().split("\n");
            for (String line : lines) {
                builder.append("\n  ").append(line);
            }
        }
        return builder.toString();
    }
}// ...existing code from src/behave/Parallel.java...
