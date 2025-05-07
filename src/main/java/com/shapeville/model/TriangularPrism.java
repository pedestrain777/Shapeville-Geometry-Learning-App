package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TriangularPrism extends Shape {
    private double base, height, length;

    public TriangularPrism(double base, double height, double length) {
        super("triangular prism", Color.LIGHTYELLOW);
        this.base = base;
        this.height = height;
        this.length = length;
    }

    @Override
    public double calculateArea() {
        // 表面积公式: 2*(1/2*base*height) + 3*base*length (近似)
        return base * height + 2 * length * base + 2 * length * height;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.setFill(color);
        // 画一个三棱柱的2D投影
        double offset = length / 3;
        double[] x1 = { x + offset, x + offset + base, x + offset + base / 2 };
        double[] y1 = { y + height, y + height, y };
        double[] x2 = { x, x + base, x + base / 2 };
        double[] y2 = { y + height + offset, y + height + offset, y + offset };
        // 前三角形
        gc.strokePolygon(x1, y1, 3);
        gc.fillPolygon(x1, y1, 3);
        // 后三角形
        gc.strokePolygon(x2, y2, 3);
        // 连接线
        for (int i = 0; i < 3; i++) {
            gc.strokeLine(x1[i], y1[i], x2[i], y2[i]);
        }
    }
}