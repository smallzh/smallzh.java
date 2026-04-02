# OpenJDK17 知识库：方法的定义与使用要点

## 0x01 方法定义与调用
方法是定义在类中的可执行代码块，包含返回值类型、方法名和参数列表。Java 将方法分为实例方法和静态方法，调用方式也不同：实例方法通过对象实例调用，静态方法通过类名直接调用。下面的示例展示两者并用一个主入口演示调用。

```java
public class DemoMethods {
    public static void main(String[] args) {
        // 调用静态方法
        int sum = DemoMethods.add(2, 3);
        System.out.println("2+3=" + sum);

        // 调用实例方法
        DemoMethods dm = new DemoMethods();
        dm.printHello("小明");
    }

    // 静态方法
    public static int add(int a, int b) {
        return a + b;
    }

    // 实例方法
    public void printHello(String name) {
        System.out.println("你好, " + name);
    }
}
```

参考要点
- 静态方法使用类名访问，适用于不需要对象状态的方法。
- 实例方法需要通过对象实例访问，方法可以访问对象的字段和其他实例方法。

参考链接
- Java 教程中的方法： https://docs.oracle.com/javase/tutorial/java/javaOO/methods.html

---

## 0x02 方法参数（形参与实参、可变参数）
方法的形参是方法签名中定义的变量名，实参是在调用时传入的实际值。Java 使用按值传参：基本类型的值会被复制，引用类型的引用会被复制但指向同一个对象。可变参数（varargs）允许传入任意数量的参数。

```java
public class MethodParams {
    public static void main(String[] args) {
        int x = 5;
        modifyPrimitive(x);
        System.out.println("x after modifyPrimitive: " + x); // 仍为 5

        int[] arr = {1, 2, 3};
        modifyArray(arr);
        System.out.println("arr[0] after modifyArray: " + arr[0]); // 变为 99

        // 可变参数
        printAll("A", "B", "C");
        printAll();
    }

    static void modifyPrimitive(int p) { p = 999; }

    static void modifyArray(int[] a) {
        if (a != null && a.length > 0) {
            a[0] = 99;
        }
    }

    static void printAll(String... items) {
        if (items.length == 0) {
            System.out.println("(empty)");
            return;
        }
        for (String s : items) System.out.print(s + " ");
        System.out.println();
    }
}
```

参考要点
- 基本类型形参不会改变实参的值，引用类型的形参可能通过修改对象内容影响实参指向的对象。
- 可变参数在方法内部表现为数组，可以像普通数组一样使用。

参考链接
- 变量参数（可变参数）介绍： https://docs.oracle.com/javase/tutorial/java/javaOO/arguments.html

---

## 0x03 方法重载
方法重载指在同一个类中，方法名相同但参数列表不同。编译期根据参数类型、数量来选择合适的重载版本。

```java
public class OverloadDemo {
    public static void main(String[] args) {
        System.out.println(add(1, 2));           // 调用 int 类型版本
        System.out.println(add(1.5, 2.5));       // 调用 double 类型版本
        System.out.println(add("A", "B"));       // 调用 String 拼接版本
    }

    public static int add(int a, int b) { return a + b; }
    public static double add(double a, double b) { return a + b; }
    public static String add(String a, String b) { return a + b; }
}
```

参考要点
- 重载有助于同一逻辑的不同输入类型或数量的处理。
- 返回值不同并不构成重载，必须以参数列表为辨识。

参考链接
- Java 方法重载： https://docs.oracle.com/javase/tutorial/java/javaOO/overloading.html

---

## 0x04 递归方法
递归方法通过在方法内部直接或间接调用自身来解决问题。通常需要一个明确的基准情形作为结束条件，避免无限递归导致栈溢出。

```java
public class RecursionDemo {
    public static void main(String[] args) {
        System.out.println("5! = " + factorial(5));
    }

    static long factorial(int n) {
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }
}
```

参考要点
- 递归要有基准条件，确保能终止。
- 递归适合分解成相同的小问题，但要注意性能和栈深限制。

参考链接
- 递归简介（Nuts and Bolts 章节）： https://docs.oracle.com/javase/tutorial/java/nutsandbolts/recursion.html

---

## 0x05 构造方法
构造方法用于创建对象并初始化实例字段。构造方法名称与类名相同，没有返回值。可以通过 this() 在同一个类中实现构造方法之间的重用。

```java
public class Person {
    private String name;
    private int age;

    // 无参构造方法，提供默认值
    public Person() {
        this("未知", 0);
    }

    // 带参数的构造方法
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + "}";
    }

    public static void main(String[] args) {
        Person p1 = new Person();
        Person p2 = new Person("李四", 28);
        System.out.println(p1);
        System.out.println(p2);
    }
}
```

参考要点
- 使用 this(...) 可以在一个构造方法中调用另一个构造方法，避免重复代码。
- 构造方法可以有多个重载版本，提供不同的初始化方式。

参考链接
- 构造方法与构造器（Constructors）： https://docs.oracle.com/javase/tutorial/java/javaOO/constructors.html

---

## 0x06 方法引用（Java 8+）
方法引用是对已有方法、构造方法或实例方法的简洁引用，常与 lambda 表达式搭配使用，提升代码可读性和简洁度。

```java
import java.util.*;
import java.util.function.Supplier;

public class MethodRefDemo {
    // 静态方法引用
    public static int compareByLength(String a, String b) {
        return Integer.compare(a.length(), b.length());
    }

    // 构造方法引用需要在静态上下文中测试
    static class Person {
        String name;
        Person() { this.name = "Unknown"; }
        public String toString() { return "Person{name='" + name + "'}"; }
    }

    public static void main(String[] args) {
        List<String> words = new ArrayList<>(Arrays.asList("apple", "banana", "cherry"));

        // 静态方法引用作为比较器
        words.sort(MethodRefDemo::compareByLength);
        System.out.println(words); // [apple, cherry, banana]

        // 实例方法引用
        String s = "hello";
        Supplier<Integer> len = s::length;
        System.out.println("length: " + len.get());

        // 构造方法引用
        Supplier<Person> personFactory = Person::new;
        Person p = personFactory.get();
        System.out.println(p);
    }
}
```

说明
- 静态方法引用、实例方法引用和构造方法引用在实际场景中都很有用，配合 Java 的函数式接口（如 Function、Supplier、Comparator 等）使用效果最佳。
- 需要时可以结合流（Streams）进行更简洁的管道处理。

参考链接
- Java 8 及以后的方法引用： https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html

---

参考总结
- 方法是 Java 中对行为的封装，包含定义、调用和重载等机制。通过上述示例可以直观理解常见用法，且覆盖了从基础到 Java 8+ 的现代用法。若想深入，可结合 Oracle 官方教程继续学习。  
- 进一步学习资源（官方教程入口）：
  - 方法基本用法： https://docs.oracle.com/javase/tutorial/java/javaOO/methods.html
  - 参数与可变参数： https://docs.oracle.com/javase/tutorial/java/javaOO/arguments.html
  - 方法重载： https://docs.oracle.com/javase/tutorial/java/javaOO/overloading.html
  - 构造方法： https://docs.oracle.com/javase/tutorial/java/javaOO/constructors.html
  - 方法引用（Java 8+）： https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html
  - 递归： https://docs.oracle.com/javase/tutorial/java/nutsandbolts/recursion.html

如需，我可以把以上示例整理成单文件对照练习，方便直接复制到本地练习环境。
