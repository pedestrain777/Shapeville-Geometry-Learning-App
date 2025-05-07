# Shapeville - Interactive Geometry Learning Application

## Requirements

- Java 17 or higher
- Maven 3.8 or higher 重要❗需要官网下载并配置环境变量。链接：https://maven.apache.org/download.cgi 下载	apache-maven-3.9.9-bin.zip即可，解压后在环境变量中增加其bin目录地址
- JavaFX 17.0.2

## Building the Project

1. Clone the repository: 先从github上克隆下来(直接下载zip也行)
   ```bash
   git clone [repository-url]
   cd shapeville
   ```

2. Build with Maven:(命令行切换到项目根目录，然后执行底下这句话，需要先下载并配置mvn)
   ```bash
   mvn clean install
   ```

## Running the Application

Run the application using Maven:(上面那句话没问题就接着执行这句话，理论上就能运行起来了)
```bash
mvn javafx:run
```

## Project Structure

```
Shapeville/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── model/      # Data models
│   │   │   ├── view/       # UI components
│   │   │   └── controller/ # Game logic
│   │   └── resources/      # Images and resources
│   └── test/              # Test files
└── pom.xml               # Maven configuration
```
