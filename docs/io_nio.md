# OpenJDK17 的 IO 与 NIO 基本知识总结

本文面向初学者，聚焦在实际使用场景中的常见 IO 与 NIO 概念、类与 API。每个知识点都附有最小可运行的代码示例，便于快速上手。

## 0x01 IO流概述

- 字节流（InputStream/OutputStream）用于处理原始二进制数据。
- 字符流（Reader/Writer）用于处理文本数据，内部通常会进行字符编码与解码。

示例：字节流复制、字符流复制、以及从字节流到字符流的转换。

```java
// 字节流：二进制文件复制
try (InputStream in = new FileInputStream("data.bin");
     OutputStream out = new FileOutputStream("copy.bin")) {
  byte[] buf = new byte[4096];
  int n;
  while ((n = in.read(buf)) != -1) {
    out.write(buf, 0, n);
  }
}
```

```java
// 字符流：文本文件逐字符复制
try (Reader reader = new FileReader("data.txt");
     Writer writer = new FileWriter("copy.txt")) {
  char[] buf = new char[4096];
  int len;
  while ((len = reader.read(buf)) != -1) {
    writer.write(buf, 0, len);
  }
}
```

```java
// 字节流转字符流：按 UTF-8 读取文本
try (InputStream in = new FileInputStream("data.txt");
     Reader r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
  int ch;
  while ((ch = r.read()) != -1) {
    System.out.print((char) ch);
  }
}
```

参考要点
- 使用字节流处理非文本数据时，不要误用字符流。
- 选择合适的缓冲层可以提升性能，例如后续章节中的缓冲流。

## 0x02 文件读写

- 直接使用字节流处理文件数据，或使用字符流处理文本内容。
- 对文本写入时可显式指定字符编码。

示例：文本写入与文本读取（默认是系统编码，建议显式 UTF-8）

```java
// 写文本到文件，指定编码
try (Writer writer = new OutputStreamWriter(new FileOutputStream("output.txt"), StandardCharsets.UTF_8)) {
  writer.write("Hello, 你好\n");
  writer.write("OpenJDK17 IO 示例");
}
```

```java
// 读取文本内容
try (Reader reader = new InputStreamReader(new FileInputStream("output.txt"), StandardCharsets.UTF_8)) {
  char[] buf = new char[1024];
  int n;
  while ((n = reader.read(buf)) != -1) {
    System.out.print(new String(buf, 0, n));
  }
}
```

## 0x03 缓冲流

- 缓冲流通过缓冲区减小 I/O 次数，提升性能。
- 常用组合：BufferedInputStream、BufferedOutputStream、BufferedReader、BufferedWriter。

示例：字节缓冲流与文本缓冲流

```java
// 字节缓冲流组合
try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream("data.bin"));
     BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("copy.bin"))) {
  byte[] buf = new byte[8192];
  int n;
  while ((n = bis.read(buf)) != -1) {
    bos.write(buf, 0, n);
  }
}
```

```java
// 文本缓冲流组合：逐行读取
try (BufferedReader br = new BufferedReader(new FileReader("data.txt"));
     BufferedWriter bw = new BufferedWriter(new FileWriter("copy.txt"))) {
  String line;
  while ((line = br.readLine()) != null) {
    bw.write(line);
    bw.newLine();
  }
}
```

## 0x04 对象序列化

- 通过实现 Serializable 接口，可以将对象写入磁盘或网络传输。
- 反序列化时要确保类的版本兼容性。

示例：可序列化的简单对象，以及序列化/反序列化过程

```java
import java.io.Serializable;

public class Person implements Serializable {
  private static final long serialVersionUID = 1L;
  private String name;
  private int age;

  public Person(String name, int age) { this.name = name; this.age = age; }
  public String getName() { return name; }
  public int getAge() { return age; }
}
```

```java
// 序列化
Person p = new Person("Alice", 30);
try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("person.ser"))) {
  oos.writeObject(p);
}
```

```java
// 反序列化
try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("person.ser"))) {
  Person p = (Person) ois.readObject();
  System.out.println(p.getName() + " " + p.getAge());
}
```

注意：同一个项目中若 Class 改动较大，版本控制和兼容性需谨慎处理。

## 0x05 NIO 概述

- NIO 核心概念包括 Buffer、Channel、Selector，适合高性能、非阻塞式 I/O。
- ByteBuffer/CharBuffer、FileChannel、SocketChannel 等是核心组件。

示例：Buffer、Channel 的基本使用

```java
import java.nio.ByteBuffer;

ByteBuffer buffer = ByteBuffer.allocate(1024);
buffer.put((byte)1);
buffer.put((byte)2);
buffer.flip();
while (buffer.hasRemaining()) {
  System.out.print(buffer.get() + " ");
}
```

