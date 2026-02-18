package behave;

public interface Node {
    Status tick();

    Status reset();

    Status status();

    String toString();
}// ...existing code from src/behave/Node.java...
