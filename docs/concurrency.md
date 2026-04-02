# OpenJDK17 多线程与并发知识库

本文面向初学者，聚焦在日常开发中常用的多线程和并发能力。每个知识点都给出可直接运行的代码示例，帮助理解与实践。

## 0x01 线程基础

- 通过 Thread 类创建线程
```java
// 方式一：继承 Thread
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Hello from MyThread");
    }
}

public class Demo {
    public static void main(String[] args) {
        MyThread t = new MyThread();
        t.start();
    }
}
```

- 通过 Runnable 接口创建线程
```java
class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Hello from MyRunnable");
    }
}

public class Demo {
    public static void main(String[] args) {
        Thread t = new Thread(new MyRunnable());
        t.start();
    }
}
```

- 使用 Lambda 简化（Runnable 的简写写法）
```java
public class Demo {
    public static void main(String[] args) {
        Thread t = new Thread(() -> System.out.println("Hello from lambda Runnable"));
        t.start();
    }
}
```

## 0x02 线程生命周期

- 简要要点：NEW -> RUNNABLE -> (BLOCKED/WAITING/TIMED_WAITING) -> TERMINATED
- 演示代码：创建、启动、等待完成
```java
public class LifecycleDemo {
    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(100);
                System.out.println("工作完成");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        System.out.println("状态（启动前）: " + t.getState());
        t.start();
        System.out.println("状态（启动后）: " + t.getState());
        t.join();
        System.out.println("状态（完成后）: " + t.getState());
    }
}
```

> 注：使用 join 可以等待线程执行结束，避免主线程过早结束。

## 0x03 线程同步

- 使用 synchronized 进行对象级别的互斥
```java
class Counter {
    private int count = 0;

    // 同步实例方法，等同于对 this 加锁
    public synchronized void incr() {
        count++;
    }

    public synchronized int get() {
        return count;
    }
}
```

- 使用同步块指定锁对象
```java
class SynchronizedBlockDemo {
    private final Object lock = new Object();
    private int value = 0;

    public void safeAdd() {
        synchronized (lock) {
            value++;
        }
    }

    public int read() {
        synchronized (lock) {
            return value;
        }
    }
}
```

## 0x04 线程通信

- wait、notify、notifyAll 的用法要在同步方法/块中使用
```java
class SharedBuffer {
    private int value = 0;
    private boolean available = false;

    public synchronized void produce(int v) throws InterruptedException {
        while (available) wait();
        value = v;
        available = true;
        notifyAll();
    }

    public synchronized int consume() throws InterruptedException {
        while (!available) wait();
        int v = value;
        available = false;
        notifyAll();
        return v;
    }
}
```

- 生产者与消费者示例
```java
public class WaitNotifyDemo {
    public static void main(String[] args) {
        SharedBuffer buf = new SharedBuffer();

        new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    buf.produce(i);
                    System.out.println("生产: " + i);
                }
            } catch (InterruptedException ignored) {}
        }).start();

        new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    int v = buf.consume();
                    System.out.println("消费: " + v);
                }
            } catch (InterruptedException ignored) {}
        }).start();
    }
}
```

## 0x05 线程池

- 使用 Executors 提供的固定线程池
```java
import java.util.concurrent.*;

public class ThreadPoolDemo {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(4);

        Future<String> f = executor.submit(() -> {
            Thread.sleep(100);
            return "任务完成";
        });

        System.out.println(f.get()); // 阻塞等待结果
        executor.shutdown();
    }
}
```

- 自定义 ThreadPoolExecutor 配置（演示用途）
```java
import java.util.concurrent.*;

public class CustomPoolDemo {
    public static void main(String[] args) {
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                2, 6,
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadFactory() {
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r, "CustomPool-Thread");
                        return t;
                    }
                });

        pool.submit(() -> System.out.println("自定义线程池工作"));
        pool.shutdown();
    }
}
```

## 0x06 Callable 与 Future

- Callable 允许任务有返回值，Future 获取结果
```java
import java.util.concurrent.*;

public class CallableDemo {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newCachedThreadPool();
        Callable<Integer> task = () -> {
            Thread.sleep(100);
            return 42;
        };

        Future<Integer> future = executor.submit(task);
        Integer result = future.get(); // 阻塞等待结果
        System.out.println("结果: " + result);
        executor.shutdown();
    }
}
```

## 0x07 Lock 接口与 ReentrantLock

- 使用显式锁，替代 synchronized
```java
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockDemo {
    private final Lock lock = new ReentrantLock();
    private int counter = 0;

    public void safeIncrement() {
        lock.lock();
        try {
            counter++;
        } finally {
            lock.unlock();
        }
    }

    public int getCounter() {
        return counter;
    }
}
```

- tryLock 示例
```java
class TryLockDemo {
    private final Lock lock = new ReentrantLock();

    public void tryWork() {
        if (lock.tryLock()) {
            try {
                System.out.println("成功获取锁，执行工作");
            } finally {
                lock.unlock();
            }
        } else {
            System.out.println("未获取到锁，稍后再试");
        }
    }
}
```

## 0x08 并发集合

- ConcurrentHashMap 示例
```java
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentMapDemo {
    public static void main(String[] args) {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        map.put("a", 1);
        map.putIfAbsent("b", 2);
        System.out.println(map.get("a"));
    }
}
```

