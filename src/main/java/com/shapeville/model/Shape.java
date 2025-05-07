package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Shape {
    protected String name;
    protected Color color;
    protected double x;
    protected double y;

    public Shape(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public abstract double calculateArea();
    
    public abstract void draw(GraphicsContext gc);
    
    public boolean isCorrectName(String input) {
        return name.equalsIgnoreCase(input.trim());
    }

    public String getName() {
        return name;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
} 