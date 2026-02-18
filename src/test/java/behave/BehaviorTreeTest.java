package behave;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class BehaviorTreeTest {
    @Test
    void testActionSuccess() {
        Action action = new Action(() -> Status.SUCCESS);
        assertEquals(Status.SUCCESS, action.tick());
        assertEquals(Status.SUCCESS, action.status());
        action.reset();
        assertEquals(Status.READY, action.status());
    }

    @Test
    void testConditionFailure() {
        Condition condition = new Condition(() -> Status.FAILURE);
        assertEquals(Status.FAILURE, condition.tick());
    }

    @Test
    void testSelector() {
        Action fail = new Action(() -> Status.FAILURE);
        Action succeed = new Action(() -> Status.SUCCESS);
        Selector selector = new Selector(Arrays.asList(fail, succeed));
        assertEquals(Status.SUCCESS, selector.tick());
    }

    @Test
    void testSequence() {
        Action a1 = new Action(() -> Status.SUCCESS);
        Action a2 = new Action(() -> Status.SUCCESS);
        Sequence sequence = new Sequence(Arrays.asList(a1, a2));
        assertEquals(Status.SUCCESS, sequence.tick());
    }

    @Test
    void testParallel() {
        Action a1 = new Action(() -> Status.SUCCESS);
        Action a2 = new Action(() -> Status.FAILURE);
        Parallel parallel = new Parallel(Arrays.asList(a1, a2), 1);
        assertEquals(Status.SUCCESS, parallel.tick());
    }

    @Test
    void testRetry() {
        AtomicInteger count = new AtomicInteger(0);
        Action a = new Action(() -> count.incrementAndGet() < 3 ? Status.FAILURE : Status.SUCCESS);
        Retry retry = new Retry(a);
        assertEquals(Status.RUNNING, retry.tick());
        assertEquals(Status.RUNNING, retry.tick());
        assertEquals(Status.SUCCESS, retry.tick());
    }

    @Test
    void testRepeatN() {
        AtomicInteger count = new AtomicInteger(0);
        Action a = new Action(() -> {
            count.incrementAndGet();
            return Status.SUCCESS;
        });
        RepeatN repeatN = new RepeatN(a, 3);
        assertEquals(Status.RUNNING, repeatN.tick());
        assertEquals(Status.RUNNING, repeatN.tick());
        assertEquals(Status.SUCCESS, repeatN.tick());
        assertEquals(3, count.get());
    }

    @Test
    void testWithTimeout() throws InterruptedException {
        Action running = new Action(() -> Status.RUNNING);
        WithTimeout timeout = new WithTimeout(running, Duration.ofMillis(100));
        assertEquals(Status.RUNNING, timeout.tick());
        Thread.sleep(120);
        assertEquals(Status.FAILURE, timeout.tick());
    }

    @Test
    void testComposite() {
        Condition c1 = new Condition(() -> Status.SUCCESS);
        Condition c2 = new Condition(() -> Status.SUCCESS);
        Action a = new Action(() -> Status.SUCCESS);
        Composite composite = new Composite(Arrays.asList(c1, c2), a);
        assertEquals(Status.SUCCESS, composite.tick());
    }

    @Test
    void testLogAndDecorators() {
        Action a = new Action(() -> Status.SUCCESS);
        Log log = new Log(a, "Action executed");
        AlwaysSuccess alwaysSuccess = new AlwaysSuccess(log);
        assertEquals(Status.SUCCESS, alwaysSuccess.tick());
    }
}
