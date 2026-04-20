package smallzh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 基于 collections.md 文档的单元测试
 * 覆盖：List、Set、Map、Queue/Deque、Collections工具类、Iterator、排序与比较
 */
@DisplayName("集合框架测试")
class CollectionsTest {

    // --- 0x02 List ---
    @Test
    @DisplayName("ArrayList 基本操作")
    void testArrayList() {
        List<String> list = new ArrayList<>();
        list.add("苹果");
        list.add("香蕉");
        assertEquals(2, list.size());
        assertTrue(list.contains("苹果"));
    }

    @Test
    @DisplayName("LinkedList 基本操作")
    void testLinkedList() {
        List<String> linkedList = new LinkedList<>();
        linkedList.add("1");
        linkedList.add("2");
        assertEquals(2, linkedList.size());
        assertEquals("1", linkedList.get(0));
    }

    @Test
    @DisplayName("Vector 基本操作")
    void testVector() {
        Vector<Integer> vector = new Vector<>();
        vector.add(10);
        vector.add(20);
        assertEquals(2, vector.size());
        assertEquals(10, vector.get(0));
    }

    // --- 0x03 Set ---
    @Test
    @DisplayName("HashSet 去重")
    void testHashSetDedup() {
        Set<String> hashSet = new HashSet<>();
        hashSet.add("A");
        hashSet.add("B");
        hashSet.add("A"); // 重复
        assertEquals(2, hashSet.size());
    }

    @Test
    @DisplayName("TreeSet 自动排序")
    void testTreeSetSorted() {
        Set<String> treeSet = new TreeSet<>();
        treeSet.add("d");
        treeSet.add("a");
        treeSet.add("c");
        List<String> sorted = new ArrayList<>(treeSet);
        assertEquals(Arrays.asList("a", "c", "d"), sorted);
    }

    @Test
    @DisplayName("LinkedHashSet 保持插入顺序")
    void testLinkedHashSetOrder() {
        Set<String> linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.add("X");
        linkedHashSet.add("Y");
        linkedHashSet.add("X"); // 重复
        assertEquals(2, linkedHashSet.size());
        Iterator<String> it = linkedHashSet.iterator();
        assertEquals("X", it.next());
        assertEquals("Y", it.next());
    }

    // --- 0x04 Map ---
    @Test
    @DisplayName("HashMap 基本操作")
    void testHashMap() {
        Map<String, Integer> hm = new HashMap<>();
        hm.put("apple", 3);
        hm.put("banana", 5);
        assertEquals(3, hm.get("apple"));
        assertEquals(2, hm.size());
    }

    @Test
    @DisplayName("TreeMap 按键排序")
    void testTreeMap() {
        Map<String, Integer> tm = new TreeMap<>();
        tm.put("c", 3);
        tm.put("a", 1);
        tm.put("b", 2);
        List<String> keys = new ArrayList<>(tm.keySet());
        assertEquals(Arrays.asList("a", "b", "c"), keys);
    }

    @Test
    @DisplayName("LinkedHashMap 保持插入顺序")
    void testLinkedHashMap() {
        Map<String, Integer> lhm = new LinkedHashMap<>();
        lhm.put("one", 1);
        lhm.put("two", 2);
        Iterator<String> it = lhm.keySet().iterator();
        assertEquals("one", it.next());
        assertEquals("two", it.next());
    }

    // --- 0x05 Queue 与 Deque ---
    @Test
    @DisplayName("Queue 先进先出")
    void testQueue() {
        Queue<String> q = new LinkedList<>();
        q.offer("一");
        q.offer("二");
        assertEquals("一", q.poll());
        assertEquals("二", q.poll());
    }

    @Test
    @DisplayName("Deque 双端队列")
    void testDeque() {
        Deque<Integer> dq = new ArrayDeque<>();
        dq.addLast(1);
        dq.addLast(2);
        dq.addFirst(0);
        assertEquals(0, dq.removeFirst());
        assertEquals(1, dq.removeFirst());
        assertEquals(2, dq.removeFirst());
    }

    // --- 0x06 Collections 工具类 ---
    @Test
    @DisplayName("Collections.sort 排序")
    void testCollectionsSort() {
        List<Integer> nums = new ArrayList<>(Arrays.asList(3, 1, 4, 2));
        Collections.sort(nums);
        assertEquals(Arrays.asList(1, 2, 3, 4), nums);
    }

    @Test
    @DisplayName("Collections.reverse 反转")
    void testCollectionsReverse() {
        List<Integer> nums = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
        Collections.reverse(nums);
        assertEquals(Arrays.asList(4, 3, 2, 1), nums);
    }

    @Test
    @DisplayName("Collections.binarySearch 二分查找")
    void testCollectionsBinarySearch() {
        List<Integer> nums = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
        int idx = Collections.binarySearch(nums, 3);
        assertEquals(2, idx);
    }

    @Test
    @DisplayName("Collections.unmodifiableList 不可变视图")
    void testUnmodifiableList() {
        List<Integer> nums = new ArrayList<>(Arrays.asList(1, 2, 3));
        List<Integer> unmodifiable = Collections.unmodifiableList(nums);
        assertThrows(UnsupportedOperationException.class, () -> unmodifiable.add(4));
    }

    // --- 0x07 Iterator ---
    @Test
    @DisplayName("Iterator 安全移除元素")
    void testIteratorRemove() {
        List<String> items = new ArrayList<>(Arrays.asList("A", "B", "C"));
        Iterator<String> it = items.iterator();
        while (it.hasNext()) {
            String s = it.next();
            if ("B".equals(s)) {
                it.remove();
            }
        }
        assertEquals(Arrays.asList("A", "C"), items);
    }

    // --- 0x08 集合排序与比较 ---
    static class SortPerson implements Comparable<SortPerson> {
        String name;
        int age;
        SortPerson(String n, int a) { this.name = n; this.age = a; }
        @Override
        public int compareTo(SortPerson o) {
            return Integer.compare(this.age, o.age);
        }
        @Override
        public String toString() { return name + "(" + age + ")"; }
    }

    @Test
    @DisplayName("Comparable 按年龄排序")
    void testComparableSort() {
        List<SortPerson> people = new ArrayList<>();
        people.add(new SortPerson("张三", 28));
        people.add(new SortPerson("李四", 22));
        people.add(new SortPerson("王五", 35));
        Collections.sort(people);
        assertEquals(22, people.get(0).age);
        assertEquals(28, people.get(1).age);
        assertEquals(35, people.get(2).age);
    }

    @Test
    @DisplayName("Comparator 按姓名排序")
    void testComparatorSort() {
        List<SortPerson> people = new ArrayList<>();
        people.add(new SortPerson("张三", 28));
        people.add(new SortPerson("李四", 22));
        people.add(new SortPerson("王五", 35));
        people.sort(Comparator.comparing(p -> p.name));
        assertEquals("张三", people.get(0).name);
    }
}