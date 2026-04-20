package smallzh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 基于 concurrency.md 文档的单元测试
 * 覆盖：线程基础、synchronized、Lock、原子类、ConcurrentHashMap、CompletableFuture
 */
@DisplayName("并发编程测试")
class ConcurrencyTest {

    // --- 0x01 线程基础 ---
    @Test
    @DisplayName("Thread 创建与启动")
    void testThreadCreation() throws InterruptedException {
        StringBuilder sb = new StringBuilder();
        Thread t = new Thread(() -> sb.append("Hello from lambda Runnable"));
        t.start();
        t.join(1000);
        assertEquals("Hello from lambda Runnable", sb.toString());
    }

    // --- 0x03 synchronized 同步 ---
    static class SyncCounter {
        private int count = 0;

        public synchronized void incr() {
            count++;
        }

        public synchronized int get() {
            return count;
        }
    }

    @Test
    @DisplayName("synchronized 保证线程安全")
    void testSynchronized() throws InterruptedException {
        SyncCounter counter = new SyncCounter();
        int threads = 10;
        int increments = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads * increments; i++) {
            executor.submit(counter::incr);
        }
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        assertEquals(threads * increments, counter.get());
    }

    // --- 0x07 Lock 接口与 ReentrantLock ---
    static class LockCounter {
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

    @Test
    @DisplayName("ReentrantLock 保证线程安全")
    void testReentrantLock() throws InterruptedException {
        LockCounter counter = new LockCounter();
        ExecutorService executor = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 100; i++) {
            executor.submit(counter::safeIncrement);
        }
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        assertEquals(100, counter.getCounter());
    }

    // --- 0x08 并发集合 ---
    @Test
    @DisplayName("ConcurrentHashMap 基本操作")
    void testConcurrentHashMap() {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        map.put("a", 1);
        map.putIfAbsent("b", 2);
        assertEquals(1, map.get("a"));
        assertEquals(2, map.get("b"));
    }

    @Test
    @DisplayName("CopyOnWriteArrayList 安全遍历")
    void testCopyOnWriteArrayList() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
        list.add("x");
        list.add("y");
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s);
        }
        assertEquals("xy", sb.toString());
    }

    // --- 0x09 原子类 ---
    @Test
    @DisplayName("AtomicInteger 原子递增")
    void testAtomicInteger() throws InterruptedException {
        AtomicInteger count = new AtomicInteger(0);
        ExecutorService executor = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 100; i++) {
            executor.submit(count::incrementAndGet);
        }
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        assertEquals(100, count.get());
    }

    @Test
    @DisplayName("AtomicReference CAS 操作")
    void testAtomicReference() {
        AtomicReference<String> ref = new AtomicReference<>("initial");
        ref.set("updated");
        assertTrue(ref.compareAndSet("updated", "final"));
        assertEquals("final", ref.get());
    }

    // --- 0x0A CompletableFuture ---
    @Test
    @DisplayName("CompletableFuture 异步组合")
    void testCompletableFuture() throws Exception {
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
            return "数据准备完成";
        });
        String result = cf.thenApply(s -> s + "，进入下一步").get(5, TimeUnit.SECONDS);
        assertEquals("数据准备完成，进入下一步", result);
    }

    @Test
    @DisplayName("CompletableFuture allOf 组合多个异步任务")
    void testCompletableFutureAllOf() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CompletableFuture<String> a = CompletableFuture.supplyAsync(() -> "A", executor);
        CompletableFuture<String> b = CompletableFuture.supplyAsync(() -> "B", executor);
        CompletableFuture<Void> all = CompletableFuture.allOf(a, b);
        all.get(5, TimeUnit.SECONDS);
        assertEquals("A", a.get());
        assertEquals("B", b.get());
        executor.shutdown();
    }

    // --- 0x02 线程生命周期 ---
    @Test
    @DisplayName("线程状态 NEW -> RUNNABLE -> TERMINATED")
    void testThreadLifecycle() throws InterruptedException {
        Thread t = new Thread(() -> {
            try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });
        assertEquals(Thread.State.NEW, t.getState());
        t.start();
        // RUNNABLE or TIMED_WAITING depending on timing
        Thread.State state = t.getState();
        assertTrue(state == Thread.State.RUNNABLE || state == Thread.State.TIMED_WAITING);
        t.join(2000);
        assertEquals(Thread.State.TERMINATED, t.getState());
    }
}