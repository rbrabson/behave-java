package behave;

import java.util.List;

/**
 * The Composite class represents a node in a behavior tree that can have
 * multiple condition nodes and a single child node. The Composite node
 * evaluates its conditions and executes the child node if all conditions
 * succeed. The status of the Composite node is determined by the status of its
 * conditions and child node.
 */
public class Composite implements Node {
    private final List<Node> conditions;
    private final Node child;
    private Status status = Status.READY;

    /**
     * Constructor takes a list of condition nodes and a child node to execute if
     * all conditions succeed.
     *
     * @param conditions The list of condition nodes.
     * @param child      The child node to execute if all conditions succeed.
     */
    public Composite(List<Node> conditions, Node child) {
        this.conditions = conditions;
        this.child = child;
    }

    /**
     * Ticks the conditions and the child node, updating the status based on their
     * results. If any condition fails, the Composite node returns FAILURE. If all
     * conditions succeed, it ticks the child node and returns its status.
     *
     * @return The current status of this node after ticking.
     */
    // results.
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

    /**
     * Resets the status to READY and resets all condition nodes and the child node
     * if they exist.
     *
     * @return The status of this node after resetting (which will be READY).
     */
    // they exist.
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
     * Provides a string representation of the node, including its current status
     * and the representations of its conditions and child node.
     *
     * @return A string representation of this node.
     */
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
}
