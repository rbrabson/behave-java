package behave;

/**
 * The Node interface defines the basic structure for all nodes in a behavior
 * tree. It includes methods for ticking the node, resetting it, checking its
 * status, and providing a string representation. All nodes in the behavior
 * tree, including composite nodes, decorator nodes, and leaf nodes, should
 * implement this interface to ensure consistent behavior and interaction within
 * the tree.
 */
public interface Node {
    /**
     * Ticks the node, updating its status based on its logic and returning the
     * current status.
     *
     * @return The current status of this node after ticking.
     */
    Status tick();

    /**
     * Resets the node to its initial state, typically setting status to READY and
     * resetting any child nodes.
     *
     * @return The status of this node after resetting (which will be READY).
     */
    Status reset();

    /**
     * Returns the current status of the node.
     *
     * @return The current status of this node.
     */
    Status status();

    /**
     * Provides a string representation of the node, including its current status
     * and any relevant information about its children.
     *
     * @return A string representation of this node.
     */
    String toString();
}
