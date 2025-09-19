package com.watermark;

import org.apache.commons.cli.*;
import org.apache.commons.imaging.ImageReadException;

import java.io.File;
import java.io.IOException;

/**
 * 主程序类，处理命令行参数并协调各个组件
 */
public class PhotoWatermark {
    public static void main(String[] args) {
        // 创建命令行选项
        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        
        try {
            CommandLine cmd = parser.parse(options, args);
            
            // 显示帮助信息
            if (cmd.hasOption("h")) {
                formatter.printHelp("PhotoWatermark", options);
                return;
            }
            
            // 获取输入路径
            String inputPath = cmd.getOptionValue("input");
            if (inputPath == null) {
                System.err.println("请提供输入路径 (-i 或 --input)");
                formatter.printHelp("PhotoWatermark", options);
                return;
            }
            
            // 获取水印配置
            WatermarkConfig config = new WatermarkConfig();
            
            if (cmd.hasOption("size")) {
                config.setFontSize(Integer.parseInt(cmd.getOptionValue("size")));
            }
            
            if (cmd.hasOption("color")) {
                config.setColor(cmd.getOptionValue("color"));
            }
            
            if (cmd.hasOption("position")) {
                config.setPosition(cmd.getOptionValue("position"));
            }
            
            // 处理图片
            processImages(inputPath, config);
            
        } catch (ParseException e) {
            System.err.println("解析命令行参数错误: " + e.getMessage());
            formatter.printHelp("PhotoWatermark", options);
        } catch (Exception e) {
            System.err.println("处理图片时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 创建命令行选项
     */
    private static Options createOptions() {
        Options options = new Options();
        
        Option input = new Option("i", "input", true, "输入图片文件或目录路径");
        input.setRequired(false);
        options.addOption(input);
        
        Option fontSize = new Option("s", "size", true, "水印字体大小 (默认: 36)");
        fontSize.setRequired(false);
        options.addOption(fontSize);
        
        Option color = new Option("c", "color", true, "水印颜色 (默认: white, 可选: black, white, red, green, blue, yellow)");
        color.setRequired(false);
        options.addOption(color);
        
        Option position = new Option("p", "position", true, "水印位置 (默认: bottomRight, 可选: topLeft, topRight, bottomLeft, bottomRight, center)");
        position.setRequired(false);
        options.addOption(position);
        
        Option help = new Option("h", "help", false, "显示帮助信息");
        options.addOption(help);
        
        return options;
    }
    
    /**
     * 处理图片
     */
    private static void processImages(String inputPath, WatermarkConfig config) throws IOException, ImageReadException {
        File input = new File(inputPath);
        
        if (!input.exists()) {
            throw new IOException("输入路径不存在: " + inputPath);
        }
        
        ImageProcessor processor = new ImageProcessor(config);
        
        if (input.isDirectory()) {
            // 处理目录中的所有图片
            File[] files = input.listFiles((dir, name) -> {
                String lowercaseName = name.toLowerCase();
                return lowercaseName.endsWith(".jpg") || 
                       lowercaseName.endsWith(".jpeg") || 
                       lowercaseName.endsWith(".png");
            });
            
            if (files == null || files.length == 0) {
                System.out.println("目录中没有找到支持的图片文件");
                return;
            }
            
            // 创建输出目录
            String outputDirName = input.getName() + "_watermark";
            File outputDir = new File(input, outputDirName);
            if (!outputDir.exists() && !outputDir.mkdirs()) {
                throw new IOException("无法创建输出目录: " + outputDir.getAbsolutePath());
            }
            
            System.out.println("开始处理 " + files.length + " 个图片文件...");
            int successCount = 0;
            
            for (File file : files) {
                try {
                    processor.processImage(file, outputDir);
                    successCount++;
                    System.out.println("已处理: " + file.getName());
                } catch (Exception e) {
                    System.err.println("处理文件 " + file.getName() + " 时出错: " + e.getMessage());
                }
            }
            
            System.out.println("处理完成。成功: " + successCount + ", 失败: " + (files.length - successCount));
            System.out.println("输出目录: " + outputDir.getAbsolutePath());
            
        } else {
            // 处理单个图片文件
            String parentDirName = input.getParentFile().getName();
            String outputDirName = parentDirName + "_watermark";
            File outputDir = new File(input.getParentFile(), outputDirName);
            if (!outputDir.exists() && !outputDir.mkdirs()) {
                throw new IOException("无法创建输出目录: " + outputDir.getAbsolutePath());
            }
            
            processor.processImage(input, outputDir);
            System.out.println("处理完成。输出目录: " + outputDir.getAbsolutePath());
        }
    }
}