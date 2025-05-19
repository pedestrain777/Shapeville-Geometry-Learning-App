package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Circle extends Shape2D {
    private double radius;
    private double inputValue;
    private boolean inputIsDiameter;

    public Circle(double radius) {
        this(radius, false);
    }

    public Circle(double value, boolean isDiameter) {
        super("circle", Color.RED, (isDiameter ? value / 2 : value) * 2, (isDiameter ? value / 2 : value) * 2);
        this.inputIsDiameter = isDiameter;
        this.inputValue = value;
        this.radius = isDiameter ? value / 2 : value;
    }

    @Override
    public double calculateArea() {
        return Math.PI * radius * radius;
    }

    public double calculateCircumference() {
        return 2 * Math.PI * radius;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.save();
        double centerX = x + width / 2;
        double centerY = y + height / 2;
        gc.translate(centerX, centerY);
        gc.scale(2, 2);
        gc.translate(-centerX, -centerY);
        gc.setFill(Color.LIGHTBLUE);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeOval(x, y, width, height);
        gc.fillOval(x, y, width, height);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        if (inputIsDiameter) {
            double leftX = x;
            double rightX = x + width;
            gc.strokeLine(leftX, centerY, rightX, centerY);
        } else {
            double rightX = x + width;
            gc.strokeLine(centerX, centerY, rightX, centerY);
        }
        double dotSize = 2;
        gc.setFill(Color.BLACK);
        gc.fillOval(centerX - dotSize / 2, centerY - dotSize / 2, dotSize, dotSize);
        gc.restore();
    }

    // 新增：用于形状识别关卡的简单绘制方法
    public void drawSimple(GraphicsContext gc) {
        gc.save();
        double centerX = x + width / 2;
        double centerY = y + height / 2;
        gc.translate(centerX, centerY);
        gc.scale(1.5, 1.5);
        gc.translate(-centerX, -centerY);
        gc.setFill(Color.LIGHTBLUE);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeOval(x, y, width, height);
        gc.fillOval(x, y, width, height);
        gc.restore();
    }

    public String getInputText() {
        String unit = inputIsDiameter ? "Diameter" : "Radius";
        return String.format("%s = %.1f units", unit, inputValue);
    }

    public String getAreaFormula() {
        return "Area = πr²";
    }

    public String getCircumferenceFormula() {
        return "Circumference = 2πr";
    }

    public String getAreaFormulaWithValues() {
        return String.format("Area = π × %.1f² = %.2f", radius, calculateArea());
    }

    public String getCircumferenceFormulaWithValues() {
        return String.format("Circumference = 2π × %.1f = %.2f", radius, calculateCircumference());
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
        this.width = radius * 2;
        this.height = radius * 2;
    }

    @Override
    public Circle copy() {
        Circle newCircle = new Circle(this.radius);
        newCircle.setColor(this.getColor());
        newCircle.setPosition(this.x, this.y);
        newCircle.setRotationX(this.rotationX);
        newCircle.setRotationY(this.rotationY);
        return newCircle;
    }
}
