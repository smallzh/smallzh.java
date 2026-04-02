# 理解 Java 8 至 Java 17 的新特性及使用方式

本页整理了 Java 8-17 的核心新特性，面向初学者，聚焦实际用法与代码示例，便于快速上手。

## 0x01 Java 8 特性

### Lambda表达式
使用箭头函数实现函数式接口，简化匿名内部类的写法。

```java
import java.util.Arrays;
import java.util.List;

public class LambdaDemo {
  public static void main(String[] args) {
    List<String> names = Arrays.asList("张三", "李四", "王五");
    // 使用 Lambda 遍历
    names.forEach(n -> System.out.println(n));

    // 也可以用方法引用
    names.forEach(System.out::println);
  }
}
```

### Stream API
用流式编程对集合进行过滤、映射、聚合等操作，语义清晰、可链式调用。

```java
import java.util.Arrays;
import java.util.List;

public class StreamDemo {
  public static void main(String[] args) {
    List<String> words = Arrays.asList("apple", "banana", "avocado", "berry");
    words.stream()
         .filter(w -> w.startsWith("a"))
         .map(String::toUpperCase)
         .forEach(System.out::println);
  }
}
```

### Optional 类
帮助避免空指针异常，显式处理可能为空的值。

```java
import java.util.Optional;

public class OptionalDemo {
  public static void main(String[] args) {
    String maybe = System.getenv("NAME");
    Optional<String> opt = Optional.ofNullable(maybe);
    String value = opt.orElse("默认值");
    System.out.println(value);
  }
}
```

### 方法引用
通过引用方法（静态方法、实例方法、构造方法）来替代 lambda 表达式，提升可读性。

```java
import java.util.Arrays;
import java.util.List;

public class MethodRefDemo {
  public static void main(String[] args) {
    List<String> names = Arrays.asList("张三", "李四", "王五");
    names.forEach(System.out::println); // 静态方法引用
    // 构造方法引用示例（简化演示）
    java.util.function.Supplier<String> s = String::new;
  }
}
```

### 默认方法（接口中的 default 方法）
接口中可以有实现的方法，便于在不破坏实现类的前提下扩展接口。

```java
public interface Greeter {
  void greet();
  default void sayHello() {
    System.out.println("你好");
  }
}
public class GreeterImpl implements Greeter {
  public void greet() { System.out.println("问候"); }
}
```

### 新的日期时间 API（java.time 包）
引入不可变的日期时间类，API 更直观、线程安全。

```java
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateTimeDemo {
  public static void main(String[] args) {
    LocalDate today = LocalDate.now();
    LocalDate nextWeek = today.plusWeeks(1);
    DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
    System.out.println(today.format(fmt));
    System.out.println(nextWeek.format(fmt));
  }
}
```

---

## 0x02 Java 9-11 特性

### 模块系统（Jigsaw）
将 JDK 分解为可组合的模块，提升封装性与可维护性。

```java
// module-info.java 示例
module com.example.app {
  requires java.logging;
  exports com.example.app;
}
```

### 集合工厂方法（List.of, Set.of, Map.of）
用更简洁的方式创建不可变集合，减少样板代码。

```java
import java.util.List;
import java.util.Set;
import java.util.Map;

public class FactoryDemo {
  public static void main(String[] args) {
    List<String> list = List.of("A", "B", "C");
    Set<Integer> set = Set.of(1, 2, 3);
    Map<String, Integer> map = Map.of("一", 1, "二", 2);
    System.out.println(list);
    System.out.println(set);
    System.out.println(map);
  }
}
```

### 私有接口方法
接口中允许出现私有方法，便于在默认方法中复用代码。

```java
public interface Processor {
  private static String normalize(String s) {
    return s.trim().toLowerCase();
  }

  default void run(String s) {
    String n = normalize(s);
    System.out.println("处理: " + n);
  }
}
```

### 局部变量类型推断（var 关键字）
在本地变量上使用 var 声明，编译器自动推断类型，提升可读性与简洁性。

