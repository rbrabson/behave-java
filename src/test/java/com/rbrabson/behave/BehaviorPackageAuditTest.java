package com.rbrabson.behave;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BehaviorPackageAuditTest {

    private static Node nodeReturning(Status result) {
        return new Node() {
            @Override
            public Status tick() {
                return result;
            }

            @Override
            public Status reset() {
                return Status.READY;
            }

            @Override
            public Status status() {
                return result;
            }
        };
    }

    @Test
    void sequenceResetStartsAtFirstChildAgain() {
        AtomicInteger firstTicks = new AtomicInteger();
        AtomicInteger secondTicks = new AtomicInteger();
        Sequence sequence = new Sequence(
                nodeReturning(Status.SUCCESS),
                new Action(() -> {
                    firstTicks.incrementAndGet();
                    return Status.SUCCESS;
                }),
                new Action(() -> {
                    secondTicks.incrementAndGet();
                    return Status.SUCCESS;
                }));

        assertEquals(Status.SUCCESS, sequence.tick());
        sequence.reset();
        assertEquals(Status.SUCCESS, sequence.tick());
        assertEquals(2, firstTicks.get());
        assertEquals(2, secondTicks.get());
    }

    @Test
    void nodesConvertNullChildStatusesToFailure() {
        Node nullStatus = nodeReturning(null);

        assertEquals(Status.FAILURE, new Repeat(nullStatus).tick());
        assertEquals(Status.FAILURE, new Retry(nullStatus).tick());
        assertEquals(Status.FAILURE, new RepeatN(nullStatus, 1).tick());
        assertEquals(Status.FAILURE, new WithTimeout(nullStatus, Duration.ofSeconds(1)).tick());
        assertEquals(Status.FAILURE, new Composite(Arrays.asList(nullStatus), null).tick());
        assertEquals(Status.FAILURE, new Composite(null, nullStatus).tick());
    }

    @Test
    void conditionResetRestoresReadyStatus() {
        Condition condition = new Condition(() -> true);

        assertEquals(Status.SUCCESS, condition.tick());
        assertEquals(Status.READY, condition.reset());
        assertEquals(Status.READY, condition.status());
    }

    @Test
    void collectionNodesSkipNullChildrenEverywhere() {
        Sequence sequence = new Sequence(Arrays.asList(null, nodeReturning(Status.SUCCESS)));
        Selector selector = new Selector(Arrays.asList(null, nodeReturning(Status.SUCCESS)));
        Parallel parallel = new Parallel(Arrays.asList(null, nodeReturning(Status.SUCCESS)));
        Composite composite = new Composite(Arrays.asList((Node) null), nodeReturning(Status.SUCCESS));

        assertEquals(Status.SUCCESS, sequence.tick());
        assertEquals(Status.SUCCESS, selector.tick());
        assertEquals(Status.SUCCESS, parallel.tick());
        assertEquals(Status.SUCCESS, composite.tick());

        sequence.reset();
        selector.reset();
        parallel.reset();
        composite.reset();
        assertEquals(Status.READY, sequence.status());
        assertEquals(Status.READY, selector.status());
        assertEquals(Status.READY, parallel.status());
        assertEquals(Status.READY, composite.status());
        assertEquals(true, sequence.toString().contains("Sequence"));
        assertEquals(true, selector.toString().contains("Selector"));
        assertEquals(true, parallel.toString().contains("Parallel"));
        assertEquals(true, composite.toString().contains("Composite"));
    }

    @Test
    void nullCollectionsAndRootDoNotThrow() {
        assertEquals(Status.SUCCESS, new Sequence((java.util.List<Node>) null).tick());
        assertEquals(Status.FAILURE, new Selector((java.util.List<Node>) null).tick());
        assertEquals(Status.SUCCESS, new Parallel((java.util.List<Node>) null).tick());
        assertEquals(Status.FAILURE, new BehaviorTree(null).tick());
        assertEquals(Status.FAILURE, new WithTimeout(nodeReturning(Status.RUNNING), null).tick());
        assertEquals(Status.FAILURE, new Composite(Collections.<Node>emptyList(), null).tick());
    }
}
