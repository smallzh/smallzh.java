# OpenJDK17 默认方法（default method）详细指南

本指南面向初学者，聚焦默认方法在接口中的实际使用与设计要点，结合简明代码示例帮助理解和应用。

## 0x01 默认方法简介与设计目的

- 设计初衷：在不破坏现有实现的前提下，为接口新增方法提供默认实现，提升接口的扩展性和向后兼容性。
- 实践意义：当需要向大量实现类添加新行为时，使用默认方法可以避免逐个修改实现类。

代码示例：为接口添加一个默认行为
```java
public interface Vehicle {
    void start();

    // 新增的默认方法，提供通用实现
    default void honk() {
        System.out.println("Honk!");
    }
}

public class Car implements Vehicle {
    @Override
    public void start() {
        System.out.println("Car started.");
    }
    // 可以直接使用 honk()，也可覆盖自定义实现
}

public class Test {
    public static void main(String[] args) {
        Vehicle c = new Car();
        c.start();  // Car started.
        c.honk();   // Honk!
    }
}
```

要点
- 默认方法是接口中的实例方法，带有实现体。
- 实现类若不覆盖，仍然可以直接使用默认实现。

## 0x02 默认方法的语法

- 关键点：在接口中使用 default 关键字来定义带实现的方法；也可以在接口中定义静态方法（static），供接口名调用。
- 语言层次：默认方法必须出现在接口中，提供给实现类以默认实现的方式使用。

代码示例：默认方法、静态方法并存的接口
```java
public interface Printer {
    void print(String s);

    // 默认方法，提供自带实现
    default void printTwice(String s) {
        print(s);
        print(s);
    }

    // 静态方法，属于接口本身
    static String uppercase(String s) {
        return s.toUpperCase();
    }
}

public class ConsolePrinter implements Printer {
    @Override
    public void print(String s) {
        System.out.println(s);
    }
}

public class Demo {
    public static void main(String[] args) {
        ConsolePrinter p = new ConsolePrinter();
        p.printTwice("hello");           // hello
                                           // hello
        System.out.println(Printer.uppercase("wow")); // WOW
    }
}
```

要点
- default 方法是实例方法，需要通过实现类的实例来调用。
- static 方法属于接口本身，通过接口名调用，与实现类无关。

扩展（可选）小提示：Java 9 及以上版本允许在接口中加入私有方法（private、private static），供默认方法内部调用以减少重复代码。实际编译要遵循 JDK 版本特性。

## 0x03 默认方法与多继承

- 场景：一个类同时实现了多个接口，而这些接口都定义了默认方法，可能产生冲突。
- 结果：编译器要求实现类覆盖冲突的默认方法，明确实现行为。

代码示例：两接口都提供相同默认方法
```java
interface A {
    default String identify() {
        return "A";
    }
}
interface B {
    default String identify() {
        return "B";
    }
}

class C implements A, B {
    // 必须覆盖冲突的方法以明确实现
    @Override
    public String identify() {
        // 选择性地调用其中一个接口的默认实现
        return A.super.identify(); // 也可以使用 B.super.identify()
    }
}

class Test {
    public static void main(String[] args) {
        System.out.println(new C().identify()); // A
    }
}
```

要点
- 如果实现类同时继承了两个接口且它们有同名且同签名的默认方法，必须覆盖该方法以解决冲突。
- 可以通过 InterfaceName.super.method() 明确调用某个接口的默认实现。

## 0x04 默认方法冲突解决规则

- 规则要点：
  - 当一个类实现了两个接口且这两个接口都提供相同签名的默认方法时，编译器会报错，必须在类中覆盖该方法以指定实现。
  - 覆盖后，可以选择直接提供自定义实现，或者在实现中使用 InterfaceName.super.method() 调用某个接口的默认实现。

