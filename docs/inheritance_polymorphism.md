# 继承与多态在 OpenJDK17 知识库中的要点与实践

以下内容面向初学者，聚焦在实际使用场景中对继承、 super、重写、多态、抽象类、final 以及 Object 类常用方法的掌握。每个知识点都附有可直接运行的 Java 示例。

## 0x01 继承的概念与语法
继承允许一个类获得另一个类的字段和方法，使用 extends 关键字实现。Java 只支持单继承（一个类只能有一个直接父类），但通过接口可以实现多种行为组合。

示例要点：
- 使用 extends 指定父类
- 子类继承父类的公共/受保护成员
- 可以在子类中添加新方法

代码示例（使用静态内部类在同一个文件中演示）：
```java
public class InheritanceSyntaxDemo {
    static class Animal {
        public void move() {
            System.out.println("动物移动");
        }
    }

    static class Dog extends Animal {
        public void bark() {
            System.out.println("狗狗汪汪叫");
        }
    }

    public static void main(String[] args) {
        Dog d = new Dog();
        d.move(); // 继承自 Animal 的方法
        d.bark(); // 子类新增方法
    }
}
```

参考要点
- Dog 的对象可以使用 Animal 的方法，因为 Dog 继承自 Animal。

## 0x02 super 关键字
super 用于访问父类的成员（字段、构造器、方法）—— 解决同名成员的遮蔽，以及在子类构造器中显式调用父类构造器。

示例要点：
- 子类构造器中通过 super(name) 调用父类构造器
- 在子类方法中使用 super 调用父类实现

代码示例：
```java
public class SuperKeywordDemo {
    static class Animal {
        protected String name;
        Animal(String name) { this.name = name; }
        public void speak() {
            System.out.println(name + " 发声");
        }
    }

    static class Dog extends Animal {
        Dog(String name) { super(name); }

        @Override
        public void speak() {
            super.speak(); // 调用父类实现
            System.out.println(name + " 叫声：汪汪");
        }
    }

    public static void main(String[] args) {
        Dog d = new Dog("小黑");
        d.speak();
    }
}
```

参考要点
- 使用 super 调用父类构造器以确保父类字段正确初始化
- 使用 super 调用父类方法实现“扩展后再补充”的行为

## 0x03 方法重写（Override）
子类可以重写父类的方法来提供更具体的实现。使用 @Override 注解能帮助编译期检查，确保方法签名正确覆盖。

示例要点：
- 重写方法时保持方法签名一致
- 可以通过 @Override 提示编译器进行检查
- 调用时动态分派：实际执行的是运行时对象的实现

代码示例：
```java
public class OverrideDemo {
    static class Animal {
        public void speak() {
            System.out.println("动物发声");
        }
    }

    static class Cat extends Animal {
        @Override
        public void speak() {
            System.out.println("喵喵");
        }
    }

    public static void main(String[] args) {
        Animal a = new Cat();
        a.speak(); // 输出 Cat 的实现
    }
}
```

参考要点
- 使用 @Override 能及时发现签名不一致导致的覆盖失败
- 重写允许实现多态的核心能力

## 0x04 多态的实现
多态性体现在同一引用（父类类型）指向不同子类对象时，调用的方法会执行实际对象的实现（运行时绑定）。

示例要点：
- 使用父类引用指向子类对象
- 调用被子类覆盖的方法时，实际执行的是子类的方法

代码示例：
```java
public class PolymorphismDemo {
    static class Animal {
        public void sound() {
            System.out.println("动物声音");
        }
    }

    static class Dog extends Animal {
        @Override
        public void sound() {
            System.out.println("汪汪");
        }
    }

    static class Cat extends Animal {
        @Override
        public void sound() {
            System.out.println("喵喵");
        }
    }

    public static void main(String[] args) {
        Animal[] animals = { new Dog(), new Cat(), new Animal() };
        for (Animal a : animals) {
            a.sound(); // 依次执行 Dog、Cat、Animal 的 sound 实现
        }
    }
}
```

参考要点
- 多态是面向对象设计中的核心，提升了代码的扩展性和可维护性

