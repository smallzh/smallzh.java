# OpenJDK17 的类与对象基础知识，帮助初学者快速入门 Java 面向对象

以下内容按知识点分成若干小节，使用简单的示例代码帮助理解和实操。

## 0x01 类的定义
类是对象的模板，定义了对象的成员变量和成员方法。通过类可以创建对象实例。

```java
public class Person {
    String name;
    int age;

    void sayHello() {
        System.out.println("你好，我是 " + name);
    }
}
```

说明：
- 以上定义了一个简单的 Person 类，包含字段 name、age 和方法 sayHello。
- 字段和方法是该类的“成员”，属于对象的属性与行为。

## 0x02 创建对象与使用
使用 new 关键字创建对象实例，并通过对象访问其成员。

```java
// 文件: Person.java
public class Person {
    public String name;
    public int age;

    public void sayHello() {
        System.out.println("你好，我是 " + name);
    }
}

// 文件: Demo.java
public class Demo {
    public static void main(String[] args) {
        Person p = new Person();
        p.name = "小明";
        p.age = 18;
        p.sayHello(); // 输出: 你好，我是 小明
    }
}
```

说明：
- 通过 new Person() 创建对象，访问对象的字段并调用其方法。
- 这是最常见的对象使用方式，适合初学者理解对象的实例化与行为调用。

## 0x03 成员变量与成员方法
成员变量（字段）是对象的状态，成员方法是对象的行为。每个对象有自己的一组字段副本。

```java
public class Counter {
    int value; // 成员变量

    void increment() { // 成员方法
        value++;
    }
}
```

说明：
- 每个 Counter 实例拥有独立的 value 值，调用 increment() 会改变该实例的状态。

## 0x04 访问修饰符（public、private、protected、default）
访问修饰符控制对类成员的可访问性，理解它们有助于实现封装。

```java
public class AccessDemo {
    public int publicVar;
    private int privateVar;
    protected int protectedVar;
    int defaultVar; // package-private，未显式声明时的默认访问级别

    public void setPrivateVar(int v) {
        privateVar = v;
    }

    public int getPrivateVar() {
        return privateVar;
    }
}
```

扩展要点：
- public：对任何人可访问。
- private：仅在当前类内可访问，常用于封装私有数据。
- protected：同一包内或子类可访问，便于子类扩展。
- default（无修饰符）：同一包内可访问。

示例演示：
- 同一个包内的类可以访问 defaultVar，但无法从其他包直接访问 privateVar。
- 通过公有方法 getPrivateVar/setPrivateVar 可以间接访问私有字段。

## 0x05 静态成员（static）
static 表示属于类本身而非任一实例。常用于工具方法、常量或计数器等场景。

```java
public class MathUtil {
    public static int add(int a, int b) {
        return a + b;
    }
}
```

使用方式：
```java
int sum = MathUtil.add(2, 3); // 5
```

静态变量的示例：
```java
public class Counter {
    public static int count = 0;

    public Counter() {
        count++;
    }
}
```

使用：
```java
new Counter();
new Counter();
System.out.println(Counter.count); // 输出 2
```

说明：
- 静态成员与具体对象无关，适合实现跨对象共享或工具性质的方法。

## 0x06 this 关键字
this 引用当前对象，常用于在构造方法、实例方法中区分参数与字段名，或在方法链调用中返回当前对象。

```java
public class Person {
    private String name;

    Person(String name) {
        this.name = name; // 使用 this 指向当前对象的字段
    }

    Person withAge(int age) {
        // 示例性方法，演示返回当前对象以实现链式调用
        System.out.println("设置年龄: " + age);
        return this;
    }

    void printName() {
        System.out.println(this.name);
    }
}
```

用法示例：
```java
Person p = new Person("小红");
p.printName();          // 输出: 小红
p.withAge(20).withAge(21); // 简单演示链式调用（示意）
```

## 0x07 包（package）与导入（import）
包用来组织类，导入语句将其他包中的类引入当前源文件中以便使用。

示例一：在一个包中定义类
```java
// 文件: src/com/example/vehicle/Car.java
package com.example.vehicle;

public class Car {
    public void honk() {
        System.out.println("嘀嘀嘀");
    }
}
```

示例二：在另一个包中使用该类
```java
// 文件: src/com/example/app/Main.java
package com.example.app;

import com.example.vehicle.Car;

public class Main {
    public static void main(String[] args) {
        Car car = new Car();
        car.honk();
    }
}
```

说明：
- 包结构可以对应文件夹层级，例如上面的 Car.java 应放在 com/example/vehicle/ 目录下。
- import 语句用于引入其他包中的类，便于在当前类中直接使用。

参考链接（适用于深入学习的官方资源）
- OpenJDK 17 官方 API 文档（Java 基本类与 API 参考）: https://docs.oracle.com/javase/17/docs/api/
- Java 教程 - 类与对象（Classes and Objects）: https://docs.oracle.com/javase/tutorial/java/javaOO/classes.html
- Java 教程 - 访问控制（Access Control）: https://docs.oracle.com/javase/tutorial/java/javaOO/access.html
- Java 教程 - this 关键字: https://docs.oracle.com/javase/tutorial/java/javaOO/thiskey.html
- Java 教程 - 包与导入（Packages and Import）: https://docs.oracle.com/javase/tutorial/java/javaOO/packages.html

如需扩展到更多细节（如接口、继承、多态、构造器细节、初始化块等），可在后续章节继续扩展。
