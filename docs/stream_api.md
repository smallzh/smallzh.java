# OpenJDK17 Stream API 指南

本指南面向初学者，围绕 Java 17 的 Stream API 展开，内容覆盖流的基础、创建方式、常用中间与终端操作、Collectors、并行流，以及与 Optional 的结合与性能要点。每个知识点都给出可直接运行的代码示例，帮助你把理论落地到实际编码中。

- 适用版本：OpenJDK 17+
- 语言风格：中文，代码示例均为 Java

## 0x01 Stream 简介与特点

- Stream 是对集合、数组等数据源的抽象化处理通道，支持声明式的聚合、转换与筛选操作。
- 特点
  - 惰性求值：中间操作不会立即执行，只有遇到终端操作时才真正触发计算。
  - 链式操作：通过管道将多个操作链接在一起，形成数据处理流水线。
  - 不修改数据源：大多数操作不会改变原始数据源，而是输出新的结果。
  - 支持并行处理：通过并行流提升吞吐量，需注意线程安全和副作用。

示例：从集合创建流，进行简单映射再收集结果
```java
import java.util.*;
import java.util.stream.*;

public class StreamIntroDemo {
    public static void main(String[] args) {
        List<String> names = List.of("alice", "bob", "charlie");
        List<String> upper = names.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        System.out.println(upper); // [ALICE, BOB, CHARLIE]
    }
}
```

惰性求值与短路示例
```java
List<Integer> data = Arrays.asList(1, 2, 3, 4, 5);
data.stream()
    .filter(n -> {
        System.out.println("filter " + n);
        return n % 2 == 0;
    })
    .findFirst(); // 触发执行，但只到遇到第一个符合条件的元素
```

简短的调试示例（使用 peek 观察流中数据流向）
```java
List<Integer> nums = Arrays.asList(1, 2, 3, 4, 5);
List<Integer> result = nums.stream()
    .filter(n -> n > 1)
    .peek(n -> System.out.println("after filter: " + n))
    .limit(3)
    .collect(Collectors.toList());
```

参考以上要点，可以理解为什么 Stream 在处理大数据量时具有天然优势：可以通过流式管道对数据进行高效、链式的变换与聚合，而不需要一次性把所有数据加载到中间对象中。

## 0x02 创建 Stream 的方式

- 通过集合（List、Set 等）获取流
- 通过数组获取流
- 使用 Stream.of 静态工厂方法创建
- 使用 Stream.iterate 或 Stream.generate 创造无限流（结合 limit 进行限定）
- 将多个流拼接（Stream.concat）

示例代码
```java
import java.util.*;
import java.util.stream.*;

public class StreamCreationDemo {
    public static void main(String[] args) {
        // 1) 从集合创建
        List<String> list = List.of("a", "b", "c");
        Stream<String> s1 = list.stream();

        // 2) 从数组创建（对象数组）
        String[] arr = {"x", "y", "z"};
        Stream<String> s2 = Arrays.stream(arr);

        // 3) Stream.of
        Stream<Integer> s3 = Stream.of(1, 2, 3, 4);

        // 4) IntStream/Stream.of时的原始类型
        int[] nums = {1, 2, 3, 4, 5};
        IntStream s4 = Arrays.stream(nums); // IntStream

        // 5) 无限流（需要 limit/generate 连用）
        Stream<Integer> s5 = Stream.iterate(0, n -> n + 1).limit(5);

        // 6) 合并流
        Stream<String> merged = Stream.concat(s1, s2); // 注意：s1 与 s2 的类型需兼容
        System.out.println("示例创建完成，总计流数：" + merged.count());
    }
}
```

注意事项
- 使用 Arrays.stream 对原始类型数组 (int[], long[], double[]) 时得到的是对应的原始类型流（IntStream、LongStream 等）。
- Stream.concat 的两个流类型需要兼容，建议在实际场景中避免拼接不同泛型的流以避免编译错误。

## 0x03 中间操作（返回新流，供后续终端操作使用）

中间操作本质上是"转换/筛选/排序"等，返回新流，不会直接触发计算，只有遇到终端操作才执行。

- filter：根据断言筛选元素
- map：将元素转换为另一类型
- flatMap：将每个元素映射为流并扁平化
- distinct：去重
- sorted：排序（可传 Comparator）
- peek：对数据进行调试观察（不改变数据）
- limit：截取前 N 个
- skip：跳过前 N 个

