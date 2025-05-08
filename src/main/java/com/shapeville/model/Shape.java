package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Shape {
    protected String name;
    protected Color color;
    protected double x;
    protected double y;
    protected double rotationY = 0;    // 绕 Y 轴旋转（左右）
    protected double rotationX = 0;    // 绕 X 轴旋转（上下）

    public Shape(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    // Y 轴旋转
    public double getRotationY() {
        return rotationY;
    }
    public void setRotationY(double angle) {
        this.rotationY = angle;
    }

    // X 轴旋转
    public double getRotationX() {
        return rotationX;
    }
    public void setRotationX(double angle) {
        this.rotationX = angle;
    }

    // 计算面积（针对 2D 形状）
    public abstract double calculateArea();

    // 绘制形状
    public abstract void draw(GraphicsContext gc);

    // 判断输入的名字是否正确
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