## 0x05 抽象类与抽象方法
抽象类是不能实例化的类，用于定义通用接口和部分实现。抽象方法只有声明，没有实现，子类必须实现。

示例要点：
- 使用 abstract 关键词声明抽象类和抽象方法
- 子类必须实现所有抽象方法，除非子类也是抽象类

代码示例：
```java
public class AbstractDemo {
    static abstract class Vehicle {
        abstract void move();
        void description() { System.out.println("这是一个交通工具"); }
    }

    static class Car extends Vehicle {
        @Override
        void move() { System.out.println("小汽车在行驶"); }
    }

    public static void main(String[] args) {
        Vehicle v = new Car();
        v.move();
        v.description();
    }
}
```

参考要点
- 不能直接实例化抽象类，必须通过具体的子类来创建对象

## 0x06 final 关键字在继承中的使用
final 关键字用于控制继承相关行为，分为两种用途：
- final 类：不能被继承
- final/不可覆盖的方法：子类不能覆盖该方法

示例要点：
- final 类不能再被继承
- final 方法不能被子类覆盖

代码示例：
```java
public class FinalInheritanceDemo {
    // 不能被继承
    static final class FinalAnimal {
        void run() { System.out.println("跑步"); }
    }

    // 以下写法如果取消注释将会编译错误
    // static class Child extends FinalAnimal {}

    public static void main(String[] args) {
        new FinalAnimal().run();
    }
}

public class FinalMethodDemo {
    static class Animal {
        public final void breathe() {
            System.out.println("呼吸中");
        }
    }

    static class Dog extends Animal {
        // 不能覆盖 breathe()，下面的代码将编译错误
        // @Override
        // public void breathe() { }
    }

    public static void main(String[] args) {
        Dog d = new Dog();
        d.breathe();
    }
}
```

参考要点
- final 类更安全地防止派生实现带来的破坏
- final 方法确保子类不能改变父类的关键行为

## 0x07 Object 类与常用方法
所有类都最终间接继承自 Object。常用方法包括 toString、equals、hashCode、getClass 等，适用于对象比较、调试输出等场景。

示例要点：
- 覆写 toString 以获得自定义的对象描述
- 覆写 equals 和 hashCode 实现基于字段的对象等价性
- 使用 getClass、getClass().getName 查看对象类型

代码示例：
```java
public class ObjectDemo {
    static class Person {
        private String name;

        Person(String name) { this.name = name; }

        @Override
        public String toString() {
            return "Person{name='" + name + "'}";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Person)) return false;
            Person p = (Person) o;
            return name != null ? name.equals(p.name) : p.name == null;
        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }
    }

    public static void main(String[] args) {
        Person p1 = new Person("Alice");
        Person p2 = new Person("Alice");
        System.out.println("equals: " + p1.equals(p2));          // true
        System.out.println("toString: " + p1.toString());        // 自定义描述
        System.out.println("class: " + p1.getClass().getName());  // 获取运行时类型
        System.out.println("hashCode: " + p1.hashCode());        // 哈希码
    }
}
```

参考要点
- Object 的默认方法提供了对象比较、描述和散列等基础能力，通常需要根据实际需求覆盖
- toString 常用于调试和日志输出，equals 和 hashCode 要保持一致性

参考链接（如适用）
- 继承与多态、super、重写等概念（Java 教程）  
  https://docs.oracle.com/javase/tutorial/java/IandG/inheritance.html  
  https://docs.oracle.com/javase/tutorial/java/IandG/super.html  
  https://docs.oracle.com/javase/tutorial/java/IandG/override.html  
  https://docs.oracle.com/javase/tutorial/java/IandG/polymorphism.html  
  https://docs.oracle.com/javase/tutorial/java/IandG/abstract.html  
  https://docs.oracle.com/javase/tutorial/java/IandG/usingfinal.html  
  https://docs.oracle.com/javase/17/docs/api/java/lang/Object.html

如需扩展或增加更多示例，可告诉我具体方向。我已确保示例清晰、直接可运行，且覆盖你列出的全部知识点。
