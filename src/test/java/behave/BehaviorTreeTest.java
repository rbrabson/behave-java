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

    @Test
    void testAlwaysFailureDecorator() {
        Action success = new Action(() -> Status.SUCCESS);
        AlwaysFailure alwaysFailure = new AlwaysFailure(success);
        assertEquals(Status.FAILURE, alwaysFailure.tick());
        alwaysFailure.reset();
        assertEquals(Status.READY, alwaysFailure.status());
    }

    @Test
    void testAlwaysSuccessDecorator() {
        Action fail = new Action(() -> Status.FAILURE);
        AlwaysSuccess alwaysSuccess = new AlwaysSuccess(fail);
        assertEquals(Status.SUCCESS, alwaysSuccess.tick());
        alwaysSuccess.reset();
        assertEquals(Status.READY, alwaysSuccess.status());
    }

    @Test
    void testInvertDecorator() {
        Action success = new Action(() -> Status.SUCCESS);
        Invert invert = new Invert(success);
        assertEquals(Status.FAILURE, invert.tick());
        invert.reset();
        assertEquals(Status.READY, invert.status());

        Action fail = new Action(() -> Status.FAILURE);
        Invert invert2 = new Invert(fail);
        assertEquals(Status.SUCCESS, invert2.tick());
    }

    @Test
    void testForeverDecorator() {
        Action success = new Action(() -> Status.SUCCESS);
        Forever forever = new Forever(success);
        assertEquals(Status.RUNNING, forever.tick());
        forever.reset();
        assertEquals(Status.READY, forever.status());
    }

    @Test
    void testWhileFailureDecorator() {
        AtomicInteger count = new AtomicInteger(0);
        Action failTwice = new Action(() -> count.incrementAndGet() < 3 ? Status.FAILURE : Status.SUCCESS);
        WhileFailure whileFailure = new WhileFailure(failTwice);
        assertEquals(Status.RUNNING, whileFailure.tick());
        assertEquals(Status.RUNNING, whileFailure.tick());
        assertEquals(Status.SUCCESS, whileFailure.tick());
        whileFailure.reset();
        assertEquals(Status.READY, whileFailure.status());
    }

    @Test
    void testWhileSuccessDecorator() {
        AtomicInteger count = new AtomicInteger(0);
        Action succeedTwice = new Action(() -> count.incrementAndGet() < 3 ? Status.SUCCESS : Status.FAILURE);
        WhileSuccess whileSuccess = new WhileSuccess(succeedTwice);
        assertEquals(Status.RUNNING, whileSuccess.tick());
        assertEquals(Status.RUNNING, whileSuccess.tick());
        assertEquals(Status.FAILURE, whileSuccess.tick());
        whileSuccess.reset();
        assertEquals(Status.READY, whileSuccess.status());
    }

    @Test
    void testBehaviorTreeNullRoot() {
        BehaviorTree tree = new BehaviorTree(null);
        assertEquals(Status.FAILURE, tree.tick());
        assertEquals(Status.READY, tree.reset());
    }

    @Test
    void testBehaviorTreeWithRoot() {
        Action action = new Action(() -> Status.SUCCESS);
        BehaviorTree tree = new BehaviorTree(action);
        assertEquals(Status.SUCCESS, tree.tick());
        assertEquals(Status.READY, tree.reset());
    }

    @Test
    void testInvertWithNullChild() {
        Invert invert = new Invert(null);
        assertEquals(Status.FAILURE, invert.tick());
    }

    @Test
    void testForeverWithNullChild() {
        Forever forever = new Forever(null);
        assertEquals(Status.RUNNING, forever.tick());
    }

    @Test
    void testWhileFailureWithNullChild() {
        WhileFailure whileFailure = new WhileFailure(null);
        assertEquals(Status.SUCCESS, whileFailure.tick());
    }

    @Test
    void testWhileSuccessWithNullChild() {
        WhileSuccess whileSuccess = new WhileSuccess(null);
        assertEquals(Status.FAILURE, whileSuccess.tick());
    }

    @Test
    void testAlwaysFailureWithNullChild() {
        AlwaysFailure alwaysFailure = new AlwaysFailure(null);
        assertEquals(Status.FAILURE, alwaysFailure.tick());
    }

    @Test
    void testAlwaysSuccessWithNullChild() {
        AlwaysSuccess alwaysSuccess = new AlwaysSuccess(null);
        assertEquals(Status.SUCCESS, alwaysSuccess.tick());
    }
}