- CopyOnWriteArrayList 示例
```java
import java.util.concurrent.CopyOnWriteArrayList;

public class CopyOnWriteDemo {
    public static void main(String[] args) {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
        list.add("x");
        for (String s : list) {
            System.out.println(s);
        }
    }
}
```

- 其他并发集合（如 ConcurrentSkipListSet、ConcurrentLinkedQueue 等）也常用，按需选用。

## 0x09 原子类

- AtomicInteger
```java
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicDemo {
    private final AtomicInteger count = new AtomicInteger(0);

    public void increment() {
        count.incrementAndGet();
    }

    public int value() {
        return count.get();
    }
}
```

- AtomicReference
```java
import java.util.concurrent.atomic.AtomicReference;

public class AtomicRefDemo {
    public static void main(String[] args) {
        AtomicReference<String> ref = new AtomicReference<>("initial");
        ref.set("updated");
        if (ref.compareAndSet("updated", "final")) {
            System.out.println("更新成功");
        }
        System.out.println("当前值: " + ref.get());
    }
}
```

## 0x0A CompletableFuture（Java 8+）

- 异步任务组合
```java
import java.util.concurrent.CompletableFuture;

public class CompletableFutureDemo {
    public static void main(String[] args) {
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(100); } catch (InterruptedException e) { }
            return "数据准备完成";
        });

        cf.thenApply(s -> s + "，进入下一步")
          .thenAccept(System.out::println);

        // 等待所有异步任务完成以避免退出
        cf.join();
    }
}
```

- 与 Executor 组合
```java
import java.util.concurrent.*;

public class CFAllOfDemo {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CompletableFuture<String> a = CompletableFuture.supplyAsync(() -> "A", executor);
        CompletableFuture<String> b = CompletableFuture.supplyAsync(() -> "B", executor);

        CompletableFuture<Void> all = CompletableFuture.allOf(a, b);
        all.thenRun(() -> {
            try {
                System.out.println(a.get() + b.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).join();

        executor.shutdown();
    }
}
```

## 0x0B 虚拟线程（Java 19 预览，Java 21 正式）

- 为什么是轻量级的线程
  - 虚拟线程与平台线程共享结构但调度更高效，适合并发 I/O 密集型场景；避免大规模线程栈开销。
  - 需要注意：部分阻塞性 API 在虚拟线程中可能表现不理想，需结合非阻塞 I/O 使用。

- 通过 Thread.startVirtualThread（Java 19 预览，Java 21 正式）
```java
public class VirtualThreadDemo {
    public static void main(String[] args) {
        Thread vt = Thread.startVirtualThread(() -> {
            System.out.println("这是一个虚拟线程");
            // 模拟 I/O 等耗时操作
        });
        try {
            vt.join();
        } catch (InterruptedException ignored) {}
    }
}
```

- 通过虚拟线程固定任务执行器（推荐在生产中使用）
```java
import java.util.concurrent.*;

public class VirtualThreadPoolDemo {
    public static void main(String[] args) throws Exception {
        // Java 21 及以上可用：为任务执行分配虚拟线程
        ExecutorService ex = Executors.newVirtualThreadPerTaskExecutor();

        Future<String> f = ex.submit(() -> {
            Thread.sleep(50); // 模拟并发 I/O
            return "虚拟线程任务完成";
        });

        System.out.println(f.get());
        ex.shutdown();
    }
}
```

- 另一种写法（Java 21+）：线程构建器
```java
public class BuilderVirtualThreadDemo {
    public static void main(String[] args) {
        Thread t = Thread.ofVirtual().start(() -> {
            System.out.println("通过 Thread.ofVirtual() 创建虚拟线程");
        });
        try { t.join(); } catch (InterruptedException ignored) {}
    }
}
```

> 注：虚拟线程在 OpenJDK 17 的知识库中属于未来趋势的内容，实际应用请参考当前 JDK 的版本与特性状态。

## 参考链接

- Java 并发基础与设计模式（公开教程与示例）
  - Java 官方教程：并发与多线程 http://docs.oracle.com/javase/tutorial/essential/concurrency/
  - Java SE 17 API 参考（Thread、Runnable、Lock、Future 等）https://docs.oracle.com/javase/17/docs/api/
  - java.util.concurrent API 参考（ConcurrentHashMap、CopyOnWriteArrayList、Executors、Future 等）https://docs.oracle.com/javase/17/docs/api/java/util/concurrent/package-summary.html
  - CompletableFuture 官方文档 https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html
- 线程池与并发集合的设计与最佳实践
  - Executor 框架概览 https://docs.oracle.com/javase/8/docs/technotes/guides/concurrency/executor.html
  - ThreadPoolExecutor 详细用法 https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadPoolExecutor.html
- 虚拟线程（Project Loom）
  - Project Loom 与虚拟线程概览（OpenJDK/文档）https://openjdk.java.net/jeps/425
  - Java 19/21 虚拟线程示例与迁移要点（社区与官方文档汇总）

如需我将以上内容整理成一个可直接粘贴到 MkDocs 的文档文件（如 docs/concurrency.md），我也可以按你的项目结构输出。
