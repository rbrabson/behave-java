
# behave-java

## Overview

behave-java is a Java library for building and executing behavior trees. It provides a set of classes for modeling actions, conditions, composites, and decorators, enabling flexible control flows for AI, robotics, and automation scenarios.

### Main Package Structure

- `behave.Action` — Represents an executable action node.
- `behave.Condition` — Represents a condition node.
- `behave.Composite` — Base class for composite nodes (e.g., Sequence, Selector, Parallel).
- `behave.Decorator` — Base class for decorator nodes (e.g., Invert, Repeat, Retry).
- `behave.BehaviorTree` — The root node for executing a tree.
- `behave.Status` — Enum for node execution status (SUCCESS, FAILURE, RUNNING).

## Usage

### Example: Building a Behavior Tree

```java
import behave.*;

// Define custom actions and conditions
Action myAction = new Action(() -> Status.SUCCESS);
Condition myCondition = new Condition(() -> true ? Status.SUCCESS : Status.FAILURE);

// Build a simple behavior tree
Sequence sequence = new Sequence(Arrays.asList(myCondition, myAction));
BehaviorTree tree = new BehaviorTree(sequence);
tree.tick(); // Executes the tree
```

### Running Tests

To run unit tests:

```sh
mvn test
```

### Building the Project

To build the project:

```sh
mvn clean install
```

## Best Practices

- Compose small, focused nodes for clarity and reuse.
- Use composites and decorators to express complex behaviors simply.
- Use decorators like `Retry`, `Repeat`, and `WithTimeout` for robustness.
- Integrate application state via lambdas or method references.
- Use `Log` nodes for debugging and tracing execution.
- Unit test custom nodes and trees; mock dependencies for flexibility.
- Extend the framework by implementing custom nodes and decorators.

## Repository Structure

```bash
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
## Usage Examples

Below are real-world inspired examples demonstrating how to use the behave package to model practical behavior trees.

### Example 1: Simple AI Agent (Patrol or Attack)

```java
import behave.*;
import java.util.*;

// Condition: Is enemy visible?
Condition enemyVisible = new Condition(() -> isEnemyVisible() ? Status.SUCCESS : Status.FAILURE);

// Action: Attack enemy
Action attack = new Action(() -> attackEnemy());

// Action: Patrol area
Action patrol = new Action(() -> patrolArea());

// Selector: If enemy is visible, attack; otherwise, patrol
Selector root = new Selector(Arrays.asList(
 new Sequence(Arrays.asList(enemyVisible, attack)),
 patrol
));

BehaviorTree tree = new BehaviorTree(root);
tree.tick();

// Helper methods (pseudo-implementations)
boolean isEnemyVisible() { /* ... */ return false; }
Status attackEnemy() { /* ... */ return Status.SUCCESS; }
Status patrolArea() { /* ... */ return Status.SUCCESS; }
```

### Example 2: Retry and Timeout for Robust Actions

```java
import behave.*;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

AtomicInteger attempts = new AtomicInteger(0);
Action unreliableAction = new Action(() -> attempts.incrementAndGet() < 3 ? Status.FAILURE : Status.SUCCESS);

// Retry the action up to 3 times, but fail if it takes too long
Retry retry = new Retry(unreliableAction);
WithTimeout timeout = new WithTimeout(retry, Duration.ofSeconds(2));

BehaviorTree tree = new BehaviorTree(timeout);
tree.tick();
```

### Example 3: Parallel Node for Multi-Tasking

```java
import behave.*;
import java.util.*;

Action scan = new Action(() -> scanForThreats());
Action move = new Action(() -> moveToWaypoint());

// Both actions must succeed for the parallel node to succeed
Parallel parallel = new Parallel(Arrays.asList(scan, move), 2);

BehaviorTree tree = new BehaviorTree(parallel);
tree.tick();

Status scanForThreats() { /* ... */ return Status.SUCCESS; }
Status moveToWaypoint() { /* ... */ return Status.SUCCESS; }
```

### Example 4: Logging and Decorators

```java
import behave.*;
import java.util.logging.Level;

Action a = new Action(() -> Status.SUCCESS);
Log log = new Log(a, "Action executed", Level.INFO);
AlwaysSuccess alwaysSuccess = new AlwaysSuccess(log);

BehaviorTree tree = new BehaviorTree(alwaysSuccess);
tree.tick();
```

### Example 5: Custom Composite with Conditions

```java
import behave.*;
import java.util.*;

Condition hasAmmo = new Condition(() -> hasAmmo() ? Status.SUCCESS : Status.FAILURE);
Action reload = new Action(() -> reloadWeapon());
Action shoot = new Action(() -> shootWeapon());

// Only shoot if has ammo, otherwise reload
Composite shootIfAmmo = new Composite(Arrays.asList(hasAmmo), shoot);
Selector root = new Selector(Arrays.asList(shootIfAmmo, reload));

BehaviorTree tree = new BehaviorTree(root);
tree.tick();

boolean hasAmmo() { /* ... */ return false; }
Status reloadWeapon() { /* ... */ return Status.SUCCESS; }
Status shootWeapon() { /* ... */ return Status.SUCCESS; }
```

// See the test suite for more advanced and edge-case scenarios.

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

## License

MIT
