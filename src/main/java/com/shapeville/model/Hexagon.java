package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Hexagon extends Shape2D {
    public Hexagon(double side) {
        super("hexagon", Color.GRAY, side, side);
    }

    @Override
    public double calculateArea() {
        // 正六边形面积公式: (3*sqrt(3)/2)*a^2
        return (3 * Math.sqrt(3) / 2) * width * width;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(color);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        double[] xPoints = new double[6];
        double[] yPoints = new double[6];
        double centerX = x + width / 2;
        double centerY = y + width / 2;
        double r = width / 2;
        for (int i = 0; i < 6; i++) {
            xPoints[i] = centerX + r * Math.cos(2 * Math.PI * i / 6 - Math.PI / 2);
            yPoints[i] = centerY + r * Math.sin(2 * Math.PI * i / 6 - Math.PI / 2);
        }
        gc.strokePolygon(xPoints, yPoints, 6);
        gc.fillPolygon(xPoints, yPoints, 6);
    }
}