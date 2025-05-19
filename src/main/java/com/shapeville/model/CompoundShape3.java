package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * 复合形状3: 凹形（矩形+小矩形）
 */
public class CompoundShape3 extends AbstractCompoundShape {
    private Rectangle rect1; // 左侧矩形
    private Rectangle rect2; // 右侧上方矩形

    public CompoundShape3() {
        super(2, "Compound Shape 3");
    }

    @Override
    protected void createComponents() {
        // 图3：凹形（矩形+小矩形） - 创建基本形状
        // 根据图片，左侧矩形尺寸 18x19，右侧上方矩形尺寸 16x16
        rect1 = new Rectangle(18 * scaleFactor, 19 * scaleFactor); // 左侧矩形 (宽x高)
        rect2 = new Rectangle(16 * scaleFactor, 16 * scaleFactor); // 右侧上方矩形 (宽x高)
        components.add(rect1);
        components.add(rect2);
    }

    @Override
    protected void positionComponents(double centerX, double centerY) {
        // 根据图片结构，左侧矩形宽18高19，右侧矩形宽16高16
        // 整体宽度为 18 + 16 = 34
        // 整体高度为 19
        double totalWidth = (18 + 16) * scaleFactor;
        double totalHeight = 19 * scaleFactor;

        // 计算整体起始位置，使其居中
        double startX = centerX - totalWidth / 2;
        double startY = centerY - totalHeight / 2;

        // 设置左侧矩形位置 (左上角)
        rect1.setPosition(startX, startY);

        // 设置右侧上方矩形位置 (右侧上方)
        rect2.setPosition(startX + rect1.getWidth(), startY); // 在左侧矩形右侧，顶部对齐
    }

    @Override
    protected void drawShape(GraphicsContext gc) {
        // 统一颜色，只绘制外轮廓
        double rect1X = rect1.getX();
        double rect1Y = rect1.getY();
        double rect1Width = rect1.getWidth();
        double rect1Height = rect1.getHeight();

        double rect2X = rect2.getX();
        double rect2Y = rect2.getY();
        double rect2Width = rect2.getWidth();
        double rect2Height = rect2.getHeight();

        // 填充整个复合图形
        double[] xPointsFill = {
                rect1X, rect1X + rect1Width, rect1X + rect1Width, rect2X + rect2Width,
                rect2X + rect2Width, rect1X
        };
        double[] yPointsFill = {
                rect1Y, rect1Y, rect1Y + (rect1Height - rect2Height),
                rect1Y + (rect1Height - rect2Height), rect1Y + rect1Height, rect1Y + rect1Height
        };
        gc.fillPolygon(xPointsFill, yPointsFill, 6);

        // 绘制外轮廓
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);

        // 绘制外围六条边
        gc.strokeLine(rect1X, rect1Y, rect1X + rect1Width, rect1Y); // 左上横边
        gc.strokeLine(rect1X + rect1Width, rect1Y, rect1X + rect1Width, rect1Y + (rect1Height - rect2Height)); // 右侧上部竖边
        gc.strokeLine(rect1X + rect1Width, rect1Y + (rect1Height - rect2Height), rect2X + rect2Width,
                rect1Y + (rect1Height - rect2Height)); // 右侧连接横边
        gc.strokeLine(rect2X + rect2Width, rect1Y + (rect1Height - rect2Height), rect2X + rect2Width,
                rect1Y + rect1Height); // 右下竖边
        gc.strokeLine(rect2X + rect2Width, rect1Y + rect1Height, rect1X, rect1Y + rect1Height); // 下横边
        gc.strokeLine(rect1X, rect1Y + rect1Height, rect1X, rect1Y); // 左侧竖边
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

        // 标注左侧总高 19 cm
        gc.fillText(String.format("%.0f cm", 19.0), rect1X - 30, rect1Y + rect1Height / 2);
        gc.strokeLine(rect1X - 5, rect1Y, rect1X - 5, rect1Y + rect1Height);
        gc.strokeLine(rect1X - 8, rect1Y, rect1X - 2, rect1Y);
        gc.strokeLine(rect1X - 8, rect1Y + rect1Height, rect1X - 2, rect1Y + rect1Height);

        // 标注顶部左侧 18 cm 宽
        gc.fillText(String.format("%.0f cm", 18.0), rect1X + rect1Width/2 - 15, rect1Y - 10);
        gc.strokeLine(rect1X, rect1Y - 5, rect1X + rect1Width, rect1Y - 5);
        gc.strokeLine(rect1X, rect1Y - 8, rect1X, rect1Y - 2);
        gc.strokeLine(rect1X + rect1Width, rect1Y - 8, rect1X + rect1Width, rect1Y - 2);

        // 标注顶部右侧 16 cm 宽 - 使用实际的顶部位置
        double rightTopY = rect1Y + (rect1Height - rect2Height); // 使用实际绘制时的y坐标
        gc.fillText(String.format("%.0f cm", 16.0), rect2X + rect2Width/2 - 15, rightTopY - 10);
        gc.strokeLine(rect2X, rightTopY - 5, rect2X + rect2Width, rightTopY - 5);
        gc.strokeLine(rect2X, rightTopY - 8, rect2X, rightTopY - 2);
        gc.strokeLine(rect2X + rect2Width, rightTopY - 8, rect2X + rect2Width, rightTopY - 2);

        // 标注右侧 16 cm 高
        double rightSideX = rect2X + rect2Width;
        double rightEdgeYStart = rect1Y + (rect1Height - rect2Height);
        double rightEdgeYEnd = rect1Y + rect1Height;
        double rightEdgeHeight = rightEdgeYEnd - rightEdgeYStart;

        gc.fillText(String.format("%.0f cm", 16.0), rightSideX + 10, rightEdgeYStart + rightEdgeHeight / 2);
        gc.strokeLine(rightSideX + 5, rightEdgeYStart, rightSideX + 5, rightEdgeYEnd);
        gc.strokeLine(rightSideX + 2, rightEdgeYStart, rightSideX + 8, rightEdgeYStart);
        gc.strokeLine(rightSideX + 2, rightEdgeYEnd, rightSideX + 8, rightEdgeYEnd);
    }

    @Override
    public double calculateArea() {
        return 598.0; // 第三个图形：凹形
    }
}