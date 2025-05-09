package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Square extends Shape2D {
    public Square(double side) {
        super("square", Color.BLUE, side, side);
    }

    @Override
    public double calculateArea() {
        return width * width;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(color);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(x, y, width, width);
        gc.fillRect(x, y, width, width);
    }
    @Override
    public Square copy() {
        Square newSquare = new Square(this.width); // width 就是 side
        newSquare.setColor(this.getColor());
        newSquare.setPosition(this.x, this.y);
        newSquare.setRotationX(this.rotationX);
        newSquare.setRotationY(this.rotationY);
        return newSquare;
    }
}