package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Pentagon extends Shape2D {
    public Pentagon(double side) {
        super("pentagon", Color.BROWN, side, side);
    }

    @Override
    public double calculateArea() {
        // 正五边形面积公式: (5/4)*a^2*(1/tan(pi/5))
        return (5.0 / 4.0) * width * width / Math.tan(Math.PI / 5);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(color);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        double[] xPoints = new double[5];
        double[] yPoints = new double[5];
        double centerX = x + width / 2;
        double centerY = y + width / 2;
        double r = width / 2;
        for (int i = 0; i < 5; i++) {
            xPoints[i] = centerX + r * Math.cos(2 * Math.PI * i / 5 - Math.PI / 2);
            yPoints[i] = centerY + r * Math.sin(2 * Math.PI * i / 5 - Math.PI / 2);
        }
        gc.strokePolygon(xPoints, yPoints, 5);
        gc.fillPolygon(xPoints, yPoints, 5);
    }
    @Override
    public Pentagon copy() {
        Pentagon newPentagon = new Pentagon(this.width); // width 就是 side
        newPentagon.setColor(this.getColor());
        newPentagon.setPosition(this.x, this.y);
        newPentagon.setRotationX(this.rotationX);
        newPentagon.setRotationY(this.rotationY);
        return newPentagon;
    }
}