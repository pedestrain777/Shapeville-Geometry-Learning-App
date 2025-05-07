package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Tetrahedron extends Shape {
    private double size;

    public Tetrahedron(double size) {
        super("tetrahedron", Color.LIGHTSEAGREEN);
        this.size = size;
    }

    @Override
    public double calculateArea() {
        return Math.sqrt(3) * size * size;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.setFill(color);
        // 画一个四面体的2D投影（三角形+辅助线）
        double x0 = x + size / 2, y0 = y;
        double x1 = x, y1 = y + size;
        double x2 = x + size, y2 = y + size;
        gc.strokePolygon(new double[] { x0, x1, x2 }, new double[] { y0, y1, y2 }, 3);
        gc.fillPolygon(new double[] { x0, x1, x2 }, new double[] { y0, y1, y2 }, 3);
        // 辅助线
        gc.setStroke(Color.DARKGRAY);
        gc.strokeLine(x0, y0, x + size / 2, y + size / 2);
        gc.strokeLine(x1, y1, x + size / 2, y + size / 2);
        gc.strokeLine(x2, y2, x + size / 2, y + size / 2);
    }
}