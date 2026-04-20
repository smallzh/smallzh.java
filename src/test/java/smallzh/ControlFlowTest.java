package smallzh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 基于 control_flow.md 文档的单元测试
 * 覆盖：条件语句、循环语句、跳转语句、增强for、switch表达式
 */
@DisplayName("控制流测试")
class ControlFlowTest {

    // --- 0x01 条件语句 if-else ---
    @Test
    @DisplayName("if-else 成绩等级判断")
    void testIfElseGrade() {
        int score = 85;
        char grade;
        if (score >= 90) {
            grade = 'A';
        } else if (score >= 80) {
            grade = 'B';
        } else {
            grade = 'C';
        }
        assertEquals('B', grade);
    }

    @Test
    @DisplayName("switch 语句判断周几")
    void testSwitchStatement() {
        int day = 3;
        String result;
        switch (day) {
            case 1: result = "周一"; break;
            case 2: result = "周二"; break;
            case 3: result = "周三"; break;
            default: result = "其他日子";
        }
        assertEquals("周三", result);
    }

    // --- 0x02 循环语句 ---
    @Test
    @DisplayName("for 循环累加")
    void testForLoop() {
        int sum = 0;
        for (int i = 1; i <= 5; i++) {
            sum += i;
        }
        assertEquals(15, sum);
    }

    @Test
    @DisplayName("while 循环计数")
    void testWhileLoop() {
        int n = 0;
        int count = 0;
        while (n < 5) {
            count++;
            n++;
        }
        assertEquals(5, count);
    }

    @Test
    @DisplayName("do-while 至少执行一次")
    void testDoWhileLoop() {
        int m = 10; // 已超过条件
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(m);
            m++;
        } while (m < 10);
        assertEquals("10", sb.toString()); // 执行了一次
    }

    // --- 0x03 跳转语句 ---
    @Test
    @DisplayName("break 跳出循环")
    void testBreak() {
        int result = 0;
        for (int i = 0; i < 10; i++) {
            if (i == 5) break;
            result = i;
        }
        assertEquals(4, result);
    }

    @Test
    @DisplayName("continue 跳过偶数")
    void testContinue() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) continue;
            sb.append(i);
        }
        assertEquals("13579", sb.toString());
    }

    @Test
    @DisplayName("return 返回最大值")
    void testReturnMax() {
        assertEquals(12, max(7, 12));
        assertEquals(7, max(7, 3));
    }

    static int max(int a, int b) {
        if (a > b) return a;
        else return b;
    }

    // --- 0x04 增强型 for 循环 ---
    @Test
    @DisplayName("for-each 遍历数组")
    void testEnhancedForArray() {
        int[] nums = {1, 2, 3, 4};
        int sum = 0;
        for (int n : nums) {
            sum += n;
        }
        assertEquals(10, sum);
    }

    @Test
    @DisplayName("for-each 遍历 List")
    void testEnhancedForList() {
        java.util.List<String> names = java.util.List.of("Alice", "Bob", "Charlie");
        int count = 0;
        for (String name : names) {
            count++;
        }
        assertEquals(3, count);
    }

    // --- 0x05 Switch 表达式（Java 14+） ---
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
    @DisplayName("switch 表达式 yield 块语法")
    void testSwitchExpressionYield() {
        int code = 42;
        String msg = switch (code) {
            case 0 -> "成功";
            case 1 -> "未找到";
            default -> {
                int computed = code * 2;
                yield "错误码：" + computed;
            }
        };
        assertEquals("错误码：84", msg);
    }
}