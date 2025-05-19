package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * 复合形状6: 直角梯形（矩形+直角三角形）
 */
public class CompoundShape6 extends AbstractCompoundShape {
    private Rectangle rect;
    private Triangle tri;

    public CompoundShape6() {
        super(5, "Compound Shape 6");
    }

    @Override
    protected void createComponents() {
        // 图6：直角梯形（矩形+直角三角形）
        rect = new Rectangle(9 * scaleFactor, 11 * scaleFactor); // 左侧矩形
        tri = new Triangle(11 * scaleFactor, 11 * scaleFactor); // 右侧直角三角形
        components.add(rect);
        components.add(tri);
    }

    @Override
    protected void positionComponents(double centerX, double centerY) {
        double totalWidth = 9 * scaleFactor + 11 * scaleFactor;
        double totalHeight = 11 * scaleFactor;
        double startX = centerX - totalWidth / 2;
        double startY = centerY - totalHeight / 2;
        rect.setPosition(startX, startY);
        tri.setPosition(startX + rect.getWidth(), startY);
    }

    @Override
    protected void drawShape(GraphicsContext gc) {
        // 统一颜色，只绘制外轮廓
        gc.setFill(Color.LIGHTBLUE);

        double x0 = rect.getX();
        double y0 = rect.getY();
        double x1 = x0 + rect.getWidth();
        double y1 = y0;
        double x2 = x1 + tri.getBase();
        double y2 = y1 + tri.getHeight();
        double x3 = x0;
        double y3 = y0 + rect.getHeight();

        double[] xPoints = { x0, x1, x2, x3 };
        double[] yPoints = { y0, y1, y2, y3 };

        gc.fillPolygon(xPoints, yPoints, 4);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);
        gc.strokePolygon(xPoints, yPoints, 4);
    }

    @Override
    public void addDimensionLabels(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(14));
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);

        double x0 = rect.getX();
        double y0 = rect.getY();
        double x1 = x0 + rect.getWidth();
        double y1 = y0;
        double x2 = x1 + tri.getBase();
        double y2 = y1 + tri.getHeight();
        double x3 = x0;
        double y3 = y0 + rect.getHeight();

        // 上边 9m
        gc.fillText("9 m", x0 + rect.getWidth() / 2 - 10, y0 - 10);
        gc.strokeLine(x0, y0 - 5, x1, y1 - 5);
        gc.strokeLine(x0, y0 - 2, x0, y0 - 8);
        gc.strokeLine(x1, y1 - 2, x1, y1 - 8);

        // 左边 11m
        gc.fillText("11 m", x0 - 30, y0 + rect.getHeight() / 2);
        gc.strokeLine(x0 - 5, y0, x0 - 5, y3);
        gc.strokeLine(x0 - 2, y0, x0 - 8, y0);
        gc.strokeLine(x0 - 2, y3, x0 - 8, y3);

        // 下边 20m
        gc.fillText("20 m", x0 + (x2 - x0) / 2 - 10, y2 + 20);
        gc.strokeLine(x3, y3 + 5, x2, y2 + 5);
        gc.strokeLine(x3, y3 + 2, x3, y3 + 8);
        gc.strokeLine(x2, y2 + 2, x2, y2 + 8);
    }

    @Override
    public double calculateArea() {
        return 159.5; // 第六个图形：梯形和直角三角形
    }
}