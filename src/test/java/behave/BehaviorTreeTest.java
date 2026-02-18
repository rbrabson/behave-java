package behave;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class BehaviorTreeTest {
    // --- Additional tests for 80%+ coverage ---
    @Test
    void testCompositeToStringAndReset() {
        Condition c1 = new Condition(() -> Status.SUCCESS);
        Action a = new Action(() -> Status.SUCCESS);
        Composite composite = new Composite(Arrays.asList(c1), a);
        composite.tick();
        assertTrue(composite.toString().contains("Composite"));
        assertEquals(Status.READY, composite.reset());
    }

    @Test
    void testParallelToStringAndReset() {
        Action a1 = new Action(() -> Status.SUCCESS);
        Action a2 = new Action(() -> Status.FAILURE);
        Parallel parallel = new Parallel(Arrays.asList(a1, a2), 1);
        parallel.tick();
        assertTrue(parallel.toString().contains("Parallel"));
        assertEquals(Status.READY, parallel.reset());
    }

    @Test
    void testSequenceToStringAndReset() {
        Action a1 = new Action(() -> Status.SUCCESS);
        Sequence sequence = new Sequence(Arrays.asList(a1));
        sequence.tick();
        assertTrue(sequence.toString().contains("Sequence"));
        assertEquals(Status.READY, sequence.reset());
    }

    @Test
    void testSelectorToStringAndReset() {
        Action a1 = new Action(() -> Status.FAILURE);
        Selector selector = new Selector(Arrays.asList(a1));
        selector.tick();
        assertTrue(selector.toString().contains("Selector"));
        assertEquals(Status.READY, selector.reset());
    }

    @Test
    void testRepeatNToStringAndReset() {
        Action a = new Action(() -> Status.SUCCESS);
        RepeatN repeatN = new RepeatN(a, 2);
        repeatN.tick();
        assertTrue(repeatN.toString().contains("RepeatN"));
        assertEquals(Status.READY, repeatN.reset());
    }

    @Test
    void testRetryToStringAndReset() {
        Action a = new Action(() -> Status.FAILURE);
        Retry retry = new Retry(a);
        retry.tick();
        assertTrue(retry.toString().contains("Retry"));
        assertEquals(Status.READY, retry.reset());
    }

    @Test
    void testWhileFailureToStringAndReset() {
        Action a = new Action(() -> Status.FAILURE);
        WhileFailure wf = new WhileFailure(a);
        wf.tick();
        assertTrue(wf.toString().contains("WhileFailure"));
        assertEquals(Status.READY, wf.reset());
    }

    @Test
    void testWhileSuccessToStringAndReset() {
        Action a = new Action(() -> Status.SUCCESS);
        WhileSuccess ws = new WhileSuccess(a);
        ws.tick();
        assertTrue(ws.toString().contains("WhileSuccess"));
        assertEquals(Status.READY, ws.reset());
    }

    @Test
    void testLogToStringAndReset() {
        Action a = new Action(() -> Status.SUCCESS);
        Log log = new Log(a, "msg");
        log.tick();
        assertTrue(log.toString().contains("Log"));
        assertEquals(Status.READY, log.reset());
    }

    @Test
    void testActionToStringAndReset() {
        Action a = new Action(() -> Status.SUCCESS);
        a.tick();
        assertTrue(a.toString().contains("Action"));
        assertEquals(Status.READY, a.reset());
    }

    @Test
    void testConditionToStringAndReset() {
        Condition c = new Condition(() -> Status.SUCCESS);
        c.tick();
        assertTrue(c.toString().contains("Condition"));
        assertEquals(Status.READY, c.reset());
    }

    @Test
    void testAlwaysSuccessToStringAndReset() {
        Action a = new Action(() -> Status.FAILURE);
        AlwaysSuccess as = new AlwaysSuccess(a);
        as.tick();
        assertTrue(as.toString().contains("AlwaysSuccess"));
        assertEquals(Status.READY, as.reset());
    }

    @Test
    void testAlwaysFailureToStringAndReset() {
        Action a = new Action(() -> Status.SUCCESS);
        AlwaysFailure af = new AlwaysFailure(a);
        af.tick();
        assertTrue(af.toString().contains("AlwaysFailure"));
        assertEquals(Status.READY, af.reset());
    }

    @Test
    void testForeverToStringAndReset() {
        Action a = new Action(() -> Status.SUCCESS);
        Forever f = new Forever(a);
        f.tick();
        assertTrue(f.toString().contains("Forever"));
        assertEquals(Status.READY, f.reset());
    }

    @Test
    void testWithTimeoutToStringAndReset() {
        Action a = new Action(() -> Status.SUCCESS);
        WithTimeout wt = new WithTimeout(a, Duration.ofMillis(10));
        wt.tick();
        assertTrue(wt.toString().contains("WithTimeout"));
        assertEquals(Status.READY, wt.reset());
    }

    @Test
    void testBehaviorTreeToStringAndReset() {
        Action a = new Action(() -> Status.SUCCESS);
        BehaviorTree tree = new BehaviorTree(a);
        tree.tick();
        assertTrue(tree.toString().contains("BehaviorTree"));
        assertEquals(Status.READY, tree.reset());
    }

    @Test
    void testStatusToStringAllValues() {
        for (Status s : Status.values()) {
            assertNotNull(s.toString());
        }
    }

    @Test
    void testCompositeAllConditionsRunning() {
        Condition running = new Condition(() -> Status.RUNNING);
        Action a = new Action(() -> Status.SUCCESS);
        Composite composite = new Composite(Arrays.asList(running), a);
        assertEquals(Status.RUNNING, composite.tick());
    }

    @Test
    void testCompositeAllConditionsNullList() {
        Composite composite = new Composite(null, null);
        assertEquals(Status.FAILURE, composite.tick());
    }

    @Test
    void testCompositeEmptyConditionsWithNullChild() {
        Composite composite = new Composite(Arrays.asList(), null);
        assertEquals(Status.FAILURE, composite.tick());
    }

    @Test
    void testCompositeEmptyConditionsWithChild() {
        Action a = new Action(() -> Status.SUCCESS);
        Composite composite = new Composite(Arrays.asList(), a);
        assertEquals(Status.SUCCESS, composite.tick());
    }

    @Test
    void testParallelMinSuccessZero() {
        Action a1 = new Action(() -> Status.SUCCESS);
        Parallel parallel = new Parallel(Arrays.asList(a1), 0);
        assertEquals(Status.SUCCESS, parallel.tick());
    }

    @Test
    void testParallelAllSuccess() {
        Action a1 = new Action(() -> Status.SUCCESS);
        Action a2 = new Action(() -> Status.SUCCESS);
        Parallel parallel = new Parallel(Arrays.asList(a1, a2), 2);
        assertEquals(Status.SUCCESS, parallel.tick());
    }

    @Test
    void testParallelNotEnoughPossibleSuccesses() {
        Action fail = new Action(() -> Status.FAILURE);
        Action running = new Action(() -> Status.RUNNING);
        Parallel parallel = new Parallel(Arrays.asList(fail, running), 2);
        assertEquals(Status.RUNNING, parallel.tick());
    }

    @Test
    void testSequenceAllReady() {
        Action ready = new Action(() -> Status.READY);
        Sequence sequence = new Sequence(Arrays.asList(ready, ready));
        assertEquals(Status.READY, sequence.tick());
    }

    @Test
    void testSequenceDefaultCase() {
        Node badNode = new Node() {
            @Override
            public Status tick() {
                return null;
            }

            @Override
            public Status reset() {
                return null;
            }

            @Override
            public Status status() {
                return null;
            }

            @Override
            public String toString() {
                return "bad";
            }
        };
        Sequence sequence = new Sequence(Arrays.asList(badNode));
        assertEquals(Status.FAILURE, sequence.tick());
    }

    @Test
    void testSelectorAllReady() {
        Action ready = new Action(() -> Status.READY);
        Selector selector = new Selector(Arrays.asList(ready, ready));
        assertEquals(Status.READY, selector.tick());
    }

    @Test
    void testSelectorDefaultCase() {
        Node badNode = new Node() {
            @Override
            public Status tick() {
                return null;
            }

            @Override
            public Status reset() {
                return null;
            }

            @Override
            public Status status() {
                return null;
            }

            @Override
            public String toString() {
                return "bad";
            }
        };
        Selector selector = new Selector(Arrays.asList(badNode));
        assertEquals(Status.FAILURE, selector.tick());
    }

    @Test
    void testRepeatNMaxCountNegative() {
        Action a = new Action(() -> Status.SUCCESS);
        RepeatN repeatN = new RepeatN(a, -1);
        assertEquals(Status.RUNNING, repeatN.tick());
    }

    @Test
    void testRepeatNChildReturnsFailure() {
        Action fail = new Action(() -> Status.FAILURE);
        RepeatN repeatN = new RepeatN(fail, 2);
        assertEquals(Status.RUNNING, repeatN.tick());
        assertEquals(Status.SUCCESS, repeatN.tick());
    }

    @Test
    void testRetryChildReturnsReady() {
        Action ready = new Action(() -> Status.READY);
        Retry retry = new Retry(ready);
        assertEquals(Status.RUNNING, retry.tick());
    }

    @Test
    void testWhileFailureChildReturnsReady() {
        Action ready = new Action(() -> Status.READY);
        WhileFailure whileFailure = new WhileFailure(ready);
        assertEquals(Status.RUNNING, whileFailure.tick());
    }

    @Test
    void testWhileSuccessChildReturnsReady() {
        Action ready = new Action(() -> Status.READY);
        WhileSuccess whileSuccess = new WhileSuccess(ready);
        assertEquals(Status.RUNNING, whileSuccess.tick());
    }

    @Test
    void testLogDefaultLevelAndNullMessage() {
        Action a = new Action(() -> Status.SUCCESS);
        Log log = new Log(a);
        assertEquals(Status.SUCCESS, log.tick());
    }

    @Test
    void testSequenceAllSuccess() {
        Action a1 = new Action(() -> Status.SUCCESS);
        Action a2 = new Action(() -> Status.SUCCESS);
        Sequence sequence = new Sequence(Arrays.asList(a1, a2));
        assertEquals(Status.SUCCESS, sequence.tick());
    }

    @Test
    void testSequenceWithFailure() {
        Action a1 = new Action(() -> Status.SUCCESS);
        Action a2 = new Action(() -> Status.FAILURE);
        Sequence sequence = new Sequence(Arrays.asList(a1, a2));
        assertEquals(Status.FAILURE, sequence.tick());
    }

    @Test
    void testSequenceWithRunning() {
        Action a1 = new Action(() -> Status.SUCCESS);
        Action a2 = new Action(() -> Status.RUNNING);
        Sequence sequence = new Sequence(Arrays.asList(a1, a2));
        assertEquals(Status.RUNNING, sequence.tick());
    }

    @Test
    void testSelectorAllFailure() {
        Action a1 = new Action(() -> Status.FAILURE);
        Action a2 = new Action(() -> Status.FAILURE);
        Selector selector = new Selector(Arrays.asList(a1, a2));
        assertEquals(Status.FAILURE, selector.tick());
    }

    @Test
    void testSelectorWithSuccess() {
        Action a1 = new Action(() -> Status.FAILURE);
        Action a2 = new Action(() -> Status.SUCCESS);
        Selector selector = new Selector(Arrays.asList(a1, a2));
        assertEquals(Status.SUCCESS, selector.tick());
    }

    @Test
    void testSelectorWithRunning() {
        Action a1 = new Action(() -> Status.FAILURE);
        Action a2 = new Action(() -> Status.RUNNING);
        Selector selector = new Selector(Arrays.asList(a1, a2));
        assertEquals(Status.RUNNING, selector.tick());
    }

    @Test
    void testRepeatNZeroMaxCount() {
        Action a = new Action(() -> Status.SUCCESS);
        RepeatN repeatN = new RepeatN(a, 0);
        assertEquals(Status.RUNNING, repeatN.tick());
    }

    @Test
    void testRepeatNRunningChild() {
        Action running = new Action(() -> Status.RUNNING);
        RepeatN repeatN = new RepeatN(running, 2);
        assertEquals(Status.RUNNING, repeatN.tick());
    }

    @Test
    void testRepeatNNullChild() {
        RepeatN repeatN = new RepeatN(null, 2);
        assertEquals(Status.FAILURE, repeatN.tick());
    }

    @Test
    void testRetrySuccessAfterFailure() {
        AtomicInteger count = new AtomicInteger(0);
        Action a = new Action(() -> count.incrementAndGet() < 2 ? Status.FAILURE : Status.SUCCESS);
        Retry retry = new Retry(a);
        assertEquals(Status.RUNNING, retry.tick());
        assertEquals(Status.SUCCESS, retry.tick());
    }

    @Test
    void testRetryRunning() {
        Action running = new Action(() -> Status.RUNNING);
        Retry retry = new Retry(running);
        assertEquals(Status.RUNNING, retry.tick());
    }

    @Test
    void testRetryNullChild() {
        Retry retry = new Retry(null);
        assertEquals(Status.FAILURE, retry.tick());
    }

    @Test
    void testWithTimeoutSuccess() {
        Action success = new Action(() -> Status.SUCCESS);
        WithTimeout timeout = new WithTimeout(success, Duration.ofMillis(100));
        assertEquals(Status.SUCCESS, timeout.tick());
    }

    @Test
    void testWithTimeoutFailure() {
        Action fail = new Action(() -> Status.FAILURE);
        WithTimeout timeout = new WithTimeout(fail, Duration.ofMillis(100));
        assertEquals(Status.FAILURE, timeout.tick());
    }

    @Test
    void testWithTimeoutNullChild() {
        WithTimeout timeout = new WithTimeout(null, Duration.ofMillis(100));
        assertEquals(Status.FAILURE, timeout.tick());
    }

    @Test
    void testActionAllStatuses() {
        for (Status s : Status.values()) {
            Action a = new Action(() -> s);
            assertEquals(s, a.tick());
        }
    }

    @Test
    void testActionNullFunction() {
        Action a = new Action(null);
        assertEquals(Status.FAILURE, a.tick());
    }

    @Test
    void testConditionAllStatuses() {
        for (Status s : Status.values()) {
            Condition c = new Condition(() -> s);
            assertEquals(s, c.tick());
        }
    }

    @Test
    void testConditionNullFunction() {
        Condition c = new Condition(null);
        assertEquals(Status.FAILURE, c.tick());
    }

    @Test
    void testRepeatDecoratorSuccessAndFailure() {
        AtomicInteger count = new AtomicInteger(0);
        Action succeedTwice = new Action(() -> count.incrementAndGet() < 3 ? Status.SUCCESS : Status.FAILURE);
        Repeat repeat = new Repeat(succeedTwice);
        assertEquals(Status.RUNNING, repeat.tick());
        assertEquals(Status.RUNNING, repeat.tick());
        assertEquals(Status.FAILURE, repeat.tick());
        repeat.reset();
        assertEquals(Status.READY, repeat.status());
    }

    @Test
    void testRepeatDecoratorWithNullChild() {
        Repeat repeat = new Repeat(null);
        assertEquals(Status.FAILURE, repeat.tick());
    }

    @Test
    void testCompositeWithNoConditionsAndNoChild() {
        Composite composite = new Composite(null, null);
        assertEquals(Status.FAILURE, composite.tick());
    }

    @Test
    void testCompositeWithNoConditionsButWithChild() {
        Action a = new Action(() -> Status.SUCCESS);
        Composite composite = new Composite(null, a);
        assertEquals(Status.SUCCESS, composite.tick());
    }

    @Test
    void testCompositeWithFailingCondition() {
        Condition fail = new Condition(() -> Status.FAILURE);
        Action a = new Action(() -> Status.SUCCESS);
        Composite composite = new Composite(Arrays.asList(fail), a);
        assertEquals(Status.FAILURE, composite.tick());
    }

    @Test
    void testParallelAllFail() {
        Action fail1 = new Action(() -> Status.FAILURE);
        Action fail2 = new Action(() -> Status.FAILURE);
        Parallel parallel = new Parallel(Arrays.asList(fail1, fail2), 1);
        assertEquals(Status.FAILURE, parallel.tick());
    }

    @Test
    void testParallelAllRunning() {
        Action running1 = new Action(() -> Status.RUNNING);
        Action running2 = new Action(() -> Status.RUNNING);
        Parallel parallel = new Parallel(Arrays.asList(running1, running2), 1);
        assertEquals(Status.RUNNING, parallel.tick());
    }

    @Test
    void testParallelEmptyChildren() {
        Parallel parallel = new Parallel(Arrays.asList(), 1);
        assertEquals(Status.SUCCESS, parallel.tick());
    }

    @Test
    void testParallelMinSuccessGreaterThanChildren() {
        Action success = new Action(() -> Status.SUCCESS);
        Parallel parallel = new Parallel(Arrays.asList(success), 5);
        assertEquals(Status.SUCCESS, parallel.tick());
    }

    @Test
    void testLogWithNullChild() {
        Log log = new Log(null, "Should warn");
        assertEquals(Status.FAILURE, log.tick());
    }

    @Test
    void testLogWithCustomLevel() {
        Action a = new Action(() -> Status.SUCCESS);
        Log log = new Log(a, "Custom", java.util.logging.Level.SEVERE);
        assertEquals(Status.SUCCESS, log.tick());
    }

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
