package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Rectangle extends Shape2D {
    
    public Rectangle(double width, double height) {
        super("rectangle", Color.YELLOW, width, height);
    }

    @Override
    public double calculateArea() {
        return width * height;
    }

    public void draw(GraphicsContext gc, double imgX, double imgY, double imgWidth, double imgHeight) {
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(3);
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(16));
        double textX = imgX + imgWidth + 30;
        double textY = imgY + 30;
        // Draw rectangle
        gc.strokeRect(imgX, imgY, imgWidth, imgHeight);
        // Draw width arrow
        gc.setStroke(Color.GREEN);
        gc.setLineWidth(2);
        gc.strokeLine(imgX, imgY + imgHeight + 15, imgX + imgWidth, imgY + imgHeight + 15);
        gc.strokeLine(imgX, imgY + imgHeight + 10, imgX, imgY + imgHeight + 20);
        gc.strokeLine(imgX + imgWidth, imgY + imgHeight + 10, imgX + imgWidth, imgY + imgHeight + 20);
        gc.setFill(Color.GREEN);
        gc.fillText("LENGTH", imgX + imgWidth / 2 - 30, imgY + imgHeight + 35);
        // Draw height arrow
        gc.setStroke(Color.TEAL);
        gc.setLineWidth(2);
        gc.strokeLine(imgX - 15, imgY, imgX - 15, imgY + imgHeight);
        gc.strokeLine(imgX - 10, imgY, imgX - 20, imgY);
        gc.strokeLine(imgX - 10, imgY + imgHeight, imgX - 20, imgY + imgHeight);
        gc.setFill(Color.TEAL);
        gc.fillText("WIDTH", imgX - 60, imgY + imgHeight / 2 + 5);
        // Show dimensions
        gc.setFill(Color.BLACK);
        gc.fillText(String.format("Width: %.1f", getWidth()), textX, textY);
        gc.fillText(String.format("Length: %.1f", getHeight()), textX, textY + 30);
    }

    @Override
    public void draw(GraphicsContext gc) {
        // Not used in new logic
    }

    public String getFormula() {
        return "Area = length × width";
    }

    public String getFormulaWithValues() {
        return String.format("Area = %.1f × %.1f = %.1f", width, height, calculateArea());
    }
    @Override
    public Rectangle copy() {
        Rectangle newRectangle = new Rectangle(this.width, this.height);
        newRectangle.setColor(this.getColor());
        newRectangle.setPosition(this.x, this.y);
        newRectangle.setRotationX(this.rotationX);
        newRectangle.setRotationY(this.rotationY);
        return newRectangle;
    }
} 