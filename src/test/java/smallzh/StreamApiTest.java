package smallzh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 基于 stream_api.md 文档的单元测试
 * 覆盖：Stream创建、中间操作、终端操作、Collectors、并行流、Optional与Stream
 */
@DisplayName("Stream API 测试")
class StreamApiTest {

    // --- 0x01 Stream 简介 ---
    @Test
    @DisplayName("Stream map + collect 基本用法")
    void testStreamMapCollect() {
        List<String> names = List.of("alice", "bob", "charlie");
        List<String> upper = names.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        assertEquals(Arrays.asList("ALICE", "BOB", "CHARLIE"), upper);
    }

    // --- 0x02 创建 Stream ---
    @Test
    @DisplayName("从集合创建 Stream")
    void testStreamFromCollection() {
        List<String> list = List.of("a", "b", "c");
        long count = list.stream().count();
        assertEquals(3, count);
    }

    @Test
    @DisplayName("Stream.of 创建流")
    void testStreamOf() {
        long count = Stream.of(1, 2, 3, 4).count();
        assertEquals(4, count);
    }

    @Test
    @DisplayName("Stream.iterate 创建无限流")
    void testStreamIterate() {
        List<Integer> result = Stream.iterate(0, n -> n + 1)
                .limit(5)
                .collect(Collectors.toList());
        assertEquals(Arrays.asList(0, 1, 2, 3, 4), result);
    }

    // --- 0x03 中间操作 ---
    @Test
    @DisplayName("filter 筛选以a开头的单词")
    void testFilter() {
        List<String> words = List.of("apple", "banana", "apricot", "avocado", "blueberry");
        List<String> aWords = words.stream()
                .filter(w -> w.startsWith("a"))
                .collect(Collectors.toList());
        assertEquals(Arrays.asList("apple", "apricot", "avocado"), aWords);
    }

    @Test
    @DisplayName("map 映射为长度")
    void testMap() {
        List<String> words = List.of("apple", "banana", "cherry");
        List<Integer> lengths = words.stream()
                .map(String::length)
                .collect(Collectors.toList());
        assertEquals(Arrays.asList(5, 6, 6), lengths);
    }

    @Test
    @DisplayName("flatMap 扁平化")
    void testFlatMap() {
        List<List<String>> groups = List.of(
                List.of("a", "b"),
                List.of("c", "d", "e")
        );
        List<String> flat = groups.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        assertEquals(Arrays.asList("a", "b", "c", "d", "e"), flat);
    }

    @Test
    @DisplayName("distinct 去重")
    void testDistinct() {
        List<Integer> nums = List.of(1, 2, 2, 3, 3, 4);
        List<Integer> unique = nums.stream().distinct().collect(Collectors.toList());
        assertEquals(Arrays.asList(1, 2, 3, 4), unique);
    }

    @Test
    @DisplayName("sorted 排序")
    void testSorted() {
        List<String> words = List.of("cherry", "apple", "banana");
        List<String> sorted = words.stream().sorted().collect(Collectors.toList());
        assertEquals(Arrays.asList("apple", "banana", "cherry"), sorted);
    }

    @Test
    @DisplayName("limit 截取")
    void testLimit() {
        List<Integer> nums = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<Integer> limited = nums.stream().limit(3).collect(Collectors.toList());
        assertEquals(Arrays.asList(1, 2, 3), limited);
    }

    // --- 0x04 终端操作 ---
    @Test
    @DisplayName("reduce 求和")
    void testReduce() {
        List<Integer> nums = Arrays.asList(3, 6, 9, 2, 5, 8);
        int sum = nums.stream().reduce(0, Integer::sum);
        assertEquals(33, sum);
    }

    @Test
    @DisplayName("count 计数")
    void testCount() {
        long c = List.of(1, 2, 3).stream().count();
        assertEquals(3, c);
    }

    @Test
    @DisplayName("min/max 最小最大值")
    void testMinMax() {
        List<Integer> nums = Arrays.asList(3, 6, 9, 2, 5, 8);
        Optional<Integer> min = nums.stream().min(Integer::compareTo);
        Optional<Integer> max = nums.stream().max(Integer::compareTo);
        assertEquals(2, min.orElse(-1));
        assertEquals(9, max.orElse(-1));
    }

    @Test
    @DisplayName("anyMatch/allMatch/noneMatch")
    void testMatchOperations() {
        List<Integer> nums = Arrays.asList(3, 6, 9, 2, 5, 8);
        assertTrue(nums.stream().anyMatch(n -> n % 2 == 0));
        assertTrue(nums.stream().allMatch(n -> n > 0));
        assertTrue(nums.stream().noneMatch(n -> n < 0));
    }

    // --- 0x05 Collectors ---
    @Test
    @DisplayName("Collectors.toList / toSet")
    void testCollectorsListSet() {
        List<String> words = List.of("apple", "banana", "apricot", "banana", "apple");
        Set<String> set = words.stream().collect(Collectors.toSet());
        assertEquals(3, set.size());
        assertTrue(set.contains("apple"));
    }

    @Test
    @DisplayName("Collectors.groupingBy 分组")
    void testGroupingBy() {
        List<String> words = List.of("apple", "banana", "apricot", "cherry");
        Map<Integer, List<String>> byLength = words.stream()
                .collect(Collectors.groupingBy(String::length));
        // apple(5), banana(6), apricot(7), cherry(6)
        assertEquals(1, byLength.get(5).size()); // apple
        assertEquals(2, byLength.get(6).size()); // banana, cherry
        assertEquals(1, byLength.get(7).size()); // apricot
    }

    @Test
    @DisplayName("Collectors.partitioningBy 分区")
    void testPartitioningBy() {
        List<String> words = List.of("apple", "banana", "apricot", "cherry");
        Map<Boolean, List<String>> longShort = words.stream()
                .collect(Collectors.partitioningBy(s -> s.length() > 5));
        assertTrue(longShort.get(true).contains("banana"));
        assertTrue(longShort.get(false).contains("apple"));
    }

    @Test
    @DisplayName("Collectors.joining 拼接字符串")
    void testJoining() {
        List<String> words = List.of("apple", "banana", "cherry");
        String joined = words.stream().collect(Collectors.joining(", "));
        assertEquals("apple, banana, cherry", joined);
    }

    // --- 0x07 Optional 与 Stream ---
    @Test
    @DisplayName("Stream findFirst 返回 Optional")
    void testStreamFindFirst() {
        List<String> names = List.of("Alice", "Bob", "Carol");
        Optional<String> first = names.stream().findFirst();
        assertTrue(first.isPresent());
        assertEquals("Alice", first.get());
    }

    @Test
    @DisplayName("空 Stream findFirst 返回 Optional.empty")
    void testEmptyStreamFindFirst() {
        List<String> empty = List.of();
        Optional<String> first = empty.stream().findFirst();
        assertTrue(first.isEmpty());
    }
}