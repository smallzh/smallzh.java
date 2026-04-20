package smallzh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.*;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 基于 io_nio.md 文档的单元测试
 * 覆盖：字节流、字符流、缓冲流、NIO Buffer/Channel、Path/Files、字符编码
 */
@DisplayName("IO与NIO测试")
class IoNioTest {

    // --- 0x01 字节流 ---
    @Test
    @DisplayName("字节流复制文件")
    void testByteStreamCopy() throws Exception {
        Path dir = Files.createTempDirectory("nio_test");
        Path src = dir.resolve("data.bin");
        Path dst = dir.resolve("copy.bin");

        Files.write(src, new byte[]{1, 2, 3, 4, 5});

        try (InputStream in = new FileInputStream(src.toFile());
             OutputStream out = new FileOutputStream(dst.toFile())) {
            byte[] buf = new byte[4096];
            int n;
            while ((n = in.read(buf)) != -1) {
                out.write(buf, 0, n);
            }
        }

        assertArrayEquals(new byte[]{1, 2, 3, 4, 5}, Files.readAllBytes(dst));
    }

    // --- 0x02 文件读写 ---
    @Test
    @DisplayName("字符流写入与读取")
    void testCharStreamWriteRead() throws Exception {
        Path dir = Files.createTempDirectory("char_test");
        Path file = dir.resolve("output.txt");

        // 写
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file.toFile()), StandardCharsets.UTF_8)) {
            writer.write("Hello, 你好\n");
            writer.write("OpenJDK17 IO 示例");
        }

        // 读
        String content = Files.readString(file, StandardCharsets.UTF_8);
        assertTrue(content.contains("Hello, 你好"));
        assertTrue(content.contains("OpenJDK17"));
    }

    // --- 0x03 缓冲流 ---
    @Test
    @DisplayName("BufferedReader 逐行读取")
    void testBufferedReader() throws Exception {
        Path dir = Files.createTempDirectory("buffer_test");
        Path file = dir.resolve("data.txt");
        Files.writeString(file, "第一行\n第二行\n第三行", StandardCharsets.UTF_8);

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file.toFile(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append(";");
            }
        }
        assertEquals("第一行;第二行;第三行;", sb.toString());
    }

    // --- 0x05 NIO Buffer ---
    @Test
    @DisplayName("ByteBuffer 基本操作")
    void testByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put((byte) 1);
        buffer.put((byte) 2);
        buffer.flip();
        assertEquals(1, buffer.get());
        assertEquals(2, buffer.get());
    }

    // --- 0x06 Path 与 Files ---
    @Test
    @DisplayName("Files 创建目录、写入、读取")
    void testPathAndFiles() throws Exception {
        Path dir = Files.createTempDirectory("files_test");
        Path file = dir.resolve("example.txt");

        Files.writeString(file, "Hello, World!", StandardCharsets.UTF_8);
        String content = Files.readString(file, StandardCharsets.UTF_8);
        assertEquals("Hello, World!", content);
    }

    @Test
    @DisplayName("Files 拷贝与删除")
    void testFilesCopyDelete() throws Exception {
        Path dir = Files.createTempDirectory("copy_test");
        Path file = dir.resolve("original.txt");
        Files.writeString(file, "test content");

        Path copy = dir.resolve("copy.txt");
        Files.copy(file, copy, StandardCopyOption.REPLACE_EXISTING);
        assertEquals("test content", Files.readString(copy));

        Files.deleteIfExists(copy);
        assertFalse(Files.exists(copy));
    }

    // --- 0x08 字符编码 ---
    @Test
    @DisplayName("UTF-8 编码与解码")
    void testCharset() {
        String s = "中文文本";
        byte[] utf8 = s.getBytes(StandardCharsets.UTF_8);
        String restored = new String(utf8, StandardCharsets.UTF_8);
        assertEquals("中文文本", restored);
    }
}