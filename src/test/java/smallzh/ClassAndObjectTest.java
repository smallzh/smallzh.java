package smallzh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 基于 class_and_object.md 文档的单元测试
 * 覆盖：类定义、对象创建、访问修饰符、static、this关键字
 */
@DisplayName("类与对象测试")
class ClassAndObjectTest {

    // --- 0x01 类的定义 ---
    static class Person {
        String name;
        int age;

        void sayHello() {
            System.out.println("你好，我是 " + name);
        }
    }

    @Test
    @DisplayName("创建对象并访问字段")
    void testCreateObject() {
        Person p = new Person();
        p.name = "小明";
        p.age = 18;
        assertEquals("小明", p.name);
        assertEquals(18, p.age);
    }

    // --- 0x03 成员变量与成员方法 ---
    static class Counter {
        int value;

        void increment() {
            value++;
        }
    }

    @Test
    @DisplayName("成员变量独立，成员方法修改实例状态")
    void testMemberVariableIndependent() {
        Counter c1 = new Counter();
        Counter c2 = new Counter();
        c1.increment();
        c1.increment();
        assertEquals(2, c1.value);
        assertEquals(0, c2.value); // c2 独立
    }

    // --- 0x04 访问修饰符 ---
    static class AccessDemo {
        public int publicVar;
        private int privateVar;
        protected int protectedVar;
        int defaultVar; // package-private

        public void setPrivateVar(int v) { privateVar = v; }
        public int getPrivateVar() { return privateVar; }
    }

    @Test
    @DisplayName("访问修饰符：public 和 private")
    void testAccessModifiers() {
        AccessDemo obj = new AccessDemo();
        obj.publicVar = 10;
        obj.setPrivateVar(20);
        assertEquals(10, obj.publicVar);
        assertEquals(20, obj.getPrivateVar());
    }

    // --- 0x05 静态成员 ---
    static class StaticCounter {
        public static int count = 0;

        public StaticCounter() {
            count++;
        }
    }

    @Test
    @DisplayName("static 成员属于类本身")
    void testStaticMember() {
        StaticCounter.count = 0; // 重置
        new StaticCounter();
        new StaticCounter();
        assertEquals(2, StaticCounter.count);
    }

    static class MathUtil {
        public static int add(int a, int b) {
            return a + b;
        }
    }

    @Test
    @DisplayName("静态方法通过类名调用")
    void testStaticMethod() {
        assertEquals(5, MathUtil.add(2, 3));
    }

    // --- 0x06 this 关键字 ---
    static class PersonWithThis {
        private String name;

        PersonWithThis(String name) {
            this.name = name;
        }

        String getName() {
            return this.name;
        }
    }

    @Test
    @DisplayName("this 引用当前对象")
    void testThisKeyword() {
        PersonWithThis p = new PersonWithThis("小红");
        assertEquals("小红", p.getName());
    }
}