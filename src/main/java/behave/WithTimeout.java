package behave;

import java.time.Duration;
import java.time.Instant;

// WithTimeout node that ticks its child node and fails if it doesn't succeed within the specified duration, 
// keeps it RUNNING if it's still running.
public class WithTimeout implements Node {
    private final Node child;
    private final Duration duration;
    private Instant startTime;
    private Status status = Status.READY;

    // Constructor takes a child node to decorate and a duration for the timeout.
    public WithTimeout(Node child, Duration duration) {
        this.child = child;
        this.duration = duration;
    }

    // Ticks the child node and updates the status of this node based on the child's
    // status and the timeout.
    @Override
    public Status tick() {
        if (child == null) {
            status = Status.FAILURE;
            return status;
        }
        if (startTime == null) {
            startTime = Instant.now();
        }
        Status childStatus = child.tick();
        switch (childStatus) {
        case SUCCESS:
        case FAILURE:
            status = childStatus;
            return status;
        case RUNNING:
            if (Duration.between(startTime, Instant.now()).compareTo(duration) >= 0) {
                status = Status.FAILURE;
                return status;
            }
            status = Status.RUNNING;
            return status;
        default:
            status = Status.FAILURE;
            return status;
        }
    }

    // Resets this node and its child node to READY, and resets the start time.
    @Override
    public Status reset() {
        status = Status.READY;
        startTime = null;
        if (child != null)
            child.reset();
        return status;
    }

    // Returns the current status of this node.
    @Override
    public Status status() {
        return status;
    }

    // Provides a string representation of this node, including its current status
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WithTimeout (" + status + ")");
        if (child != null) {
            String[] lines = child.toString().split("\n");
            builder.append("\n  " + lines[0]);
            for (int i = 1; i < lines.length; i++) {
                builder.append("\n  " + lines[i]);
            }
        }
        return builder.toString();
    }
}
