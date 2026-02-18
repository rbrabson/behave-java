package behave;

import java.util.List;

// Parallel node that ticks all its children and returns SUCCESS if at least a specified number of them succeed, 
// FAILURE if it's impossible for enough to succeed, and RUNNING otherwise.
public class Parallel implements Node {
    private final List<Node> children;
    private int minSuccessCount;
    private Status status = Status.READY;

    // Constructor takes a list of child nodes and the minimum number of successes
    // required for this node to succeed.
    public Parallel(List<Node> children, int minSuccessCount) {
        this.children = children;
        this.minSuccessCount = minSuccessCount;
    }

    // Overloaded constructor for when the minimum success count is not provided,
    // defaulting to 1.
    @Override
    public Status reset() {
        for (Node child : children) {
            child.reset();
        }
        status = Status.READY;
        return status;
    }

    // Ticks all child nodes and updates the status of this node based on the
    // results, following the logic described above.
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

    // Returns the current status of this node.
    @Override
    public Status status() {
        return status;
    }

    // Provides a string representation of the node, including its current status
    // and the string representations of its children.
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
}
