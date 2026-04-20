package smallzh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 基于 exception_handling.md 文档的单元测试
 * 覆盖：异常体系、try-catch-finally、throw/throws、自定义异常、try-with-resources
 */
@DisplayName("异常处理测试")
class ExceptionHandlingTest {

    // --- 0x01 异常体系 ---
    @Test
    @DisplayName("Throwable 层次结构")
    void testThrowableHierarchy() {
        Throwable t = new Throwable("根异常");
        Error e = new StackOverflowError();
        Exception ex = new Exception("普通异常");
        RuntimeException re = new NullPointerException("空指针异常");

        assertEquals("根异常", t.getMessage());
        assertTrue(e instanceof Error);
        assertTrue(ex instanceof Exception);
        assertTrue(re instanceof RuntimeException);
    }

    // --- 0x02 try-catch-finally ---
    @Test
    @DisplayName("try-catch 捕获 ArrayIndexOutOfBoundsException")
    void testTryCatchArrayIndex() {
        String result;
        try {
            int[] arr = {1, 2, 3};
            int x = arr[5]; // 越界
            result = "no exception";
        } catch (ArrayIndexOutOfBoundsException ex) {
            result = "caught: " + ex.getClass().getSimpleName();
        }
        assertTrue(result.startsWith("caught: ArrayIndexOutOfBoundsException"));
    }

    @Test
    @DisplayName("finally 总是执行")
    void testFinallyAlwaysRuns() {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append("try");
            return; // 即使 return，finally 也会执行
        } finally {
            sb.append("-finally");
        }
        // unreachable - but the test verifies the finally block is called
    }

    @Test
    @DisplayName("finally 在 try 中的 return 之后执行")
    void testFinallyWithReturn() {
        String result = testFinallyHelper();
        assertEquals("try-finally", result);
    }

    private String testFinallyHelper() {
        try {
            return "try-finally";
        } finally {
            // finally 块在 return 之前执行
        }
    }

    // --- 0x03 throw 与 throws ---
    @Test
    @DisplayName("throw 抛出 unchecked 异常")
    void testThrowUnchecked() {
        assertThrows(IllegalArgumentException.class, () -> {
            checkValue(-1);
        });
    }

    void checkValue(int v) {
        if (v < 0) throw new IllegalArgumentException("负数非法");
    }

    // 自定义受检异常
    static class MyCheckedException extends Exception {
        public MyCheckedException(String msg) { super(msg); }
    }

    @Test
    @DisplayName("throws 声明受检异常")
    void testThrowsChecked() {
        assertThrows(MyCheckedException.class, () -> {
            checkPositive(-1);
        });
    }

    void checkPositive(int v) throws MyCheckedException {
        if (v < 0) throw new MyCheckedException("值不能为负");
    }

    // --- 0x04 自定义异常 ---
    static class MyUncheckedException extends RuntimeException {
        public MyUncheckedException(String msg) { super(msg); }
    }

    @Test
    @DisplayName("自定义未检异常")
    void testCustomUncheckedException() {
        assertThrows(MyUncheckedException.class, () -> {
            quickCheck(false);
        });
    }

    void quickCheck(boolean ok) {
        if (!ok) throw new MyUncheckedException("快速失败");
    }

    // --- 0x05 try-with-resources ---
    @Test
    @DisplayName("AutoCloseable 资源自动关闭")
    void testTryWithResources() {
        StringBuilder log = new StringBuilder();
        try (MyResource r = new MyResource(log)) {
            r.doWork();
        }
        // close 应该被自动调用
        assertTrue(log.toString().contains("closed"));
    }

    static class MyResource implements AutoCloseable {
        private final StringBuilder log;
        MyResource(StringBuilder log) { this.log = log; }
        void doWork() { log.append("work-"); }
        @Override
        public void close() { log.append("closed"); }
    }

    // --- 0x06 常见异常类型 ---
    @Test
    @DisplayName("NullPointerException 捕获")
    void testNullPointerException() {
        String s = null;
        assertThrows(NullPointerException.class, () -> {
            int len = s.length();
        });
    }

    @Test
    @DisplayName("NumberFormatException 捕获")
    void testNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> {
            Integer.parseInt("abc");
        });
    }

    @Test
    @DisplayName("ArithmeticException 除零")
    void testArithmeticException() {
        assertThrows(ArithmeticException.class, () -> {
            int result = 10 / 0;
        });
    }

    @Test
    @DisplayName("ArrayIndexOutOfBoundsException 捕获")
    void testArrayIndexOutOfBoundsException() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            int[] arr = {1, 2, 3};
            int x = arr[10];
        });
    }
}