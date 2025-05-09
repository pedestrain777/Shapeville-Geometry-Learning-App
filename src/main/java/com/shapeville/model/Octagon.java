package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Octagon extends Shape2D {
    public Octagon(double side) {
        super("octagon", Color.ORANGE, side, side);
    }

    @Override
    public double calculateArea() {
        // 正八边形面积公式: 2*(1+sqrt(2))*a^2
        return 2 * (1 + Math.sqrt(2)) * width * width;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(color);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        double[] xPoints = new double[8];
        double[] yPoints = new double[8];
        double centerX = x + width / 2;
        double centerY = y + width / 2;
        double r = width / 2;
        for (int i = 0; i < 8; i++) {
            xPoints[i] = centerX + r * Math.cos(2 * Math.PI * i / 8 - Math.PI / 2);
            yPoints[i] = centerY + r * Math.sin(2 * Math.PI * i / 8 - Math.PI / 2);
        }
        gc.strokePolygon(xPoints, yPoints, 8);
        gc.fillPolygon(xPoints, yPoints, 8);
    }

    @Override
    public Octagon copy() {
        Octagon newOctagon = new Octagon(this.width); // width 就是 side
        newOctagon.setColor(this.getColor());
        newOctagon.setPosition(this.x, this.y);
        newOctagon.setRotationX(this.rotationX);
        newOctagon.setRotationY(this.rotationY);
        return newOctagon;
    }
}