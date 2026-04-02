# Optional

Optional 是 Java 8 引入的一个轻量容器，用于对可能为 null 的值进行显式处理。OpenJDK17 中，Optional 目标在于减少空指针异常的风险，提供一组链式、函数式的 API 来处理存在与否的值。它不是万能的替代品，也不是用于字段或方法参数的常态包装。本文面向初学者，结合简单示例讲解常用用法与注意点。

## 0x01 Optional 简介与设计目的

- 设计初衷：用一个容器来表示"可能有值也可能没有值"的情景，避免直接返回或传递 null，从而降低空指针异常的概率。
- 核心思想：通过一系列方法来安全地"取值、变换、过滤、组合"，而不是直接对返回值进行空值判定。
- 小结：适合用作方法返回值，明确表达"值可有可无"的语义；不建议将 Optional 用作字段、方法参数或循环中的频繁对象包装。

示例要点：
- 当方法可能没有结果时，可以返回 Optional<T> 而不是 null。
- 使用链式操作来对可能为空的值进行处理，避免大量的空值判断。

```java
import java.util.Optional;

public class OptionalDemo {
    public static Optional<String> findName(boolean ok) {
        return ok ? Optional.of("Alice") : Optional.empty();
    }

    public static void main(String[] args) {
        Optional<String> maybe = findName(true);
        maybe.ifPresent(System.out::println); // 打印 Alice
    }
}
```

## 0x02 创建 Optional（empty, of, ofNullable）

- Optional.empty(): 创建一个空的 Optional，表示无值。
- Optional.of(T value): 创建一个非空值的 Optional，value 不能为 null，否则抛出 NullPointerException。
- Optional.ofNullable(T value): value 允许为 null，null 时返回 Optional.empty，否则返回包含值的 Optional。

```java
import java.util.Optional;

public class OptionalCreation {
    public static void main(String[] args) {
        Optional<String> empty = Optional.empty();
        Optional<String> nonNull = Optional.of("Hello");
        Optional<String> nullable = Optional.ofNullable(null);

        System.out.println(empty.isPresent());        // false
        System.out.println(nonNull.get());            // Hello
        System.out.println(nullable.isPresent());     // false
    }
}
```

注意要点：
- 使用 Optional.of 时，确实不能传入 null；否则会抛出异常。
- 当值可能为 null 时，优先考虑 Optional.ofNullable。

## 0x03 获取值（get, orElse, orElseGet, orElseThrow）

- get(): 直接获取值。如果 Optional 为空，调用时会抛 NoSuchElementException，谨慎使用，通常先判定是否存在。
- orElse(T other): 如果存在则返回值，否则返回提供的默认值 other。
- orElseGet(Supplier): 与 orElse 类似，但默认值通过 Supplier 延迟计算，只有在真的需要时才执行。
- orElseThrow(Supplier<? extends Throwable>): 如果存在则返回值，否则抛出指定异常（也有无参的 orElseThrow，抛出 NoSuchElementException）。

```java
import java.util.Optional;

public class OptionalGetOrElse {
    public static void main(String[] args) {
        Optional<String> some = Optional.of("Java");
        Optional<String> none = Optional.empty();

        // get()
        System.out.println(some.get()); // Java
        // System.out.println(none.get()); // 不安全，会抛 NoSuchElementException

        // orElse / orElseGet
        System.out.println(none.orElse("default")); // default
        System.out.println(none.orElseGet(() -> "computed")); // computed

        // orElseThrow
        String value = some.orElseThrow(() -> new IllegalStateException("missing"));
        System.out.println(value); // Java

        // orElseThrow 对空值的异常演示（注释掉以避免运行时异常）
        // none.orElseThrow(() -> new IllegalArgumentException("absent"));
    }
}
```

实用总结：
- 优先避免使用 get()，改用 orElse 或 orElseGet 处理缺失情形。
- 对于需要抛错的语义，使用 orElseThrow。

## 0x04 转换操作（map, flatMap）

- map(Function): 将 Optional<T> 的值转换为 Optional<U>，若原值存在则包装转换后的值，否则返回空 Optional。
- flatMap(Function): 与 map 类似，但返回的是 Optional<U>，不会出现二次包装的情况，常用于链式调用中避免嵌套 Optional。

```java
import java.util.Optional;

public class OptionalMapFlatMap {
    public static void main(String[] args) {
        Optional<String> name = Optional.of("alice");

        // map 示例：将字符串长度映射为 Optional<Integer>
        Optional<Integer> lengthOpt = name.map(String::length);
        lengthOpt.ifPresent(System.out::println); // 5

        // flatMap 示例：将字符串转成 Optional<Integer>，再扁平化
        Optional<Integer> flat = name.flatMap(s -> Optional.of(s.length()));
        flat.ifPresent(System.out::println); // 5
    }
}
```

使用场景：
- map 适合对现有值进行变换，得到新的非 Optional 值，再包装成 Optional。
- flatMap 适合从一个值衍生出一个 Optional 的场景，避免 Optional 的嵌套。

## 0x05 过滤操作（filter）

- filter(Predicate): 按条件过滤当前 Optional 的值。若条件为真，返回原值的 Optional；为假时返回 Optional.empty。

```java
import java.util.Optional;

public class OptionalFilter {
    public static void main(String[] args) {
        Optional<String> name = Optional.of("Alice");
        Optional<String> longName = name.filter(n -> n.length() > 4);

        longName.ifPresent(System.out::println); // Alice

        Optional<String> shortName = name.filter(n -> n.length() > 10);
        System.out.println(shortName.isPresent()); // false
    }
}
```

