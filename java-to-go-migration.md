# Nudging Java Code Toward Go

A pragmatic roadmap for making Java code more Go-like, and eventually translatable.

---

## Low Hanging Fruit — Do These First

### Replace POJOs with Records
Records map almost directly to Go structs. If it's just data, make it a record:
```java
record Point(int x, int y) {}  // → type Point struct { X, Y int }
```

### Replace Checked Exceptions with Unchecked
Go has no checked exceptions — errors are return values. Start treating exceptions as
unrecoverable and stop declaring `throws` everywhere. This nudges you toward explicit
error handling.

### Stop Using Null — Use Optional
Go has no null — zero values and explicit checks instead. `Optional<T>` isn't perfect
but trains you toward the same thinking:
```java
Optional<User> findUser(String id)  // → (User, error) in Go
```

---

## Structural Changes

### Favour Composition Over Inheritance
Go has no inheritance at all. Every time you reach for `extends`, ask if you can use
a field instead:
```java
class Engine { ... }
class Car {
    private final Engine engine;  // composition, not extends
}
```

### Keep Interfaces Small — One or Two Methods
Go interfaces are typically tiny. `io.Reader` is just one method. Fight the urge to
make fat interfaces — split them up:
```java
interface Reader { Data read(); }
interface Writer { void write(Data d); }
// not one big ReaderWriter class hierarchy
```

### Avoid Deep Class Hierarchies
Go has none. If your hierarchy is more than one level deep, that's a smell worth
addressing now. Flatten toward interfaces + records.

---

## Concurrency Style

### Move Toward Virtual Threads + BlockingQueue
This maps reasonably well to goroutines + channels:
```java
// Producer
Thread.ofVirtual().start(() -> {
    queue.put(fetchData());
});

// Consumer
Thread.ofVirtual().start(() -> {
    Data d = queue.take();
    process(d);
});
```

### Use StructuredTaskScope for Cancellation
Maps to Go's `context.WithCancel()` pattern and will translate more cleanly.

---

## Harder — But Worth Doing

### Eliminate Static State
Go packages have no static state — everything is passed explicitly. Static fields are
a significant translation obstacle.

### Make Dependencies Explicit Parameters
Go has no dependency injection frameworks — you just pass things in. Start moving away
from Spring/CDI injection toward constructor parameters:
```java
// Instead of @Autowired everywhere
class OrderService {
    OrderService(Repository repo, PaymentGateway pay) { ... }
}
```

### Return Error-like Values Instead of Throwing
The most Go-like thing you can do, and the most painful in Java. A simple pattern:
```java
sealed interface Result<T> permits Success, Failure {}
record Success<T>(T value) implements Result<T> {}
record Failure<T>(String error) implements Result<T> {}

Result<User> findUser(String id) { ... }
```
Then use pattern matching to handle it:
```java
switch (findUser(id)) {
    case Success<User> s -> process(s.value());
    case Failure<User> f -> log(f.error());
}
```

---

## Things to Just Accept Are Different

- **Package system** — Java packages and Go packages are philosophically different, don't fight it too hard
- **Build tooling** — Maven/Gradle isn't going away, just keep it simple
- **Generics** — Java generics and Go generics are different enough that fighting for parity isn't worth the effort

---

## Suggested Order

1. Records everywhere you have POJOs
2. Composition over inheritance
3. Thin interfaces
4. Eliminate static state
5. Explicit constructor dependencies
6. `Result<T>` pattern for error handling
7. Virtual threads + queues for concurrency

Each step makes the code more Go-like *and* arguably better Java in its own right —
so you're not making sacrifices along the way, you're just progressively shedding the
parts of Java that don't translate.
