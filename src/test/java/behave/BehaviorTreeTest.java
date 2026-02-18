package behave;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class BehaviorTreeTest {
    static class RunForNTicks {
        int numTicksToRun = 0;
        int timesTicked = 0;

        RunForNTicks(int numTicks) {
            this.numTicksToRun = numTicks;
        }

        Status run() {
            timesTicked++;
            return timesTicked >= numTicksToRun ? Status.SUCCESS : Status.RUNNING;
        }
    }

    @Test
    void testUsingClassForAction() {
        RunForNTicks runForNTicks = new RunForNTicks(5);
        Action runTC = new Action(() -> runForNTicks.run());
        BehaviorTree tree = new BehaviorTree(runTC);
        while (tree.tick() == Status.RUNNING) {
            // Loop until the tree returns SUCCESS
        }
        assertEquals(Status.SUCCESS, tree.tick());
    }

    // --- Additional targeted tests for uncovered branches in decorator nodes ---
    @Test
    void testInvertAllBranches() {
        // Child returns SUCCESS
        Invert invertSuccess = new Invert(new Action(() -> Status.SUCCESS));
        assertEquals(Status.FAILURE, invertSuccess.tick());
        // Child returns FAILURE
        Invert invertFailure = new Invert(new Action(() -> Status.FAILURE));
        assertEquals(Status.SUCCESS, invertFailure.tick());
        // Child returns READY
        Invert invertReady = new Invert(new Action(() -> Status.READY));
        assertEquals(Status.READY, invertReady.tick());
        // Child returns null (default case)
        Invert invertNull = new Invert(new Action(() -> null));
        assertEquals(Status.SUCCESS, invertNull.tick());
    }

    @Test
    void testRepeatAllBranches() {
        // Child returns SUCCESS (should call child.reset and return RUNNING)
        AtomicInteger resetCount = new AtomicInteger(0);
        Node child = new Node() {
            @Override
            public Status tick() {
                return Status.SUCCESS;
            }

            @Override
            public Status reset() {
                resetCount.incrementAndGet();
                return Status.READY;
            }

            @Override
            public Status status() {
                return Status.READY;
            }

            @Override
            public String toString() {
                return "child";
            }
        };
        Repeat repeat = new Repeat(child);
        assertEquals(Status.RUNNING, repeat.tick());
        assertEquals(1, resetCount.get());
        // Child returns FAILURE
        Repeat repeatFail = new Repeat(new Action(() -> Status.FAILURE));
        assertEquals(Status.FAILURE, repeatFail.tick());
        // Child returns READY (default case)
        Repeat repeatReady = new Repeat(new Action(() -> Status.READY));
        assertEquals(Status.FAILURE, repeatReady.tick());
        // Child returns null (default case)
        Repeat repeatNull = new Repeat(new Action(() -> null));
        assertEquals(Status.FAILURE, repeatNull.tick());
    }

    @Test
    void testLogAllBranches() {
        // Child returns SUCCESS, custom message and level
        Log logSuccess = new Log(new Action(() -> Status.SUCCESS), "msg", java.util.logging.Level.INFO);
        assertEquals(Status.SUCCESS, logSuccess.tick());
        // Child returns FAILURE, no message, no level
        Log logFail = new Log(new Action(() -> Status.FAILURE));
        assertEquals(Status.FAILURE, logFail.tick());
        // Child returns RUNNING, null message, null level
        Log logRunning = new Log(new Action(() -> Status.RUNNING), null, null);
        assertEquals(Status.RUNNING, logRunning.tick());
        // Child returns READY, null message, null level
        Log logReady = new Log(new Action(() -> Status.READY), null, null);
        assertEquals(Status.READY, logReady.tick());
        // Child returns null (default case)
        Log logNull = new Log(new Action(() -> null));
        assertEquals(Status.FAILURE, logNull.tick());
    }

    @Test
    void testWhileFailureAllBranches() {
        // Child returns FAILURE (should call child.reset and return RUNNING)
        AtomicInteger resetCount = new AtomicInteger(0);
        Node child = new Node() {
            @Override
            public Status tick() {
                return Status.FAILURE;
            }

            @Override
            public Status reset() {
                resetCount.incrementAndGet();
                return Status.READY;
            }

            @Override
            public Status status() {
                return Status.READY;
            }

            @Override
            public String toString() {
                return "child";
            }
        };
        WhileFailure wf = new WhileFailure(child);
        assertEquals(Status.RUNNING, wf.tick());
        assertEquals(1, resetCount.get());
        // Child returns RUNNING
        WhileFailure wfRunning = new WhileFailure(new Action(() -> Status.RUNNING));
        assertEquals(Status.RUNNING, wfRunning.tick());
        // Child returns SUCCESS
        WhileFailure wfSuccess = new WhileFailure(new Action(() -> Status.SUCCESS));
        assertEquals(Status.SUCCESS, wfSuccess.tick());
        // Child returns null (default case)
        WhileFailure wfNull = new WhileFailure(new Action(() -> null));
        assertEquals(Status.RUNNING, wfNull.tick());
    }

    @Test
    void testWhileSuccessAllBranches() {
        // Child returns SUCCESS (should call child.reset and return RUNNING)
        AtomicInteger resetCount = new AtomicInteger(0);
        Node child = new Node() {
            @Override
            public Status tick() {
                return Status.SUCCESS;
            }

            @Override
            public Status reset() {
                resetCount.incrementAndGet();
                return Status.READY;
            }

            @Override
            public Status status() {
                return Status.READY;
            }

            @Override
            public String toString() {
                return "child";
            }
        };
        WhileSuccess ws = new WhileSuccess(child);
        assertEquals(Status.RUNNING, ws.tick());
        assertEquals(1, resetCount.get());
        // Child returns RUNNING
        WhileSuccess wsRunning = new WhileSuccess(new Action(() -> Status.RUNNING));
        assertEquals(Status.RUNNING, wsRunning.tick());
        // Child returns FAILURE
        WhileSuccess wsFail = new WhileSuccess(new Action(() -> Status.FAILURE));
        assertEquals(Status.FAILURE, wsFail.tick());
        // Child returns null (default case)
        WhileSuccess wsNull = new WhileSuccess(new Action(() -> null));
        assertEquals(Status.FAILURE, wsNull.tick());
    }

    @Test
    void testAlwaysSuccessAllBranches() {
        // Child returns all statuses, AlwaysSuccess should always return SUCCESS
        for (Status s : Status.values()) {
            AlwaysSuccess as = new AlwaysSuccess(new Action(() -> s));
            assertEquals(Status.SUCCESS, as.tick());
        }
        // Child is null
        AlwaysSuccess asNull = new AlwaysSuccess(null);
        assertEquals(Status.SUCCESS, asNull.tick());
    }

    @Test
    void testAlwaysFailureAllBranches() {
        // Child returns all statuses, AlwaysFailure should always return FAILURE
        for (Status s : Status.values()) {
            AlwaysFailure af = new AlwaysFailure(new Action(() -> s));
            assertEquals(Status.FAILURE, af.tick());
        }
        // Child is null
        AlwaysFailure afNull = new AlwaysFailure(null);
        assertEquals(Status.FAILURE, afNull.tick());
    }

    @Test
    void testForeverAllBranches() {
        // Child returns all statuses, Forever should always return RUNNING
        for (Status s : Status.values()) {
            Forever f = new Forever(new Action(() -> s));
            assertEquals(Status.RUNNING, f.tick());
        }
        // Child is null
        Forever fNull = new Forever(null);
        assertEquals(Status.RUNNING, fNull.tick());
    }

    // --- Targeted tests for uncovered branches ---
    @Test
    void testInvertNullChildAndRunning() {
        Invert invert = new Invert(null);
        assertEquals(Status.FAILURE, invert.tick());
        Action running = new Action(() -> Status.RUNNING);
        Invert invert2 = new Invert(running);
        assertEquals(Status.RUNNING, invert2.tick());
    }

    @Test
    void testRepeatNullChildAndRunning() {
        Repeat repeat = new Repeat(null);
        assertEquals(Status.FAILURE, repeat.tick());
        Action running = new Action(() -> Status.RUNNING);
        Repeat repeat2 = new Repeat(running);
        assertEquals(Status.RUNNING, repeat2.tick());
    }

    @Test
    void testLogLevelsAndNulls() {
        Action running = new Action(() -> Status.RUNNING);
        Log log = new Log(running, null, null);
        assertEquals(Status.RUNNING, log.tick());
        Action ready = new Action(() -> Status.READY);
        Log log2 = new Log(ready, null, null);
        assertEquals(Status.READY, log2.tick());
        Log log3 = new Log(null, null, null);
        assertEquals(Status.FAILURE, log3.tick());
    }

    @Test
    void testWithTimeoutDefaultCase() throws InterruptedException {
        Action ready = new Action(() -> Status.READY);
        WithTimeout wt = new WithTimeout(ready, Duration.ofMillis(10));
        assertEquals(Status.FAILURE, wt.tick());
    }

    @Test
    void testWhileFailureDefaultCase() {
        Action nullStatus = new Action(() -> null);
        WhileFailure wf = new WhileFailure(nullStatus);
        assertEquals(Status.RUNNING, wf.tick());
    }

    @Test
    void testWhileSuccessDefaultCase() {
        Action nullStatus = new Action(() -> null);
        WhileSuccess ws = new WhileSuccess(nullStatus);
        assertEquals(Status.FAILURE, ws.tick());
    }

    @Test
    void testAlwaysSuccessNullChild() {
        AlwaysSuccess as = new AlwaysSuccess(null);
        assertEquals(Status.SUCCESS, as.tick());
    }

    @Test
    void testAlwaysFailureNullChild() {
        AlwaysFailure af = new AlwaysFailure(null);
        assertEquals(Status.FAILURE, af.tick());
    }

    @Test
    void testForeverNullChild() {
        Forever f = new Forever(null);
        assertEquals(Status.RUNNING, f.tick());
    }

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
        assertEquals(Status.FAILURE, parallel.tick());
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
        assertEquals(Status.FAILURE, repeatN.tick());
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
        assertEquals(Status.FAILURE, whileSuccess.tick());
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