```java
// 使用 FileChannel 进行文件复制
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

try (FileChannel in = new FileInputStream("input.txt").getChannel();
     FileChannel out = new FileOutputStream("output.txt").getChannel()) {
  in.transferTo(0, in.size(), out);
}
```

```java
// 简单的非阻塞服务器示例（Selector 相关代码片段）
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.nio.*;

Selector selector = Selector.open();
ServerSocketChannel server = ServerSocketChannel.open();
server.bind(new InetSocketAddress(8080));
server.configureBlocking(false);
server.register(selector, SelectionKey.OP_ACCEPT);

while (true) {
  selector.select();
  for (SelectionKey key : selector.selectedKeys()) {
    if (key.isAcceptable()) {
      ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
      SocketChannel sc = ssc.accept();
      sc.configureBlocking(false);
      sc.register(selector, SelectionKey.OP_READ);
    } else if (key.isReadable()) {
      // 读取数据
    }
  }
  selector.selectedKeys().clear();
}
```

注释:
- 生产环境中使用选择器时需要处理并发、缓冲区大小、异常等细节。
- 本示例仅展示核心 API 的用法。

## 0x06 Path 与 Files 工具类

- Java 7 引入的 Path、Files 提供对文件系统的便捷操作。
- 常见操作包括创建目录、创建文件、读取写入、删除、拷贝等。

示例：创建目录、写入、读取、拷贝

```java
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

Path dir = Paths.get("data");
Path file = dir.resolve("example.txt");

Files.createDirectories(dir);
Files.write(file, "Hello, World!".getBytes(StandardCharsets.UTF_8));
System.out.println(Files.readAllLines(file, StandardCharsets.UTF_8));
```

```java
// 拷贝与删除
Path copy = Paths.get("example_copy.txt");
Files.copy(file, copy, StandardCopyOption.REPLACE_EXISTING);
Files.deleteIfExists(copy);
```

示例：目录遍历与文件操作

```java
// 列出目录中的普通文件
try (java.util.stream.Stream<Path> stream = Files.list(dir)) {
  stream.forEach(System.out::println);
}

// 递归遍历
try {
  Files.walk(dir).forEach(System.out::println);
} catch (IOException e) {
  e.printStackTrace();
}
```

## 0x07 文件操作（创建、删除、遍历目录等）

- 除了 Path/Files 之外，还可以直接使用一些 Files 的方法完成常见操作。
- 注意：删除目录时要确保目录为空，或者使用递归删除策略。

示例：创建/删除/遍历

```java
// 创建空目录
Path newDir = Paths.get("logs");
Files.createDirectory(newDir);

// 删除空目录
Files.delete(newDir);

// 遍历当前目录的文件与子目录
Files.walk(Paths.get("."))
  .filter(Files::isRegularFile)
  .forEach(System.out::println);
```

## 0x08 字符编码

- 了解字符集（Charset）及常用编码（如 UTF-8、ISO-8859-1）对文本处理至关重要。
- 读写时显式指定编码，避免平台默认编码带来的兼容性问题。

示例：编码与解码

```java
import java.nio.charset.StandardCharsets;

String s = "中文文本";
byte[] utf8 = s.getBytes(StandardCharsets.UTF_8);
String restored = new String(utf8, StandardCharsets.UTF_8);
System.out.println(restored);
```

```java
// 使用指定编码写入与读取
try (java.io.Writer w = new java.io.OutputStreamWriter(new java.io.FileOutputStream("utf8.txt"), StandardCharsets.UTF_8);
     java.io.Reader r = new java.io.InputStreamReader(new java.io.FileInputStream("utf8.txt"), StandardCharsets.UTF_8)) {
  w.write("编码测试：中文字符");
  w.flush();
  char[] buf = new char[1024];
  int n = r.read(buf);
  System.out.print(new String(buf, 0, n));
}
```

## 参考链接

- Java IO 基础概念（InputStream/OutputStream、Reader/Writer、缓冲流等）  
  https://docs.oracle.com/javase/17/docs/api/java/io/package-summary.html

- Java NIO 基本概念（Buffer、Channel、Selector）  
  https://docs.oracle.com/javase/17/docs/api/java/nio/package-summary.html

- Java NIO.2（Path、Files、FileSystem 等）  
  https://docs.oracle.com/javase/17/docs/api/java/nio/file/package-summary.html

- Java SE 学习资源与示例库（官方文档入口）  
  https://docs.oracle.com/javase/17/

如需扩展到特定场景的示例（如网络 I/O、文件内存映射、并发 I/O 等），可以继续扩展相应章节。
