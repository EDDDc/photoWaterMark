package com.watermark;

import java.awt.Color;

/**
 * 水印配置类，存储水印的字体大小、颜色和位置
 */
public class WatermarkConfig {
    // 水印位置枚举
    public enum Position {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER
    }
    
    private int fontSize = 36; // 默认字体大小
    private Color color = Color.WHITE; // 默认颜色
    private Position position = Position.BOTTOM_RIGHT; // 默认位置
    
    public WatermarkConfig() {
        // 使用默认值
    }
    
    public int getFontSize() {
        return fontSize;
    }
    
    public void setFontSize(int fontSize) {
        if (fontSize > 0) {
            this.fontSize = fontSize;
        }
    }
    
    public Color getColor() {
        return color;
    }
    
    public void setColor(String colorName) {
        switch (colorName.toLowerCase()) {
            case "black":
                this.color = Color.BLACK;
                break;
            case "white":
                this.color = Color.WHITE;
                break;
            case "red":
                this.color = Color.RED;
                break;
            case "green":
                this.color = Color.GREEN;
                break;
            case "blue":
                this.color = Color.BLUE;
                break;
            case "yellow":
                this.color = Color.YELLOW;
                break;
            default:
                // 保持默认值
                break;
        }
    }
    
    public Position getPosition() {
        return position;
    }
    
    public void setPosition(String positionName) {
        switch (positionName.toLowerCase()) {
            case "topleft":
            case "top_left":
                this.position = Position.TOP_LEFT;
                break;
            case "topright":
            case "top_right":
                this.position = Position.TOP_RIGHT;
                break;
            case "bottomleft":
            case "bottom_left":
                this.position = Position.BOTTOM_LEFT;
                break;
            case "bottomright":
            case "bottom_right":
                this.position = Position.BOTTOM_RIGHT;
                break;
            case "center":
                this.position = Position.CENTER;
                break;
            default:
                // 保持默认值
                break;
        }
    }
}