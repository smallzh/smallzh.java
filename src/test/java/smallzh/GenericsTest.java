package smallzh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 基于 generics.md 文档的单元测试
 * 覆盖：泛型类、泛型方法、泛型接口、有界类型参数、通配符、类型擦除
 */
@DisplayName("泛型测试")
class GenericsTest {

    // --- 0x02 泛型类 ---
    static class Box<T> {
        private T value;

        public Box() {}
        public Box(T value) { this.value = value; }

        public void set(T value) { this.value = value; }
        public T get() { return value; }
    }

    @Test
    @DisplayName("泛型类 Box<String>")
    void testGenericBoxString() {
        Box<String> stringBox = new Box<>("hello");
        assertEquals("hello", stringBox.get());
    }

    @Test
    @DisplayName("泛型类 Box<Integer>")
    void testGenericBoxInteger() {
        Box<Integer> intBox = new Box<>(123);
        assertEquals(123, intBox.get());
    }

    // --- 0x03 泛型方法 ---
    @Test
    @DisplayName("泛型方法 max")
    void testGenericMethodMax() {
        Integer maxVal = max(10, 20);
        assertEquals(20, maxVal);
        String maxStr = max("apple", "banana");
        assertEquals("banana", maxStr);
    }

    static <T extends Comparable<T>> T max(T a, T b) {
        return a.compareTo(b) >= 0 ? a : b;
    }

    // --- 0x04 泛型接口 ---
    interface Converter<T, R> {
        R convert(T input);
    }

    static class StringToIntegerConverter implements Converter<String, Integer> {
        @Override
        public Integer convert(String input) {
            return Integer.valueOf(input);
        }
    }

    @Test
    @DisplayName("泛型接口 Converter")
    void testGenericInterface() {
        Converter<String, Integer> conv = new StringToIntegerConverter();
        assertEquals(123, conv.convert("123"));
    }

    // --- 0x05 类型参数命名约定 ---
    static class Pair<K, V> {
        private K key;
        private V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() { return key; }
        public V getValue() { return value; }
    }

    @Test
    @DisplayName("Pair<K, V> 泛型类")
    void testPairGeneric() {
        Pair<String, Integer> pair = new Pair<>("age", 25);
        assertEquals("age", pair.getKey());
        assertEquals(25, pair.getValue());
    }

    // --- 0x06 有界类型参数 ---
    static class NumericBox<T extends Number> {
        private T value;
        public NumericBox(T value) { this.value = value; }
        public double doubleValue() { return value.doubleValue(); }
    }

    @Test
    @DisplayName("有界类型参数 NumericBox<Integer>")
    void testBoundedTypeParameter() {
        NumericBox<Integer> intBox = new NumericBox<>(42);
        assertEquals(42.0, intBox.doubleValue());
    }

    // --- 0x07 通配符 ---
    @Test
    @DisplayName("上界通配符 ? extends Number 只读")
    void testUpperBoundedWildcard() {
        List<Integer> intList = Arrays.asList(10, 20, 30);
        Number n = readOnly(intList);
        assertNotNull(n);
    }

    static Number readOnly(List<? extends Number> list) {
        return list.get(0);
    }

    @Test
    @DisplayName("下界通配符 ? super Integer 可写")
    void testLowerBoundedWildcard() {
        List<Number> numbers = new ArrayList<>();
        addNumbers(numbers);
        assertEquals(2, numbers.size());
    }

    static void addNumbers(List<? super Integer> list) {
        list.add(1);
        list.add(2);
    }

    // --- 0x08 类型擦除 ---
    @Test
    @DisplayName("类型擦除：运行时 List<String> 和 List<Integer> 是同一类型")
    void testTypeErasure() {
        List<String> s = new ArrayList<>();
        List<Integer> i = new ArrayList<>();
        assertTrue(s.getClass() == i.getClass()); // 运行时类型相同
    }

    // --- 0x09 泛型与集合 ---
    @Test
    @DisplayName("泛型 List 和 Map 使用")
    void testGenericsWithCollections() {
        List<String> fruits = new ArrayList<>();
        fruits.add("apple");
        fruits.add("banana");

        Map<String, Integer> counts = new HashMap<>();
        counts.put("apple", 3);
        counts.put("banana", 5);

        assertEquals(2, fruits.size());
        assertEquals(3, counts.get("apple"));
    }
}