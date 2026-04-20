package smallzh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 基于 data_type.md 文档的单元测试
 * 覆盖：基本数据类型、引用类型、类型转换、常量
 */
@DisplayName("数据类型测试")
class DataTypeTest {

    // --- 0x01 基本数据类型 ---
    @Test
    @DisplayName("byte 取值范围 -128 到 127")
    void testByteRange() {
        byte min = -128;
        byte max = 127;
        assertEquals(-128, min);
        assertEquals(127, max);
    }

    @Test
    @DisplayName("short 取值范围")
    void testShortRange() {
        short x = 32000;
        short y = -32000;
        assertEquals(32000, x);
        assertEquals(-32000, y);
    }

    @Test
    @DisplayName("int 类型常用赋值")
    void testIntUsage() {
        int age = 25;
        int score = 1000000;
        assertEquals(25, age);
        assertEquals(1000000, score);
    }

    @Test
    @DisplayName("long 类型需要 L 后缀")
    void testLongUsage() {
        long distance = 1234567890123L;
        assertEquals(1234567890123L, distance);
    }

    @Test
    @DisplayName("float 类型需要 f 后缀")
    void testFloatUsage() {
        float pi = 3.14f;
        assertTrue(Math.abs(pi - 3.14f) < 0.001);
    }

    @Test
    @DisplayName("double 为默认浮点类型")
    void testDoubleUsage() {
        double g = 9.81;
        assertTrue(Math.abs(g - 9.81) < 0.001);
    }

    @Test
    @DisplayName("char 16位无符号字符")
    void testCharUsage() {
        char ch = 'A';
        char next = (char) (ch + 1);
        assertEquals('A', ch);
        assertEquals('B', next);

        // Unicode 转义
        char heart = '\u2665';
        assertTrue(heart > 0);
    }

    @Test
    @DisplayName("boolean 只有 true 和 false")
    void testBooleanUsage() {
        boolean isActive = true;
        boolean isFinished = false;
        assertTrue(isActive);
        assertFalse(isFinished);
    }

    // --- 0x0A 引用类型 ---
    @Test
    @DisplayName("String 引用类型")
    void testStringReference() {
        String s = "Hello";
        assertEquals(5, s.length());
    }

    @Test
    @DisplayName("数组 引用类型")
    void testArrayReference() {
        int[] arr = {1, 2, 3};
        assertEquals(3, arr.length);
        assertEquals(1, arr[0]);
    }

    // --- 0x0B String 的使用 ---
    @Test
    @DisplayName("String 不可变与方法")
    void testStringMethods() {
        String name = "Alice";
        assertEquals(5, name.length());
        assertEquals("ALICE", name.toUpperCase());
        assertEquals("Alice", name); // 不可变，原字符串不变
    }

    // --- 0x0C 数组的使用 ---
    @Test
    @DisplayName("数组长度与元素访问")
    void testArrayUsage() {
        int[] nums = {1, 2, 3, 4, 5};
        assertEquals(5, nums.length);
        assertEquals(1, nums[0]);
        assertEquals(5, nums[4]);
    }

    // --- 0x0F 类型转换 ---
    @Test
    @DisplayName("自动类型转换：窄到宽")
    void testAutoTypeConversion() {
        byte a = 5;
        short b = a;
        int c = b;
        long d = c;
        assertEquals(5, a);
        assertEquals(5, b);
        assertEquals(5, c);
        assertEquals(5L, d);
    }

    @Test
    @DisplayName("强制类型转换可能溢出")
    void testNarrowingCast() {
        int x = 300;
        byte y = (byte) x;
        // 300 超出 byte 范围，结果非预期
        assertNotEquals(300, y);
    }

    @Test
    @DisplayName("int 到 byte 溢出具体值")
    void testNarrowingCastOverflow() {
        int large = 130;
        byte small = (byte) large;
        // 130 - 256 = -126
        assertEquals(-126, small);
    }

    // --- 0x12 变量声明与初始化 ---
    @Test
    @DisplayName("变量声明与初始化")
    void testVarDeclaration() {
        int a;
        a = 5;
        String s = "hello";
        assertEquals(5, a);
        assertEquals("hello", s);
    }

    // --- 0x13 常量 final ---
    @Test
    @DisplayName("final 常量不可修改")
    void testFinalVariable() {
        final int MAX_COUNT = 100;
        assertEquals(100, MAX_COUNT);
        // MAX_COUNT = 200; // 编译错误
    }
}