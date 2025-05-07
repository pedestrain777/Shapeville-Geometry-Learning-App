package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Cuboid extends Shape {
    private double width, height, depth;

    public Cuboid(double width, double height, double depth) {
        super("cuboid", Color.LIGHTGREEN);
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    @Override
    public double calculateArea() {
        return 2 * (width * height + width * depth + height * depth);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.setFill(color);
        double offset = depth / 2;
        double x1 = x + offset, y1 = y;
        double x2 = x, y2 = y + offset;
        // 前方矩形
        gc.strokeRect(x1, y1, width, height);
        gc.fillRect(x1, y1, width, height);
        // 后方矩形
        gc.strokeRect(x2, y2, width, height);
        // 连接线
        gc.strokeLine(x1, y1, x2, y2);
        gc.strokeLine(x1 + width, y1, x2 + width, y2);
        gc.strokeLine(x1, y1 + height, x2, y2 + height);
        gc.strokeLine(x1 + width, y1 + height, x2 + width, y2 + height);
    }
}