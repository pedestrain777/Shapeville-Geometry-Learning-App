package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Circle extends Shape2D {
    private double radius;

    public Circle(double radius) {
        super("circle", Color.RED, radius * 2, radius * 2);
        this.radius = radius;
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
        gc.setFill(color);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeOval(x, y, width, height);
        gc.fillOval(x, y, width, height);
    }

    public String getAreaFormula() {
        return "Area = πr²";
    }

    public String getCircumferenceFormula() {
        return "Circumference = 2πr";
    }

    public String getAreaFormulaWithValues() {
        return String.format("Area = π × %.1f² = %.1f", radius, calculateArea());
    }

    public String getCircumferenceFormulaWithValues() {
        return String.format("Circumference = 2π × %.1f = %.1f", radius, calculateCircumference());
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
        this.width = radius * 2;
        this.height = radius * 2;
    }

    // 新增circle的copy
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