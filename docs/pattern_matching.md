# OpenJDK17 知识库：模式匹配

本章节面向初学者，聚焦模式匹配在 Java 语言中的实际用法与常见场景。通过直观的代码示例，帮助你理解传统类型检查的局限性，以及如何借助模式匹配提升代码可读性与安全性。

## 0x01 传统类型检查的问题

- 问题1：需要重复进行类型判断与强制类型转换，易出错且冗长。
- 问题2：在多分支分支中很容易出现"分支覆盖不全"的风险，或需要大量手工维护类型转换逻辑。
- 问题3：在复杂结构中，层层嵌套的 if-else 会降低代码可读性。

示例：传统做法需要进行多次类型判断和强转。

```java
public static void printLength(Object obj) {
    if (obj instanceof String) {
        String s = (String) obj;
        System.out.println("字符串长度为: " + s.length());
    } else {
        System.out.println("不是字符串");
    }
}
```

- 解释：如果要扩展支持更多类型，需要再追加一个分支，并进行相应的强转，代码会快速臃肿。

---

## 0x02 Pattern Matching 简介

- 核心思想：模式匹配把"测试类型"与"绑定变量"的操作合二为一，减少显式强转。
- 结果：代码更简洁、可读性更强，且在某些场景中编译器可以更好地进行类型推断。

示例：使用 instanceof 的模式匹配。

```java
public static void printLength(Object obj) {
    if (obj instanceof String s) {
        System.out.println("字符串长度为: " + s.length());
    } else {
        System.out.println("不是字符串");
    }
}
```

- 说明：在 if 块内，变量 s 的作用域仅限于该分支，避免了在分支外部错误使用的问题。

---

## 0x03 instanceof 模式匹配（Java 16 正式）

- 版本演进：模式匹配在 Java 16 正式发布时，正式提供了对 instanceof 的模式匹配支持（非预览特性）。通过在 instanceof 中直接绑定变量，省去了显式强转步骤。
- 使用要点：
  - 模式变量仅在该分支内可见。
  - 可以与其他条件组合使用。

示例1：简单的字符串匹配和绑定。

```java
Object obj = "hello";
if (obj instanceof String s) {
    System.out.println("长度: " + s.length());
} else {
    System.out.println("不是字符串");
}
```

示例2：对数值类型的匹配。

```java
Number n = 42;
if (n instanceof Integer i) {
    System.out.println("整数值加一: " + (i + 1));
}
```

- 小结：模式变量绑定后，可以直接在分支内安全使用，无需显式强转。

---

## 0x04 Switch 表达式中的模式匹配

- 目标：在 switch 表达式/语句中使用模式匹配，实现对多种类型的解构与分支处理，且 switch 表达式可返回值。
- 优势：解决大量类型判断的冗余并提升结构化清晰度；结合守卫（when）条件，可以实现更细粒度的分支控制。

示例1：基础的 switch 表达式模式匹配。

```java
Object obj = "hello";
String desc = switch (obj) {
    case Integer i -> "是一个整型，值为 " + i;
    case String s -> "是一个字符串，长度为 " + s.length();
    case null -> "空对象";
    default -> "未知类型";
};
System.out.println(desc);
```

示例2：带守卫的模式匹配（when 条件）。

```java
Object obj = "abcdef";
String info = switch (obj) {
    case String s when s.length() > 5 -> "长字符串: " + s;
    case String s -> "短字符串: " + s;
    default -> "其他类型";
};
System.out.println(info);
```

- 说明：switch 中的模式匹配提高了分支的表达力，守卫可以把额外条件放在模式外层，使逻辑更清晰。

---

## 0x05 Record Patterns（Java 19 预览，Java 21 正式）

- 背景：Record Patterns 允许对记录组件进行解构匹配，进一步提升对数据载体的直观匹配能力。
- 适用对象：对使用记录类型的数据结构进行模式匹配，直接提取组件值。

示例1：对记录进行解构匹配。

```java
record Point(int x, int y) {}

Object obj = new Point(3, 4);
if (obj instanceof Point(int x, int y)) {
    System.out.println("点坐标：" + x + "," + y);
}
```

示例2：在 switch 中使用记录模式。

```java
switch (obj) {
    case Point(int x, int y) -> System.out.println("点坐标：" + x + "," + y);
    default -> System.out.println("未知结构");
}
```

- 说明：Record Patterns 在 Java 19 作为预览引入，Java 21 正式版中正式可用，帮助代码更简洁地提取记录组件。

---

## 0x06 模式匹配与 Sealed Classes

- 背景：Sealed Classes/Interfaces 限定子类的集合，结合模式匹配可以实现更强的类型安全与更完整的分支覆盖。
- 优势：编译器可对 switch 的分支进行穷尽性检查，减少运行时错误。

示例：定义一个受限的 Shape 家族，并对其进行模式匹配。

```java
// 1) 定义可密封的类型层级
sealed interface Shape permits Circle, Rectangle {}

final class Circle implements Shape {
    final double r;
    Circle(double r) { this.r = r; }
}

final class Rectangle implements Shape {
    final double w, h;
    Rectangle(double w, double h) { this.w = w; this.h = h; }
}

// 2) 使用模式匹配进行判断
Shape s = new Circle(2.5);

if (s instanceof Circle c) {
    System.out.println("圆的半径: " + c.r);
}

// 3) 在 switch 中使用模式匹配
String kind = switch (s) {
    case Circle c -> "圆";
    case Rectangle r -> "矩形";
    default -> "未知形状";
};
System.out.println("形状类别: " + kind);
```

- 说明：对 sealed 类型的切换分支若覆盖了所有子类型，则编译期可以穷尽，提升安全性与可维护性。

---

## 0x07 模式匹配的最佳实践

- 优先使用 switch 表达式来处理多分支逻辑，能给出明确的返回值并避免原始多层 if-else。
- 结合守卫（when 条件）使用更复杂的边界条件，避免把逻辑塞进单一模式。
- 对于数据载体，优先使用记录（Records）和记录模式，以简化解构和提取操作。
- 在可能的情况下使用受限的类型（sealed classes/interfaces）以提高穷尽性检查的力度。
- 模式变量的作用域仅限于所属分支，避免在分支之外误用。若需要跨分支共享，请使用后续的变量绑定策略或其他设计。
- 编写测试覆盖各种模式分支，确保在切换不同输入时行为一致。
- 避免过度嵌套模式，保持模式结构简单直观，便于阅读和维护。

示例：结合 switch、记录、密封类型的综合用法

```java
sealed interface Result permits Success, Failure {}

final class Success implements Result {
    final String value;
    Success(String value) { this.value = value; }
}
final class Failure implements Result {
    final int code;
    Failure(int code) { this.code = code; }
}

public static String describe(Result r) {
    return switch (r) {
        case Success s -> "成功: " + s.value;
        case Failure f -> "失败代码: " + f.code;
        // 由于 Result 是 sealed 的，编译器能确保穷尽覆盖
    };
}
```

- 说明：通过密封类型与模式匹配结合，代码更具鲁棒性且易于扩展。

---

## 参考链接

- OpenJDK Jeps: Pattern Matching for instanceof（Java 16 正式、预览演进的核心特性） https://openjdk.java.net/jeps/394
- OpenJDK Jeps: Pattern Matching for switch（开拓 Switch 的模式匹配能力） https://openjdk.java.net/jeps/406
- OpenJDK Jeps: Record Patterns（19 预览、21 正式） https://openjdk.java.net/jeps/425
- Sealed Classes（Java 教程与示例） https://docs.oracle.com/javase/tutorial/java/IandI/sealed.html
