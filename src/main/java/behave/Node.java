package behave;

// Node interface that all behavior tree nodes implement.
public interface Node {
    // Ticks the node, updating its status based on its logic and returning the
    // current status.
    Status tick();

    // Resets the node to its initial state, typically setting status to READY and
    // resetting any child nodes.
    Status reset();

    // Returns the current status of the node.
    Status status();

    // Provides a string representation of the node, including its current status
    // and any relevant information about its children.
    String toString();
}