示例代码
```java
import java.util.*;
import java.util.stream.*;

public class IntermediateOpsDemo {
    public static void main(String[] args) {
        List<String> words = List.of("apple", "banana", "apricot", "avocado", "blueberry");

        // filter
        List<String> aWords = words.stream()
                .filter(w -> w.startsWith("a"))
                .collect(Collectors.toList());

        // map
        List<Integer> lengths = words.stream()
                .map(String::length)
                .collect(Collectors.toList());

        // flatMap
        List<List<String>> groups = List.of(
                List.of("a", "b"),
                List.of("c", "d", "e")
        );
        List<String> flat = groups.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());

        // distinct
        List<Integer> nums = List.of(1, 2, 2, 3, 3, 4);
        List<Integer> unique = nums.stream().distinct().collect(Collectors.toList());

        // sorted
        List<String> sorted = words.stream().sorted().collect(Collectors.toList());

        // peek + limit
        List<String> limited = words.stream()
                .peek(w -> System.out.println("处理中: " + w))
                .limit(3)
                .collect(Collectors.toList());
    }
}
```

- 注意：sorted 是有状态的操作，会在整个流上排序，可能代价较大；而 map/filter 等通常是无状态的，适合链式组合以实现高效流水线。

## 0x04 终端操作（触发执行并产出结果）

终端操作会触发流水线的执行，并产生具体结果或副作用。

- forEach：逐个消费元素
- collect：收集结果（常用收集器）
- reduce：聚合成单个值
- count：计数
- min/max：最小/最大值
- findFirst/findAny：查找元素
- anyMatch/allMatch/noneMatch：匹配谓词

示例代码
```java
import java.util.*;
import java.util.stream.*;

public class TerminalOpsDemo {
    public static void main(String[] args) {
        List<Integer> nums = Arrays.asList(3, 6, 9, 2, 5, 8);

        // forEach
        nums.stream().forEach(System.out::println);

        // collect
        List<Integer> collected = nums.stream().filter(n -> n > 5).collect(Collectors.toList());

        // reduce（求和）
        int sum = nums.stream().reduce(0, Integer::sum);

        // count
        long c = nums.stream().count();

        // min/max
        Optional<Integer> min = nums.stream().min(Integer::compareTo);
        Optional<Integer> max = nums.stream().max(Integer::compareTo());

        // findFirst/findAny
        Optional<Integer> first = nums.stream().findFirst();
        Optional<Integer> any = nums.parallelStream().findAny();

        // anyMatch / allMatch / noneMatch
        boolean hasEven = nums.stream().anyMatch(n -> n % 2 == 0);
        boolean allPositive = nums.stream().allMatch(n -> n > 0);
        boolean noneNegative = nums.stream().noneMatch(n -> n < 0);
    }
}
```

在实际项目中，常用的终端操作组合是：通过 collect 将结果聚合为集合、通过 findFirst/findAny 进行快速探测、通过 reduce 做自定义聚合等。

## 0x05 收集器 Collectors（常用聚合工具）

Collectors 提供了一组强大且直观的聚合工具，帮助将流中的元素最终转化为 List、Set、Map 等结构，或进行分组、分区、拼接等。

- toList：转换为 List
- toSet：转换为 Set
- toMap：转换为 Map（需处理 key 冲突）
- groupingBy：按键分组
- partitioningBy：按断言分区
- joining：将字符串拼接为单一字符串

示例代码
```java
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class CollectorsDemo {
    public static void main(String[] args) {
        List<String> words = List.of("apple", "banana", "apricot", "banana", "apple");

        // toList / toSet
        List<String> list = words.stream().distinct().collect(Collectors.toList());
        Set<String> set = words.stream().collect(Collectors.toSet());

        // toMap（处理冲突示例）
        Map<String, Integer> lengthMap = words.stream()
                .collect(Collectors.toMap(
                        Function.identity(), // key = 单词本身
                        String::length,      // value = 长度
                        (a, b) -> a));          // 冲突时保留已有键

        // groupingBy
        Map<Integer, List<String>> byLength = words.stream()
                .collect(Collectors.groupingBy(String::length));

        // partitioningBy
        Map<Boolean, List<String>> longShort = words.stream()
                .collect(Collectors.partitioningBy(s -> s.length() > 5));

        // joining
        String joined = words.stream().collect(Collectors.joining(", ", "[", "]"));
        System.out.println(joined); // [apple, banana, apricot, banana, apple]
    }
}
```

具备一定的使用场景后，Collectors 提供的分组与分区能力尤为强大，适用于数据统计、分组汇总等场景。

## 0x06 并行流

并行流通过拆分数据源并行执行流中的操作，以提升处理吞吐量，尤其在多核 CPU 环境下表现明显。

示例代码
```java
import java.util.*;
import java.util.stream.*;

public class ParallelDemo {
    public static void main(String[] args) {
        List<Integer> nums = IntStream.range(0, 1_000_000)
                .boxed()
                .collect(Collectors.toList());

        // 串行流
        long t1 = System.currentTimeMillis();
        long sumSeq = nums.stream().mapToInt(Integer::intValue).sum();
        long dSeq = System.currentTimeMillis() - t1;

        // 并行流
        long t2 = System.currentTimeMillis();
        long sumPara = nums.parallelStream().mapToInt(Integer::intValue).sum();
        long dPara = System.currentTimeMillis() - t2;

        System.out.println("串行求和: " + sumSeq + " 用时: " + dSeq + "ms");
        System.out.println("并行求和: " + sumPara + " 用时: " + dPara + "ms");
    }
}
```

