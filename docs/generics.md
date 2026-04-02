# 泛型在 Java 中提供类型安全与代码复用能力

本文面向 OpenJDK17，面向初学者，聚焦泛型的核心概念、用法与常见模式。每个知识点都附有可直接运行的代码示例，帮助理解与实际应用。

## 0x01 泛型的概念与优势
- 概念要点
  - 在类、接口和方法中引入类型参数，从而在编译时进行类型检查，减少运行时的类型转换和强制类型转换带来的错误。
- 主要优势
  - 提高类型安全，避免强制类型转换错误。
  - 提高代码复用性，可以写出对多种类型通用的实现。
  - API 设计更通用，使用方不需要为不同类型重复编写代码。
- 示例
```java
import java.util.ArrayList;
import java.util.List;

public class GenericsDemo {
    public static void main(String[] args) {
        // 使用泛型 List 指定元素类型为 String
        List<String> names = new ArrayList<>();
        names.add("Alice");
        names.add("Bob");
        String first = names.get(0); // 不需要强制类型转换
        System.out.println(first);
        
        // 未使用泛型的情况（需要强制类型转换，容易出错）
        List rawList = new ArrayList();
        rawList.add("Charlie");
        String s = (String) rawList.get(0);
        System.out.println(s);
    }
}
```

---

## 0x02 泛型类
- 核心点
  - 将类型参数应用到类的字段、方法和构造中，从而实现对多种类型的通用封装。
- 示例
```java
public class Box<T> {
    private T value;

    public Box() {}

    public Box(T value) {
        this.value = value;
    }

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public static void main(String[] args) {
        Box<String> stringBox = new Box<>("hello");
        System.out.println(stringBox.get());

        Box<Integer> intBox = new Box<>(123);
        System.out.println(intBox.get());
    }
}
```

---

## 0x03 泛型方法
- 核心点
  - 方法上定义类型参数，独立于所属的类的类型参数，便于在同一个方法中处理多种类型。
- 示例
```java
public class GenericMethodDemo {
    // 泛型方法：可以接收任意类型的数组并打印
    public static <T> void printArray(T[] arr) {
        for (T elem : arr) {
            System.out.print(elem + " ");
        }
        System.out.println();
    }

    // 有界的泛型方法：T 必须实现 Comparable<T>
    public static <T extends Comparable<T>> T max(T a, T b) {
        return a.compareTo(b) >= 0 ? a : b;
    }

    public static void main(String[] args) {
        String[] words = {"apple", "banana", "cherry"};
        printArray(words);

        Integer maxVal = max(10, 20);
        System.out.println("max = " + maxVal);
    }
}
```

---

## 0x04 泛型接口
- 核心点
  - 在接口中使用类型参数，提升接口的通用性与可重用性。
- 示例
```java
// 泛型接口：将输入类型映射为输出类型
public interface Converter<T, R> {
    R convert(T input);
}

// 基于该接口实现的具体转换
public class StringToIntegerConverter implements Converter<String, Integer> {
    @Override
    public Integer convert(String input) {
        return Integer.valueOf(input);
    }
}

class Demo {
    public static void main(String[] args) {
        Converter<String, Integer> conv = new StringToIntegerConverter();
        System.out.println(conv.convert("123"));
    }
}
```

---

## 0x05 类型参数的命名约定（T、E、K、V 等）
- 核心点
  - 命名约定有助于阅读和理解代码中类型参数的角色。
  - 常见命名：
    - T: 通用类型
    - E: 元素（通常出现在集合中）
    - K: 键
    - V: 值
    - N: 数字类型
- 示例
```java
// 使用命名更具意义的泛型类
public class Pair<K, V> {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() { return key; }
    public V getValue() { return value; }
}
```

---

## 0x06 有界类型参数（extends, super）
- 核心点
  - 上界（extends）限定类型参数的上限，确保该类型具备某些行为。
  - 下界（super）用于接收较低层面的类型，常用于生产者/消费者场景。
