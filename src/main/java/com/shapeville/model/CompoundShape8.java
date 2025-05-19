package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * 复合形状8: 复杂矩形组合（T形）
 */
public class CompoundShape8 extends AbstractCompoundShape {
    private Rectangle rectTop; // 上方正方形
    private Rectangle rectBottom; // 下方长方形

    public CompoundShape8() {
        super(7, "Compound Shape 8");
    }

    @Override
    protected void createComponents() {
        // 图8：复杂矩形组合（T形） - 创建基本形状
        // 上方正方形36x36，下方长方形60x36
        rectTop = new Rectangle(12 * scaleFactor, 12 * scaleFactor); // 上方正方形
        rectBottom = new Rectangle(24 * scaleFactor, 12 * scaleFactor); // 下方长方形
        components.add(rectTop);
        components.add(rectBottom);
    }

    @Override
    protected void positionComponents(double centerX, double centerY) {
        // 图8：复杂矩形组合（T形）- 定位
        double totalWidth = 24 * scaleFactor;
        double totalHeight = 12 * scaleFactor + 12 * scaleFactor;
        double startX = centerX - totalWidth / 2;
        double startY = centerY - totalHeight / 2;

        // 上方正方形居中
        rectTop.setPosition(startX + 6 * scaleFactor, startY);

        // 下方长方形
        rectBottom.setPosition(startX, startY + 12 * scaleFactor);
    }

    @Override
    protected void drawShape(GraphicsContext gc) {
        double x0 = rectBottom.getX();
        double y0 = rectBottom.getY();
        double w = scaleFactor;
        double h = scaleFactor;

        // 计算顶点，顺时针
        double[] xPoints = {
                x0, // 左下
                x0, // 左上
                x0 + 6 * w, // 上方正方形左上
                x0 + 6 * w, // 上方正方形右上
                x0 + 18 * w, // 上方正方形右上
                x0 + 18 * w, // 上方正方形右下
                x0 + 24 * w, // 右上
                x0 + 24 * w, // 右下
                x0 // 回到左下
        };
        double[] yPoints = {
                y0 + 12 * h, // 左下
                y0, // 左上
                y0, // 上方正方形左上
                y0 - 12 * h, // 上方正方形右上
                y0 - 12 * h, // 上方正方形右上
                y0, // 上方正方形右下
                y0, // 右上
                y0 + 12 * h, // 右下
                y0 + 12 * h // 回到左下
        };

        gc.fillPolygon(xPoints, yPoints, 8);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);
        gc.strokePolygon(xPoints, yPoints, 8);
    }

    @Override
    public void addDimensionLabels(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(14));
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);

        double x0 = rectBottom.getX();
        double y0 = rectBottom.getY();
        double x1 = rectBottom.getX() + rectBottom.getWidth();
        double y1 = rectBottom.getY();
        double x2 = rectBottom.getX() + rectBottom.getWidth();
        double y2 = rectBottom.getY() + rectBottom.getHeight();
        double x3 = rectBottom.getX();
        double y3 = rectBottom.getY() + rectBottom.getHeight();
        double x4 = rectTop.getX();
        double y4 = rectTop.getY();
        double x5 = rectTop.getX() + rectTop.getWidth();
        double y5 = rectTop.getY();
        double x6 = rectTop.getX() + rectTop.getWidth();
        double y6 = rectTop.getY() + rectTop.getHeight();
        double x7 = rectTop.getX();
        double y7 = rectTop.getY() + rectTop.getHeight();

        // 顶部宽 36cm
        gc.fillText("36 cm", x4 + rectTop.getWidth() / 2 - 15, y4 - 10);
        gc.strokeLine(x4, y4 - 5, x5, y4 - 5);
        gc.strokeLine(x4, y4 - 2, x4, y4 - 8);
        gc.strokeLine(x5, y4 - 2, x5, y4 - 8);

        // 左侧高 36cm
        gc.fillText("36 cm", x4 - 30, y4 + rectTop.getHeight() / 2);
        gc.strokeLine(x4 - 5, y4, x4 - 5, y7);
        gc.strokeLine(x4 - 2, y4, x4 - 8, y4);
        gc.strokeLine(x4 - 2, y7, x4 - 8, y7);

        // 底部宽 60cm
        gc.fillText("60 cm", x0 + rectBottom.getWidth() / 2 - 15, y2 + 35);
        gc.strokeLine(x0, y2 + 20, x2, y2 + 20);
        gc.strokeLine(x0, y2 + 17, x0, y2 + 23);
        gc.strokeLine(x2, y2 + 17, x2, y2 + 23);

        // 下方高 36cm
        gc.fillText("36 cm", x1 + 10, y1 + rectBottom.getHeight() / 2);
        gc.strokeLine(x1 + 5, y1, x1 + 5, y2);
        gc.strokeLine(x1 + 2, y1, x1 + 8, y1);
        gc.strokeLine(x1 + 2, y2, x1 + 8, y2);
    }

    @Override
    public double calculateArea() {
        return 3456.0; // 第八个图形：复杂矩形组合
    }
}