package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Angle {
   private double value;
    private AngleType type;
    private double x;
    private double y;
    private double radius;

    public Angle(double value) {
        this.value = value;
        this.type = determineType();
        this.radius = 50; // Default radius for drawing
    }

    public AngleType determineType() {
        if (value == 90) {
            return AngleType.RIGHT;
        } else if (value == 180) {
            return AngleType.STRAIGHT;
        } else if (value > 180 && value < 360) {
            return AngleType.REFLEX;
        } else if (value > 90 && value < 180) {
            return AngleType.OBTUSE;
        } else {
            return AngleType.ACUTE;
        }
    }

    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(2);

        // Draw first line (horizontal, 0° 起始)
        gc.strokeLine(x, y, x + radius, y);

        // 计算第二条线的终点
        double endX = x + radius * Math.cos(Math.toRadians(value));
        double endY = y - radius * Math.sin(Math.toRadians(value));
        gc.strokeLine(x, y, endX, endY);

        // 画弧：除 90° 特殊方块外，其他用 Arc
        if (type == AngleType.RIGHT) {
            double size = 20;
            gc.strokeRect(x, y - size, size, size);
        } else {
            double arcSize = 40;
            double arcX = x - arcSize / 2;
            double arcY = y - arcSize / 2;
            double startAngle = 0;
            double arcExtent = value;
            gc.strokeArc(
                    arcX, arcY,
                    arcSize, arcSize,
                    startAngle,
                    arcExtent,
                    javafx.scene.shape.ArcType.OPEN
            );
        }
    }

    public boolean isCorrectType(String input) {
        try {
            return type == AngleType.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getValue() {
        return value;
    }

    public AngleType getType() {

        return type;
    }

    public String getTypeDescription() {
        return switch (type) {
            case ACUTE -> "less than 90° and greater than 0°";
            case RIGHT -> "equal to 90°";
            case OBTUSE -> "less than 180° and greater than 90°";
            case STRAIGHT -> "equal to 180°";
            case REFLEX -> "greater than 180° and less than 360°";
        };
    }
}
