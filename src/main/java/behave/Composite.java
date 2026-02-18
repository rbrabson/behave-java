package behave;

import java.util.List;

public class Composite implements Node {
    private final List<Node> conditions;
    private final Node child;
    private Status status = Status.READY;

    public Composite(List<Node> conditions, Node child) {
        this.conditions = conditions;
        this.child = child;
    }

    @Override
    public Status tick() {
        if ((conditions == null || conditions.isEmpty()) && child == null) {
            status = Status.FAILURE;
            return status;
        }
        if (conditions == null || conditions.isEmpty()) {
            if (child != null) {
                status = child.tick();
                return status;
            }
            status = Status.FAILURE;
            return status;
        }
        for (Node cond : conditions) {
            Status condStatus = cond.tick();
            switch (condStatus) {
            case SUCCESS:
                continue;
            case RUNNING:
                status = Status.RUNNING;
                return status;
            case FAILURE:
            case READY:
            default:
                status = Status.FAILURE;
                return status;
            }
        }
        if (child != null) {
            status = child.tick();
            return status;
        }
        status = Status.SUCCESS;
        return status;
    }

    @Override
    public Status reset() {
        if (conditions != null) {
            for (Node cond : conditions) {
                cond.reset();
            }
        }
        if (child != null) {
            child.reset();
        }
        status = Status.READY;
        return status;
    }

    @Override
    public Status status() {
        return status;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Composite (" + status + ")");
        if (conditions != null) {
            for (Node cond : conditions) {
                String[] lines = cond.toString().split("\n");
                for (String line : lines) {
                    builder.append("\n  ").append(line);
                }
            }
        }
        if (child != null) {
            String[] lines = child.toString().split("\n");
            for (String line : lines) {
                builder.append("\n  ").append(line);
            }
        }
        return builder.toString();
    }
}// ...existing code from src/behave/Composite.java...
