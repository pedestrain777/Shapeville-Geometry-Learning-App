package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Sphere extends Shape {
    private double radius;

    public Sphere(double radius) {
        super("sphere", Color.GOLD);
        this.radius = radius;
    }

    @Override
    public double calculateArea() {
        return 4 * Math.PI * radius * radius;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.setFill(color);
        // 画一个球体的2D投影（圆+椭圆）
        gc.strokeOval(x, y, radius * 2, radius * 2);
        gc.fillOval(x, y, radius * 2, radius * 2);
        gc.setStroke(Color.DARKGRAY);
        gc.strokeOval(x, y + radius * 0.7, radius * 2, radius * 0.6);
    }
}