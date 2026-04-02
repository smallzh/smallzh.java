# OpenJDK17 数据类型入门

本篇文档按知识点组织，包含实用的代码示例，帮助初学者在实际场景中理解和使用 Java 的数据类型。

## 0x01 基本数据类型概览
Java 的基本数据类型（原始类型）共 8 种，分为整型、浮点型、字符型和布尔型。下方示例演示常见声明和初始化。

```java
public class TypesDemo {
    public static void main(String[] args) {
        byte b = 127;          // -128 ~ 127
        short s = 32000;         // -32768 ~ 32767
        int i = 123456789;       // -2^31 ~ 2^31-1
        long l = 1234567890123L; // 需要后缀 L 表示常量为 long
        float f = 3.14f;         // 需要后缀 f 表示常量为 float
        double d = 6.28;           // 默认双精度
        char c = 'A';            // 16 位字符
        boolean flag = true;     // true/false
        System.out.println(b + " " + s + " " + i + " " + l + " " + f + " " + d + " " + c + " " + flag);
    }
}
```

## 0x02 byte 的使用
byte 是最小的整型，内存占用少，适合处理二进制数据或网络协议中的字节流。

```java
public class ByteDemo {
    public static void main(String[] args) {
        byte a = -128;
        byte b = 127;
        System.out.println("a=" + a + ", b=" + b);
    }
}
```

## 0x03 short 的使用
short 适合用于需要节省内存的整数场景，取值范围比 byte 大。

```java
public class ShortDemo {
    public static void main(String[] args) {
        short x = 32000;
        short y = -32000;
        System.out.println("x=" + x + ", y=" + y);
    }
}
```

## 0x04 int 的使用
int 是最常用的整型，32 位，范围较大。

```java
public class IntDemo {
    public static void main(String[] args) {
        int age = 25;
        int score = 1000000;
        System.out.println("age=" + age + ", score=" + score);
    }
}
```

## 0x05 long 的使用
long 用于很大的整数，常量要以 L 结尾以区分 int。

```java
public class LongDemo {
    public static void main(String[] args) {
        long distance = 1234567890123L;
        System.out.println("distance=" + distance);
    }
}
```

## 0x06 float 的使用
float 用于单精度浮点数，常用于占用内存较低且精度需求不高的场景。

```java
public class FloatDemo {
    public static void main(String[] args) {
        float pi = 3.14f;
        System.out.println("pi=" + pi);
    }
}
```

## 0x07 double 的使用
double 为双精度浮点数，默认类型，精度通常更高。

```java
public class DoubleDemo {
    public static void main(String[] args) {
        double g = 9.81;
        System.out.println("g=" + g);
    }
}
```

## 0x08 char 的使用
char 是 16 位无符号的字符，常用于表示单个字符。

```java
public class CharDemo {
    public static void main(String[] args) {
        char ch = 'A';
        char next = (char) (ch + 1);
        System.out.println("ch=" + ch + ", next=" + next);
        
        // 使用 Unicode 转义
        char heart = '\u2665';
        System.out.println("heart=" + heart);
    }
}
```

## 0x09 boolean 的使用
boolean 只有两种值：true 或 false。适合控制流程和条件判断。

```java
public class BooleanDemo {
    public static void main(String[] args) {
        boolean isActive = true;
        boolean isFinished = false;
        System.out.println("isActive=" + isActive + ", isFinished=" + isFinished);
    }
}
```

## 0x0A 引用类型概览
引用类型保存对象的引用，可能为 null。包含 String、数组、类、接口等。

```java
public class ReferenceDemo {
    public static void main(String[] args) {
        String s = "Hello";
        int[] arr = {1, 2, 3};
        Object obj = new int[]{4, 5, 6};
        System.out.println("s=" + s + ", arr.length=" + arr.length);
    }
}
```

## 0x0B String 的使用
String 是引用类型，表示文本序列，具有丰富的方法，但不可变。

```java
public class StringDemo {
    public static void main(String[] args) {
        String name = "Alice";
        int len = name.length();
        String upper = name.toUpperCase();
        System.out.println(name + " 长度=" + len + " 大写=" + upper);
    }
}
```

## 0x0C 数组的使用
数组是同一类型元素的有序集合，长度固定。

```java
public class ArrayDemo {
    public static void main(String[] args) {
        int[] nums = {1, 2, 3, 4, 5};
        System.out.println("长度=" + nums.length + ", 第一个元素=" + nums[0]);
    }
}
```

## 0x0D 自定义类的使用
通过自定义类来组织数据和行为。

```java
public class Person {
    String name;
    int age;
    Person(String name, int age) { this.name = name; this.age = age; }
}

public class ClassDemo {
    public static void main(String[] args) {
        Person p = new Person("Bob", 28);
        System.out.println("姓名=" + p.name + ", 年龄=" + p.age);
    }
}
```

## 0x0E 接口的使用
接口定义能力，多个实现类遵循统一契约。

```java
interface Printable {
    void printInfo();
}

class User implements Printable {
    String username;
    User(String username) { this.username = username; }
    @Override
    public void printInfo() {
        System.out.println("用户: " + username);
    }
}

public class InterfaceDemo {
    public static void main(String[] args) {
        Printable p = new User("guest");
        p.printInfo();
    }
}
```

## 0x0F 类型转换概览
类型转换分为自动类型转换和强制类型转换。注意可能丢失信息或引发异常。

```java
public class CastOverview {
    public static void main(String[] args) {
        // 自动类型转换（更窄到更宽）
        byte b = 10;
        short s = b;          // 自动提升为 short
        int i = s;              // 自动提升为 int

        // 强制类型转换需要显式写出
        int large = 130;
        byte small = (byte) large; // 可能得到 -126 或其它数值，注意溢出
        System.out.println("i=" + i + ", small=" + small);
    }
}
```

## 0x10 自动类型转换
自动提升发生在兼容范围内的类型之间，避免显式转换带来的繁琐。

```java
public class AutoPromotion {
    public static void main(String[] args) {
        byte a = 5;
        short b = a;   // 自动提升
        int c = b;     // 自动提升
        long d = c;      // 自动提升
        System.out.println("a=" + a + ", b=" + b + ", c=" + c + ", d=" + d);
    }
}
```

## 0x11 强制类型转换
需要显式写出强制转换，且可能导致数据丢失或溢出。

```java
public class NarrowingCast {
    public static void main(String[] args) {
        int x = 300;
        byte y = (byte) x; // 越界会产生非预期的结果
        System.out.println("x=" + x + ", y=" + y);
    }
}
```

## 0x12 变量声明与初始化
变量在使用前必须声明，初始化时可在同一语句或分开进行。

```java
public class VarDeclaration {
    public static void main(String[] args) {
        int a;          // 声明
        a = 5;          // 初始化
        String s = "hello"; // 声明并初始化
        System.out.println("a=" + a + ", s=" + s);
    }
}
```

## 0x13 常量（final 关键字）
使用 final 声明的变量不可再次赋值，常用于定义常量。

```java
public class FinalDemo {
    public static void main(String[] args) {
        final int MAX_COUNT = 100;
        // MAX_COUNT = 200; // 编译错误：不能为常量重新赋值
        System.out.println("MAX_COUNT=" + MAX_COUNT);
    }
}
```

## 参考链接
- Oracle 官方数据类型教程（Java 基本类型、包装类、赋值与转换等）https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html
- Java 语言规范（相关类型与转换规则的正式描述）https://docs.oracle.com/javase/specs/jls/se17/html/index.html
