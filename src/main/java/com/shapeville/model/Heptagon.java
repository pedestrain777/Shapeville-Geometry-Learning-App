package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Heptagon extends Shape2D {
    public Heptagon(double side) {
        super("heptagon", Color.PINK, side, side);
    }

    @Override
    public double calculateArea() {
        // 正七边形面积公式: (7/4)*a^2*(1/tan(pi/7))
        return (7.0 / 4.0) * width * width / Math.tan(Math.PI / 7);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(color);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        double[] xPoints = new double[7];
        double[] yPoints = new double[7];
        double centerX = x + width / 2;
        double centerY = y + width / 2;
        double r = width / 2;
        for (int i = 0; i < 7; i++) {
            xPoints[i] = centerX + r * Math.cos(2 * Math.PI * i / 7 - Math.PI / 2);
            yPoints[i] = centerY + r * Math.sin(2 * Math.PI * i / 7 - Math.PI / 2);
        }
        gc.strokePolygon(xPoints, yPoints, 7);
        gc.fillPolygon(xPoints, yPoints, 7);
    }
}