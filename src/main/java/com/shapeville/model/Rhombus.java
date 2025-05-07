package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Rhombus extends Shape2D {
    public Rhombus(double d1, double d2) {
        super("rhombus", Color.CYAN, d1, d2);
    }

    @Override
    public double calculateArea() {
        // 菱形面积公式: (d1 * d2) / 2
        return width * height / 2;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(color);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        double[] xPoints = new double[4];
        double[] yPoints = new double[4];
        double centerX = x + width / 2;
        double centerY = y + height / 2;
        xPoints[0] = centerX;
        yPoints[0] = centerY - height / 2;
        xPoints[1] = centerX + width / 2;
        yPoints[1] = centerY;
        xPoints[2] = centerX;
        yPoints[2] = centerY + height / 2;
        xPoints[3] = centerX - width / 2;
        yPoints[3] = centerY;
        gc.strokePolygon(xPoints, yPoints, 4);
        gc.fillPolygon(xPoints, yPoints, 4);
    }
}