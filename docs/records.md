# OpenJDK17 知识库: 记录类（Records）

以下文档面向初学者，聚焦在 OpenJDK17 环境中使用 Records 的实际要点。每个知识点都含有可直接运行的代码示例，帮助理解与落地。

---

## 0x01 传统 JavaBean 的问题

Tradition 类（JavaBean）常见问题包括可变性、样板代码与值语义不一致等。下面的示例展示了典型的 JavaBean 实现以及它带来的麻烦。

```java
// 传统 JavaBean 示例：可变性、样板代码和不一致的值语义
public class PersonBean {
    private String name;
    private int age;

    public PersonBean() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonBean)) return false;
        PersonBean that = (PersonBean) o;
        return age == that.age && java.util.Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, age);
    }

    @Override
    public String toString() {
        return "PersonBean{name='" + name + "', age=" + age + "}";
    }
}
```

- 问题点：
  - 状态可变，容易引入不一致性。
  - 需要大量模板代码（getters/setters、equals、hashCode、toString）。
  - 值语义依赖手动实现，容易出错且不可比对对象的"值相等性"。

---

## 0x02 Records 简介

Records 是一种特殊的引用类型，专门用于透明的数据载体。核心特征是：组件不可变、简化语法、自动生成常用方法（等于、哈希、字符串表示）等。Records 不是普通类的替代品，而是在明确"数据载体"场景下的更好选择。

```java
// 简单的记录类示例
public record PersonRecord(String name, int age) {}
```

使用示例：
```java
PersonRecord p = new PersonRecord("Alice", 30);
System.out.println(p.name()); // Alice
System.out.println(p.age());  // 30
System.out.println(p);          // PersonRecord[name=Alice, age=30]
```

- 重要点：组件名称即是访问器方法名（如 name()、age()），且记录类默认是 final 的，不能被继承。

---

## 0x03 Records 的语法

- 声明语法
  - 公开的记录类使用关键字 record，组件列表放在圆括号内，类型在前，名称在后。

```java
public record Point(int x, int y) { }
```

- 可以在记录中添加方法、静态成员、以及实现接口
```java
public record Point(int x, int y) implements Comparable<Point> {
    // 实例方法
    public int sum() { return x + y; }

    // 兼容性较强的比较实现
    @Override
    public int compareTo(Point o) {
        int dx = Integer.compare(this.x, o.x);
        if (dx != 0) return dx;
        return Integer.compare(this.y, o.y);
    }

    //  compact 构造器用于校验
    public Point {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("坐标必须为非负数");
        }
    }

    // 额外构造函数（代理构造，最终委托给规范构造函数）
    public Point(int v) {
        this(v, v);
    }
}
```

- 泛型记录
```java
public record Pair<T>(T first, T second) { }
```

- 组件可访问性
```java
Pair<String> p = new Pair<>("A", "B");
System.out.println(p.first());  // A
System.out.println(p.second()); // B
```

- 注意点
  - 记录是不可变的，字段是最终的。
  - 可以声明静态字段和静态方法，但不能有实例字段。
  - 记录可以实现接口，但不能继承（它们继承自 java.lang.Record ）。

---

## 0x04 自动生成的方法（equals、hashCode、toString）

Records 自动为所有组件生成基于值的 equals、hashCode 和 toString 实现，且实现会把所有组件作为值的一部分进行比较。

```java
public record PersonRecord(String name, int age) { }

public class Demo {
    public static void main(String[] args) {
        PersonRecord a = new PersonRecord("Bob", 25);
        PersonRecord b = new PersonRecord("Bob", 25);
        PersonRecord c = new PersonRecord("Carol", 30);

        System.out.println(a.equals(b)); // true
        System.out.println(a.hashCode() == b.hashCode()); // true
        System.out.println(a); // PersonRecord[name=Bob, age=25]
        System.out.println(c); // PersonRecord[name=Carol, age=30]
    }
}
```

- 结论：使用 Records 可以获得简单且稳定的值语义，与传统 JavaBean 相比，显著减少样板代码。

---

## 0x05 Records 与构造函数