应用要点：
- 结合 map/flatMap/orElse 的链式调用，灵活控制"存在且符合条件"的分支逻辑。

## 0x06 判断操作（isPresent, isEmpty, ifPresent）

- isPresent(): 判断是否包含值，返回 boolean。
- isEmpty(): 判断是否为空（Java 11 及以后版本有这个方法）。
- ifPresent(Consumer): 如果存在则执行指定操作。

```java
import java.util.Optional;

public class OptionalJudgment {
    public static void main(String[] args) {
        Optional<String> opt = Optional.ofNullable(null);

        System.out.println(opt.isPresent()); // false
        System.out.println(opt.isEmpty());   // true

        opt.ifPresent(System.out::println); // 不执行任何输出

        Optional<String> another = Optional.of("Hi");
        another.ifPresent(System.out::println); // 打印 Hi
    }
}
```

搭配要点：
- isPresent/isEmpty 适合在条件分支中快速判断，避免空指针。
- ifPresent 常用于回调式处理。

## 0x07 Optional 与 Stream 的结合使用

- Optional.stream(): 将 Optional 转换为 Stream，便于与流式操作组合。若 Optional 有值，返回仅含该值的单元素 Stream；若为空，返回空流。
- 将 Optional 与 Stream 链接，简化空值处理场景。

```java
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.List;

public class OptionalStream {
    public static void main(String[] args) {
        Optional<String> optional = Optional.of("OpenJDK");

        // 使用 Optional.stream() 与 Stream 处理的示例
        List<String> upper = optional.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        System.out.println(upper); // [OPENJDK]

        // 更常见的用法：将可能为空的值转为流，参与后续流操作
        Optional<Integer> maybeLen = Optional.of("Hello").map(String::length);
        long count = maybeLen.stream().filter(n -> n > 3).count();
        System.out.println(count); // 1
    }
}
```

提示：
- Optional.stream() 在 Java 9+ 版本普遍可用，方便将 Optional 融入现有的流式管道中。
- 使用时记得导入 java.util.stream.Collectors 等相关类。

## 0x08 Optional 的最佳实践（何时使用、何时不使用）

- 何时使用
  - 方法返回存在性不确定的值时，用 Optional 作为返回类型，替代返回 null。
  - 需要对缺失情况进行清晰处理，避免空指针异常。
  - 与 Stream 结合使用时，可以减少显式判空的代码量。

- 何时不使用
  - 作为字段、方法参数或集合元素的包装，这会增加复杂度和性能成本。
  - 高性能场景的极端微优化，Optional 的开销可能不划算。
  - 需要对 null 进行简单快速处理时，直接返回默认值或抛出异常可能更直观。

- 设计要点
  - 优先使用 orElseGet / orElseThrow 来实现缺失时的行为，避免不必要的计算。
  - 尽量避免在链式调用中滥用 get()，尽量用 isPresent / ifPresent / map 之类的组合完成任务。
  - 对于需要返回多层嵌套的 Optional，优先考虑 flatMap 链接，避免嵌套 Optional。

示例要点：
- 通过一个示例方法，展示返回 Optional 的场景，并在调用端用 orElseGet 实现懒惰求值。

```java
import java.util.Optional;

public class OptionalBestPractice {
    // 假设 Find 用户的邮箱，如果不存在则返回 Optional.empty
    public static Optional<String> findEmailByUserId(String userId) {
        // 简化示例：模拟可能找不到邮箱
        if ("user1".equals(userId)) {
            return Optional.of("user1@example.com");
        }
        return Optional.empty();
    }

    public static void main(String[] args) {
        String email = findEmailByUserId("unknown")
                .orElseGet(() -> "default@example.com");

        System.out.println(email); // default@example.com
    }
}
```

## 0x09 常见错误与反模式

- 直接在 Optional 上调用 get() 而不检查存在性
  - 不良示例：optional.get()，空时抛异常。
- 将 null 包装进 Optional
  - 不良示例：Optional.of(null) // 抛 NPE
- 将 Optional 用作字段或方法参数的包裹
  - 反模式：public Optional<User> user; 或 public void setUser(Optional<User> user)
- 过度嵌套 Optional
  - 过多的 map/flatMap 嵌套，导致可读性下降，考虑合并逻辑或使用辅助方法
- 忽略 orElseGet 的懒惰性
  - 如果默认值计算成本高，使用 orElse(...) 会提前计算，不如 orElseGet

示例对比：
- 错误使用 get 的例子
```java
Optional<String> opt = Optional.empty();
// System.out.println(opt.get()); // 会抛 NoSuchElementException
```

- 不要对字段使用 Optional
```java
// 不推荐
class User {
    public Optional<String> nickname; // 不建议作为字段
}
```

## 0x0A 参考链接

- 官方 Javadoc（OpenJDK 17）: Java base Optional API 文档
  - https://docs.oracle.com/javase/17/docs/api/java.base/java/util/Optional.html
- Baeldung 的 Optional 指南（实用示例与最佳实践）
  - https://www.baeldung.com/java-optional
- Optional 与 Stream 的结合与常见用法（示例与说明）
  - https://docs.oracle.com/javase/9/docs/api/java/util/Optional.html#stream--
- Optional 的核心设计与用法要点（Java 入门教程） 
  - https://www.oracle.com/java/technologies/javase/jdk8-downloads.html
