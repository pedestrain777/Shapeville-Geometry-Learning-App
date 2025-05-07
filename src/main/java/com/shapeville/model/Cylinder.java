package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Cylinder extends Shape {
    private double radius, height;

    public Cylinder(double radius, double height) {
        super("cylinder", Color.LIGHTCORAL);
        this.radius = radius;
        this.height = height;
    }

    @Override
    public double calculateArea() {
        return 2 * Math.PI * radius * (radius + height);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.setFill(color);
        double ellipseHeight = radius / 2;
        // 上椭圆
        gc.strokeOval(x, y, radius * 2, ellipseHeight);
        gc.fillOval(x, y, radius * 2, ellipseHeight);
        // 下椭圆
        gc.strokeOval(x, y + height, radius * 2, ellipseHeight);
        // 侧面
        gc.strokeLine(x, y + ellipseHeight / 2, x, y + height + ellipseHeight / 2);
        gc.strokeLine(x + radius * 2, y + ellipseHeight / 2, x + radius * 2, y + height + ellipseHeight / 2);
    }
}