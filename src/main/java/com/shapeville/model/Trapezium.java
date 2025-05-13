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

    // 新的绘制方法，参数为绘制区域
    public void draw(GraphicsContext gc, double imgX, double imgY, double imgWidth, double imgHeight) {
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(3);
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(16));
        double textX = imgX + imgWidth + 30;
        double textY = imgY + 30;
        // Draw trapezium
        double topWidth = imgWidth * 0.6;
        double bottomWidth = imgWidth;
        double offset = (bottomWidth - topWidth) / 2;
        double[] xPoints = {imgX + offset, imgX + offset + topWidth, imgX + bottomWidth, imgX};
        double[] yPoints = {imgY, imgY, imgY + imgHeight, imgY + imgHeight};
        gc.strokePolygon(xPoints, yPoints, 4);
        // Draw top arrow
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        gc.strokeLine(imgX + offset, imgY - 15, imgX + offset + topWidth, imgY - 15);
        gc.strokeLine(imgX + offset, imgY - 10, imgX + offset, imgY - 20);
        gc.strokeLine(imgX + offset + topWidth, imgY - 10, imgX + offset + topWidth, imgY - 20);
        gc.setFill(Color.RED);
        gc.fillText("a", imgX + offset + topWidth / 2 - 5, imgY - 25);
        // Draw bottom arrow
        gc.setStroke(Color.ORANGE);
        gc.setLineWidth(2);
        gc.strokeLine(imgX, imgY + imgHeight + 15, imgX + bottomWidth, imgY + imgHeight + 15);
        gc.strokeLine(imgX, imgY + imgHeight + 10, imgX, imgY + imgHeight + 20);
        gc.strokeLine(imgX + bottomWidth, imgY + imgHeight + 10, imgX + bottomWidth, imgY + imgHeight + 20);
        gc.setFill(Color.ORANGE);
        gc.fillText("b", imgX + bottomWidth / 2 - 5, imgY + imgHeight + 35);
        // Draw height arrow
        gc.setStroke(Color.PURPLE);
        gc.setLineWidth(2);
        gc.strokeLine(imgX + offset, imgY, imgX + offset, imgY + imgHeight);
        gc.strokeLine(imgX + offset - 5, imgY, imgX + offset + 5, imgY);
        gc.strokeLine(imgX + offset - 5, imgY + imgHeight, imgX + offset + 5, imgY + imgHeight);
        gc.setFill(Color.PURPLE);
        gc.fillText("HEIGHT", imgX + offset - 35, imgY + imgHeight / 2);
        // Show dimensions
        gc.setFill(Color.BLACK);
        gc.fillText(String.format("a: %.1f", getTopWidth()), textX, textY);
        gc.fillText(String.format("b: %.1f", getBottomWidth()), textX, textY + 30);
        gc.fillText(String.format("Height: %.1f", getHeight()), textX, textY + 60);
    }

    @Override
    public void draw(GraphicsContext gc) {
        // Not used in new logic
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