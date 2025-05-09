package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Oval extends Shape2D {
    public Oval(double width, double height) {
        super("oval", Color.PURPLE, width, height);
    }

    @Override
    public double calculateArea() {
        return Math.PI * (width / 2) * (height / 2);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(color);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeOval(x, y, width, height);
        gc.fillOval(x, y, width, height);
    }
    @Override
    public Oval copy() {
        Oval newOval = new Oval(this.width, this.height);
        newOval.setColor(this.getColor());
        newOval.setPosition(this.x, this.y);
        newOval.setRotationX(this.rotationX);
        newOval.setRotationY(this.rotationY);
        return newOval;
    }
}