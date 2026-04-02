# OpenJDK17 的集合框架包含两大体系

OpenJDK17 的集合框架由两大体系组成：Collection 体系和 Map 体系。Collection 体系下的接口如 List、Set、Queue、Deque 等提供有序或无序、可重复性的集合；Map 体系专注于键值对映射，常见实现有 HashMap、TreeMap、Hashtable、LinkedHashMap 等。下面通过简明示例，帮助初学者快速上手。

## 0x01 集合框架概述

- Collection 体系包含 Collection 的子接口及其实现（List、Set、Queue、Deque 等），用于存储单一对象的集合。
- Map 体系不是 Collection 的子接口，而是独立的键值对映射接口及其实现。
- 常见用途示例：遍历、查找、排序、去重、线程安全等。

示例代码（同时演示 Collection 和 Map 的基本用法）：

```java
import java.util.*;

public class OverviewDemo {
  public static void main(String[] args) {
    // Collection 体系示例：List
    List<String> list = new ArrayList<>();
    list.add("苹果");
    list.add("香蕉");

    // Map 体系示例：键值对
    Map<String, String> map = new HashMap<>();
    map.put("城市", "北京");
    map.put("国家", "中国");

    System.out.println("List: " + list);
    System.out.println("Map: " + map);
  }
}
```

参考要点：
- List、Set、Queue、Deque 均实现 Collection 接口，提供通用操作如 add、remove、contains、size、isEmpty 等。
- Map 通过 key/value 的映射提供 put/get/remove/containsKey/containsValue 等操作。

## 0x02 List 接口及实现类

List 是有序、可重复的集合，常见实现有 ArrayList、LinkedList、Vector。

- ArrayList：基于动态数组，检索快、插入尾部成本低，随机访问性能好。
- LinkedList：基于双向链表，插入删除在中间位置成本低于数组，但随机访问略慢。
- Vector：线程安全的可扩展数组，默认方法与 ArrayList 相同，但方法是同步的，性能负责比 ArrayList 略低。

示例代码：

```java
import java.util.*;

public class ListDemo {
  public static void main(String[] args) {
    // ArrayList 示例
    List<String> arrayList = new ArrayList<>();
    arrayList.add("A");
    arrayList.add("B");
    System.out.println("ArrayList: " + arrayList);

    // LinkedList 示例
    List<String> linkedList = new LinkedList<>();
    linkedList.add("1");
    linkedList.add("2");
    System.out.println("LinkedList: " + linkedList);

    // Vector 示例（同步，较老的用法，现代多用 ArrayList）
    Vector<Integer> vector = new Vector<>();
    vector.add(10);
    vector.add(20);
    System.out.println("Vector: " + vector);
  }
}
```

要点总结：
- 选择 ArrayList 作为默认 List 实现，除非需要频繁在中间插入或移除。
- Vector 适用于需要线程安全的简单场景，但性能较 ArrayList 低，通常优先考虑同步手段或并发集合。

## 0x03 Set 接口及实现类

Set 表示不重复的集合，常见实现有 HashSet、TreeSet、LinkedHashSet。

- HashSet：基于哈希表，元素无序且快速查找。
- TreeSet：基于红黑树，自动排序（自然顺序或自定义 Comparator）。
- LinkedHashSet：保持插入顺序的 HashSet，遍历顺序与插入顺序一致。

示例代码：

```java
import java.util.*;

public class SetDemo {
  public static void main(String[] args) {
    // HashSet
    Set<String> hashSet = new HashSet<>();
    hashSet.add("A");
    hashSet.add("B");
    hashSet.add("A"); // 重复只会保留一个
    System.out.println("HashSet: " + hashSet);

    // TreeSet
    Set<String> treeSet = new TreeSet<>();
    treeSet.add("d");
    treeSet.add("a");
    treeSet.add("c");
    System.out.println("TreeSet: " + treeSet); // 自动按字母排序

    // LinkedHashSet
    Set<String> linkedHashSet = new LinkedHashSet<>();
    linkedHashSet.add("X");
    linkedHashSet.add("Y");
    linkedHashSet.add("X"); // 重复不影响
    System.out.println("LinkedHashSet: " + linkedHashSet);
  }
}
```

