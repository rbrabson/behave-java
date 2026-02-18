package behave;

import java.util.List;

// Sequence node that ticks its child nodes in order until one fails, returning the first failure or the last success.
public class Sequence implements Node {
    private final List<Node> children;
    private Status status = Status.READY;

    // Constructor takes a list of child nodes to evaluate in order.
    public Sequence(List<Node> children) {
        this.children = children;
    }

    // Resets all child nodes and sets this node's status to READY.
    @Override
    public Status reset() {
        for (Node child : children) {
            child.reset();
        }
        status = Status.READY;
        return status;
    }

    // Ticks each child node in order until one fails, returning the first
    @Override
    public Status tick() {
        for (Node child : children) {
            Status s = child.tick();
            switch (s) {
            case SUCCESS:
                break;
            case READY:
            case RUNNING:
            case FAILURE:
                status = s;
                return status;
            default:
                status = Status.FAILURE;
                return status;
            }
        }
        status = Status.SUCCESS;
        return status;
    }

    // Returns the current status of this node.
    @Override
    public Status status() {
        return status;
    }

    // Provides a string representation of this node, including its current status,
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Sequence (" + status + ")");
        for (Node child : children) {
            String[] lines = child.toString().split("\n");
            for (String line : lines) {
                builder.append("\n  ").append(line);
            }
        }
        return builder.toString();
    }
}
