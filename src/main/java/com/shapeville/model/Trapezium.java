package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Trapezium extends Shape2D {
    private double topWidth;    // 上底
    private double bottomWidth; // 下底
    private double height;      // 高

    public Trapezium(double topWidth, double bottomWidth, double height) {
        super("trapezium", Color.CYAN, Math.max(topWidth, bottomWidth), height);
        this.topWidth = topWidth;
        this.bottomWidth = bottomWidth;
        this.height = height;
    }

    // 单参数构造函数，用于Task3生成随机梯形
    public Trapezium(double widthFactor, double height) {
        this(widthFactor, widthFactor * 1.5, height); // 下底是上底的1.5倍
    }

    @Override
    public double calculateArea() {
        return 0.5 * (topWidth + bottomWidth) * height;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(color);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        // 计算梯形四个顶点
        double offset = (bottomWidth - topWidth) / 2;
        double[] xPoints = {
            x + offset,                // 左上
            x + offset + topWidth,     // 右上
            x + bottomWidth,           // 右下
            x                          // 左下
        };
        
        double[] yPoints = {
            y,          // 左上
            y,          // 右上
            y + height, // 右下
            y + height  // 左下
        };

        gc.strokePolygon(xPoints, yPoints, 4);
        gc.fillPolygon(xPoints, yPoints, 4);
        
        // 绘制高度标识
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeLine(x + bottomWidth/2, y + height, x + bottomWidth/2 - (bottomWidth - topWidth)/4, y);
        
        // 标注尺寸
        gc.setFill(Color.BLACK);
        gc.fillText(String.format("%.1f", topWidth), x + offset + topWidth/2, y - 5);
        gc.fillText(String.format("%.1f", bottomWidth), x + bottomWidth/2, y + height + 15);
        gc.fillText(String.format("%.1f", height), x - 15, y + height/2);
    }

    public String getFormula() {
        return "Area = ½ × (a + c) × h";
    }

    public String getFormulaWithValues() {
        return String.format("Area = ½ × (%.1f + %.1f) × %.1f = %.1f", 
                              topWidth, bottomWidth, height, calculateArea());
    }

    public double getTopWidth() {
        return topWidth;
    }

    public void setTopWidth(double topWidth) {
        this.topWidth = topWidth;
        this.width = Math.max(topWidth, bottomWidth);
    }
    
    public double getBottomWidth() {
        return bottomWidth;
    }

    public void setBottomWidth(double bottomWidth) {
        this.bottomWidth = bottomWidth;
        this.width = Math.max(topWidth, bottomWidth);
    }

    @Override
    public void setHeight(double height) {
        this.height = height;
        super.setHeight(height);
    }

    @Override
    public Trapezium copy() {
        Trapezium newTrapezium = new Trapezium(this.topWidth, this.bottomWidth, this.height);
        newTrapezium.setColor(this.getColor());
        newTrapezium.setPosition(this.x, this.y);
        newTrapezium.setRotationX(this.rotationX);
        newTrapezium.setRotationY(this.rotationY);
        return newTrapezium;
    }
} 