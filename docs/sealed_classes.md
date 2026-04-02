# OpenJDK17 知识库：密封类（Sealed Classes）

Sealed Classes 通过对扩展关系进行显式的控制，让代码的继承结构更加可预测与可维护。本文面向初学者，结合实际场景和可运行的代码示例，介绍如何在 OpenJDK17 中使用密封类及相关特性。

## 0x01 继承控制的问题

- 背景：在没有密封的场景下，继承关系可以任意扩展。当业务需要对某一类对象的子类做出"封闭"约束时，后续新增子类可能会打破现有模式匹配、分支逻辑或枚举式处理的完整性。
- 风险点：新增子类后，编译期对"穷举性"判断往往无反馈，运行时才暴露问题，导致分支处理需要频繁修改，且跨模块的 API 边界更难以维护。

示例：没有密封的表达式树
```java
abstract class Expr {}
class NumberExpr extends Expr { int value; NumberExpr(int v){ value = v; } }
class AddExpr extends Expr { Expr left, right; AddExpr(Expr l, Expr r){ left = l; right = r; } }

// 评估表达式
static int eval(Expr e) {
  if (e instanceof NumberExpr ne) return ne.value;
  if (e instanceof AddExpr ae) return eval(ae.left) + eval(ae.right);
  throw new IllegalArgumentException("Unknown expression type");
}
```
- 问题：如果后来再新增一个 MinusExpr(-), 评估逻辑就需要修改，否则会抛出异常，且编译器无法提前警示。

更安全的方向是通过密封类对扩展进行显式约束，让编译期就能知晓穷举范围。

## 0x02 Sealed Classes 简介

- 目标：对允许的子类集合进行显式声明，防止任意新增子类破坏现有设计。
- 结果：编译器能够在某些场景下对模式匹配和 switch 的穷举性进行检查，提升代码的可预测性和可维护性。

核心要点
- 使用 sealed 关键字修饰父类或接口
- 通过 permits 指定允许的子类（或者将子类标记为 non-sealed/final 来继续扩展控制）
- 可以与模式匹配（pattern matching）和 switch 组合，获得更强的穷举性检查

示例（概念演示）
```java
public sealed class Expr permits NumberExpr, AddExpr {
    // 公共接口/方法
}
public final class NumberExpr extends Expr { int value; NumberExpr(int v){ value = v; } }
public final class AddExpr extends Expr { Expr left, right; AddExpr(Expr l, Expr r){ left = l; right = r; } }
```

## 0x03 Sealed Classes 的语法

- sealed class|interface：声明密封类型
- permits 子句：列出该密封类型允许的具体实现/子类
- 子类的约束：实现/子类本身也需要相应的修饰符（final、sealed、non-sealed）

示例：密封父类与两个实现
```java
public sealed abstract class Shape permits Circle, Rectangle {
    abstract double area();
}

public final class Circle extends Shape {
    private final double radius;
    Circle(double r){ this.radius = r; }
    @Override double area() { return Math.PI * radius * radius; }
}

public final class Rectangle extends Shape {
    private final double width, height;
    Rectangle(double w, double h){ this.width = w; this.height = h; }
    @Override double area() { return width * height; }
}
```

扩展点
- 父类也可以是 interface：
```java
public sealed interface Vehicle permits Car, Bike {}

public final class Car implements Vehicle { /* 实现 */ }
public final class Bike implements Vehicle { /* 实现 */ }
```

- 也可以将某些实现设为非密封以允许未来扩展：
```java
public sealed class Animal permits Dog, Cat, Bird {}
public final class Dog extends Animal {}
public non-sealed class Bird extends Animal {} // Bird 及其后续扩展不再受限
```

## 0x04 permits 关键字

- 作用：明确规定哪些子类可以扩展/实现密封父类/接口
- 作用域：permits 的子类必须在同一个源文件中，或在同一模块/包下可见，且在编译期被识别为父类型的实现
- 约束效果：未在 permits 中出现的新子类将无法编译成为该父类型的直接子类

示例：若未将 MultiplyExpr 加入 permits，新增子类将报错
```java
public sealed class Expression permits NumberExpr, AddExpr { }

public final class NumberExpr extends Expression { int value; }
public final class AddExpr extends Expression { Expression left, right; }

// 若要再添加一个子类 MultiplyExpr，必须更新父类的 permits
// public final class MultiplyExpr extends Expression { ... }
```

## 0x05 Sealed Classes 与 final、non-sealed

- final：禁止再次被继承，常用于叶子节点
- non-sealed：允许在后续阶段继续扩展，冲击点在于为未来子类提供扩展入口

