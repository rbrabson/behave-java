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
            for (int i = 0; i < conditions.size(); i++) {
                builder.append("\n  Condition[" + i + "]: " + conditions.get(i));
            }
        }
        if (child != null) {
            String[] lines = child.toString().split("\n");
            builder.append("\n  Child: " + lines[0]);
            for (int i = 1; i < lines.length; i++) {
                builder.append("\n  " + lines[i]);
            }
        }
        return builder.toString();
    }
}
