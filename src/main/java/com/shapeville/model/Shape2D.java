package com.shapeville.model;

import javafx.scene.paint.Color;

public abstract class Shape2D extends Shape {
    protected double width;
    protected double height;

    public Shape2D(String name, Color color, double width, double height) {
        super(name, color);
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }


    @Override
    public abstract Shape2D copy();
}