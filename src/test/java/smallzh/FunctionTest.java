package smallzh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 基于 function.md 文档的单元测试
 * 覆盖：方法定义与调用、参数（值传递、可变参数）、方法重载、递归、构造方法
 */
@DisplayName("方法测试")
class FunctionTest {

    // --- 0x01 方法定义与调用 ---
    static int add(int a, int b) {
        return a + b;
    }

    @Test
    @DisplayName("静态方法调用")
    void testStaticMethodCall() {
        assertEquals(5, FunctionTest.add(2, 3));
    }

    // --- 0x02 方法参数 ---
    static void modifyPrimitive(int p) { p = 999; }

    static void modifyArray(int[] a) {
        if (a != null && a.length > 0) a[0] = 99;
    }

    static String joinAll(String... items) {
        if (items.length == 0) return "(empty)";
        StringBuilder sb = new StringBuilder();
        for (String s : items) sb.append(s);
        return sb.toString();
    }

    @Test
    @DisplayName("基本类型值传递：不影响实参")
    void testPrimitivePassByValue() {
        int x = 5;
        modifyPrimitive(x);
        assertEquals(5, x); // 不受影响
    }

    @Test
    @DisplayName("引用类型传递：可能影响实参指向的对象")
    void testReferencePassByValue() {
        int[] arr = {1, 2, 3};
        modifyArray(arr);
        assertEquals(99, arr[0]); // 被修改了
    }

    @Test
    @DisplayName("可变参数")
    void testVarargs() {
        assertEquals("ABC", joinAll("A", "B", "C"));
        assertEquals("(empty)", joinAll());
    }

    // --- 0x03 方法重载 ---
    static int addOverload(int a, int b) { return a + b; }
    static double addOverload(double a, double b) { return a + b; }
    static String addOverload(String a, String b) { return a + b; }

    @Test
    @DisplayName("方法重载：不同参数类型")
    void testMethodOverloading() {
        assertEquals(3, addOverload(1, 2));
        assertEquals(4.0, addOverload(1.5, 2.5));
        assertEquals("AB", addOverload("A", "B"));
    }

    // --- 0x04 递归方法 ---
    static long factorial(int n) {
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }

    @Test
    @DisplayName("递归阶乘")
    void testRecursion() {
        assertEquals(120, factorial(5));
        assertEquals(1, factorial(0));
        assertEquals(1, factorial(1));
    }

    // --- 0x05 构造方法 ---
    static class PersonConstruct {
        private String name;
        private int age;

        public PersonConstruct() {
            this("未知", 0);
        }

        public PersonConstruct(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age + "}";
        }
    }

    @Test
    @DisplayName("构造方法重载与 this() 调用")
    void testConstructorOverload() {
        PersonConstruct p1 = new PersonConstruct();
        PersonConstruct p2 = new PersonConstruct("李四", 28);
        assertTrue(p1.toString().contains("未知"));
        assertTrue(p2.toString().contains("李四"));
    }
}