# OpenJDK17 Text Blocks（文本块）知识库

本文面向初学者，聚焦 Text Blocks 在 OpenJDK17 中的实际用法与注意点。通过对比传统字符串的问题，逐步讲解语法、转义、格式化和在常见文本场景中的应用。每个知识点都配有可直接运行的 Java 代码示例。

## 0x01 传统字符串的问题

- 多行文本拼接难以阅读，容易造成格式错乱；需要手动在每行末尾添加换行符与拼接符。
- 需要大量转义字符（如引号、换行、反斜线），阅读性差，维护成本高。

示例：用传统字符串表示多行文本
```java
String htmlOld = "<div>\n" +
                 "  <p>Hello</p>\n" +
                 "</div>";
```

示例：用传统字符串表示 JSON
```java
String jsonOld = "{\n" +
                 "  \"name\": \"Alice\",\n" +
                 "  \"age\": 30\n" +
                 "}";
```

这些写法在文本较多时会显得笨拙且易错，且和代码结构的缩进不易对齐，阅读体验差。

## 0x02 Text Blocks 简介

- Text Blocks 是 Java 语言在 Java 13+ 引入的用于书写多行字符串的语法糖。它允许把文本直接写在多行中，提升可读性。
- 适合嵌入 HTML、SQL、JSON 等需要多行文本的场景，同时可以结合 stripIndent() 等方法管理缩进。

示例：简单的文本块
```java
String greeting = """
    你好，世界！
    这是一个 Text Block 的示例。
    """;
```

示例：嵌入多行 HTML
```java
String htmlBlock = """
    <html>
      <head><title>示例</title></head>
      <body>
        <h1>Hello</h1>
      </body>
    </html>
    """;
```

提示：文本块本质上是字符串常量，编辑时仍需注意缩进、引号和转义。

## 0x03 基本语法（三引号）

- 以三个连续的双引号开始和结束文本块。
- 文本块的第一行通常从换行开始，后续行按源代码的缩进规则处理。
- 可以在文本块中直接包含引号，不需要像传统字符串那样逐行拼接。

示例：带缩进的 HTML 模板
```java
String template = """
    <ul>
      <li>项1</li>
      <li>项2</li>
    </ul>
    """;
```

说明：
- 文本块的缩进会被编译器处理，若希望统一对齐且不受源代码缩进影响，可以在末尾添加 .stripIndent()。

示例：带变量的文本块（结合格式化）
```java
String name = "小明";
String message = """
    你好，%s！欢迎使用 Text Blocks。
    """.formatted(name);
```

## 0x04 转义字符处理

- 文本块中的反斜杠转义行为与普通字符串一致，遇到无效转义会编译错误，因此需要正确转义。
- 避免在文本块中误用未支持的转义序列。

示例：包含反斜杠的路径
```java
String path = """
    C:\\Program Files\\Java\\openjdk-17
    """;
```

示例：包含换行的转义字符
```java
String withEscape = """
    这是一段包含转义的文本：\\n实际显示为两字符
    """;
```

示例：文本块内的引号
```java
String quote = """
    他对她说："你好！"
    """;
```

注意：若文本块中需要实际的换行，请直接在文本块中换行；若需要保留特定的转义序列，请确保转义正确。

## 0x05 Text Blocks 与格式化

- 缩进控制：stripIndent() 可以去除文本块共同的前导缩进，便于代码美观且文本内容保持预期格式。
- 动态内容：可以使用 .formatted(...) 将变量插入文本块。
- 调整文本块的缩进：indent(int n) 可以为文本块的每一行增加指定数量的空格。

示例：stripIndent 的典型用法
```java
String email = """
    From: user@example.com
    To: recipient@example.com
    Subject: 测试
    Body:
        你好，
        这是一封示例邮件。
    """.stripIndent();
```

示例：将文本块整段向右缩进
```java
String indented = """
    line one
    line two
    """.indent(4);
```

示例：文本块中插入变量
```java
String user = "Alice";
String welcome = """
    Welcome, %s!
    """.formatted(user);
```

注意：formatted() 以及 stripIndent()/indent() 在 Java 17 中均可使用。文本块适合嵌入模板化文本，但对于复杂的模板，仍可结合 Java 的模板方法或外部模板引擎。

## 0x06 Text Blocks 与 SQL、JSON、HTML

- SQL：使用文本块书写固定的查询，同时通过 PreparedStatement 设置参数，避免拼接导致的 SQL 注入风险。
- JSON/HTML：直接将结构化文本写在文本块中，便于可读性，但涉及动态数据时要小心转义与编码。

示例：SQL 查询（带占位符参数）
```java
String sql = """
    SELECT id, username
    FROM users
    WHERE email = ?
    """;

PreparedStatement ps = connection.prepareStatement(sql);
ps.setString(1, userEmail);
```

示例：JSON 文本块
```java
String userName = "Alice";
String json = """
    {
      "name": "%s",
      "age": 28,
      "roles": ["user","admin"]
    }
    """.formatted(userName);
```

示例：HTML 页面模板
```java
String pageTitle = "欢迎页面";
String htmlPage = """
    <!doctype html>
    <html lang="zh-CN">
      <head>
        <meta charset="UTF-8">
        <title>%s</title>
      </head>
      <body>
        <h1>%s</h1>
      </body>
    </html>
    """.formatted(pageTitle, pageTitle);
```

提示：
- 对于 SQL，优先使用 PreparedStatement，避免将变量直接拼接进文本块。
- 对于 JSON/HTML，若包含来自不可信源的动态数据，请进行适当的编码/转义。

## 0x07 Text Blocks 的最佳实践

- 优先使用文本块书写长文本，避免拼接，提升可读性。
- 使用 .stripIndent() 去除公共前导缩进，确保文本内容在输出时不受源代码缩进影响。
- 使用 .formatted(...) 将动态内容注入文本块，保持代码整洁。
- 避免在文本块中嵌入过多模板逻辑；对动态数据使用适当的编码或模板机制。
- 对 SQL 使用 PreparedStatement，避免直接拼接文本块中的变量。
- 记得在需要时结合文本块与外部模板或资源文件，以保持文本的可维护性和本地化能力。
- 兼容性注意：Text Blocks 适用于 Java 15+，OpenJDK17 已为稳定特性，尽量在代码库中统一采用文本块而非多行字符串拼接。

示例：在 JSON/HTML 中混合静态文本和动态数据
```java
String userName = "Alice";
String json = """
    {
      "name": "%s",
      "age": %d
    }
    """.formatted(userName, 30);
```

参考该文本块的最佳实践后，可以在实际项目中逐步替换旧的多行拼接字符串，提升可读性和维护性。

## 参考链接

- Java 官方入门教程中的文本块章节（介绍多行字符串与基本用法）
  - https://docs.oracle.com/javase/tutorial/java/nutsandbolts/textblocks.html
- Java 17 Text Blocks 官方概览与示例
  - https://docs.oracle.com/en/java/javase/17/text-blocks/overview.html
