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

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(color);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        double[] xPoints = {
            x + width/2,  // top point
            x,           // bottom left
            x + width    // bottom right
        };
        
        double[] yPoints = {
            y,           // top point
            y + height,  // bottom left
            y + height   // bottom right
        };

        gc.strokePolygon(xPoints, yPoints, 3);
        gc.fillPolygon(xPoints, yPoints, 3);
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