示例：
```java
public sealed class Vehicle permits Car, Truck {}

public final class Car extends Vehicle { void honk() { System.out.println("car honk"); } }
public non-sealed class Truck extends Vehicle { // 允许继续扩展
    // 将来可以再有 SUB_TRUCK 等进一步子类
}
```

对比：
- 如果希望某个实现不可再扩展，使用 final。
- 如果希望在未来保持灵活性，可以使用 non-sealed。

## 0x06 Sealed Classes 与接口

- 密封接口的实现同样受到 permits 的约束
- 实现类必须在 permits 列表中，否则编译失败
- 实现类可以是 final、sealed、或 non-sealed

示例：密封接口及实现
```java
public sealed interface Shape permits Circle, Rectangle {}

public final class Circle implements Shape {
    double radius;
    Circle(double r){ this.radius = r; }
}

public final class Rectangle implements Shape {
    double width, height;
    Rectangle(double w, double h){ this.width = w; this.height = h; }
}
```

- 如果需要未来扩展，可以将 Circle/Rectangle 声明为 non-sealed：
```java
public non-sealed class Circle implements Shape { /* 新子类可能出现 */ }
```

## 0x07 Sealed Classes 与模式匹配

- 通过 switch 表达式/模式匹配，可以对密封类型实现穷举性检查
- 编译器可确保 switch 的分支覆盖所有被 permits 的实现

示例：对 Shape 的模式匹配
```java
public sealed interface Shape permits Circle, Rectangle {}

public final class Circle implements Shape {
    double radius; Circle(double r){ this.radius = r; }
}
public final class Rectangle implements Shape {
    double width, height; Rectangle(double w, double h){ this.width = w; this.height = h; }
}

static String describe(Shape s) {
    return switch (s) {
        case Circle c -> "Circle, radius=" + c.radius;
        case Rectangle r -> "Rectangle, " + r.width + "x" + r.height;
        // 不需要 default，因为 Shape 被密封，编译器知道所有可能
    };
}
```

进阶要点
- 如果未来添加新的实现类，如 Polygon，则需要同时更新 Shape 的 permits，以及 describe 的 switch 分支，否则编译报错，确保穷举性。

示例（新增实现时的编译约束）
```java
// 假设新增实现
public final class Triangle implements Shape {
    double a, b, c;
    Triangle(double a, double b, double c){ this.a=a; this.b=b; this.c=c; }
}

// 需要更新为
public final class Triangle implements Shape { /* 新实现 */ }

// 并在 describe 中添加分支
case Triangle t -> "Triangle, sides=" + t.a + "," + t.b + "," + t.c;
```

## 0x08 Sealed Classes 的最佳实践

- 何时使用密封：对领域模型中的"有限集合"或"域边界"非常有益，能在编译期控制扩展范围，降低隐式多态带来的风险
- 止于叶子节点：对不希望再扩展的实现，使用 final；需要未来扩展时，考虑 non-sealed
- 与模式匹配协同：在对密封层进行模式匹配时，编译器能帮助发现未覆盖的分支，提升代码健壮性
- 组合使用接口与类：接口也可密封，帮助 API 边界更加清晰；实现类在 permits 中明确列出，易于阅读与维护
- 与版本迁移的平滑性：若计划将来允许扩展，请从开始就使用 non-sealed，避免未来全面重构

简要总结
- 使用密封类可以把一个"家族"限定在一个受控集合内，从而提升代码的可预测性和安全性
- 通过 permits、final、non-sealed 的组合，可以在稳定性与灵活性之间找到平衡点
- 与模式匹配结合时，编译器对穷举性的检查将变得更可靠

代码要点回顾
- 密封父类/接口使用：public sealed class Foo permits Bar1, Bar2 { … }
- 子类限定：public final class Bar1 extends Foo { … }，或 public non-sealed class Bar2 extends Foo { … }
- 密封接口实现：public sealed interface Biz permits A, B { }；实现类必须列在 permits 中
- 模式匹配与 switch 的穷举性：若父类型被密封，编译器会要求覆盖所有允许的子类

## 参考链接

- OpenJDK Jeps 409 — Sealed Classes: https://openjdk.org/jeps/409
- Baeldung — Java Sealed Classes: https://www.baeldung.com/java-sealed-classes
- Baeldung — Java Sealed Interfaces: https://www.baeldung.com/java-sealed-interfaces
- Oracle 官方（Sealed Classes 相关概览与文档资源，便于进一步学习）: https://www.oracle.com/java/technologies/javase/sealed-classes.html
