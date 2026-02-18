package behave;

import java.time.Duration;
import java.time.Instant;

public class WithTimeout implements Node {
    private final Node child;
    private final Duration duration;
    private Instant startTime;
    private Status status = Status.READY;

    public WithTimeout(Node child, Duration duration) {
        this.child = child;
        this.duration = duration;
    }

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

    @Override
    public Status reset() {
        status = Status.READY;
        startTime = null;
        if (child != null)
            child.reset();
        return status;
    }

    @Override
    public Status status() {
        return status;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WithTimeout (" + status + ", Duration: " + duration + ")");
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
