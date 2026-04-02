# Lambda 表达式在 OpenJDK17 知识库

本章节面向初学者，聚焦在日常开发中对 Lambda 表达式的理解、写法以及与集合框架的结合使用。每个知识点都给出可直接运行的 Java 代码示例，便于上手实践。

## 0x01 Lambda 简介与语法

- 概念要点
  - Lambda 表达式是一个可传递的匿名函数实现，目标类型必须是一个函数式接口（只有一个待实现方法的接口）。
  - 基本语法形态：参数列表 -> 目标实现体。参数可以省略类型（只在需要时显式给出类型），实现体可以是表达式或代码块。
- 常见示例
```java
// 无参 Lambda，目标类型为 Supplier<T>
java.util.function.Supplier<String> greeting = () -> "Hello";

// 双参 Lambda，目标类型为 BinaryOperator<Integer>
java.util.function.BinaryOperator<Integer> add = (a, b) -> a + b;

// 单参 Lambda，参数类型可省略，示例字符串长度
java.util.function.Function<String, Integer> length = s -> s.length();

// 代码块形式，带多条语句
java.util.function.Function<String, Integer> parseAndLength = s -> {
    int len = s.trim().length();
    return len;
};
```

- 何时使用 Lambda
  - 当需要将一段行为作为参数传递时，如排序、遍历、回调等场景。
  - 自动推断目标类型时，通常省略参数类型，代码更简洁。

## 0x02 函数式接口（@FunctionalInterface）

- 关键点
  - 函数式接口是仅有一个抽象方法的接口，才可以被用作 Lambda 的目标类型。
  - 使用 @FunctionalInterface 注解可以在编译时检查接口是否符合"只有一个抽象方法"的约束。
  - 默认方法和静态方法不计入抽象方法数量，仍然允许。
- 示例
```java
@FunctionalInterface
interface Printer {
    void print(String message);

    // 该默认方法不会影响函数式接口的唯一抽象方法
    default void info(String msg) {
        System.out.println("Info: " + msg);
    }

    // 静态方法也不计入抽象方法数量
    static void staticHelp() {
        System.out.println("Help");
    }
}

// 使用 Lambda 实现
Printer p = msg -> System.out.println("输出: " + msg);
p.print("hello");
```

- 小结
  - 将一个方法签名对应到一个 Lambda 实现，前提是该方法所在接口是函数式接口。

## 0x03 Lambda 的各种写法

- 无参、单参、多参以及显式与隐式类型的对比
```java
// 无参（无类型）- Supplier 示例
Supplier<Integer> one = () -> 1;

// 单参，省略类型推断
Function<String, Integer> toLen = s -> s.length();

// 单参，显式写出类型
Function<String, Integer> toLenTyped = (String s) -> s.length();

// 多参，带返回值
BiFunction<Integer, Integer, Integer> sum = (a, b) -> a + b;

// 代码块形式，包含多条语句
Function<String, Integer> complicated = s -> {
    String t = s.trim();
    return t.length() * 2;
};
```

- 带类型和不带类型的对比
```java
// 带类型
Predicate<String> hasLen = (String s) -> s.length() > 3;

// 无类型（编译器推断）
Predicate<String> hasLenInferred = s -> s.length() > 3;
```

- 表达式 vs 代码块
```java
// 表达式：返回值直接作为结果
Function<Integer, Integer> square = x -> x * x;

// 代码块：需要显式返回
Function<Integer, Integer> cube = x -> {
    int y = x * x * x;
    return y;
};
```

- 使用场景示例
```java
List<String> names = Arrays.asList("Bob", "Alice", "John");
// 作为 Runnable 的简短实现
Thread t = new Thread(() -> System.out.println("Running in thread: " + Thread.currentThread().getName()));
t.start();
```

## 0x04 Lambda 与集合操作

- forEach
```java
List<String> fruits = Arrays.asList("apple", "banana", "cherry");
fruits.forEach(f -> System.out.println(f));

// 也可以使用方法引用
fruits.forEach(System.out::println);
```

- sort（排序）
```java
List<String> names = new ArrayList<>(Arrays.asList("Zara", "Anna", "Lucy"));
names.sort((a, b) -> a.length() - b.length()); // 按名称长度排序
// 或使用方法引用辅助排序
names.sort(Comparator.comparingInt(String::length));
```

- 结合 Streams 的常见用法
```java
List<String> filtered = names.stream()
    .filter(n -> n.startsWith("A"))
    .sorted()
    .collect(Collectors.toList());
```

