package com.rbrabson.time;

import java.util.concurrent.TimeUnit;

/**
 * This is the ElapsedTimer class. It is an elapsed time clock with nanosecond
 * precision, or at least as precise as the System.nanoTime() is.
 */
public class ElapsedTimer {
    private long startTime;

    /**
     * This creates a new ElapsedTimer with the start time set to its creation time.
     */
    public ElapsedTimer() {
        reset();
    }

    /**
     * This resets the ElapsedTimer's start time to the current time using
     * System.nanoTime().
     */
    public void reset() {
        startTime = System.nanoTime();
    }

    /**
     * This returns the elapsed time in nanoseconds since the start time of the
     * ElapsedTimer.
     *
     * @return this returns the elapsed time in nanoseconds.
     */
    public long getElapsedTime() {
        return (System.nanoTime() - startTime);
    }

    /**
     * This returns the elapsed time in seconds since the start time of the
     * ElapsedTimer. This is equivalent to calling getElapsedTime(TimeUnit.SECONDS).
     *
     * @return this returns the elapsed time in seconds.
     */
    public double getElapsedTimeSeconds() {
        return getElapsedTime(TimeUnit.SECONDS);
    }

    /**
     * This returns the elapsed time in the specified TimeUnit since the start time
     * of the ElapsedTimer.
     * 
     * @param timeUnit the TimeUnit to convert the elapsed time to.
     * @return this returns the elapsed time in the specified TimeUnit.
     */
    public double getElapsedTime(TimeUnit timeUnit) {
        long elapsedTime = getElapsedTime();
        switch (timeUnit) {
        case NANOSECONDS:
            return elapsedTime;
        case MICROSECONDS:
            return elapsedTime / 1_000.0;
        case MILLISECONDS:
            return elapsedTime / 1_000_000.0;
        case SECONDS:
            return elapsedTime / 1_000_000_000.0;
        case MINUTES:
            return elapsedTime / (60 * 1_000_000_000.0);
        case HOURS:
            return elapsedTime / (3600 * 1_000_000_000.0);
        case DAYS:
            return elapsedTime / (24 * 3600 * 1_000_000_000.0);
        default:
            throw new IllegalArgumentException("Unsupported TimeUnit: " + timeUnit);
        }
    }
}