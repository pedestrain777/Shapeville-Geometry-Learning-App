package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * 复合形状2: L形（两个矩形组合）
 */
public class CompoundShape2 extends AbstractCompoundShape {
    private Rectangle rect1; // 左上角矩形
    private Rectangle rect2; // 下方矩形

    public CompoundShape2() {
        super(1, "Compound Shape 2");
    }

    @Override
    protected void createComponents() {
        // 图2：两个矩形拼L形
        // 根据图片，左上角矩形尺寸 10x11，下方矩形尺寸 20x10
        rect1 = new Rectangle(10 * scaleFactor, 11 * scaleFactor); // 左上角矩形 (宽x高)
        rect2 = new Rectangle(20 * scaleFactor, 10 * scaleFactor); // 下方矩形 (宽x高)
        components.add(rect1);
        components.add(rect2);
    }

    @Override
    protected void positionComponents(double centerX, double centerY) {
        // 根据图片结构，下方矩形宽度 20，高度 10。左上角矩形宽度 10，高度 11。总高度 21。
        // 整体宽度 20，整体高度 21
        double totalWidth = 20 * scaleFactor;
        double totalHeight = (11 + 10) * scaleFactor; // 11(左上高)+10(下方高)

        // 计算整体起始位置，使其居中
        double startX = centerX - totalWidth / 2;
        double startY = centerY - totalHeight / 2;

        // 设置下方矩形位置 (左下角)
        rect2.setPosition(startX, startY + 11 * scaleFactor); // 在左上角矩形下方

        // 设置左上角矩形位置 (左上角)
        rect1.setPosition(startX, startY);
    }

    @Override
    protected void drawShape(GraphicsContext gc) {
        // 图2的特殊绘制：统一颜色，只绘制外轮廓
        double rect1X = rect1.getX();
        double rect1Y = rect1.getY();
        double rect1Width = rect1.getWidth();
        double rect1Height = rect1.getHeight();

        double rect2X = rect2.getX();
        double rect2Y = rect2.getY();
        double rect2Width = rect2.getWidth();
        double rect2Height = rect2.getHeight();

        // 填充整个复合图形 (L形)
        double[] xPointsFill = {
                rect1X, rect1X + rect1Width, rect1X + rect1Width, rect2X + rect2Width,
                rect2X + rect2Width, rect2X
        };
        double[] yPointsFill = {
                rect1Y, rect1Y, rect1Y + rect1Height, rect1Y + rect1Height,
                rect2Y + rect2Height, rect2Y + rect2Height
        };
        gc.fillPolygon(xPointsFill, yPointsFill, 6);

        // 绘制外轮廓
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);

        // 绘制外围的六条边
        gc.strokeLine(rect1X, rect1Y, rect1X + rect1Width, rect1Y); // 左上横边
        gc.strokeLine(rect1X + rect1Width, rect1Y, rect1X + rect1Width, rect1Y + rect1Height); // 右上竖边
        gc.strokeLine(rect1X + rect1Width, rect1Y + rect1Height, rect2X + rect2Width, rect1Y + rect1Height); // 连接两矩形的横边
        gc.strokeLine(rect2X + rect2Width, rect1Y + rect1Height, rect2X + rect2Width, rect2Y + rect2Height); // 右下竖边
        gc.strokeLine(rect2X + rect2Width, rect2Y + rect2Height, rect2X, rect2Y + rect2Height); // 下横边
        gc.strokeLine(rect2X, rect2Y + rect2Height, rect2X, rect1Y); // 左侧总竖边
    }

    @Override
    public void addDimensionLabels(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(14));
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);

        double rect1X = rect1.getX();
        double rect1Y = rect1.getY();
        double rect1Width = rect1.getWidth();
        double rect1Height = rect1.getHeight();

        double rect2X = rect2.getX();
        double rect2Y = rect2.getY();
        double rect2Width = rect2.getWidth();
        double rect2Height = rect2.getHeight();

        // 标注尺寸 (根据图片从左到右，从上到下)
        // 左侧总高 21 cm
        gc.fillText(String.format("%.0f cm", 21.0), rect1X - 30, rect1Y + (rect1Height + rect2Height) / 2);
        gc.strokeLine(rect1X - 5, rect1Y, rect1X - 5, rect2Y + rect2Height);
        gc.strokeLine(rect1X - 2, rect1Y, rect1X - 8, rect1Y);
        gc.strokeLine(rect1X - 2, rect2Y + rect2Height, rect1X - 8, rect2Y + rect2Height);

        // 左上横边 10 cm
        gc.fillText(String.format("%.0f cm", 10.0), rect1X + rect1Width / 2 - 15, rect1Y - 10);
        gc.strokeLine(rect1X, rect1Y - 5, rect1X + rect1Width, rect1Y - 5);
        gc.strokeLine(rect1X, rect1Y - 2, rect1X, rect1Y - 8);
        gc.strokeLine(rect1X + rect1Width, rect1Y - 2, rect1X + rect1Width, rect1Y - 8);

        // 右上竖边 11 cm
        gc.fillText(String.format("%.0f cm", 11.0), rect1X + rect1Width + 10, rect1Y + rect1Height / 2);
        gc.strokeLine(rect1X + rect1Width + 5, rect1Y, rect1X + rect1Width + 5, rect1Y + rect1Height);
        gc.strokeLine(rect1X + rect1Width + 2, rect1Y, rect1X + rect1Width + 8, rect1Y);
        gc.strokeLine(rect1X + rect1Width + 2, rect1Y + rect1Height, rect1X + rect1Width + 8, rect1Y + rect1Height);

        // 连接两矩形的横边 10 cm
        gc.fillText(String.format("%.0f cm", 10.0), rect1X + rect1Width + (rect2Width - rect1Width) / 2,
                rect1Y + rect1Height + 15);
        gc.strokeLine(rect1X + rect1Width, rect1Y + rect1Height + 5, rect2X + rect2Width, rect1Y + rect1Height + 5);
        gc.strokeLine(rect1X + rect1Width, rect1Y + rect1Height + 2, rect1X + rect1Width, rect1Y + rect1Height + 8);
        gc.strokeLine(rect2X + rect2Width, rect1Y + rect1Height + 2, rect2X + rect2Width, rect1Y + rect1Height + 8);

        // 右下竖边 10 cm
        gc.fillText(String.format("%.0f cm", 10.0), rect2X + rect2Width + 10, rect2Y + rect2Height / 2);
        gc.strokeLine(rect2X + rect2Width + 5, rect2Y, rect2X + rect2Width + 5, rect2Y + rect2Height);
        gc.strokeLine(rect2X + rect2Width + 2, rect2Y, rect2X + rect2Width + 8, rect2Y);
        gc.strokeLine(rect2X + rect2Width + 2, rect2Y + rect2Height, rect2X + rect2Width + 8, rect2Y + rect2Height);

        // 下横边 20 cm
        gc.fillText(String.format("%.0f cm", 20.0), rect2X + rect2Width / 2 - 15, rect2Y + rect2Height + 15);
        gc.strokeLine(rect2X, rect2Y + rect2Height + 5, rect2X + rect2Width, rect2Y + rect2Height + 5);
        gc.strokeLine(rect2X, rect2Y + rect2Height + 2, rect2X, rect2Y + rect2Height + 8);
        gc.strokeLine(rect2X + rect2Width, rect2Y + rect2Height + 2, rect2X + rect2Width, rect2Y + rect2Height + 8);
    }

    @Override
    public double calculateArea() {
        return 310.0; // 第二个图形：L形（11x11 + 20x10）
    }
}