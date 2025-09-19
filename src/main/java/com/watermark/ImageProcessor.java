package com.watermark;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 图片处理类，负责读取EXIF信息和添加水印
 */
public class ImageProcessor {
    private final WatermarkConfig config;
    
    public ImageProcessor(WatermarkConfig config) {
        this.config = config;
    }
    
    /**
     * 处理单个图片
     */
    public void processImage(File imageFile, File outputDir) throws IOException, ImageReadException {
        // 读取图片
        BufferedImage image = ImageIO.read(imageFile);
        if (image == null) {
            throw new IOException("不支持的图片格式: " + imageFile.getName());
        }
        
        // 获取拍摄日期
        String dateText = getDateFromExif(imageFile);
        
        // 添加水印
        addWatermark(image, dateText);
        
        // 保存图片
        String extension = FilenameUtils.getExtension(imageFile.getName());
        File outputFile = new File(outputDir, imageFile.getName());
        ImageIO.write(image, extension, outputFile);
    }
    
    /**
     * 从EXIF信息中获取拍摄日期
     */
    private String getDateFromExif(File imageFile) throws IOException, ImageReadException {
        String defaultDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        
        try {
            ImageMetadata metadata = Imaging.getMetadata(imageFile);
            
            if (metadata instanceof JpegImageMetadata) {
                JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
                TiffImageMetadata exif = jpegMetadata.getExif();
                
                if (exif != null) {
                    // 尝试获取原始拍摄日期
                    TiffField dateTimeField = jpegMetadata.findEXIFValue(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
                    if (dateTimeField != null) {
                        String dateTime = dateTimeField.getStringValue();
                        // EXIF日期格式通常为 "yyyy:MM:dd HH:mm:ss"
                        if (dateTime.length() >= 10) {
                            // 只提取年月日部分
                            return dateTime.substring(0, 10).replace(':', '-');
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("读取EXIF信息时出错: " + e.getMessage());
        }
        
        return defaultDate;
    }
    
    /**
     * 添加水印到图片
     */
    private void addWatermark(BufferedImage image, String text) {
        Graphics2D g2d = image.createGraphics();
        
        // 设置渲染提示以获得更好的文本质量
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // 设置字体
        Font font = new Font("Arial", Font.BOLD, config.getFontSize());
        g2d.setFont(font);
        
        // 设置颜色
        g2d.setColor(config.getColor());
        
        // 计算文本尺寸
        FontMetrics fontMetrics = g2d.getFontMetrics();
        int textWidth = fontMetrics.stringWidth(text);
        int textHeight = fontMetrics.getHeight();
        
        // 根据配置确定水印位置
        int x, y;
        int padding = 20; // 边距
        
        switch (config.getPosition()) {
            case TOP_LEFT:
                x = padding;
                y = textHeight + padding;
                break;
            case TOP_RIGHT:
                x = image.getWidth() - textWidth - padding;
                y = textHeight + padding;
                break;
            case BOTTOM_LEFT:
                x = padding;
                y = image.getHeight() - padding;
                break;
            case CENTER:
                x = (image.getWidth() - textWidth) / 2;
                y = (image.getHeight() + textHeight) / 2;
                break;
            case BOTTOM_RIGHT:
            default:
                x = image.getWidth() - textWidth - padding;
                y = image.getHeight() - padding;
                break;
        }
        
        // 绘制文本阴影以增强可读性
        g2d.setColor(getContrastColor(config.getColor()));
        g2d.drawString(text, x + 2, y + 2);
        
        // 绘制文本
        g2d.setColor(config.getColor());
        g2d.drawString(text, x, y);
        
        g2d.dispose();
    }
    
    /**
     * 获取对比色，用于文本阴影
     */
    private Color getContrastColor(Color color) {
        double luminance = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;
        return luminance > 0.5 ? Color.BLACK : Color.WHITE;
    }
}