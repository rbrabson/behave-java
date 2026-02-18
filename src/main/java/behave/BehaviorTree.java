package behave;

// BehaviorTree class that manages the execution of a behavior tree with a given root node.
public class BehaviorTree implements Node {
    private Node root;
    private Status status;

    // Constructor takes a root node for the behavior tree and initializes the
    // status to READY.
    public BehaviorTree(Node root) {
        this.root = root;
        this.status = Status.READY;
    }

    // Ticks the root node of the behavior tree and updates the status accordingly.
    @Override
    public Status tick() {
        if (root == null) {
            status = Status.FAILURE;
            return status;
        }
        status = root.tick();
        return status;
    }

    // Resets the behavior tree by resetting the root node and setting the status
    // back to READY.
    @Override
    public Status reset() {
        if (root != null) {
            root.reset();
        }
        status = Status.READY;
        return status;
    }

    // Returns the current status of the behavior tree.
    @Override
    public Status status() {
        return status;
    }

    // Provides a string representation of the behavior tree, including its current
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("BehaviorTree (" + status() + ")");
        if (root != null) {
            String[] lines = root.toString().split("\n");
            for (String line : lines) {
                builder.append("\n  ").append(line);
            }
        }
        return builder.toString();
    }
}
