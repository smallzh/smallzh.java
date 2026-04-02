# OpenJDK17 知识库：方法引用

方法引用是一种简洁的函数式编程风格，允许直接使用已存在的方法或构造函数作为函数式接口的实现，从而让代码更紧凑、可读性更高。以下内容面向初学者，结合实际使用给出可直接照抄的示例。

## 0x01 方法引用简介与语法

- 基本思想：使用双冒号运算符 (::) 来引用现有的方法或构造函数，然后将引用赋给符合该方法签名的函数式接口。
- 适用场景：需要把一个方法作为参数传递给高阶函数（如 map、filter、sort、collect 等）。
- 四种常见形式：
  - 静态方法引用：ClassName::staticMethod
  - 特定对象的实例方法引用（引用对象固定）：instance::method
  - 特定类型的任意对象实例方法引用（引用对象类型固定，实例在调用时传入）：ClassName::instanceMethod
  - 构造方法引用：ClassName::new

示例要点：
- 需要与所选的函数式接口的签名匹配。
- 参数个数和返回值需对应，编译器会将引用的方法作为实现注入到接口的方法体中。

示例代码（简要汇总）：
```java
// 静态方法引用
Function<String, Integer> parse = Integer::parseInt;
Integer n = parse.apply("42"); // 42

// 特定对象的实例方法引用
String prefix = "Hello";
Supplier<String> upper = prefix::toUpperCase;
String s = upper.get(); // "HELLO"

// 任意对象的实例方法引用（特定类型）
BiPredicate<String, String> equalsIgnoreCase = String::equalsIgnoreCase;
boolean same = equalsIgnoreCase.test("abc", "ABC"); // true

// 构造方法引用（无参）
Supplier<List<String>> listFactory = ArrayList::new;
List<String> list = listFactory.get(); // 新的空 ArrayList

// 构造方法引用（有参示例，需匹配对应构造函数）
Function<String, Person> personFactory = Person::new; // 需要有 Person(String name) 构造函数
```

如何选用哪种形式：
- 如果已有一个方法需要直接作为实现，可以用方法引用替代等价的 Lambda 表达式。
- 合理选择类型引用或构造引用，有助于提升可读性。

## 0x02 静态方法引用（ClassName::staticMethod）

- 使用场景：直接把一个静态方法作为函数式接口实现。
- 常见示例：将字符串转换为数字、对集合进行排序对比等。

示例代码：
```java
// 静态方法引用：将字符串解析为整数
Function<String, Integer> parse = Integer::parseInt;
int num = parse.apply("123"); // 123

// 使用流处理：将字符串转换为整数列表
List<String> strings = Arrays.asList("1", "2", "3");
List<Integer> nums = strings.stream().map(Integer::parseInt).collect(Collectors.toList());
```

注意点：
- 静态方法引用的参数与返回值必须与目标函数式接口的签名完全匹配。

## 0x03 特定对象的实例方法引用（instance::method）

- 使用场景：引用一个已经存在的对象的实例方法。
- 优势：可读性高，避免在 Lambda 中显式传递对象。

示例代码：
```java
// 已有对象的实例方法引用
String fixed = "hello";
Supplier<String> upper = fixed::toUpperCase;
System.out.println(upper.get()); // HELLO

// 使用标准输出流的实例方法引用
Consumer<String> printer = System.out::println;
printer.accept("打印这行文本");
```

注意点：
- 引用的是固定对象的方法，例如上面的 System.out，属于一个具体实例。

## 0x04 特定类型的任意对象方法引用（ClassName::instanceMethod）

- 使用场景：引用某一类型的任意对象的实例方法，方法调用时会传入该对象作为参数。
- 常见示例：对集合中元素的实例方法进行统一处理。

示例代码：
```java
// 任意对象的实例方法引用（字符串对比示例）
BiPredicate<String, String> equalsIgnoreCase = String::equalsIgnoreCase;
boolean match = equalsIgnoreCase.test("Java", "JAVA"); // true

// 将所有字符串转为大写（对每个元素调用 toUpperCase）
List<String> words = Arrays.asList("apple", "banana", "cherry");
List<String> upperWords = words.stream().map(String::toUpperCase).collect(Collectors.toList());
// ["APPLE", "BANANA", "CHERRY"]
```

注意点：
- ClassName::method 形式在方法签名上等同于 (obj, ...) -> obj.method(...)

## 0x05 构造方法引用（ClassName::new）

