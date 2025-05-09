package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Rectangle extends Shape2D {
    
    public Rectangle(double width, double height) {
        super("rectangle", Color.YELLOW, width, height);
    }

    @Override
    public double calculateArea() {
        return width * height;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(color);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(x, y, width, height);
        gc.fillRect(x, y, width, height);
    }

    public String getFormula() {
        return "Area = length × width";
    }

    public String getFormulaWithValues() {
        return String.format("Area = %.1f × %.1f = %.1f", width, height, calculateArea());
    }
    @Override
    public Rectangle copy() {
        Rectangle newRectangle = new Rectangle(this.width, this.height);
        newRectangle.setColor(this.getColor());
        newRectangle.setPosition(this.x, this.y);
        newRectangle.setRotationX(this.rotationX);
        newRectangle.setRotationY(this.rotationY);
        return newRectangle;
    }
} 