- 规范构造函数（canonical ctor）用于参数校验，默认不能直接重新赋值字段，但可以在构造阶段进行有效性检查。

```java
public record User(String id, String name) {
    // 规范构造函数：仅做校验
    public User {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id 不能为空");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name 不能为空");
        }
    }
}
```

- 额外构造函数
```java
public record User(String id, String name) {
    public User(String id) {
        this(id, "Unknown");
    }
}
```

- 小结
  - 规范构造函数用于参数校验，确保对象创建时即处于正确状态。
  - 可以通过额外构造函数提供多种创建方式，但最终仍然通过 canonical ctor 完成字段赋值。

---

## 0x06 Records 与继承

- Records 不能继承其他类（除了隐式继承自 java.lang.Record），但可以实现接口。
- 实现接口的示例

```java
public interface Describable {
    String description();
}

public record User(String id, String name) implements Describable {
    @Override
    public String description() {
        return id + " " + name;
    }
}
```

- 不能有自定义父类继承关系的场景：Record 是最终类，无法 extend 其他类。

---

## 0x07 Records 与序列化

- Records 自身不会隐式实现 Serializable；如果需要序列化，需要显式声明实现接口，且组件可序列化。

```java
import java.io.*;

public record User(String id, String name) implements Serializable {
    private static final long serialVersionUID = 1L;
}
```

- 简单的序列化示例

```java
public class SerializeDemo {
    public static void main(String[] args) throws Exception {
        User user = new User("u123", "Alice");

        // 序列化
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("user.dat"))) {
            oos.writeObject(user);
        }

        // 反序列化
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("user.dat"))) {
            User u = (User) ois.readObject();
            System.out.println(u);
        }
    }
}
```

- 小结
  - 需要序列化时，显式实现 Serializable。
  - 若组件中包含不可序列化的对象，序列化将失败；需通过封装或替换为可序列化类型来处理。

---

## 0x08 Records 的最佳实践

- 适用场景
  - 作为简单数据载体（DTO、值对象、配置对象等），追求不可变性和低样板代码。
  - 当需要严格的值语义、简化比较和调试时，Records 是优选。

- 不宜将 Records 用于
  - 需要复杂可变状态的实体（例如需要持续修改内部状态的对象）。
  - 需要从父类继承实现的场景（Records 不能继承其他类）。

- 关于不可变字段的注意
  - 如果组件是可变引用类型（如 List、Date 等），应尽量使用不可变类型（如 List.of、LocalDate/LocalDateTime 等）。
  - 可以覆盖组件访问器，提供不可变视图，避免外部直接修改内部数据。

- 构造与工厂
  - 使用 compact 构造函数进行参数校验，保证对象创建后处于有效状态。
  - 如需多种创建路径，可以添加额外构造函数或静态工厂方法，但不要改变组件的核心不可变性。

- 性能与语义
  - Records 的 equals/hashCode/toString 基于组件值计算，适合需要稳定的值相等性判断的场景。
  - 对于大对象或包含大量数据的记录，关注序列化和拷贝成本，必要时考虑不把大集合作为单一组件。

- 代码示例总结
```java
// 适合的简单数据载体
public record Point(int x, int y) { 
    public Point {
        if (x < 0 || y < 0) throw new IllegalArgumentException("点坐标必须为非负数");
    }

    public int manhattanDistance() {
        return Math.abs(x) + Math.abs(y);
    }
}

// 包含可序列化组件的记录
public record User(String id, String name) implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    // 覆盖访问器以返回不可变的视图（示例，视具体需求而定）
    // public List<String> roles() { return List.copyOf(roles); } // 如有 List 组件时的处理方式
}
```

## 参考链接

- 记录的官方 API 文档（Java 17）：https://docs.oracle.com/en/java/javase/17/docs/api/java/lang/Record.html
- Java Tutorials 记录（Records）页面：https://docs.oracle.com/javase/tutorial/java/data/records.html
- Baeldung 的 Java Records 系列教程：https://www.baeldung.com/java-records
- Java Guides 的 Records 教程：https://www.javaguides.net/2021/04/java-records-tutorial.html