- 使用场景：通过引用构造函数来替代 Lambda 创建对象的代码。
- 常见示例：无参构造、有参构造、数组构造等。

示例代码：
```java
// 无参构造函数引用
Supplier<List<String>> listFactory = ArrayList::new;
List<String> list = listFactory.get();

// 有参构造函数引用（需要相应的构造函数）
class Person {
    private String name;
    public Person(String name) { this.name = name; }
}
Function<String, Person> personFactory = Person::new;
Person p = personFactory.apply("Alice");
```

扩展：
- 构造函数引用可以与 Function、BiFunction、Supplier 等函数式接口结合，简化对象创建的代码。

## 0x06 方法引用与 Lambda 的对比

- 本质关系：方法引用是对 Lambda 的一种语法糖，目标都是将方法作为函数式接口的实现。
- 当 Lambda 仅表达对一个现成方法的直接调用时，优先使用方法引用以提升可读性。
- 示例对比：
```java
// 使用 Lambda
List<String> lower = words.stream().map(s -> s.toLowerCase()).collect(Collectors.toList());

// 使用方法引用（更简洁）
List<String> lower2 = words.stream().map(String::toLowerCase).collect(Collectors.toList());

// 静态方法引用对比
Function<String, Integer> parse1 = s -> Integer.parseInt(s);
Function<String, Integer> parse2 = Integer::parseInt;
```

要点：
- 当你需要额外的处理（如条件、组合、异常处理等），Lambda 可能更灵活；若只是简单地把一个方法作为函数式接口的实现，方法引用更简洁。

## 0x07 方法引用与集合操作的结合

- 集合操作经常与方法引用搭配使用，提升代码可读性与表达力。
- 常见场景包括：映射、过滤、排序、聚合等。

示例代码：
```java
List<String> words = Arrays.asList("apple", "Banana", "cherry");

// 将所有元素转为大写并排序
List<String> upperSorted = words.stream()
    .map(String::toUpperCase)
    .sorted(String::compareTo)
    .collect(Collectors.toList());

// 打印结果
upperSorted.forEach(System.out::println);
```

要点：
- 使用 String::toUpperCase、String::compareTo 等方法引用，避免显式写 Lambda。

## 0x08 方法引用与 Stream 的使用

- Stream 提供了强大而简洁的管线式处理能力，方法引用往往是管线中最清晰的实现方式。
- 常见用法覆盖选择、变换、聚合等。

示例代码：
```java
// 从对象列表中提取字段，用方法引用简化
class User {
    private String name;
    private int age;
    public User(String name, int age) { this.name = name; this.age = age; }
    public String getName() { return name; }
    public int getAge() { return age; }
}
List<User> users = Arrays.asList(new User("Alice", 30), new User("Bob", 25));

// 提取名字列表
List<String> names = users.stream().map(User::getName).collect(Collectors.toList());

// 过滤并收集
List<String> adults = users.stream()
    .filter(u -> u.getAge() >= 18)
    .map(User::getName)
    .collect(Collectors.toList());
```

要点：
- 常用的 map、filter、collect 等操作，十分契合方法引用的风格。

## 0x09 方法引用的最佳实践

- 优先级排序：在可能的情况下优先使用方法引用，提升可读性；只有在需要额外逻辑、参数转换时才使用 Lambda。
- 命名与可读性：选择清晰的引用名称和目标方法，避免过度嵌套的引用。
- 与工厂模式结合：使用 ClassName::new 构造方法引用来实现简单的工厂函数，降低样板代码。
- 组合与链式调用：在流式处理链中，方法引用能显著减少代码量，但要避免影响可读性。
- 与空值处理：方法引用本身不处理空值，需要在前置步骤显式处理空值或使用 Optional 等方式保护调用。

实践清单：
- 将简单的 Lambda 替换为方法引用时，优先替换。
- 对于集合操作，优先用 map、filter、forEach 等链式调用配合方法引用。
- 构造函数引用适用于工厂场景或快速对象创建，避免冗长的构造调用。
- 避免在复杂逻辑中滥用方法引用，必要时回退到显式 Lambda。

## 参考链接

- Oracle 官方教程：Method References (Java Tutorials) - https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html
- Java 官方语言特性综述（Java 8+）：https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html
- Baeldung：A Complete Guide to Java Method References - https://www.baeldung.com/java-method-references
- 菜鸟教程及中文资料合集（入门性参考）https://www.runoob.com/java/java-method-reference.html
