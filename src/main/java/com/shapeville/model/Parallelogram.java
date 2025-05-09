package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Parallelogram extends Shape2D {
    private double base;
    private double height;
    private double shearFactor = 0.5; // 控制平行四边形的倾斜程度

    public Parallelogram(double base, double height) {
        super("parallelogram", Color.ORANGE, base, height);
        this.base = base;
        this.height = height;
    }

    @Override
    public double calculateArea() {
        return base * height;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(color);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        // 计算平行四边形的四个点
        double offset = height * shearFactor;
        double[] xPoints = {
            x + offset,      // 左上
            x + width + offset, // 右上
            x + width,       // 右下
            x               // 左下
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
        gc.strokeLine(x, y + height, x, y);
        
        // 标注尺寸
        gc.setFill(Color.BLACK);
        gc.fillText(String.format("%.1f", base), x + width/2, y + height + 15);
        gc.fillText(String.format("%.1f", height), x - 15, y + height/2);
    }

    public String getFormula() {
        return "Area = base × height";
    }

    public String getFormulaWithValues() {
        return String.format("Area = %.1f × %.1f = %.1f", base, height, calculateArea());
    }

    public double getBase() {
        return base;
    }

    public void setBase(double base) {
        this.base = base;
        this.width = base;
    }

    @Override
    public void setHeight(double height) {
        this.height = height;
        super.setHeight(height);
    }

    @Override
    public Parallelogram copy() {
        Parallelogram newParallelogram = new Parallelogram(this.base, this.height);
        newParallelogram.setColor(this.getColor());
        newParallelogram.setPosition(this.x, this.y);
        newParallelogram.setRotationX(this.rotationX);
        newParallelogram.setRotationY(this.rotationY);
        return newParallelogram;
    }
} 