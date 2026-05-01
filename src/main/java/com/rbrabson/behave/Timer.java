package com.rbrabson.behave;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

import com.rbrabson.time.ElapsedTimer;

/**
 * The Timer class is a decorator node in a behavior tree that adds a timing
 * mechanism to its child node. When ticked, the Timer will start a timer and
 * only tick its child node after the specified duration has elapsed. If the
 * Timer is reset, it will reset both itself and its child node. The Timer
 * updates its status based on the timing and the status of its child node.
 */
public class Timer implements Node {
    private final Node child;
    private Status status = Status.READY;
    private ElapsedTimer timer;
    private boolean timerStarted = false;

    /**
     * Constructor takes a child node to decorate and a duration in milliseconds.
     *
     * @param child The child node to decorate.
     */
    public Timer(Node child) {
        this.child = child;
        this.timer = new ElapsedTimer();
    }

    @Override
    public Status tick() {
        if (!timerStarted) {
            timerStarted = true;
            timer.reset();
        }
        if (child == null) {
            status = Status.FAILURE;
            return status;
        }
        Status childStatus = child.tick();
        if (childStatus == null) {
            status = Status.FAILURE;
            return status;
        }
        status = childStatus;
        return status;
    }

    @Override
    public Status reset() {
        status = Status.READY;
        if (child != null) {
            status = child.reset();
        }
        timerStarted = false;
        timer.reset();

        return status;
    }

    @Override
    public Status status() {
        return status;
    }

    /**
     * Returns the elapsed time since the timer was started in the specified time
     * unit.
     * 
     * @param unit The time unit to return the elapsed time in.
     * @return The elapsed time in the specified time unit.
     */
    public double elapsedTime(TimeUnit unit) {
        return timer.getElapsedTime(unit);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Timer (").append(status).append(")");
        builder.append(" [").append(elapsedTime(TimeUnit.MILLISECONDS)).append(" ms]");
        if (child != null) {
            String[] lines = child.toString().split("\n");
            for (String line : lines) {
                builder.append("\n  ").append(line);
            }
        }
        return builder.toString();
    }
}
