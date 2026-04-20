package smallzh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 基于 date_time_api.md 文档的单元测试
 * 覆盖：LocalDate/LocalTime/LocalDateTime、Instant、Duration/Period、
 *       DateTimeFormatter、ZonedDateTime
 */
@DisplayName("日期时间API测试")
class DateTimeApiTest {

    // --- 0x03 LocalDate / LocalTime / LocalDateTime ---
    @Test
    @DisplayName("LocalDate 创建与操作")
    void testLocalDate() {
        LocalDate date = LocalDate.of(2026, 4, 1);
        LocalDate nextWeek = date.plusDays(7);
        assertEquals("2026-04-01", date.toString());
        assertEquals("2026-04-08", nextWeek.toString());
    }

    @Test
    @DisplayName("LocalTime 创建与操作")
    void testLocalTime() {
        LocalTime time = LocalTime.of(14, 30, 0);
        LocalTime timePlusOneHour = time.plusHours(1);
        assertEquals("14:30", time.toString().substring(0, 5));
        assertEquals("15:30", timePlusOneHour.toString().substring(0, 5));
    }

    @Test
    @DisplayName("LocalDateTime 组合与操作")
    void testLocalDateTime() {
        LocalDate date = LocalDate.of(2026, 4, 1);
        LocalTime time = LocalTime.of(14, 30, 0);
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        LocalDateTime updated = dateTime.plusDays(2).plusHours(3);
        assertEquals("2026-04-03T17:30", updated.toString());
    }

    @Test
    @DisplayName("LocalDate.parse 解析")
    void testLocalDateParse() {
        LocalDate parsedDate = LocalDate.parse("2026-04-01");
        assertEquals(2026, parsedDate.getYear());
        assertEquals(4, parsedDate.getMonthValue());
    }

    // --- 0x04 Instant ---
    @Test
    @DisplayName("Instant 创建与操作")
    void testInstant() {
        Instant now = Instant.now();
        Instant later = now.plusSeconds(3600);
        assertTrue(later.isAfter(now));
        assertEquals(3600, later.getEpochSecond() - now.getEpochSecond());
    }

    // --- 0x05 Duration 与 Period ---
    @Test
    @DisplayName("Duration 计算时间差")
    void testDuration() {
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime end = start.plusHours(5).plusMinutes(30);
        Duration duration = Duration.between(start, end);
        assertEquals(5, duration.toHours());
        assertEquals(330, duration.toMinutes());
    }

    @Test
    @DisplayName("Period 计算日期间隔")
    void testPeriod() {
        LocalDate date1 = LocalDate.of(2020, 1, 1);
        LocalDate date2 = date1.plusMonths(3).plusDays(10);
        Period period = Period.between(date1, date2);
        assertEquals(3, period.getMonths());
        assertEquals(10, period.getDays());
    }

    // --- 0x06 DateTimeFormatter ---
    @Test
    @DisplayName("DateTimeFormatter 格式化与解析")
    void testDateTimeFormatter() {
        LocalDateTime now = LocalDateTime.of(2026, 4, 1, 15, 20, 30);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss", Locale.CHINESE);
        String text = now.format(fmt);
        assertEquals("2026/04/01 15:20:30", text);

        LocalDateTime parsed = LocalDateTime.parse("2026/04/01 15:20:30", fmt);
        assertEquals(now, parsed);
    }

    // --- 0x07 ZonedDateTime ---
    @Test
    @DisplayName("ZonedDateTime 时区处理")
    void testZonedDateTime() {
        ZoneId shanghai = ZoneId.of("Asia/Shanghai");
        ZonedDateTime zdtShanghai = ZonedDateTime.of(2026, 4, 1, 12, 0, 0, 0, shanghai);

        // 时区转换
        ZonedDateTime zdtNewYork = zdtShanghai.withZoneSameInstant(ZoneId.of("America/New_York"));
        // 上海12:00 对应纽约不同时间（取决于夏令时）
        assertNotEquals(zdtShanghai.getZone(), zdtNewYork.getZone());
    }

    // --- 0x08 日期时间运算 ---
    @Test
    @DisplayName("日期时间加减与比较")
    void testDateTimeOperations() {
        LocalDate date = LocalDate.of(2026, 4, 1);
        LocalDate datePlus = date.plusDays(10).minusWeeks(1);
        assertEquals("2026-04-04", datePlus.toString());

        LocalDateTime dateTime = LocalDateTime.of(2026, 4, 1, 9, 15);
        LocalDateTime dateTimeLater = dateTime.plusDays(1).plusHours(2);
        assertTrue(dateTime.isBefore(dateTimeLater));
    }
}