```java
import java.util.List;

public class VarDemo {
  public static void main(String[] args) {
    var greeting = "你好"; // 推断为 String
    var nums = List.of(1, 2, 3); // 推断为 List<Integer>
    System.out.println(greeting + " 世界");
    nums.forEach(System.out::println);
  }
}
```

---

## 0x03 Java 12-17 特性

### Switch表达式（Java 14 正式发布）
将 switch 作为表达式使用，支持 yield 等语法，增强灵活性。

```java
enum Color { RED, GREEN, BLUE }

public class SwitchExprDemo {
  public static void main(String[] args) {
    Color color = Color.RED;
    String name = switch (color) {
      case RED -> "红色";
      case GREEN -> "绿色";
      case BLUE -> "蓝色";
    };
    System.out.println(name);
  }
}
```

### Text Blocks 文本块（Java 15 正式发布）
用于简化多行字符串的书写，尤其适合编写 JSON、SQL 等文本。

```java
public class TextBlockDemo {
  public static void main(String[] args) {
    String json = """
      {
        "name": "张三",
        "age": 30
      }
      """;
    System.out.println(json);
  }
}
```

### Records 记录类（Java 16 正式发布）
用来简化不可变数据载体的定义，语法更简洁，自动生成常用方法。

```java
public record Point(int x, int y) {}

public class RecordsDemo {
  public static void main(String[] args) {
    Point p = new Point(3, 4);
    System.out.println(p.x() + "," + p.y());
  }
}
```

### Sealed Classes 密封类（Java 17 正式发布）
限制一个类可以被哪些子类继承，增强类型系统的控制力。

```java
public sealed class Shape permits Circle, Rectangle {}
public final class Circle extends Shape {}
public final class Rectangle extends Shape {}

public class SealedDemo {
  public static void main(String[] args) {
    Shape s = new Circle();
    System.out.println(s.getClass().getSimpleName());
  }
}
```

### Pattern Matching for instanceof（Java 16 正式发布）
在进行类型判断时直接绑定变量，简化代码。

```java
public class PatternDemo {
  public static void main(String[] args) {
    Object obj = "hello";
    if (obj instanceof String s) {
      System.out.println(s.length());
    }
  }
}
```

---

## 参考链接

- Java 8 特性
  - Lambda 表达式与语言指南：https://docs.oracle.com/javase/8/docs/technotes/guides/language/lambda-expressions.html
  - Stream API：https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html
  - Optional：http://docs.oracle.com/javase/8/docs/api/java/util/Optional.html
  - 默认方法：https://docs.oracle.com/javase/8/docs/technotes/guides/language/defaultmethods.html
  - java.time（日期时间 API）：https://docs.oracle.com/javase/8/docs/api/java/time/

- Java 9-11 特性
  - 模块系统（Jigsaw）：https://openjdk.java.net/projects/jigsaw/
  - 集合工厂方法（List.of、Set.of、Map.of）：https://docs.oracle.com/javase/9/docs/api/java/util/List.html#of-E-、
    https://docs.oracle.com/javase/9/docs/api/java/util/Set.html#of-E-、https://docs.oracle.com/javase/9/docs/api/java/util/Map.html#of-K-V-
  - 局部变量类型推断（var）：https://openjdk.java.net/jeps/286
  - 私有接口方法：相关内容可参考 Java 9 语言特性集成的接口能力实现，详见 JLS 与官方语言特性说明

- Java 12-17 特性
  - Switch 表达式（JEP 361，正式在 Java 14 引入）：https://openjdk.java.net/jeps/361
  - Text Blocks（JEP 368，正式在 Java 15 的预览基础上实现）：https://openjdk.java.net/jeps/368
  - Records（JEP 359）：https://openjdk.java.net/jeps/359
  - Sealed Classes（JEP 409）：https://openjdk.java.net/jeps/409
  - Pattern Matching for instanceof（JEP 394）：https://openjdk.java.net/jeps/394

你可以直接将以上内容粘贴到 Markdown 文件中，保存为 docs/ja_java_8_17_features.md（或你项目中合适的路径和命名），即可在知识库中使用。
