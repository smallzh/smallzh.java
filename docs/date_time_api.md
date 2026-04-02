# OpenJDK17 日期时间 API（java.time）详细指南

## 0x01 旧日期时间 API 的问题
在 Java 8 之前，常用的日期时间类主要来自 java.util.Date、java.util.Calendar、以及 java.text.SimpleDateFormat。这些 API 存在明显的问题：可变性导致线程不安全、设计不清晰、时区处理容易混乱、格式化/解析的工具类在并发场景下容易出错。下面给出一个简单示例，说明旧 API 的易错点（可变对象和时区依赖的困惑）：

```java
// 旧 API 易错演示（可变 Date 对象和简单格式化的线程安全问题）
import java.util.Date;
import java.text.SimpleDateFormat;

public class OldApiIssue {
    public static void main(String[] args) {
        Date now = new Date();
        System.out.println("当前时间: " + now);

        // Date 是可变的，下面的修改会改变同一个对象的时间
        now.setTime(now.getTime() + 1000);

        // SimpleDateFormat 非线程安全，以下用法在并发场景可能产生错乱
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("格式化后: " + sdf.format(now));
    }
}
```

这些问题在实际项目中会带来维护成本和潜在 Bug，因此引入了更清晰、不可变的日期时间 API。

---

## 0x02 java.time 包概述
java.time 包在 Java 8 引入，目标是提供不可变、线程安全且语义清晰的日期时间模型。核心思想是把"点在时间线上的瞬间"和"具有时区或日历含义的时间段"分离开来，避免混乱的时区推断和可变状态。常见类型包括 LocalDate、LocalTime、LocalDateTime、Instant、Duration、Period、ZonedDateTime、DateTimeFormatter 等。示例展示基本用法：

```java
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class TimeOverview {
    public static void main(String[] args) {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        LocalDateTime dateTime = LocalDateTime.now();

        System.out.println("日期: " + date);
        System.out.println("时间: " + time);
        System.out.println("日期时间: " + dateTime);
    }
}
```

本 API 集成了丰富的工厂方法与解析能力，例如 LocalDate.of(year, month, day) 与 LocalDate.parse("2026-04-01")，以及与时区无关的局部日期时间表示。重要的是，所有日期时间类都是不可变的，方法如 plusDays、minusMonths 等都会返回新的实例。

---

## 0x03 LocalDate、LocalTime、LocalDateTime
LocalDate 表示不含时区的年月日，LocalTime 表示不含日期的时分秒，LocalDateTime 将两者组合为一个日期时间点（同样不携带时区信息）。它们都是不可变的，任何"修改"操作都会返回新对象。常见用法如下：

```java
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class LocalDateTimeExamples {
    public static void main(String[] args) {
        // LocalDate 示例
        LocalDate date = LocalDate.of(2026, 4, 1);
        LocalDate nextWeek = date.plusDays(7);

        // LocalTime 示例
        LocalTime time = LocalTime.of(14, 30, 0);
        LocalTime timePlusOneHour = time.plusHours(1);

        // LocalDateTime 示例（把日期和时间组合）
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        LocalDateTime updated = dateTime.plusDays(2).plusHours(3);

        System.out.println("日期: " + date);
        System.out.println("下周日期: " + nextWeek);
        System.out.println("时间: " + time);
        System.out.println("加一小时后的时间: " + timePlusOneHour);
        System.out.println("日期时间: " + dateTime);
        System.out.println("更新后的日期时间: " + updated);

        // 解析与格式化（ISO 本地日期时间格式）
        LocalDate parsedDate = LocalDate.parse("2026-04-01");
        LocalDateTime parsedDateTime = LocalDateTime.parse("2026-04-01T14:30:00");
        System.out.println("解析的日期: " + parsedDate);
        System.out.println("解析的日期时间: " + parsedDateTime);
    }
}
```

要点提示：
- LocalDate/LocalTime/LocalDateTime 都是不可变对象，链式调用很自然。
- 常用工厂方法有 of、parse、from（若与其他类型转换时使用）。

---