代码示例：覆盖并解决冲突
```java
interface X {
    default void bloop() {
        System.out.println("X bloop");
    }
}
interface Y {
    default void bloop() {
        System.out.println("Y bloop");
    }
}
class Z implements X, Y {
    @Override
    public void bloop() {
        // 指定使用 X 的默认实现
        X.super.bloop();
        // 也可以写成：Y.super.bloop();
        // 或者提供你自己的实现：
        // System.out.println("Z bloop");
    }
}
```

要点
- 最稳妥的做法是在实现类中显式覆盖冲突的方法，明确行为。

## 0x05 默认方法与静态方法

- 区别：默认方法是实例行为的一部分，需要通过对象调用；静态方法属于接口本身，通过接口调用，不依赖实现类的实例。

代码示例：静态方法与默认方法的组合
```java
interface Calculator {
    int add(int a, int b);

    // 默认方法，依赖实现类提供的 add
    default int addOne(int a) {
        return add(a, 1);
    }

    // 静态方法，直接通过接口调用
    static int square(int x) {
        return x * x;
    }
}

class SimpleCalc implements Calculator {
    @Override
    public int add(int a, int b) {
        return a + b;
    }
}

class Demo {
    public static void main(String[] args) {
        Calculator calc = new SimpleCalc();
        System.out.println(calc.add(2, 3)); // 5
        System.out.println(calc.addOne(5)); // 6
        System.out.println(Calculator.square(4)); // 16
    }
}
```

要点
- 静态方法不能被覆盖；它属于接口本身。
- 默认方法为实现类提供可选的默认行为，便于向后兼容扩展。

## 0x06 默认方法的实际应用案例

- 场景：向已有接口添加新功能而不破坏现有实现。通过默认方法提供新的行为，让实现类自动获得新能力。
- 案例：为可标识的对象增加"描述信息"，而不强制修改所有实现类。
  
代码示例：为可标识对象提供自带描述的默认方法
```java
public interface Identifiable {
    String getId();

    // 新增默认方法，利用已有方法生成描述
    default String describe() {
        return "Identifiable[id=" + getId() + "]";
    }
}

class User implements Identifiable {
    private final String id;
    private final String name;

    User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

class Test {
    public static void main(String[] args) {
        User u = new User("u123", "Alice");
        System.out.println(u.describe()); // Identifiable[id=u123]
    }
}
```

扩展应用要点
- 简化演化路径：当你需要为一个广泛使用的接口添加新能力时，默认方法可以让实现类自动拥有新能力，无需修改大量实现代码。
- 与组合优于继承的思路协同：将默认行为放在接口中，使对象能够通过实现不同接口组合出不同的行为。

## 0x07 默认方法的最佳实践

- 仅在确实需要向接口添加新能力且确保向后兼容时使用默认方法。
- 默认方法应尽量小而专注，避免把复杂逻辑塞进接口，减少对实现的耦合和副作用。
- 当两个接口都提供同名默认方法时，务必在实现类中给出明确覆盖，避免编译错误。
- 使用接口的默认方法来提供辅助行为时，考虑是否应把核心行为留给实现类（接口中提供的只是便捷方法）。
- 为接口提供合理的文档说明，明确默认实现的行为边界和调用约束。

要点总结
- 默认方法是向后兼容地向接口添加能力的强大工具，适合向现有 API 演化。
- 多接口冲突时要明确覆盖，必要时用 InterfaceName.super.method() 指定调用来源。
- 静态方法作为辅助工具存在于接口层，与实现类相互独立。

## 参考链接

- Java Tutorials: Default Methods in Interfaces（默认方法）  
  https://docs.oracle.com/javase/tutorial/java/IandI/defaultmethods.html
- Baeldung: Java 8 Default Methods in Interfaces（中文解读友好教程）  
  https://www.baeldung.com/java-8-default-methods
- JournalDev: Java 8 Default Methods in Interfaces（实用示例与讲解）  
  https://www.journaldev.com/10657/java-8-default-methods
