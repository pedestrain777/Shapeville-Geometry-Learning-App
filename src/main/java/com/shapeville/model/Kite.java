package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Kite extends Shape2D {
    public Kite(double d1, double d2) {
        super("kite", Color.HOTPINK, d1, d2);
    }

    @Override
    public double calculateArea() {
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
        xPoints[3] = centerX - width / 4;
        yPoints[3] = centerY;
        gc.strokePolygon(xPoints, yPoints, 4);
        gc.fillPolygon(xPoints, yPoints, 4);
    }

    // 关键新增
    @Override
    public Kite copy() {
        Kite newKite = new Kite(this.width, this.height);
        newKite.setColor(this.getColor());
        newKite.setPosition(this.x, this.y);
        newKite.setRotationX(this.rotationX);
        newKite.setRotationY(this.rotationY);
        return newKite;
    }
}