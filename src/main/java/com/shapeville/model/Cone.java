package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Cone extends Shape {
    private double radius, height;

    public Cone(double radius, double height) {
        super("cone", Color.LIGHTPINK);
        this.radius = radius;
        this.height = height;
    }

    @Override
    public double calculateArea() {
        return Math.PI * radius * (radius + Math.sqrt(height * height + radius * radius));
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.setFill(color);
        // 画一个圆锥体的2D投影
        double baseY = y + height;
        gc.strokeOval(x, baseY, radius * 2, radius / 2);
        gc.fillOval(x, baseY, radius * 2, radius / 2);
        gc.strokeLine(x + radius, y, x, baseY + radius / 4);
        gc.strokeLine(x + radius, y, x + radius * 2, baseY + radius / 4);
    }
}