# Behavior Tree

## Usage Examples

Below are examples demonstrating how to use each class in the behave package. These are minimal examples; see the test suite for more advanced usage.

### Core Classes

```java
// Create a simple behavior tree with an Action node
import behave.*;

BehaviorTree tree = new BehaviorTree(new Action(() -> Status.SUCCESS));
tree.tick(); // returns Status.SUCCESS
```

### Action

```java
Action action = new Action(() -> Status.SUCCESS);
action.tick(); // returns Status.SUCCESS
```

### Condition

```java
Condition condition = new Condition(() -> Status.FAILURE);
condition.tick(); // returns Status.FAILURE
```

### Composite

```java
Condition c1 = new Condition(() -> Status.SUCCESS);
Condition c2 = new Condition(() -> Status.SUCCESS);
Action a = new Action(() -> Status.SUCCESS);
Composite composite = new Composite(Arrays.asList(c1, c2), a);
composite.tick(); // returns Status.SUCCESS
```

### Selector

```java
Action fail = new Action(() -> Status.FAILURE);
Action succeed = new Action(() -> Status.SUCCESS);
Selector selector = new Selector(Arrays.asList(fail, succeed));
selector.tick(); // returns Status.SUCCESS
```

### Sequence

```java
Action a1 = new Action(() -> Status.SUCCESS);
Action a2 = new Action(() -> Status.SUCCESS);
Sequence sequence = new Sequence(Arrays.asList(a1, a2));
sequence.tick(); // returns Status.SUCCESS
```

### Parallel

```java
Action a1 = new Action(() -> Status.SUCCESS);
Action a2 = new Action(() -> Status.FAILURE);
Parallel parallel = new Parallel(Arrays.asList(a1, a2), 1);
parallel.tick(); // returns Status.SUCCESS
```

### Retry

```java
AtomicInteger count = new AtomicInteger(0);
Action a = new Action(() -> count.incrementAndGet() < 3 ? Status.FAILURE : Status.SUCCESS);
Retry retry = new Retry(a);
retry.tick(); // returns Status.RUNNING until success
```

### Repeat

```java
AtomicInteger count = new AtomicInteger(0);
Action a = new Action(() -> count.incrementAndGet() < 3 ? Status.SUCCESS : Status.FAILURE);
Repeat repeat = new Repeat(a);
repeat.tick(); // returns Status.RUNNING until failure
```

### RepeatN

```java
AtomicInteger count = new AtomicInteger(0);
Action a = new Action(() -> { count.incrementAndGet(); return Status.SUCCESS; });
RepeatN repeatN = new RepeatN(a, 3);
repeatN.tick(); // returns Status.RUNNING, then Status.SUCCESS after 3 times
```

### Forever

```java
Action a = new Action(() -> Status.SUCCESS);
Forever forever = new Forever(a);
forever.tick(); // always returns Status.RUNNING
```

### Invert

```java
Action a = new Action(() -> Status.SUCCESS);
Invert invert = new Invert(a);
invert.tick(); // returns Status.FAILURE
```

### AlwaysSuccess / AlwaysFailure

```java
Action a = new Action(() -> Status.FAILURE);
AlwaysSuccess alwaysSuccess = new AlwaysSuccess(a);
alwaysSuccess.tick(); // returns Status.SUCCESS

Action b = new Action(() -> Status.SUCCESS);
AlwaysFailure alwaysFailure = new AlwaysFailure(b);
alwaysFailure.tick(); // returns Status.FAILURE
```

### WhileSuccess / WhileFailure

```java
AtomicInteger count = new AtomicInteger(0);
Action a = new Action(() -> count.incrementAndGet() < 3 ? Status.SUCCESS : Status.FAILURE);
WhileSuccess ws = new WhileSuccess(a);
ws.tick(); // returns Status.RUNNING until failure

AtomicInteger count2 = new AtomicInteger(0);
Action b = new Action(() -> count2.incrementAndGet() < 3 ? Status.FAILURE : Status.SUCCESS);
WhileFailure wf = new WhileFailure(b);
wf.tick(); // returns Status.RUNNING until success
```

### WithTimeout

```java
Action running = new Action(() -> Status.RUNNING);
WithTimeout timeout = new WithTimeout(running, Duration.ofMillis(100));
timeout.tick(); // returns Status.RUNNING, then Status.FAILURE after timeout
```

### Log

```java
Action a = new Action(() -> Status.SUCCESS);
Log log = new Log(a, "Action executed");
log.tick(); // logs the status and returns Status.SUCCESS
```

## Class Overview

**Core Classes:**

- `BehaviorTree`: The main class representing a behavior tree, managing the root node and tree status.
- `Node`: Interface for all behavior tree nodes. All node types implement this.
- `Status`: Enum for node states: READY, RUNNING, SUCCESS, FAILURE.

**Node Types:**

- `Action`: Leaf node that executes a user-provided action function.
- `Condition`: Leaf node that evaluates a user-provided condition function.
- `Composite`: Runs a list of condition nodes and a child node; child runs only if all conditions succeed.
- `Selector`: Runs children in order, returning SUCCESS on the first child that succeeds.
- `Sequence`: Runs children in order, returning FAILURE on the first child that fails.
- `Parallel`: Runs all children in parallel; succeeds if a minimum number of children succeed.

**Decorators:**

- `Retry`: Repeats its child until it succeeds.
- `Repeat`: Repeats its child until it fails.
- `RepeatN`: Repeats its child a fixed number of times.
- `Forever`: Runs its child forever (always RUNNING).
- `Invert`: Inverts the result of its child (SUCCESS→FAILURE, FAILURE→SUCCESS).
- `AlwaysSuccess`: Returns SUCCESS regardless of its child's result.
- `AlwaysFailure`: Returns FAILURE regardless of its child's result.
- `WhileSuccess`: Repeats its child while it returns SUCCESS or RUNNING.
- `WhileFailure`: Repeats its child while it returns FAILURE or RUNNING.
- `WithTimeout`: Runs its child, but fails if it takes longer than a specified duration.
- `Log`: Logs the status of its child each tick.

See the source files in `src/main/java/behave/` for implementation details.

## Behvior Tree

A Java implementation of a flexible behavior tree framework, inspired by the Go [behave](../behave) library.

## Features

- BehaviorTree, Node, and Status abstractions
- Action, Condition, Composite, Selector, Sequence, Parallel nodes
- Decorators: Retry, Repeat, Invert, AlwaysSuccess, AlwaysFailure, RepeatN, Forever, WhileSuccess, WhileFailure, WithTimeout, Log
- Easy to extend and integrate

## Usage

## Repository Structure

``` bash
behave-java/
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   └── java/
│   │       └── behave/
│   │           ├── Action.java
│   │           ├── AlwaysFailure.java
│   │           ├── AlwaysSuccess.java
│   │           ├── BehaviorTree.java
│   │           ├── Composite.java
│   │           ├── Condition.java
│   │           ├── Forever.java
│   │           ├── Invert.java
│   │           ├── Log.java
│   │           ├── Node.java
│   │           ├── Parallel.java
│   │           ├── Repeat.java
│   │           ├── RepeatN.java
│   │           ├── Retry.java
│   │           ├── Selector.java
│   │           ├── Sequence.java
│   │           ├── Status.java
│   │           ├── WhileFailure.java
│   │           ├── WhileSuccess.java
│   │           └── WithTimeout.java
```

- Core implementation: `src/main/java/behave/`
- Unit tests: `src/test/java/test/behave/BehaviorTreeTest.java`

Build and test with Maven:

```sh
mvn clean test
```

## License

MIT