## 0x04 Instant 时间戳
Instant 表示时间线上的一个时间点，通常与 UTC 基准对齐，适合作为时间戳。它可以与时区无关的日期时间进行互转。常见用法包括获取当前时间、计算时差、与旧 API 互转等。

```java
import java.time.Instant;

public class InstantExample {
    public static void main(String[] args) {
        Instant now = Instant.now();
        Instant later = now.plusSeconds(3600);

        long epochSec = now.getEpochSecond();
        int nano = now.getNano();

        System.out.println("当前瞬时: " + now);
        System.out.println("一小时后: " + later);
        System.out.println("Epoch Second: " + epochSec + ", Nano: " + nano);

        // 与旧 API 互操作
        java.util.Date dateFromInstant = java.util.Date.from(now);
        System.out.println("Date from Instant: " + dateFromInstant);
    }
}
```

Instant 常用于与时区无关的时间点计算，后续再结合 ZoneId/ZonedDateTime 进行时区处理。

---

## 0x05 Duration 与 Period
Duration 是基于时间单位的持续时间，精确到秒和纳秒；Period 是基于日历日期的间隔，单位包括年、月、日。它们用于表示时间段的长度，适用于不同场景的加减运算。

```java
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.LocalDate;

public class DurationPeriodExample {
    public static void main(String[] args) {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(5).plusMinutes(30);

        Duration duration = Duration.between(start, end);

        LocalDate date1 = LocalDate.of(2020, 1, 1);
        LocalDate date2 = date1.plusMonths(3).plusDays(10);
        Period period = Period.between(date1, date2);

        System.out.println("持续时长: " + duration.toHours() + " 小时");
        System.out.println("日历间隔: " + period.getYears() + " 年 " + period.getMonths() + " 月 " + period.getDays() + " 天");
    }
}
```

要点：
- Duration 适合计算"时间量"如相隔多少小时、分钟、秒。
- Period 适合计算基于日期的间隔，如相差多少年、月、日。

---

## 0x06 DateTimeFormatter 格式化
DateTimeFormatter 提供强大的格式化能力，包含内置格式和自定义模式。格式化和解析都很直观，并且支持区域设置 Locale。

```java
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FormatterExample {
    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now();

        // 自定义模式
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss", Locale.CHINESE);
        String text = now.format(fmt);

        // 解析文本
        LocalDateTime parsed = LocalDateTime.parse("2026/04/01 15:20:30", fmt);

        System.out.println("格式化输出: " + text);
        System.out.println("解析结果: " + parsed);
    }
}
```

要点提示：
- 常用模式如 yyyy-MM-dd、HH:mm、mm、ss 等，支持更多字母组合表示不同字段。
- 也可以使用 DateTimeFormatter.ISO_LOCAL_DATE、ISO_LOCAL_TIME 等内置格式。

---

## 0x07 ZonedDateTime 时区处理
ZoneId 表示时区，ZonedDateTime 将日期时间与时区信息结合。时区处理是在分布式系统、跨地区应用中最常见的需求。

```java
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.LocalDateTime;

public class ZoneDateTimeExample {
    public static void main(String[] args) {
        // 指定时区的当前日期时间
        ZoneId shanghai = ZoneId.of("Asia/Shanghai");
        ZonedDateTime zdtShanghai = ZonedDateTime.now(shanghai);

        // 指定日期时间与时区组合
        LocalDateTime local = LocalDateTime.of(2026, 4, 1, 12, 0);
        ZonedDateTime zdtUtc = local.atZone(ZoneId.of("UTC"));

        // 时区转换
        ZonedDateTime zdtNewYork = zdtShanghai.withZoneSameInstant(ZoneId.of("America/New_York"));

        System.out.println("上海时间: " + zdtShanghai);
        System.out.println("UTC 时间（从本地时间创建）: " + zdtUtc);
        System.out.println("纽约时间（与上海同瞬时）: " + zdtNewYork);
    }
}
```

要点：
- ZoneId 支持 IANA 时区数据库，推荐用标准时区名称。
- withZoneSameInstant 实现同一瞬时点在不同时区的表示，toInstant 可用于跨时区转换。

---

