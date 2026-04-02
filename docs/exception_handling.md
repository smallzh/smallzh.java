# OpenJDK17 的异常处理知识点汇总

本文面向初学者，聚焦实际使用场景，提供简洁实用的示例。内容覆盖异常体系、基本流程、自定义异常、try-with-resources 以及常见异常处理要点。

## 0x01 异常体系结构

- Throwable 是异常体系的根类，Error 表示 JVM 发生的严重错误，Exception 是可捕获的异常的基类，RuntimeException 属于未检查异常（不强制捕获或声明），其他 Exception 为受检异常（需要在方法签名或调用处处理）。
- 示例展示不同层级以及受检/未检的区别。

```java
public class ThrowableHierarchy {
    public static void main(String[] args) {
        Throwable t = new Throwable("根异常");
        Error e = new StackOverflowError();
        Exception ex = new Exception("普通异常");
        RuntimeException re = new NullPointerException("空指针异常");

        System.out.println(t.getClass().getSimpleName());
        System.out.println(e.getClass().getSimpleName());
        System.out.println(ex.getClass().getSimpleName());
        System.out.println(re.getClass().getSimpleName());
    }
}
```

- 区分示例（受检与未检的区别）：

```java
import java.io.IOException;

// 受检异常示例：需要在方法签名或调用处处理
class CheckedDemo {
    void read() throws IOException { /* 可能抛出 IOException */ }
}

// 未检异常示例：运行时抛出，不强制捕获
class UncheckedDemo {
    void divide(int a, int b) {
        if (b == 0) throw new ArithmeticException("除数为0");
    }
}
```

参考要点：受检异常必须显式处理或向上抛出；未检异常通常是编程错误或不可预期的边界情况。

## 0x02 try-catch-finally 语句

- 说明：try 包含可能抛出异常的代码，catch 捕获特定异常并处理，finally 始终执行（用于清理资源等）。
- 支持多重捕获（Java 7+）。

```java
public class TryCatchFinallyDemo {
    public static void main(String[] args) {
        try {
            int[] arr = {1, 2, 3};
            int x = arr[5]; // 将抛出 ArrayIndexOutOfBoundsException
        } catch (ArrayIndexOutOfBoundsException | NullPointerException ex) {
            System.out.println("捕获到异常: " + ex);
        } finally {
            System.out.println(" finally 块总是执行");
        }
    }
}
```

- finally 的一个常见用法是确保资源清理；如果在 try 或 catch 中使用 return，finally 仍会执行。

```java
public class FinallyReturnDemo {
    public static String test() {
        try {
            return "try";
        } finally {
            System.out.println("Finally 执行");
        }
    }
    public static void main(String[] args) {
        System.out.println(test());
    }
}
```

## 0x03 throw 与 throws 关键字

- throw：用于抛出一个异常对象。
- throws：用于在方法签名中声明该方法可能抛出的异常类型，调用方需要处理或继续抛出。

- 通过自定义的受检异常来演示 throws 的使用：

```java
// 自定义受检异常（需在方法签名中声明）
class MyCheckedException extends Exception {
    public MyCheckedException(String msg) { super(msg); }
}

class ThrowThrowsDemo {
    void check(int v) throws MyCheckedException {
        if (v < 0) throw new MyCheckedException("值不能为负");
    }

    void caller() throws MyCheckedException {
        check(-1);
    }
}
```

- 未检异常示例（不需要 throws）：

```java
class UncheckedThrowDemo {
    void check(int v) {
        if (v < 0) throw new IllegalArgumentException("负数非法");
    }
}
```

- 要点总结：throw 用于抛出具体异常对象，throws 声明方法可能抛出的异常类型，帮助调用方决定处理策略。

## 0x04 自定义异常

- 根据需要选择继承 Exception（受检）或 RuntimeException（未检）。

```java
// 受检自定义异常
class MyCheckedException extends Exception {
    public MyCheckedException(String msg) { super(msg); }
}

// 未检自定义异常
class MyUncheckedException extends RuntimeException {
    public MyUncheckedException(String msg) { super(msg); }
}
```

