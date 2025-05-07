package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Cube extends Shape {
    private double size;

    public Cube(double size) {
        super("cube", Color.LIGHTBLUE);
        this.size = size;
    }

    @Override
    public double calculateArea() {
        return 6 * size * size;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.setFill(color);
        // 画一个立方体的2D投影
        double offset = size / 3;
        double x1 = x + offset, y1 = y;
        double x2 = x, y2 = y + offset;
        // 前方正方形
        gc.strokeRect(x1, y1, size, size);
        gc.fillRect(x1, y1, size, size);
        // 后方正方形
        gc.strokeRect(x2, y2, size, size);
        // 连接线
        gc.strokeLine(x1, y1, x2, y2);
        gc.strokeLine(x1 + size, y1, x2 + size, y2);
        gc.strokeLine(x1, y1 + size, x2, y2 + size);
        gc.strokeLine(x1 + size, y1 + size, x2 + size, y2 + size);
    }
}