## 0x08 日期时间运算（加减、比较）
DateTime API 的运算方法与对象类型紧密对应，LocalDate、LocalTime、LocalDateTime、Instant 等都提供加减方法，同时提供比较、等价判断方法。

```java
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class OperationsExample {
    public static void main(String[] args) {
        LocalDate date = LocalDate.of(2026, 4, 1);
        LocalDate datePlus = date.plusDays(10).minusWeeks(1);

        LocalTime time = LocalTime.of(9, 15);
        LocalTime timeNext = time.plus(2, ChronoUnit.HOURS);

        LocalDateTime dateTime = LocalDateTime.of(date, time);
        LocalDateTime dateTimeLater = dateTime.plusDays(1).plusHours(2);

        boolean isBefore = dateTime.isBefore(dateTimeLater);
        Duration d = Duration.between(dateTime, dateTimeLater);

        System.out.println("日期变化: " + datePlus);
        System.out.println("时间变化: " + timeNext);
        System.out.println("日期时间比较: " + isBefore);
        System.out.println("时间差: " + d.toHours() + " 小时");
    }
}
```

要点：
- plusXxx、minusXxx 系列方法支持链式调用，返回新对象。
- isBefore、isAfter、isEqual 等方法用于比较。
- TemporalUnit（如 ChronoUnit.HOURS）提供灵活的单位运算。

---

## 0x09 与旧 API 的互操作
虽说 java.time 提供了替代方案，但在现实项目中仍需与旧 API 进行互操作。下面给出常见的互操作场景：Date/Instant 的互转，以及 LocalDateTime 与时区的互转；以及利用旧 API 的日常用法来演示互转。

```java
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Calendar;

public class InteropExample {
    public static void main(String[] args) {
        // Instant 与 Date 互转
        Instant now = Instant.now();
        Date dateFromInstant = Date.from(now);
        Instant instantFromDate = dateFromInstant.toInstant();

        // LocalDateTime 与时区无关的（UTC/系统默认时区）互转
        LocalDateTime ldt = LocalDateTime.now();
        Instant instantFromLdt = ldt.atZone(ZoneId.systemDefault()).toInstant();
        LocalDateTime ldtFromInstant = LocalDateTime.ofInstant(instantFromLdt, ZoneId.systemDefault());

        // Date 与 Calendar 的互转
        Date d = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        Date dFromCal = cal.getTime();

        System.out.println("Date <-> Instant: " + dateFromInstant + " | " + instantFromDate);
        System.out.println("LDT <-> Instant: " + ldtFromInstant + " | " + instantFromLdt);
        System.out.println("Date <-> Calendar: " + dFromCal);
    }
}
```

要点：
- Date.from(Instant) 与 Date.toInstant() 提供了简单的互转入口。
- LocalDateTime 可以通过 atZone(...) 与系统时区或指定时区进行对 Instant 的转换。
- Calendar 可以通过 setTime/getTime 与 Date 之间进行互转，尽管日常推荐优先使用 java.time Types。

---

# 参考链接
- Date-Time API 概览（Java 8+）：https://docs.oracle.com/javase/8/docs/api/java/time/package-summary.html
- LocalDate：https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html
- LocalTime：https://docs.oracle.com/javase/8/docs/api/java/time/LocalTime.html
- LocalDateTime：https://docs.oracle.com/javase/8/docs/api/java/time/LocalDateTime.html
- Instant：https://docs.oracle.com/javase/8/docs/api/java/time/Instant.html
- Duration：https://docs.oracle.com/javase/8/docs/api/java/time/Duration.html
- Period：https://docs.oracle.com/javase/8/docs/api/java/time/Period.html
- DateTimeFormatter：https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
- ZoneId：https://docs.oracle.com/javase/8/docs/api/java/time/ZoneId.html
- ZonedDateTime：https://docs.oracle.com/javase/8/docs/api/java/time/ZonedDateTime.html
- java.util.Date：https://docs.oracle.com/javase/8/docs/api/java/util/Date.html
- java.util.Calendar：https://docs.oracle.com/javase/8/docs/api/java/util/Calendar.html
