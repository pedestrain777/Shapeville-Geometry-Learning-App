package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SquarePyramid extends Shape {
    private double base, height;

    public SquarePyramid(double base, double height) {
        super("square-based pyramid", Color.LIGHTSLATEGRAY);
        this.base = base;
        this.height = height;
    }

    @Override
    public double calculateArea() {
        // 近似表面积
        return base * base + 2 * base * Math.sqrt((base / 2) * (base / 2) + height * height);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.setFill(color);
        // 画一个方锥体的2D投影
        double x0 = x + base / 2;
        double y0 = y;
        double x1 = x, y1 = y + base;
        double x2 = x + base, y2 = y + base;
        double x3 = x + base / 2, y3 = y + base / 2 + height;
        // 底面
        gc.strokeRect(x, y + base, base, base / 4);
        // 侧面三角形
        gc.strokeLine(x1, y1, x0, y0);
        gc.strokeLine(x2, y2, x0, y0);
        gc.strokeLine(x1, y1, x3, y3);
        gc.strokeLine(x2, y2, x3, y3);
        gc.strokeLine(x3, y3, x0, y0);
    }
}