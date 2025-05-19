package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * 复合形状7: 五边形（矩形+三角形）
 */
public class CompoundShape7 extends AbstractCompoundShape {
    private Rectangle rect;
    private Triangle tri;

    public CompoundShape7() {
        super(6, "Compound Shape 7");
    }

    @Override
    protected void createComponents() {
        // 图7：复合形状（五边形，矩形+三角形） - 创建基本形状
        rect = new Rectangle(14 * scaleFactor, 5 * scaleFactor); // 矩形
        tri = new Triangle(14 * scaleFactor, 10 * scaleFactor); // 三角形
        components.add(rect);
        components.add(tri);
    }

    @Override
    protected void positionComponents(double centerX, double centerY) {
        // 图7：复合形状（五边形） - 定位基本形状
        double totalWidth = 14 * scaleFactor;
        double totalHeight = 15 * scaleFactor;
        double startX = centerX - totalWidth / 2;
        double startY = centerY - totalHeight / 2;
        rect.setPosition(startX, startY);
        tri.setPosition(startX, startY + rect.getHeight());
    }

    @Override
    protected void drawShape(GraphicsContext gc) {
        // 统一颜色，只绘制外轮廓
        gc.setFill(Color.LIGHTBLUE);

        double x0 = rect.getX();
        double y0 = rect.getY();
        double x1 = x0;
        double y1 = y0 + rect.getHeight();
        double x2 = x1 + rect.getWidth();
        double y2 = y1;
        double x3 = x0 + rect.getWidth();
        double y3 = y0;
        double x4 = x0 + (3.0 / 7.0) * rect.getWidth();
        double y4 = y0 - tri.getHeight();

        double[] xPoints = { x0, x1, x2, x3, x4 };
        double[] yPoints = { y0, y1, y2, y3, y4 };

        gc.fillPolygon(xPoints, yPoints, 5);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);
        gc.strokePolygon(xPoints, yPoints, 5);
    }

    @Override
    public void addDimensionLabels(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(14));
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);

        double x0 = rect.getX(),
                y0 = rect.getY(); // 矩形左上
        double w = rect.getWidth(), // = 14×scale
                h1 = rect.getHeight(); // = 5×scale
        double h2 = tri.getHeight(); // = 10×scale

        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);
        gc.setLineWidth(1);
        gc.setFont(Font.font(14));

        double offset = 8; // 主标注线离图形边缘的距离
        double tick = 5; // 刻线超出主线两端的长度

        // —— 1) 三角高度（15） —— //
        double apexX = x0 + w * (3.0 / 7.0);
        double apexY = y0 - h2;
        double baseY = y0 + h1;
        double lineTop = apexY - offset / 4;
        double lineBot = baseY + offset;
        gc.strokeLine(apexX, lineTop, apexX, lineBot);
        gc.strokeLine(apexX - tick, lineTop, apexX + tick, lineTop);
        gc.strokeLine(apexX - tick, lineBot, apexX + tick, lineBot);
        gc.fillText("15cm", apexX + 5, (lineTop + lineBot) / 2 + 5);

        // —— 2) 矩形宽度（分为6和8） —— //
        double bottomY = y0 + h1;
        double yLine = bottomY + offset;

        // 左侧宽度 6cm
        gc.strokeLine(x0, yLine, apexX, yLine);
        gc.strokeLine(x0, yLine - tick, x0, yLine + tick);
        gc.strokeLine(apexX, yLine - tick, apexX, yLine + tick);
        gc.fillText("6cm", x0 + (apexX - x0) / 2 - 10, yLine + 15);

        // 右侧宽度 8cm
        gc.strokeLine(apexX, yLine, x0 + w, yLine);
        gc.strokeLine(apexX, yLine - tick, apexX, yLine + tick);
        gc.strokeLine(x0 + w, yLine - tick, x0 + w, yLine + tick);
        gc.fillText("8cm", apexX + (x0 + w - apexX) / 2 - 10, yLine + 15);

        // —— 3) 矩形高度（5） —— //
        double xLeft = x0 - offset;
        double yTopL = y0;
        double yBotL = y0 + h1;
        gc.strokeLine(xLeft, yTopL, xLeft, yBotL);
        gc.strokeLine(xLeft - tick, yTopL, xLeft + tick, yTopL);
        gc.strokeLine(xLeft - tick, yBotL, xLeft + tick, yBotL);
        gc.fillText("5cm", xLeft - 15, yTopL + h1 / 2 + 5);
    }

    @Override
    public double calculateArea() {
        return 140.0; // 第七个图形：复合形状（五边形）
    }
}