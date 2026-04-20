package smallzh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 基于 inheritance_polymorphism.md 文档的单元测试
 * 覆盖：继承、super、方法重写、多态、抽象类、final、Object类
 */
@DisplayName("继承与多态测试")
class InheritancePolymorphismTest {

    // --- 0x01 继承 ---
    static class Animal {
        public void move() {
            System.out.println("动物移动");
        }
    }

    static class DogInh extends Animal {
        public void bark() {
            System.out.println("狗狗汪汪叫");
        }
    }

    @Test
    @DisplayName("子类继承父类方法并添加新方法")
    void testInheritance() {
        DogInh d = new DogInh();
        d.move(); // 继承自 Animal
        d.bark(); // 子类新增
        // 若无异常则测试通过
    }

    // --- 0x02 super 关键字 ---
    static class AnimalSuper {
        protected String name;
        AnimalSuper(String name) { this.name = name; }
        public String speak() { return name + " 发声"; }
    }

    static class DogSuper extends AnimalSuper {
        DogSuper(String name) { super(name); }

        @Override
        public String speak() {
            return super.speak() + " | " + name + " 叫声：汪汪";
        }
    }

    @Test
    @DisplayName("super 调用父类构造器和方法")
    void testSuperKeyword() {
        DogSuper d = new DogSuper("小黑");
        String result = d.speak();
        assertTrue(result.contains("小黑 发声"));
        assertTrue(result.contains("汪汪"));
    }

    // --- 0x03 方法重写 Override ---
    static class AnimalOverride {
        public String speak() { return "动物发声"; }
    }

    static class CatOverride extends AnimalOverride {
        @Override
        public String speak() { return "喵喵"; }
    }

    @Test
    @DisplayName("方法重写：动态分派")
    void testMethodOverride() {
        AnimalOverride a = new CatOverride();
        assertEquals("喵喵", a.speak());
    }

    // --- 0x04 多态 ---
    static class AnimalPoly {
        public String sound() { return "动物声音"; }
    }

    static class DogPoly extends AnimalPoly {
        @Override
        public String sound() { return "汪汪"; }
    }

    static class CatPoly extends AnimalPoly {
        @Override
        public String sound() { return "喵喵"; }
    }

    @Test
    @DisplayName("多态：同一引用不同行为")
    void testPolymorphism() {
        AnimalPoly[] animals = { new DogPoly(), new CatPoly(), new AnimalPoly() };
        assertEquals("汪汪", animals[0].sound());
        assertEquals("喵喵", animals[1].sound());
        assertEquals("动物声音", animals[2].sound());
    }

    // --- 0x05 抽象类 ---
    static abstract class Vehicle {
        abstract String move();
        String description() { return "这是一个交通工具"; }
    }

    static class CarAbstract extends Vehicle {
        @Override
        String move() { return "小汽车在行驶"; }
    }

    @Test
    @DisplayName("抽象类与抽象方法")
    void testAbstractClass() {
        Vehicle v = new CarAbstract();
        assertEquals("小汽车在行驶", v.move());
        assertEquals("这是一个交通工具", v.description());
    }

    // --- 0x06 final 关键字 ---
    static final class FinalAnimal {
        String run() { return "跑步"; }
    }

    @Test
    @DisplayName("final 类不能被继承")
    void testFinalClass() {
        FinalAnimal fa = new FinalAnimal();
        assertEquals("跑步", fa.run());
    }

    static class AnimalWithFinal {
        public final String breathe() { return "呼吸中"; }
    }

    static class DogFinal extends AnimalWithFinal {
        // 不能覆盖 breathe()
    }

    @Test
    @DisplayName("final 方法不能被覆盖")
    void testFinalMethod() {
        DogFinal d = new DogFinal();
        assertEquals("呼吸中", d.breathe());
    }

    // --- 0x07 Object 类常用方法 ---
    static class PersonObj {
        private String name;

        PersonObj(String name) { this.name = name; }

        @Override
        public String toString() { return "Person{name='" + name + "'}"; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PersonObj)) return false;
            PersonObj p = (PersonObj) o;
            return name != null ? name.equals(p.name) : p.name == null;
        }

        @Override
        public int hashCode() { return name != null ? name.hashCode() : 0; }
    }

    @Test
    @DisplayName("Object 类 toString/equals/hashCode")
    void testObjectMethods() {
        PersonObj p1 = new PersonObj("Alice");
        PersonObj p2 = new PersonObj("Alice");
        assertTrue(p1.equals(p2));
        assertEquals(p1.hashCode(), p2.hashCode());
        assertTrue(p1.toString().contains("Alice"));
    }
}