# 图片EXIF水印工具

这是一个Java命令行工具，可以读取图片的EXIF信息中的拍摄时间，并将其作为水印添加到图片上。

## 功能特点

- 读取图片EXIF信息中的拍摄日期（年月日）
- 支持自定义水印字体大小
- 支持自定义水印颜色（黑、白、红、绿、蓝、黄）
- 支持自定义水印位置（左上角、右上角、左下角、右下角、居中）
- 处理后的图片保存在原目录的子目录中

## 使用方法

### 编译项目

```bash
mvn clean package
```

### 运行程序

```bash
java -jar target/photo-watermark-1.0-SNAPSHOT-jar-with-dependencies.jar -i <图片路径> [选项]
```

### 命令行选项

- `-i, --input <path>` - 输入图片文件或目录路径（必需）
- `-s, --size <size>` - 水印字体大小（默认：36）
- `-c, --color <color>` - 水印颜色（默认：white，可选：black, white, red, green, blue, yellow）
- `-p, --position <position>` - 水印位置（默认：bottomRight，可选：topLeft, topRight, bottomLeft, bottomRight, center）
- `-h, --help` - 显示帮助信息

### 示例

处理单个图片：
```bash
java -jar target/photo-watermark-1.0-SNAPSHOT-jar-with-dependencies.jar -i photo.jpg
```

处理目录中的所有图片：
```bash
java -jar target/photo-watermark-1.0-SNAPSHOT-jar-with-dependencies.jar -i photos/
```

自定义水印：
```bash
java -jar target/photo-watermark-1.0-SNAPSHOT-jar-with-dependencies.jar -i photos/ -s 48 -c red -p topRight
```

## 系统要求

- Java 11 或更高版本
- Maven（用于构建）