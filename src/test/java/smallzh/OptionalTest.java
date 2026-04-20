package smallzh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Optional;
import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 基于 optional.md 文档的单元测试
 * 覆盖：创建、取值、map/flatMap、filter、isPresent/isEmpty
 */
@DisplayName("Optional 测试")
class OptionalTest {

    // --- 0x02 创建 Optional ---
    @Test
    @DisplayName("Optional.empty 创建空 Optional")
    void testEmpty() {
        Optional<String> empty = Optional.empty();
        assertFalse(empty.isPresent());
    }

    @Test
    @DisplayName("Optional.of 创建非空 Optional")
    void testOf() {
        Optional<String> nonNull = Optional.of("Hello");
        assertTrue(nonNull.isPresent());
        assertEquals("Hello", nonNull.get());
    }

    @Test
    @DisplayName("Optional.of 不允许 null")
    void testOfNull() {
        assertThrows(NullPointerException.class, () -> Optional.of(null));
    }

    @Test
    @DisplayName("Optional.ofNullable 允许 null")
    void testOfNullable() {
        Optional<String> nullable = Optional.ofNullable(null);
        assertFalse(nullable.isPresent());
        Optional<String> present = Optional.ofNullable("Hi");
        assertTrue(present.isPresent());
    }

    // --- 0x03 获取值 ---
    @Test
    @DisplayName("orElse 提供默认值")
    void testOrElse() {
        Optional<String> none = Optional.empty();
        assertEquals("default", none.orElse("default"));
    }

    @Test
    @DisplayName("orElseGet 延迟计算默认值")
    void testOrElseGet() {
        Optional<String> none = Optional.empty();
        assertEquals("computed", none.orElseGet(() -> "computed"));
    }

    @Test
    @DisplayName("orElseThrow 抛出异常")
    void testOrElseThrow() {
        Optional<String> none = Optional.empty();
        assertThrows(IllegalStateException.class, () -> {
            none.orElseThrow(() -> new IllegalStateException("missing"));
        });
    }

    @Test
    @DisplayName("orElseThrow 有值时正常返回")
    void testOrElseThrowWithValue() {
        Optional<String> some = Optional.of("Java");
        assertEquals("Java", some.orElseThrow(() -> new IllegalStateException("missing")));
    }

    // --- 0x04 转换操作 map/flatMap ---
    @Test
    @DisplayName("map 转换 Optional 的值")
    void testMap() {
        Optional<String> name = Optional.of("alice");
        Optional<Integer> lengthOpt = name.map(String::length);
        assertTrue(lengthOpt.isPresent());
        assertEquals(5, lengthOpt.get());
    }

    @Test
    @DisplayName("flatMap 避免嵌套 Optional")
    void testFlatMap() {
        Optional<String> name = Optional.of("alice");
        Optional<Integer> flat = name.flatMap(s -> Optional.of(s.length()));
        assertTrue(flat.isPresent());
        assertEquals(5, flat.get());
    }

    // --- 0x05 过滤操作 filter ---
    @Test
    @DisplayName("filter 条件为真保留值")
    void testFilterTrue() {
        Optional<String> name = Optional.of("Alice");
        Optional<String> longName = name.filter(n -> n.length() > 3);
        assertTrue(longName.isPresent());
        assertEquals("Alice", longName.get());
    }

    @Test
    @DisplayName("filter 条件为假返回空")
    void testFilterFalse() {
        Optional<String> name = Optional.of("Alice");
        Optional<String> shortName = name.filter(n -> n.length() > 10);
        assertFalse(shortName.isPresent());
    }

    // --- 0x06 判断操作 ---
    @Test
    @DisplayName("isPresent / isEmpty")
    void testIsPresentIsEmpty() {
        Optional<String> opt = Optional.ofNullable(null);
        assertFalse(opt.isPresent());
        assertTrue(opt.isEmpty());

        Optional<String> another = Optional.of("Hi");
        assertTrue(another.isPresent());
        assertFalse(another.isEmpty());
    }

    @Test
    @DisplayName("ifPresent 执行操作")
    void testIfPresent() {
        StringBuilder sb = new StringBuilder();
        Optional.of("Hi").ifPresent(sb::append);
        assertEquals("Hi", sb.toString());

        Optional.ofNullable(null).ifPresent(sb::append);
        assertEquals("Hi", sb.toString()); // 空值不执行
    }

    // --- 0x07 Optional 与 Stream 结合 ---
    @Test
    @DisplayName("Optional.stream() 与 Stream 结合")
    void testOptionalStream() {
        Optional<String> optional = Optional.of("OpenJDK");
        List<String> upper = optional.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        assertEquals(List.of("OPENJDK"), upper);
    }

    // --- 0x08 最佳实践 ---
    @Test
    @DisplayName("方法返回 Optional 使用 orElseGet")
    void testBestPractice() {
        String email = findEmailByUserId("unknown")
                .orElseGet(() -> "default@example.com");
        assertEquals("default@example.com", email);
    }

    static Optional<String> findEmailByUserId(String userId) {
        if ("user1".equals(userId)) {
            return Optional.of("user1@example.com");
        }
        return Optional.empty();
    }

    @Test
    @DisplayName("方法返回 Optional 找到值")
    void testFindEmailFound() {
        String email = findEmailByUserId("user1")
                .orElseGet(() -> "default@example.com");
        assertEquals("user1@example.com", email);
    }
}