注意事项
- 并行流并不总是更快。对小数据量、或管道中包含大量有序性排序、需要共享可变状态的场景，成本可能更高。
- 避免在并行流中引入副作用（如修改外部可变变量、打印到控制台等），尽量让操作无状态、无副作用。
- 某些数据源天然不易并行（如顺序依赖强的数据源、昂贵的锁竞争场景），需要谨慎评估。

## 0x07 Optional 与 Stream 的结合

Optional 常用于查找、聚合等结果可能为空的场景。当使用流进行查找时，经常返回 Optional，例如 findFirst、min、max 等。

示例代码
```java
import java.util.*;
import java.util.stream.*;

public class OptionalStreamDemo {
    static class Person {
        String name;
        int score;
        Person(String name, int score) { this.name = name; this.score = score; }
        String getName() { return name; }
        int getScore() { return score; }
    }

    public static void main(String[] args) {
        List<Person> people = List.of(
                new Person("Alice", 85),
                new Person("Bob", 92),
                new Person("Carol", 78)
        );

        // 查找分数最高的名字（结果可能为空）
        Optional<String> topName = people.stream()
                .max(Comparator.comparingInt(Person::getScore))
                .map(Person::getName);

        topName.ifPresent(System.out::println); // Bob

        // 使用 Optional 的默认值
        String defaultName = topName.orElse("未知");
        System.out.println(defaultName);
    }
}
```

要点
- Optional 提供更安全的缺失值处理，结合 map、orElse、orElseGet、orElseThrow 等方法使用非常直观。
- 流中的 findFirst/findAny、min/max 等操作会返回 Optional，避免直接返回 null。

## 0x08 Stream 的性能考虑与最佳实践

为确保实际开发中的性能与可维护性，以下是一些常用的建议与要点：

- 使用原始类型流以避免装箱开销
  - 尽量用 IntStream、LongStream、DoubleStream 等代替 Object 的封装类型。
  - 示例：用 mapToInt 替代 map + Integer::intValue
```java
IntStream.range(0, 1_000_000)
    .map(n -> n * 2)
    .sum();
```

- 尽量短路操作优先执行
  - findFirst、findAny、anyMatch、allMatch、noneMatch、limit 等在可能的情况下会提前结束，避免对整个数据集的遍历。

- 避免在中间操作中产生副作用
  - 尽量让逻辑无状态、无副作用，避免并行流中出现数据竞争或不可预测行为。

- 小心排序和聚合的代价
  - sorted、distinct 等操作通常需要遍历并重新排序或去重，成本较高，应放在流水线后端且尽量在数据量较小的阶段完成。

- 使用合适的收集器
  - 如果需要收集为 List，优先使用 Collectors.toList()；如需不可变集合，考虑使用 toList/或转成 ArrayList 的替代实现（视 JDK 版本而定）。
  - 分组、分区等操作要尽可能在数据量较大时使用，以免频繁创建中间对象。

- 并行流的适用场景
  - 数据量大、CPU 核心数充足、处理过程为无状态且相对独立的情况，往往受益于并行。
  - 注意：并行吞吐量受线程切换、内存带宽、同步成本影响，别盲目追求"并行"等号，最好在实际数据上做基准测试。

- 与 Optional 的结合
  - 使用 Optional 避免空指针，结合 map、orElse、orElseGet、orElseThrow 等方法编写健壮代码。

- 最小可观察的单元测试与调试
  - 开发阶段可通过 peek 与逐步断点观察数据流向，确保流水线行为符合预期，但生产代码中避免副作用性打印。

- 容错与边界情况
  - 数据源可能为 null、空集合，确保使用 Optional 与默认值策略来处理边界情况。

综合示例总结
- 使用流式 API 时，一般遵循"先筛选、再映射、再聚合"的思路，尽量让中间操作保持无副作用且无状态，最后以一个合适的终端操作完成任务。
- 面向性能的优化，优先关注数据量、数据源性质、以及是否需要并行化。对性能敏感的场景，建议做基准测试，避免盲目提升并行度。

## 参考链接

- Java 官方流 API - Stream 及 Collector 参考
  - https://docs.oracle.com/javase/17/docs/api/java/util/stream/Package-summary.html
  - https://docs.oracle.com/javase/17/docs/api/java/util/stream/Stream.html
  - https://docs.oracle.com/javase/17/docs/api/java/util/stream/Collectors.html
- Baeldung - Java 8+ Stream 系列教程（适合初学者，通俗易懂）
  - https://www.baeldung.com/java-streams
- Oracle Java Tutorials（中文译本与示例）
  - https://docs.oracle.com/javase/tutorial/essential/streams/index.html
