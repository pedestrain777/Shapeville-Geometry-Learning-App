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

    public void draw(GraphicsContext gc, double imgX, double imgY, double imgWidth, double imgHeight) {
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(3);
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(16));
        double textX = imgX + imgWidth + 30;
        double textY = imgY + 30;
        // Draw parallelogram
        double offset = 40;
        double[] xPoints = {imgX + offset, imgX + imgWidth, imgX + imgWidth - offset, imgX};
        double[] yPoints = {imgY, imgY, imgY + imgHeight, imgY + imgHeight};
        gc.strokePolygon(xPoints, yPoints, 4);
        // Draw base arrow
        gc.setStroke(Color.ORANGE);
        gc.setLineWidth(2);
        gc.strokeLine(imgX, imgY + imgHeight + 15, imgX + imgWidth - offset, imgY + imgHeight + 15);
        gc.strokeLine(imgX, imgY + imgHeight + 10, imgX, imgY + imgHeight + 20);
        gc.strokeLine(imgX + imgWidth - offset, imgY + imgHeight + 10, imgX + imgWidth - offset, imgY + imgHeight + 20);
        gc.setFill(Color.ORANGE);
        gc.fillText("BASE", imgX + (imgWidth - offset) / 2 - 20, imgY + imgHeight + 35);
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
        gc.fillText(String.format("Base: %.1f", getBase()), textX, textY);
        gc.fillText(String.format("Height: %.1f", getHeight()), textX, textY + 30);
    }

    @Override
    public void draw(GraphicsContext gc) {
        // Not used in new logic
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