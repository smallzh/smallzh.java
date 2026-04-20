package smallzh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 基于 records.md 文档的单元测试
 * 覆盖：Record定义、自动生成方法、构造函数校验、Record与接口、密封类
 * 基于 sealed_classes.md 的密封类测试
 * 基于 pattern_matching.md 的模式匹配测试
 * 基于 switch_expressions.md 的switch表达式测试
 * 基于 text_blocks.md 的文本块测试
 * 基于 default_methods.md 的默认方法测试
 */
@DisplayName("Java新特性测试")
class JavaNewFeaturesTest {

    // ======================== Records ========================
    record PersonRecord(String name, int age) {}

    @Test
    @DisplayName("Record 基本创建与访问器")
    void testRecordBasic() {
        PersonRecord p = new PersonRecord("Alice", 30);
        assertEquals("Alice", p.name());
        assertEquals(30, p.age());
    }

    @Test
    @DisplayName("Record 自动生成 toString")
    void testRecordToString() {
        PersonRecord p = new PersonRecord("Bob", 25);
        assertTrue(p.toString().contains("Bob"));
        assertTrue(p.toString().contains("25"));
    }

    @Test
    @DisplayName("Record 自动生成 equals 和 hashCode")
    void testRecordEqualsHashCode() {
        PersonRecord a = new PersonRecord("Bob", 25);
        PersonRecord b = new PersonRecord("Bob", 25);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    // Record 带校验的compact构造器
    record UserRecord(String id, String name) {
        public UserRecord {
            if (id == null || id.isBlank()) {
                throw new IllegalArgumentException("id 不能为空");
            }
        }
    }

    @Test
    @DisplayName("Record compact 构造器校验")
    void testRecordCompactConstructor() {
        assertThrows(IllegalArgumentException.class, () -> new UserRecord("", "test"));
        UserRecord u = new UserRecord("u123", "Alice");
        assertEquals("u123", u.id());
    }

    // Record 实现接口
    interface Describable {
        String description();
    }

    record UserWithInterface(String id, String name) implements Describable {
        @Override
        public String description() { return id + " " + name; }
    }

    @Test
    @DisplayName("Record 实现接口")
    void testRecordImplementsInterface() {
        UserWithInterface u = new UserWithInterface("1", "Alice");
        assertEquals("1 Alice", u.description());
    }

    // 泛型 Record
    record Pair<T>(T first, T second) {}

    @Test
    @DisplayName("泛型 Record")
    void testGenericRecord() {
        Pair<String> p = new Pair<>("A", "B");
        assertEquals("A", p.first());
        assertEquals("B", p.second());
    }

    // ======================== Sealed Classes ========================
    sealed interface Shape permits CircleSealed, RectangleSealed {}

    static final class CircleSealed implements Shape {
        final double radius;
        CircleSealed(double r) { this.radius = r; }
    }

    static final class RectangleSealed implements Shape {
        final double width, height;
        RectangleSealed(double w, double h) { this.width = w; this.height = h; }
    }

    @Test
    @DisplayName("密封类与模式匹配")
    void testSealedClassPatternMatching() {
        Shape s = new CircleSealed(2.5);
        String desc;
        if (s instanceof CircleSealed c) {
            desc = "Circle, radius=" + c.radius;
        } else if (s instanceof RectangleSealed r) {
            desc = "Rectangle, " + r.width + "x" + r.height;
        } else {
            desc = "Unknown";
        }
        assertEquals("Circle, radius=2.5", desc);
    }

    @Test
    @DisplayName("密封类 instanceof 模式匹配")
    void testSealedClassInstanceof() {
        Shape s = new RectangleSealed(3, 4);
        String kind;
        if (s instanceof CircleSealed c) {
            kind = "圆";
        } else if (s instanceof RectangleSealed r) {
            kind = "矩形";
        } else {
            kind = "未知";
        }
        assertEquals("矩形", kind);
    }

    // ======================== Pattern Matching ========================
    @Test
    @DisplayName("instanceof 模式匹配")
    void testInstanceofPatternMatching() {
        Object obj = "hello";
        if (obj instanceof String s) {
            assertEquals(5, s.length());
        } else {
            fail("Should be String");
        }
    }

    // ======================== Switch Expressions ========================
    @Test
    @DisplayName("switch 表达式箭头语法")
    void testSwitchExpression() {
        int score = 7;
        String level = switch (score) {
            case 10, 9 -> "优秀";
            case 7, 8  -> "良好";
            default      -> "需要努力";
        };
        assertEquals("良好", level);
    }

    @Test
    @DisplayName("枚举 switch 表达式")
    void testEnumSwitchExpression() {
        enum Size { SMALL, MEDIUM, LARGE }
        Size s = Size.MEDIUM;
        String label = switch (s) {
            case SMALL -> "S";
            case MEDIUM -> "M";
            case LARGE -> "L";
        };
        assertEquals("M", label);
    }

    // ======================== Text Blocks ========================
    @Test
    @DisplayName("Text Block 基本用法")
    void testTextBlock() {
        String greeting = """
            你好，世界！
            这是一个 Text Block 的示例。
            """;
        assertTrue(greeting.contains("你好，世界！"));
        assertTrue(greeting.contains("Text Block"));
    }

    @Test
    @DisplayName("Text Block formatted 格式化")
    void testTextBlockFormatted() {
        String user = "小明";
        String message = """
            你好，%s！欢迎使用 Text Blocks。
            """.formatted(user);
        assertTrue(message.contains("小明"));
    }

    @Test
    @DisplayName("Text Block 与传统字符串对比")
    void testTextBlockComparison() {
        String htmlOld = "<div>\n" +
                         "  <p>Hello</p>\n" +
                         "</div>";
        String htmlBlock = """
            <div>
              <p>Hello</p>
            </div>
            """.stripIndent();
        // 两者内容逻辑等价（换行和缩进格式）
        assertTrue(htmlBlock.contains("<div>"));
        assertTrue(htmlBlock.contains("<p>Hello</p>"));
    }

    // ======================== Default Methods ========================
    interface VehicleDefault {
        void start();
        default void honk() { return; } // 需要在实现类中验证
    }

    static class CarDefault implements VehicleDefault {
        private boolean started = false;
        public void start() { started = true; }
        public boolean isStarted() { return started; }
    }

    @Test
    @DisplayName("默认方法可以被实现类使用")
    void testDefaultMethod() {
        CarDefault car = new CarDefault();
        car.start();
        assertTrue(car.isStarted());
    }

    // 默认方法冲突解决
    interface A {
        default String identify() { return "A"; }
    }
    interface B {
        default String identify() { return "B"; }
    }
    static class C implements A, B {
        @Override
        public String identify() { return A.super.identify(); }
    }

    @Test
    @DisplayName("默认方法冲突解决")
    void testDefaultMethodConflict() {
        C c = new C();
        assertEquals("A", c.identify());
    }

    // ======================== Lambda Expressions ========================
    @Test
    @DisplayName("Lambda 无参 Supplier")
    void testLambdaSupplier() {
        java.util.function.Supplier<String> greeting = () -> "Hello";
        assertEquals("Hello", greeting.get());
    }

    @Test
    @DisplayName("Lambda 双参 BinaryOperator")
    void testLambdaBinaryOperator() {
        java.util.function.BinaryOperator<Integer> add = (a, b) -> a + b;
        assertEquals(5, add.apply(2, 3));
    }

    @Test
    @DisplayName("Lambda 变量捕获（effectively final）")
    void testLambdaVariableCapture() {
        int factor = 3;
        java.util.function.Function<Integer, Integer> times = x -> x * factor;
        assertEquals(15, times.apply(5));
    }

    // ======================== Method References ========================
    @Test
    @DisplayName("静态方法引用 Integer::parseInt")
    void testStaticMethodReference() {
        java.util.function.Function<String, Integer> parse = Integer::parseInt;
        assertEquals(123, parse.apply("123"));
    }

    @Test
    @DisplayName("特定对象实例方法引用")
    void testInstanceMethodReference() {
        String prefix = "Hello";
        java.util.function.Supplier<String> upper = prefix::toUpperCase;
        assertEquals("HELLO", upper.get());
    }

    @Test
    @DisplayName("构造方法引用")
    void testConstructorReference() {
        java.util.function.Supplier<java.util.ArrayList<String>> listFactory = java.util.ArrayList::new;
        java.util.ArrayList<String> list = listFactory.get();
        assertTrue(list.isEmpty());
    }

    // ======================== var 局部变量类型推断 ========================
    @Test
    @DisplayName("var 局部变量类型推断")
    void testVarTypeInference() {
        var greeting = "你好";
        var nums = java.util.List.of(1, 2, 3);
        assertInstanceOf(String.class, greeting);
        assertEquals(3, nums.size());
    }

    // ======================== 集合工厂方法 ========================
    @Test
    @DisplayName("List.of / Set.of / Map.of 不可变集合")
    void testFactoryMethods() {
        var list = java.util.List.of("A", "B", "C");
        var set = java.util.Set.of(1, 2, 3);
        var map = java.util.Map.of("一", 1, "二", 2);

        assertEquals(3, list.size());
        assertEquals(3, set.size());
        assertEquals(2, map.size());
        assertThrows(UnsupportedOperationException.class, () -> list.add("D"));
    }
}