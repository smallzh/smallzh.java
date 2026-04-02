# OpenJDK17 知识库：Switch 表达式详解

本文面向初学者，系统介绍 Switch 表达式在 OpenJDK17 及以后的版本中的用法、语法要点以及实践建议。每个知识点都配有可直接运行的代码示例，帮助快速上手和对比掌握。

## 0x01 传统 switch 语句的问题

传统的 switch 语句需要在每个分支后面显式写出 break 来防止穿透，否则容易产生意料之外的落空或重复执行。此外， switch 语句往往需要先定义一个变量来保存结果，代码可读性和可维护性较差，且在很多场景下只能执行"过程"而非直接返回一个值。

示例：使用传统 switch 计算星期几对应的工作日标签
```java
int day = 3;
String label;
switch (day) {
  case 1:
  case 2:
  case 3:
  case 4:
  case 5:
    label = "工作日";
    break;
  case 6:
  case 7:
    label = "周末";
    break;
  default:
    label = "未知";
    break;
}
System.out.println(label); // 输出：工作日
```

要点总结：
- 需要大量 break，易出错；
- 代码往往是多步"过程"，不直接返回值；
- 穿透和分支组合会降低可读性。

## 0x02 Switch 表达式简介

Switch 表达式把 switch 变成一个表达式，可以直接返回一个值。使用箭头语法（->）可以让分支更简单，复杂分支也可以通过块语句结合 yield возвращения 值。

简单示例：使用 switch 表达式直接获得结果
```java
int day = 3;
String label = switch (day) {
  case 1, 2, 3, 4, 5 -> "工作日";
  case 6, 7 -> "周末";
  default -> "未知";
};
System.out.println(label); // 输出：工作日
```

要点总结：
- Switch 变成一个表达式，结果直接赋值给变量；
- 使用箭头语法时，默认不会再需要 break；
- default 提供兜底情况，确保穷尽性。

## 0x03 箭头语法（->）

箭头语法是 Switch 表达式的核心，简洁且更易读。单行分支通常直接返回一个值；多分支可以使用块语句。

示例：简单箭头分支和多分支组合
```java
int score = 85;
String grade = switch (score) {
  case 90 -> "A";
  case 80 -> "B";
  default -> "C";
};
System.out.println(grade); // 输出：B
```

示例：多语句分支需要块语句
```java
int value = 42;
String result = switch (value) {
  case 1, 2 -> "Low";
  case 3 -> "Medium";
  default -> {
    int v = value * 2;
    yield "Value is " + v;
  }
};
System.out.println(result); // 输出：Value is 84
```

要点总结：
- 箭头语法适合简单映射，代码更紧凑；
- 对于多语句分支，必须使用块并借助 yield 产出结果。

## 0x04 yield 关键字

yield 用于 Switch 表达式的块分支中，将块内的计算结果返回给 Switch 表达式的结果。没有 yield，就无法从块中产出值。

示例：块中使用 yield 输出结果
```java
int n = 7;
String msg = switch (n) {
  case 1 -> "one";
  case 2 -> "two";
  default -> {
    int v = n * 3;
    yield "value=" + v;
  }
};
System.out.println(msg); // 输出：value=21
```

要点总结：
- yield 必须出现在块语句内部；
- yield 的返回值就是整个 switch 表达式的结果。

## 0x05 Switch 表达式与枚举

Switch 表达式对枚举类型使用非常自然，分支直接对应枚举常量，代码简洁明了。

示例：枚举大小的映射
```java
enum Size { SMALL, MEDIUM, LARGE }

Size s = Size.MEDIUM;
String label = switch (s) {
  case SMALL -> "S";
  case MEDIUM -> "M";
  case LARGE -> "L";
};
System.out.println(label); // 输出：M
```

要点总结：
- 枚举分支写法清晰，便于后续扩展；
- 不需要额外的 if/else 线索，提高可读性。

## 0x06 Switch 表达式与模式匹配（Java 17+）

模式匹配使 switch 的 case 能直接绑定变量、进行类型检查等，写法更简洁，提升可读性。

示例：简单的模式匹配（针对 Object，绑定具体类型的变量）
```java
Object obj1 = "hello";
String text = switch (obj1) {
  case String s -> "字符串：" + s;
  case Integer i -> "整数：" + i;
  default -> "其他";
};
System.out.println(text); // 输出：字符串：hello
```

示例：对不同类型进行进一步处理
```java
Object value = 123;
String info = switch (value) {
  case String s -> "字符串长度=" + s.length();
  case Integer i -> "整数值=" + i;
  default -> "未知类型";
};
System.out.println(info); // 输出：整数值=123
```

要点总结：
- 模式匹配让类型判断和变量绑定更直观；
- switch 表达式在处理多态数据时更具表达力；
- 具体实现和可用性可能随 JDK 版本的预览/稳定性阶段而不同，使用时留意当前 JDK 的特性状态。

## 0x07 Switch 表达式的最佳实践

- 优先使用 Switch 表达式来获取一个值，避免坐后室的多行 if/else。
- 将同一组分支用逗号分隔成一个规则，例如 case MONDAY, FRIDAY -> ...
- 对于复杂逻辑或多语句分支，使用块并通过 yield 输出结果。
- 避免在同一个 switch 中混用箭头语法和带冒号的语法；应尽量统一风格。
- 对枚举类型，显式覆盖所有枚举值，确保穷尽性（或提供 default）。
- 在需要进行类型判断和绑定时优先使用模式匹配，提升可读性和安全性。
- 对可变数据或副作用较小的计算，尽量使 switch 表达式保持纯粹性，便于测试和推理。

示例小结：当你需要根据一个输入值返回一个结果时，优先考虑 Switch 表达式；当你需要进行多步处理时，使用块和 yield。

## 参考链接

- OpenJDK Jeps: Switch Expressions（Java 14 及后续演进，JEP 361）https://openjdk.java.net/jeps/361
- OpenJDK Jeps: Pattern Matching for Switch（模式匹配 for switch，预览/逐步成熟，JEP 406）https://openjdk.java.net/jeps/406
- Oracle Java Tutorials: Switch Statements（传统 switch 的介绍与示例）https://docs.oracle.com/javase/tutorial/java/nutsandbolts/switch.html
- Java SE 17 官方文档与特性概览（主题相关章节）https://openjdk.java.net/projects/jeps/

说明
- 文中示例均以 Java 语言特性为核心编写，适用于 OpenJDK17 及以上版本的日常学习与实战练习。
- 如果你在具体版本中遇到"模式匹配 for switch"作为预览特性，需要在编译时开启相应的预览选项，例如 javac 的 --enable-preview 和运行时的 --release 指向相应版本。文档中的示例以标准化用法呈现，实际使用请结合当前 JDK 版本的文档。