要点总结：
- 若需要快速去重且不在意顺序，使用 HashSet。
- 需要有序遍历时，优先选择 LinkedHashSet（保持插入顺序）或 TreeSet（按排序规则）。

## 0x04 Map 接口及实现类

Map 提供键到值的映射，常见实现有 HashMap、TreeMap、Hashtable、LinkedHashMap。

- HashMap：无序、键唯一，适合大多数场景。
- TreeMap：基于红黑树，按键的自然顺序或自定义 Comparator 排序。
- Hashtable：早期的线程安全实现，方法均为同步。与 HashMap 不同的是它不允许 null 键和值。
- LinkedHashMap：保持插入顺序的 HashMap，遍历顺序与插入顺序一致；也支持按访问顺序的最近最少使用策略。

示例代码：

```java
import java.util.*;

public class MapDemo {
  public static void main(String[] args) {
    // HashMap
    Map<String, Integer> hm = new HashMap<>();
    hm.put("apple", 3);
    hm.put("banana", 5);
    System.out.println("HashMap: " + hm);

    // TreeMap
    Map<String, Integer> tm = new TreeMap<>();
    tm.put("c", 3);
    tm.put("a", 1);
    tm.put("b", 2);
    System.out.println("TreeMap: " + tm);

    // Hashtable
    Hashtable<String, Integer> ht = new Hashtable<>();
    ht.put("X", 24);
    System.out.println("Hashtable: " + ht);

    // LinkedHashMap
    Map<String, Integer> lhm = new LinkedHashMap<>();
    lhm.put("one", 1);
    lhm.put("two", 2);
    System.out.println("LinkedHashMap: " + lhm);
  }
}
```

要点总结：
- 使用 HashMap/LinkedHashMap 作为默认 Map 实现，HashMap 提供高性能；LinkedHashMap 适合需要稳定遍历顺序的场景。
- TreeMap 适合需要排序的场景，Hashtable 提供简单的线程安全需求但已较少使用。

## 0x05 Queue 与 Deque 接口

Queue 代表队列，通常采用先进先出（FIFO）语义；Deque 则是双端队列，支持在两端进行插入与删除。

示例代码：

```java
import java.util.*;

public class QueueDequeDemo {
  public static void main(String[] args) {
    // Queue 实现：LinkedList 常用于 Queue
    Queue<String> q = new LinkedList<>();
    q.offer("一");
    q.offer("二");
    System.out.println("队列取出: " + q.poll());

    // Deque 实现：ArrayDeque 常用
    Deque<Integer> dq = new ArrayDeque<>();
    dq.addLast(1);
    dq.addLast(2);
    dq.addFirst(0);
    while (!dq.isEmpty()) {
      System.out.println("Deque 出队: " + dq.removeFirst());
    }
  }
}
```

要点总结：
- Queue 用于按顺序处理元素，常用方法包含 offer/poll/peek。
- Deque 提供在两端插入删除的能力，ArrayDeque 是高性能实现之一。

## 0x06 Collections 工具类

Collections 提供对集合的算法与多种包装器，常用包括排序、查找、反转、不可变/同步视图等。

示例代码：

```java
import java.util.*;

public class CollectionsDemo {
  public static void main(String[] args) {
    List<Integer> nums = new ArrayList<>(Arrays.asList(3, 1, 4, 2));
    Collections.sort(nums); // 排序
    System.out.println("排序后: " + nums);

    Collections.reverse(nums); // 反转
    System.out.println("反转后: " + nums);

    List<Integer> unmodifiable = Collections.unmodifiableList(nums);
    System.out.println("不可变视图: " + unmodifiable);

    // 二分查找需先排好序
    int idx = Collections.binarySearch(nums, 3);
    System.out.println("数字 3 的索引（排序后列表）: " + idx);
  }
}
```