- 小结
  - Lambda 让集合操作更简洁，结合 Streams 可以实现复杂的数据处理管道。

## 0x05 Lambda 变量捕获

- effectively final 的概念
  - Lambda 可以捕获外围作用域中的 final 或 effectively final 的变量，即变量在被捕获前后不可再被修改。
- 示例
```java
int factor = 3; // facto r 为 effectively final
java.util.function.Function<Integer, Integer> times = x -> x * factor;
System.out.println(times.apply(5)); // 15

// 以下代码会编译错误，因为试图修改 factor
// factor = 4; // 不能在已经被 lambda 捕获后再修改
```

- 注意
  - 不要在 lambda 外部改变被捕获的变量，否则编译会失败。

## 0x06 Lambda 与 this 关键字

- this 的指向
  - 在 Lambda 内，this 指向的是外部最近的实例对象，也就是包含 Lambda 的对象实例。
  - 与匿名内部类不同，后者的 this 指向的是匿名内部类自身。
- 示例
```java
public class LambdaThisDemo {
    private String name = "Outer";

    public void showThis() {
        Runnable r = () -> System.out.println("this.name = " + this.name);
        r.run();
    }

    public static void main(String[] args) {
        new LambdaThisDemo().showThis(); // 输出: this.name = Outer
    }
}

// 与匿名内部类对比
class AnonymousThisDemo {
    private String name = "Outer";

    void show() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                // 在这里 this 指向 AnonymousThisDemo 匿名内部类的实例
                System.out.println("this.name = " + AnonymousThisDemo.this.name);
            }
        };
        r.run();
    }
}
```

- 要点
  - 在需要访问外部对象成员时，Lambda 可以简洁地使用 this。
  - 如果需要显式引用外部对象的成员，可以直接使用外部对象的引用。

## 0x07 常见的函数式接口

- Predicate<T>：用于条件判断
```java
Predicate<String> notEmpty = s -> s != null && !s.isEmpty();
System.out.println(notEmpty.test("abc")); // true
```

- Consumer<T>：消费型接口，执行动作但无返回值
```java
Consumer<String> printer = s -> System.out.println(">> " + s);
printer.accept("Hello Lambda");
```

- Function<T, R>：将 T 映射为 R
```java
Function<String, Integer> len = String::length;
System.out.println(len.apply("hello")); // 5
```

- Supplier<T>：无参数，返回 T
```java
Supplier<Double> random = Math::random;
System.out.println(random.get());
```

- 小结
  - 这些接口是编写简洁、可重用的 Lambda 的常用工具。组合使用（如 map、filter、collect）可以实现强大而清晰的数据处理流程。

## 0x08 Lambda 的最佳实践与常见陷阱

- 最佳实践
  - 尽量让 Lambda 尽可能短小，复杂逻辑放在方法中再通过方法引用或调用来实现。
  - 优先考虑方法引用（如 String::toLowerCase、ClassName::staticMethod）以提高可读性。
  - 避免在 Lambda 内部修改外部可变状态，遵循无副作用原则，便于并发和测试。
  - 使用流（Streams）处理集合，避免在 forEach 循环中进行复杂数据处理，以利于链式处理和并行化。
  - 处理受检查异常的情况：Lambda 本身不能抛出检查型异常；可以通过包装或将异常处理分离到单独的方法。
- 常见陷阱
  - 捕获过多状态导致可读性下降，避免把 Lambda 做成"巨型函数"。
  - 在高并发场景下错误使用共享可变数据，导致竞态条件。
  - 过度使用并行流，未把任务的开销和上下文切换成本考虑清楚，可能适得其反。
  - 忽略空指针检查，直接对可能为 null 的输入调用方法。
- 实践要点总结
  - 先用简单的 Lambda 实现需求，再根据需要重构为方法引用或提取到独立方法中。
  - 遵循清晰的命名和适当的命名空间划分，提升可维护性。
  - 编写测试覆盖 Lambda 的边界情况，确保行为稳定。

## 参考链接

- Java Lambda 表达式入门（Oracle Java Tutorials，适合初学者）: https://docs.oracle.com/javase/8/docs/tutorial/java/javaOO/lambdaexpressions.html
- Java 8+ 函数式接口与包说明（java.util.function）: https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html
- Lambda 与集合与 Streams 的基础用法（Java 官方教程）: https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html
- Predicate/Consumer/Function/Supplier 示例与用法（官方 API 文档与示例）: https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html#PackageSummary
- 进一步了解 Comparator 与 Lambda 的结合使用（排序示例）: https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html
