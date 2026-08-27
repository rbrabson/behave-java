package com.rbrabson.time;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ElapsedTimerTest {

    @Test
    void constructorStartsTimerImmediately() {
        ElapsedTimer timer = new ElapsedTimer();

        assertNotNull(timer);
        assertTrue(timer.getElapsedTime() >= 0);
    }

    @Test
    void elapsedTimeAdvances() throws InterruptedException {
        ElapsedTimer timer = new ElapsedTimer();
        long before = timer.getElapsedTime();

        Thread.sleep(20);

        assertTrue(timer.getElapsedTime() > before);
    }

    @Test
    void resetStartsMeasuringFromReset() throws InterruptedException {
        ElapsedTimer timer = new ElapsedTimer();
        Thread.sleep(20);

        timer.reset();

        assertTrue(timer.getElapsedTime() >= 0);
        assertTrue(timer.getElapsedTime(TimeUnit.MILLISECONDS) < 100);
    }

    @Test
    void supportsEveryTimeUnit() throws InterruptedException {
        ElapsedTimer timer = new ElapsedTimer();
        Thread.sleep(20);

        assertTrue(timer.getElapsedTime(TimeUnit.NANOSECONDS) >= 20_000_000L);
        assertTrue(timer.getElapsedTime(TimeUnit.MICROSECONDS) >= 20_000.0);
        assertTrue(timer.getElapsedTime(TimeUnit.MILLISECONDS) >= 20.0);
        assertTrue(timer.getElapsedTime(TimeUnit.SECONDS) >= 0.020);
        assertTrue(timer.getElapsedTime(TimeUnit.MINUTES) >= 0.020 / 60.0);
        assertTrue(timer.getElapsedTime(TimeUnit.HOURS) >= 0.020 / 3600.0);
        assertTrue(timer.getElapsedTime(TimeUnit.DAYS) >= 0.020 / (24.0 * 3600.0));
    }

    @Test
    void secondsConvenienceMethodUsesSecondsConversion() throws InterruptedException {
        ElapsedTimer timer = new ElapsedTimer();
        Thread.sleep(20);

        double seconds = timer.getElapsedTimeSeconds();
        double convertedSeconds = timer.getElapsedTime(TimeUnit.SECONDS);

        assertTrue(seconds >= 0.020);
        assertEquals(seconds, convertedSeconds, 0.005);
    }

    @Test
    void nullTimeUnitIsRejected() {
        ElapsedTimer timer = new ElapsedTimer();

        assertThrows(NullPointerException.class, () -> timer.getElapsedTime(null));
    }
}
