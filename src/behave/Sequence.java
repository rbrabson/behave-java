package behave;

import java.util.List;

public class Sequence implements Node {
    private final List<Node> children;
    private Status status = Status.READY;

    public Sequence(List<Node> children) {
        this.children = children;
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

    @Override
    public Status status() {
        return status;
    }

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
