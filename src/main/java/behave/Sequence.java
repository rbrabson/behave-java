package behave;

import java.util.Arrays;
import java.util.List;

/**
 * The Sequence class is a composite node in a behavior tree that evaluates its
 * child nodes in order until one of them returns FAILURE. If a child node
 * returns SUCCESS, the Sequence moves on to the next child. If a child node
 * returns RUNNING, the Sequence returns RUNNING and will continue ticking that
 * child on the next tick. If all child nodes return SUCCESS, the Sequence
 * returns SUCCESS. This node is useful for creating behaviors that should
 * perform a series of actions in order, such as picking up an item, then using
 * it on an enemy.
 */
public class Sequence implements Node {
    private final List<Node> children;
    private Status status = Status.READY;

    /**
     * Constructor takes a list of child nodes to evaluate in order.
     *
     * @param children The list of child nodes to evaluate.
     */
    public Sequence(List<Node> children) {
        this.children = children;
    }

    /**
     * Constructor takes an array of child nodes to evaluate in order.
     * 
     * @param children The array of child nodes to evaluate.
     */
    public Sequence(Node... children) {
        this(Arrays.asList(children));
    }

    /**
     * Resets all child nodes and sets this node's status to READY.
     *
     * @return The status of this node after resetting (which will be READY).
     */
    @Override
    public Status reset() {
        for (Node child : children) {
            child.reset();
        }
        status = Status.READY;
        return status;
    }

    /**
     * Ticks each child node in order until one fails, returning the first failure
     * or the last success.
     *
     * @return The status of this node after ticking.
     */
    @Override
    public Status tick() {
        for (Node child : children) {
            Status s = child.tick();
            if (s == null) {
                status = Status.FAILURE;
                return status;
            }
            switch (s) {
            case SUCCESS:
                continue;
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
     * and the string representation of its child nodes.
     *
     * @return A string representation of this node.
     */
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
