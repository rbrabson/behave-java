package behave;

public class BehaviorTree {
    private Node root;
    private Status status;

    public BehaviorTree(Node root) {
        this.root = root;
        this.status = Status.READY;
    }

    public Status tick() {
        if (root == null) {
            status = Status.FAILURE;
            return status;
        }
        status = root.tick();
        return status;
    }

    public BehaviorTree reset() {
        if (root != null) {
            root.reset();
        }
        status = Status.READY;
        return this;
    }

    public Status status() {
        return status;
    }

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