- 使用示例：

```java
public class CustomExceptionDemo {
    static void require(boolean ok) throws MyCheckedException {
        if (!ok) throw new MyCheckedException("条件不满足");
    }

    static void quick(boolean ok) {
        if (!ok) throw new MyUncheckedException("快速失败");
    }

    public static void main(String[] args) {
        try {
            require(false);
        } catch (MyCheckedException e) {
            e.printStackTrace();
        }
        quick(false);
    }
}
```

- 设计小贴士：受检异常用于需要调用方恢复的情景，未检异常用于编程错误或不可恢复的条件。

## 0x05 try-with-resources（Java 7+）

- 说明：try-with-resources 能在代码块结束时自动关闭实现 AutoCloseable 的资源，简化清理工作。
- 常见用法（读取文件示例）：

```java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TryWithResourcesDemo {
    public static void main(String[] args) {
        try (BufferedReader br = new BufferedReader(new FileReader("input.txt"))) {
            String line = br.readLine();
            System.out.println(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

- 自定义资源示例：

```java
class MyResource implements AutoCloseable {
    void open() { System.out.println("资源已打开"); }
    void doWork() { System.out.println("正在使用资源"); }
    @Override
    public void close() { System.out.println("资源已关闭"); }
}

public class CustomResourceDemo {
    public static void main(String[] args) {
        try (MyResource r = new MyResource()) {
            r.open();
            r.doWork();
        }
    }
}
```

- 要点：在 try 块内声明的资源会在块结束时自动 close，减少显式 finally 的需要。

## 0x06 常见异常类型及处理

- 常见异常及处理思路：尽量捕获最具体的异常、避免吞掉异常、对输入进行校验、提前防御性检查、在必要时抛出有意义的错误信息。

- 示例：常见异常及处理策略

```java
public class CommonExceptionsDemo {
    public static void main(String[] args) {
        // 1) NullPointerException
        String s = null;
        try {
            int len = s.length();
        } catch (NullPointerException ex) {
            System.out.println("空指针错误：" + ex.getMessage());
        }

        // 2) ArrayIndexOutOfBoundsException
        int[] arr = {1, 2, 3};
        try {
            int x = arr[3];
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("下标越界：" + ex.getMessage());
        }

        // 3) NumberFormatException
        try {
            int v = Integer.parseInt("abc");
        } catch (NumberFormatException ex) {
            System.out.println("数字格式错误：" + ex.getMessage());
        }

        // 4) IOException（使用 try-with-resources 演示）
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("input.txt"))) {
            br.readLine();
        } catch (java.io.IOException ex) {
            System.out.println("IO 错误：" + ex.getMessage());
        }

        // 5) ClassNotFoundException
        try {
            Class.forName("com.example.NoClass");
        } catch (ClassNotFoundException ex) {
            System.out.println("类未找到：" + ex.getMessage());
        }
    }
}
```

- 通用处理原则
  - 先做入参校验，尽量在方法入口就发现问题并给出清晰信息
  - 捕获具体异常，不要使用 catch (Exception) 或 catch (Throwable)
  - 适时将异常信息封装为业务含义更清晰的异常并传递上层
  - 日志记录异常信息时，避免输出敏感信息

参考链接（官方文档与教程）
- The Java Tutorials — Exceptions: https://docs.oracle.com/javase/tutorial/essential/exceptions/index.html
- Throwable 类及层级（API 参考）: https://docs.oracle.com/javase/17/docs/api/java/lang/Throwable.html
- AutoCloseable 与 try-with-resources（API 参考）: https://docs.oracle.com/javase/17/docs/api/java/lang/AutoCloseable.html
- RuntimeException（API 参考）: https://docs.oracle.com/javase/17/docs/api/java/lang/RuntimeException.html

如需继续扩展本知识库的其他主题（例如并发中的异常处理、异步编程中的异常传递等），请告知我将按同样风格继续扩展。