- 示例
```java
// 上界：T 必须是 Number 的子类，确保可以调用 Number 的方法
public class NumericBox<T extends Number> {
    private T value;

    public NumericBox(T value) { this.value = value; }

    public double doubleValue() {
        return value.doubleValue();
    }

    public static void main(String[] args) {
        NumericBox<Integer> intBox = new NumericBox<>(42);
        System.out.println(intBox.doubleValue());

        // NumericBox<String> 会编译错误，因为 String 不是 Number 的子类
        // NumericBox<String> bad = new NumericBox<>("abc"); // 编译错误
    }
}

// 下界：List<? super Integer> 可以向其中添加 Integer，适合消费者场景
import java.util.ArrayList;
import java.util.List;

class LowerBoundDemo {
    public static void addNumbers(List<? super Integer> list) {
        list.add(1);
        list.add(2);
    }

    public static void main(String[] args) {
        List<Number> numbers = new ArrayList<>();
        addNumbers(numbers);
        // numbers 现在包含 1, 2
        System.out.println(numbers);
    }
}
```

---

## 0x07 通配符（?, ? extends, ? super）
- 核心点
  - 通配符用于灵活地表达类型的上界、下界和未知类型。
  - 典型场景：只读时使用 extends；只写时使用 super；既可读又可写但要注意边界。
- 示例
```java
import java.util.ArrayList;
import java.util.List;

class WildcardDemo {
    public static void readOnly(List<? extends Number> list) {
        // 只读，能读取元素，不能添加新的元素（除了 null）
        Number n = list.get(0);
        System.out.println(n);
        // list.add(1); // 编译错误
    }

    public static void writeOnly(List<? super Integer> list) {
        // 只写，可以添加 Integer 及其子类，但读取时类型为 Object
        list.add(1);
        list.add(new Integer(2));
        Object obj = list.get(0);
        System.out.println(obj);
    }

    public static void main(String[] args) {
        List<Integer> intList = new ArrayList<>();
        intList.add(10);
        readOnly(intList);

        List<Number> numList = new ArrayList<>();
        writeOnly(numList);
        System.out.println(numList);
    }
}
```

---

## 0x08 类型擦除
- 核心点
  - 在编译时，泛型信息会被擦除，运行时只保留原始类型信息。
  - 同一运行时类型会对不同的泛型参数表现为同一类型。
- 示例
```java
import java.util.ArrayList;
import java.util.List;

class ErasureDemo {
    public static void main(String[] args) {
        List<String> s = new ArrayList<>();
        List<Integer> i = new ArrayList<>();

        // 运行时类型都是 ArrayList
        System.out.println(s.getClass() == i.getClass()); // true

        // 泛型信息在运行时不可见，编译时还会进行类型检查
        s.add("hello");
        // s.add(123); // 编译错误，类型不匹配

        System.out.println(s.get(0));
    }
}
```

---

## 0x09 泛型与集合的结合使用
- 核心点
  - Java 集合框架广泛使用泛型，提升集合在类型上的安全性和可读性。
- 示例
```java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CollectionGenDemo {
    public static void main(String[] args) {
        // List 的泛型使用
        List<String> fruits = new ArrayList<>();
        fruits.add("apple");
        fruits.add("banana");
        for (String fruit : fruits) {
            System.out.println(fruit);
        }

        // Map 的泛型使用
        Map<String, Integer> counts = new HashMap<>();
        counts.put("apple", 3);
        counts.put("banana", 5);
        for (Map.Entry<String, Integer> e : counts.entrySet()) {
            System.out.println(e.getKey() + ": " + e.getValue());
        }

        // 使用通配符和流式 API 的示例
        List<? extends Number> nums = new ArrayList<Integer>();
        Number n = nums.get(0); // 只读
        System.out.println(n);
    }
}
```

---

## 0x0A 参考链接
- 官方 Java 泛型教程（Oracle）: https://docs.oracle.com/javase/tutorial/java/generics/index.html
- 运行时类型信息与擦除机制: https://docs.oracle.com/javase/tutorial/java/generics/erasure.html
- 通配符（wildcards）与边界示例: https://docs.oracle.com/javase/tutorial/java/generics/wildcards.html
- 泛型的使用与设计概览（包含示例与最佳实践）: https://docs.oracle.com/javase/tutorial/java/generics/overview.html

如需深入特定主题的扩展示例，欢迎告诉我你最感兴趣的部分，我可以再追加更具体的示例与讲解。