要点总结：
- 使用 Collections.sort(List) 对可变列表排序，排序后可再使用 binarySearch 进行快速定位。
- Collections.unmodifiableX 系列方法可以得到不可变视图，提升安全性。

## 0x07 迭代器（Iterator）

Iterator 提供对集合的遍历能力，并允许在遍历过程中安全地移除元素（通过 iterator 的 remove）。

示例代码：

```java
import java.util.*;

public class IteratorDemo {
  public static void main(String[] args) {
    List<String> items = new ArrayList<>(Arrays.asList("A","B","C"));
    Iterator<String> it = items.iterator();
    while (it.hasNext()) {
      String s = it.next();
      if ("B".equals(s)) {
        it.remove(); // 安全地在遍历中移除
      }
    }
    System.out.println("遍历结果: " + items);
  }
}
```

要点总结：
- 不要在遍历时直接修改底层集合（如 list.remove(color)），应使用迭代器的 remove 方法避免并发问题。

## 0x08 集合的排序与比较

排序与比较通常通过实现 Comparable 接口或使用 Comparator 来完成。下面给出一个简单示例，展示按年龄排序和按姓名排序的两种方式。

示例代码：

```java
import java.util.*;

class Person implements Comparable<Person> {
  String name;
  int age;
  Person(String n, int a) { this.name = n; this.age = a; }
  @Override
  public int compareTo(Person o) {
    return Integer.compare(this.age, o.age); // 按年龄排序
  }
  @Override
  public String toString() { return name + "(" + age + ")"; }
}

public class SortDemo {
  public static void main(String[] args) {
    List<Person> people = new ArrayList<>();
    people.add(new Person("张三", 28));
    people.add(new Person("李四", 22));
    people.add(new Person("王五", 35));

    // 使用实现了 Comparable 的排序（按年龄）
    Collections.sort(people);
    System.out.println("按年龄排序: " + people);

    // 使用自定义 Comparator 按姓名排序
    people.sort(Comparator.comparing(p -> p.name));
    System.out.println("按姓名排序: " + people);
  }
}
```

进阶要点：
- 将类实现 Comparable，定义自然排序。
- 使用 Comparator 进行灵活多维排序，尤其在无法修改目标类时非常有用。
- TreeSet/TreeMap 的排序依赖于元素的自然顺序或提供的 Comparator。

参考链接（OpenJDK/Java 17 文档）
- Java 7+ 集合框架总览与包文档（java.util）：https://docs.oracle.com/javase/17/docs/api/java.util/package-summary.html
- ArrayList 文档：https://docs.oracle.com/javase/17/docs/api/java/util/ArrayList.html
- LinkedList 文档：https://docs.oracle.com/javase/17/docs/api/java/util/LinkedList.html
- Vector 文档：https://docs.oracle.com/javase/17/docs/api/java/util/Vector.html
- HashSet 文档：https://docs.oracle.com/javase/17/docs/api/java/util/HashSet.html
- TreeSet 文档：https://docs.oracle.com/javase/17/docs/api/java/util/TreeSet.html
- LinkedHashSet 文档：https://docs.oracle.com/javase/17/docs/api/java/util/LinkedHashSet.html
- HashMap 文档：https://docs.oracle.com/javase/17/docs/api/java/util/HashMap.html
- TreeMap 文档：https://docs.oracle.com/javase/17/docs/api/java/util/TreeMap.html
- Hashtable 文档：https://docs.oracle.com/javase/17/docs/api/java/util/Hashtable.html
- LinkedHashMap 文档：https://docs.oracle.com/javase/17/docs/api/java/util/LinkedHashMap.html
- Queue 接口文档：https://docs.oracle.com/javase/17/docs/api/java/util/Queue.html
- Deque 接口文档：https://docs.oracle.com/javase/17/docs/api/java/util/Deque.html
- Iterator 接口文档：https://docs.oracle.com/javase/17/docs/api/java/util/Iterator.html
- Collections 工具类文档：https://docs.oracle.com/javase/17/docs/api/java/util/Collections.html

如需，我可以把这份文档整理成 MkDocs/Docs 结构的草案，方便直接导入到你的知识库中。
