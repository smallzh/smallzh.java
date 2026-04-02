# OpenJDK17 控制流语句指南

以下内容面向初学者，聚焦实际用法与常见写法。每个知识点均包含可直接运行的 Java 代码示例。

## 0x01 条件语句

- If-else 基本用法
  - 通过 if 判断条件来控制执行路径，必要时使用 else 分支处理不符合条件的情况。
  - 示例演示对分数判断等级的简单逻辑。

```java
public class IfElseDemo {
    public static void main(String[] args) {
        int score = 85;
        char grade;
        if (score >= 90) {
            grade = 'A';
        } else if (score >= 80) {
            grade = 'B';
        } else {
            grade = 'C';
        }
        System.out.println("成绩等级: " + grade);
    }
}
```

- Switch 语句（简介用法）
  - 适用于对离散值的多分支处理。注意 Java 17 中的 switch 仍可用传统写法（带 break），但更推荐使用后续章节的表达式写法。
  
```java
public class SwitchStmtDemo {
    public static void main(String[] args) {
        int day = 3; // 1: 周一, 2: 周二, ...
        switch (day) {
            case 1:
                System.out.println("周一");
                break;
            case 2:
                System.out.println("周二");
                break;
            case 3:
                System.out.println("周三");
                break;
            default:
                System.out.println("其他日子");
        }
    }
}
```

> 说明：Switch 表达式（下面 0x05 会介绍）可以让 switch 直接返回值，简化代码结构。

参考链接（基础语法）：Oracle 官方教程中的条件与开关语句
- https://docs.oracle.com/javase/tutorial/java/nutsandbolts/if.html
- https://docs.oracle.com/javase/tutorial/java/nutsandbolts/switch.html

## 0x02 循环语句

- for 循环（计数型）
  - 适用于已知迭代次数的场景，常用于累加、遍历固定次数。
  
```java
public class ForLoopDemo {
    public static void main(String[] args) {
        int sum = 0;
        for (int i = 1; i <= 5; i++) {
            sum += i;
        }
        System.out.println("前五个整数之和: " + sum);
    }
}
```

- while 循环
  - 先判断条件再执行循环，适合未知次数的场景。

```java
public class WhileLoopDemo {
    public static void main(String[] args) {
        int n = 0;
        while (n < 5) {
            System.out.println("n = " + n);
            n++;
        }
    }
}
```

- do-while 循环
  - 先执行一次循环体，再判断条件，确保循环体至少执行一次。

```java
public class DoWhileDemo {
    public static void main(String[] args) {
        int m = 0;
        do {
            System.out.println("m = " + m);
            m++;
        } while (m < 5);
    }
}
```

参考链接（循环基础）：
- https://docs.oracle.com/javase/tutorial/java/nutsandbolts/for.html

## 0x03 跳转语句

- break 跳出循环或 switch
  - 常用于在满足条件时提前结束循环。

```java
public class BreakDemo {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            if (i == 5) {
                break;
            }
            System.out.println("i = " + i);
        }
    }
}
```

- continue 跳过本次循环的剩余部分，进入下一次循环判断
  
```java
public class ContinueDemo {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                continue; // 跳过偶数
            }
            System.out.println("奇数 i = " + i);
        }
    }
}
```

- return 跳出当前方法
  - 在方法中遇到 return 时，直接返回结果并结束当前方法的执行。

```java
public class ReturnDemo {
    public static void main(String[] args) {
        System.out.println("最大值: " + max(7, 12));
    }

    static int max(int a, int b) {
        if (a > b) {
            return a;
        } else {
            return b;
        }
    }
}
```

参考链接（跳转语句/控制流合集）：
- https://docs.oracle.com/javase/tutorial/java/nutsandbolts/branch.html
- https://docs.oracle.com/javase/tutorial/java/noundsbolts/if.html

## 0x04 增强型 for 循环（for-each）

- 以更简洁的方式遍历数组和集合，避免手动维护下标。
  
```java
public class EnhancedForDemo {
    public static void main(String[] args) {
        int[] nums = {1, 2, 3, 4};
        for (int n : nums) {
            System.out.println("n = " + n);
        }

        // 使用 List 的 for-each
        java.util.List<String> names = java.util.List.of("Alice", "Bob", "Charlie");
        for (String name : names) {
            System.out.println("name = " + name);
        }
    }
}
```

参考链接（循环与集合遍历）：  
- https://docs.oracle.com/javase/tutorial/java/nutsandbolts/for.html

## 0x05 Switch 表达式（Java 14+）

- Switch 表达式用于将 switch 作为表达式使用，直接返回值，代码更简洁。  
  - 使用箭头语法（->）来定义分支，避免手动写 break。
  
```java
public class SwitchExprDemo {
    public static void main(String[] args) {
        int score = 7;
        String level = switch (score) {
            case 10, 9 -> "优秀";
            case 7, 8  -> "良好";
            default      -> "需要努力";
        };
        System.out.println("等级: " + level);
    }
}
```

- 还可以结合变量和块语法使用 yield 的方式返回值（块内可进行多步运算）：

```java
public class SwitchExprBlockDemo {
    public static void main(String[] args) {
        int code = 42;
        String msg = switch (code) {
            case 0 -> "成功";
            case 1 -> "未找到";
            default -> {
                // 复杂逻辑可以放在块中
                int computed = code * 2;
                yield "错误码：" + computed;
            }
        };
        System.out.println("消息: " + msg);
    }
}
```

- Switch 表达式也可用于枚举和其他离散值的映射场景，语义更清晰。

参考链接（Switch Expressions 指南）：
- https://docs.oracle.com/javase/tutorial/java/nutsandbolts/switch.html

参考总览（常用控制流参考文档）
- If-else 与 Switch（条件语句）: https://docs.oracle.com/javase/tutorial/java/nutsandbolts/if.html
- Switch 语句（传统）: https://docs.oracle.com/javase/tutorial/java/nutsandbolts/switch.html
- For/While/Do-While 循环: https://docs.oracle.com/javase/tutorial/java/nutsandbolts/for.html
- Switch 表达式与模式匹配（Java 14+）: https://docs.oracle.com/javase/tutorial/java/nutsandbolts/switch.html

使用场景小结
- 条件语句用于分支选择，逻辑简单清晰时优先使用 if-else。
- Switch 语句适合离散取值分支，代码结构清晰时优于多重 if-else。
- 循环语句用于重复执行，明确迭代次数时优先 for，未知次数时使用 while/do-while。
- 增强型 for 循环（for-each）简化集合和数组遍历，避免显式下标错误。
- Switch 表达式让分支直接产出值，代码更紧凑，适合需要将分支结果映射为一个变量的场景。
