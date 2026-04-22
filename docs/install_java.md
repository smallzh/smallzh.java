# 如何安装 Java 和 JVMS？

## 0x01 OpenJDK 下载地址

以下是几个常用的 OpenJDK 发行版下载源：

| 发行版 | 地址 | 特点 |
|--------|------|------|
| Microsoft Build of OpenJDK | [https://learn.microsoft.com/zh-cn/java/openjdk/download](https://learn.microsoft.com/zh-cn/java/openjdk/download) | 微软维护，适合 Azure 环境 |
| Eclipse Temurin (Adoptium) | [https://adoptium.net/zh-CN/temurin/releases/](https://adoptium.net/zh-CN/temurin/releases/) | 社区维护，支持多平台 |
| OpenJDK 官网 | [https://openjdk.org/](https://openjdk.org/) | 官方源码构建 |
| Amazon Corretto | [https://aws.amazon.com/corretto/](https://aws.amazon.com/corretto/) | AWS 维护，长期支持 |
| Azul Zulu | [https://www.azul.com/downloads/](https://www.azul.com/downloads/) | 商业支持完善 |

> LTS (Long-Term Support) 版本：Java 8、11、17、21、25，建议生产环境使用 LTS 版本。

## 0x02 Windows 系统安装

### 方法一：使用安装包（推荐）

1. 从 [Adoptium](https://adoptium.net/zh-CN/temurin/releases/) 下载 Windows 安装包 (`.msi`)
2. 双击运行安装程序
3. 按向导提示完成安装

### 方法二：手动配置环境变量

1. 下载 ZIP 压缩包并解压到指定目录，如 `C:\Program Files\Java\jdk-17`
2. 配置环境变量：

```powershell
# 设置 JAVA_HOME
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-17", "Machine")

# 添加到 PATH
$path = [Environment]::GetEnvironmentVariable("Path", "Machine")
[Environment]::SetEnvironmentVariable("Path", "$path;%JAVA_HOME%\bin", "Machine")
```

3. 验证安装：

```shell
java -version
javac -version
```

### 方法三：使用包管理器

#### WinGet (Windows 10/11)

```shell
# 搜索可用的 JDK
winget search EclipseAdoptium.Temurin

# 安装 JDK 17
winget install EclipseAdoptium.Temurin.17.JDK

# 安装 JDK 21
winget install EclipseAdoptium.Temurin.21.JDK
```

#### Chocolatey

```shell
# 安装 JDK 17
choco install temurin17

# 安装 JDK 21
choco install temurin21
```

#### Scoop

```shell
# 添加 Java 仓库
scoop bucket add java

# 安装 JDK 17
scoop install temurin17-jdk

# 安装 JDK 21
scoop install temurin21-jdk
```

## 0x03 macOS 系统安装

### 方法一：使用 Homebrew（推荐）

```shell
# 安装 Homebrew（如未安装）
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# 搜索可用的 JDK
brew search temurin

# 安装 JDK 17
brew install --cask temurin@17

# 安装 JDK 21
brew install --cask temurin@21
```

### 方法二：手动安装

1. 从 [Adoptium](https://adoptium.net/zh-CN/temurin/releases/) 下载 macOS 安装包 (`.pkg`)
2. 双击运行安装程序
3. 按向导提示完成安装

### 方法三：使用 SDKMAN

```shell
# 安装 SDKMAN
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# 列出可用的 Java 版本
sdk list java

# 安装 JDK 17
sdk install java 17.0.9-tem

# 安装 JDK 21
sdk install java 21.0.1-tem

# 切换版本
sdk use java 17.0.9-tem
sdk default java 17.0.9-tem
```

## 0x04 Linux 系统安装

### Ubuntu/Debian

```shell
# 添加 Adoptium 仓库
sudo apt-get install -y wget apt-transport-https
wget -qO - https://packages.adoptium.net/artifactory/api/gpg/key/public | sudo apt-key add -
echo "deb https://packages.adoptium.net/artifactory/deb $(awk -F= '/^VERSION_CODENAME/{print$2}' /etc/os-release) main" | sudo tee /etc/apt/sources.list.d/adoptium.list

# 更新并安装
sudo apt-get update
sudo apt-get install temurin-17-jdk

# 验证
java -version
```

### CentOS/RHEL/Fedora

```shell
# 添加 Adoptium 仓库
cat <<EOF | sudo tee /etc/yum.repos.d/adoptium.repo
[Adoptium]
name=Adoptium
baseurl=https://packages.adoptium.net/artifactory/rpm/$(uname -m)/centos/$(rpm -E %rhel)/
enabled=1
gpgcheck=1
gpgkey=https://packages.adoptium.net/artifactory/api/gpg/key/public
EOF

# 安装 JDK 17
sudo yum install temurin-17-jdk

# 或使用 dnf（Fedora 等）
sudo dnf install temurin-17-jdk
```

### Arch Linux

```shell
# 从 AUR 安装
yay -S jdk17-temurin
# 或
paru -S jdk17-temurin
```

### 使用 SDKMAN（通用方法）

```shell
# 安装 SDKMAN
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# 安装 JDK
sdk install java 17.0.9-tem
sdk install java 21.0.1-tem

# 切换版本
sdk use java 17.0.9-tem
```

### 使用容器（Docker）

```dockerfile
# Dockerfile 示例
FROM eclipse-temurin:17-jdk-alpine

# 或使用特定版本
FROM eclipse-temurin:21-jdk
```

```shell
# 运行临时容器
docker run -it eclipse-temurin:17-jdk java -version
```

## 0x05 JVMS - Java 版本管理工具

[JVMS](https://github.com/ystyle/jvms) 是一个专门用于 Windows 系统的 Java 多版本管理工具，类似于 Node.js 的 nvm。

### JVMS 特点

- 无需依赖其他库，使用 Go 编写
- 不需要预先安装 JDK
- 使用符号链接实现版本切换
- 切换后在所有终端窗口生效
- 支持自定义 JDK 下载源

### 安装 JVMS

1. 从 [GitHub Releases](https://github.com/ystyle/jvms/releases) 下载最新版本
2. 解压并将 `jvms.exe` 复制到指定目录
3. 以管理员身份运行 CMD 或 PowerShell
4. 初始化 JVMS：

```powershell
# 进入 jvms.exe 所在目录
cd C:\Tools\jvms

# 初始化
.\jvms.exe init
```

### JVMS 常用命令

```shell
# 查看所有可用命令
jvms help

# 列出已安装的 JDK
jvms list
# 或简写
jvms ls

# 查看可下载的 JDK 版本
jvms rls

# 安装指定版本
jvms install 17.0.9
# 或简写
jvms i 17.0.9

# 切换到指定版本
jvms switch 17.0.9
# 或简写
jvms s 17.0.9

# 删除指定版本
jvms remove 17.0.9
# 或简写
jvms rm 17.0.9

# 设置代理
jvms proxy http://proxy.example.com:8080
```

### 添加本地 JDK 版本

如果你已经有本地的 JDK 安装，可以将其添加到 JVMS 管理：

```powershell
# 1. 复制 JDK 文件夹到 jvms/store 目录
# 例如：复制到 C:\Tools\jvms\store\17.0.1

# 2. 重命名文件夹为版本号（如 17.0.1）

# 3. 检查是否识别
jvms list

# 4. 切换使用
jvms switch 17.0.1

# 5. 验证
java -version
```

### 支持的 JDK 发行版

JVMS 支持多种 JDK 发行版：

- Oracle JDK（默认索引，仅 LTS 版本）
- Amazon Corretto（默认索引，仅 LTS 版本）
- Eclipse Temurin (Adoptium)（通过 API 动态获取）
- Azul Zulu（通过 API 动态获取）

### 自定义下载服务器

如需在内网环境使用，可搭建私有 JDK 下载服务器：

1. 创建 `index.json` 文件：

```json
[
  {
    "version": "17.0.9",
    "url": "http://internal.server/jdk/17.0.9.zip"
  },
  {
    "version": "21.0.1",
    "url": "http://internal.server/jdk/21.0.1.zip"
  }
]
```

2. 部署到静态文件服务器（如 Nginx）

3. 初始化时指定自定义源：

```powershell
jvms init --originalpath http://internal.server/index.json

# 修改默认 JAVA_HOME 路径
jvms init --java_home D:\Java
```

### macOS/Linux 的替代方案

macOS 和 Linux 用户可以使用以下工具：

#### SDKMAN（推荐）

```shell
# 安装
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# 使用
sdk list java
sdk install java 17.0.9-tem
sdk use java 17.0.9-tem
sdk default java 17.0.9-tem
```

#### jEnv（macOS/Linux）

```shell
# macOS 安装
brew install jenv

# 配置
export PATH="$HOME/.jenv/bin:$PATH"
eval "$(jenv init -)"

# 添加 JDK
jenv add /Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home

# 切换版本
jenv global 17.0
jenv local 17.0  # 项目级别
```

## 0x06 验证安装

安装完成后，验证 Java 是否正确安装：

```shell
# 查看 Java 运行时版本
java -version

# 查看 Java 编译器版本
javac -version

# 查看 JAVA_HOME 路径（Windows）
echo %JAVA_HOME%

# 查看 JAVA_HOME 路径（macOS/Linux）
echo $JAVA_HOME
```

## 参考

1. [JVMS GitHub](https://github.com/ystyle/jvms)
2. [Adoptium 官方文档](https://adoptium.net/zh-CN/docs/)
3. [SDKMAN 官方文档](https://sdkman.io/usage)
4. [Microsoft OpenJDK](https://learn.microsoft.com/zh-cn/java/openjdk/)
