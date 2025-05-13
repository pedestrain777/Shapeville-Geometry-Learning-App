package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Triangle extends Shape2D {
    private double base;
    private double height;

    public Triangle(double base, double height) {
        super("triangle", Color.GREEN, base, height);
        this.base = base;
        this.height = height;
    }

    @Override
    public double calculateArea() {
        return 0.5 * base * height;
    }

    public void draw(GraphicsContext gc, double imgX, double imgY, double imgWidth, double imgHeight) {
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(3);
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(16));
        double textX = imgX + imgWidth + 30;
        double textY = imgY + 30;
        // Draw triangle
        double[] xPoints = {imgX, imgX + imgWidth, imgX + imgWidth / 2};
        double[] yPoints = {imgY + imgHeight, imgY + imgHeight, imgY};
        gc.strokePolygon(xPoints, yPoints, 3);
        // Draw base arrow
        gc.setStroke(Color.ORANGE);
        gc.setLineWidth(2);
        gc.strokeLine(imgX, imgY + imgHeight + 15, imgX + imgWidth, imgY + imgHeight + 15);
        gc.strokeLine(imgX, imgY + imgHeight + 10, imgX, imgY + imgHeight + 20);
        gc.strokeLine(imgX + imgWidth, imgY + imgHeight + 10, imgX + imgWidth, imgY + imgHeight + 20);
        gc.setFill(Color.ORANGE);
        gc.fillText("BASE", imgX + imgWidth / 2 - 20, imgY + imgHeight + 35);
        // Draw height arrow
        gc.setStroke(Color.PURPLE);
        gc.setLineWidth(2);
        gc.strokeLine(imgX + imgWidth / 2, imgY, imgX + imgWidth / 2, imgY + imgHeight);
        gc.strokeLine(imgX + imgWidth / 2 - 5, imgY, imgX + imgWidth / 2 + 5, imgY);
        gc.strokeLine(imgX + imgWidth / 2 - 5, imgY + imgHeight, imgX + imgWidth / 2 + 5, imgY + imgHeight);
        gc.setFill(Color.PURPLE);
        gc.fillText("HEIGHT", imgX + imgWidth / 2 + 10, imgY + imgHeight / 2);
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
        return "Area = ½ × base × height";
    }

    public String getFormulaWithValues() {
        return String.format("Area = ½ × %.1f × %.1f = %.1f", base, height, calculateArea());
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
    public Triangle copy() {
        Triangle newTriangle = new Triangle(this.base, this.height);
        newTriangle.setColor(this.getColor());
        newTriangle.setPosition(this.x, this.y);
        newTriangle.setRotationX(this.rotationX);
        newTriangle.setRotationY(this.rotationY);
        return newTriangle;
    }
}