package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * 复合形状9: 楼梯形状（两个矩形）
 */
public class CompoundShape9 extends AbstractCompoundShape {
    private Rectangle rectLeft; // 左侧矩形
    private Rectangle rectRight; // 右侧矩形

    public CompoundShape9() {
        super(8, "Compound Shape 9");
    }

    @Override
    protected void createComponents() {
        // 图9：楼梯形状（两个矩形） - 创建基本形状
        // 根据图片，左侧矩形长11，宽10，右侧矩形长宽都为8
        rectLeft = new Rectangle(10 * scaleFactor, 11 * scaleFactor); // 左侧矩形 (宽x高)
        rectRight = new Rectangle(8 * scaleFactor, 8 * scaleFactor); // 右侧矩形 (宽x高)
        components.add(rectLeft);
        components.add(rectRight);
    }

    @Override
    protected void positionComponents(double centerX, double centerY) {
        // 图9：楼梯形状 - 定位基本形状
        // 根据图片结构，左侧矩形宽10高11，右侧矩形宽8高8
        // 整体宽度为 10 + 8 = 18
        // 整体高度为 11
        double totalWidth = (10 + 8) * scaleFactor;
        double totalHeight = 11 * scaleFactor;

        // 计算整体起始位置，使其居中
        double startX = centerX - totalWidth / 2;
        double startY = centerY - totalHeight / 2;

        // 设置左侧矩形位置 (左上角)
        rectLeft.setPosition(startX, startY);

        // 设置右侧矩形位置 (右侧上方)
        rectRight.setPosition(startX + rectLeft.getWidth(),
                startY + (rectLeft.getHeight() - rectRight.getHeight())); // 在左侧矩形右侧，底部对齐
    }

    @Override
    protected void drawShape(GraphicsContext gc) {
        // 统一颜色，只绘制外轮廓
        double rectLeftX = rectLeft.getX();
        double rectLeftY = rectLeft.getY();
        double rectLeftWidth = rectLeft.getWidth();
        double rectLeftHeight = rectLeft.getHeight();

        double rectRightX = rectRight.getX();
        double rectRightY = rectRight.getY();
        double rectRightWidth = rectRight.getWidth();
        double rectRightHeight = rectRight.getHeight();

        // 填充整个复合图形
        double[] xPointsFill = {
                rectLeftX, // 左上
                rectLeftX + rectLeftWidth, // 左上到右上（连接）
                rectLeftX + rectLeftWidth, // 转折点，左矩形右上
                rectRightX + rectRightWidth, // 右矩形右上
                rectRightX + rectRightWidth, // 右矩形右下
                rectLeftX + rectLeftWidth, // 左矩形右下
                rectLeftX, // 左矩形左下
        };

        double[] yPointsFill = {
                rectLeftY, // 左上
                rectLeftY, // 左矩形上边
                rectRightY, // 连接点（右矩形顶部）
                rectRightY, // 右矩形右上
                rectRightY + rectRightHeight, // 右矩形右下
                rectLeftY + rectLeftHeight, // 左矩形右下
                rectLeftY + rectLeftHeight // 左下
        };
        gc.fillPolygon(xPointsFill, yPointsFill, xPointsFill.length);

        // 绘制外轮廓
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);

        // 绘制外围六条边
        gc.strokeLine(rectLeftX, rectLeftY, rectLeftX + rectLeftWidth, rectLeftY); // 左上横边
        gc.strokeLine(rectLeftX + rectLeftWidth, rectLeftY, rectLeftX + rectLeftWidth, rectRightY); // 左上到右上的竖边
        gc.strokeLine(rectLeftX + rectLeftWidth, rectRightY, rectRightX + rectRightWidth, rectRightY); // 右侧横边
        gc.strokeLine(rectRightX + rectRightWidth, rectRightY, rectRightX + rectRightWidth,
                rectRightY + rectRightHeight); // 右下竖边
        gc.strokeLine(rectRightX + rectRightWidth, rectRightY + rectRightHeight, rectLeftX,
                rectRightY + rectRightHeight); // 下横边
        gc.strokeLine(rectLeftX, rectRightY + rectRightHeight, rectLeftX, rectLeftY); // 左侧竖边
    }

    @Override
    public void addDimensionLabels(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(14));
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);

        double rectLeftX = rectLeft.getX();
        double rectLeftY = rectLeft.getY();
        double rectLeftWidth = rectLeft.getWidth();
        double rectLeftHeight = rectLeft.getHeight();

        double rectRightX = rectRight.getX();
        double rectRightY = rectRight.getY();
        double rectRightWidth = rectRight.getWidth();
        double rectRightHeight = rectRight.getHeight();

        // 标注左侧高 11 cm
        gc.fillText(String.format("%.0f cm", 11.0), rectLeftX - 30, rectLeftY + rectLeftHeight / 2);
        gc.strokeLine(rectLeftX - 5, rectLeftY, rectLeftX - 5, rectLeftY + rectLeftHeight);
        gc.strokeLine(rectLeftX - 8, rectLeftY, rectLeftX - 2, rectLeftY);
        gc.strokeLine(rectLeftX - 8, rectLeftY + rectLeftHeight, rectLeftX - 2, rectLeftY + rectLeftHeight);

        // 标注底部总宽 18 cm
        gc.fillText(String.format("%.0f cm", 18.0), rectLeftX + (rectLeftWidth + rectRightWidth) / 2 - 15,
                rectLeftY + rectLeftHeight + 15);
        gc.strokeLine(rectLeftX, rectLeftY + rectLeftHeight + 5, rectLeftX + rectLeftWidth + rectRightWidth,
                rectLeftY + rectLeftHeight + 5);
        gc.strokeLine(rectLeftX, rectLeftY + rectLeftHeight + 2, rectLeftX, rectLeftY + rectLeftHeight + 8);
        gc.strokeLine(rectLeftX + rectLeftWidth + rectRightWidth, rectLeftY + rectLeftHeight + 2,
                rectLeftX + rectLeftWidth + rectRightWidth, rectLeftY + rectLeftHeight + 8);

        // 标注左侧顶部宽 10 cm
        gc.fillText(String.format("%.0f cm", 10.0), rectLeftX + rectLeftWidth / 2 - 15, rectLeftY - 10);
        gc.strokeLine(rectLeftX, rectLeftY - 5, rectLeftX + rectLeftWidth, rectLeftY - 5);
        gc.strokeLine(rectLeftX, rectLeftY - 8, rectLeftX, rectLeftY - 2);
        gc.strokeLine(rectLeftX + rectLeftWidth, rectLeftY - 8, rectLeftX + rectLeftWidth, rectLeftY - 2);

        // 标注右侧高 8 cm
        gc.fillText(String.format("%.0f cm", 8.0), rectRightX + rectRightWidth + 10,
                rectRightY + rectRightHeight / 2);
        gc.strokeLine(rectRightX + rectRightWidth + 5, rectRightY, rectRightX + rectRightWidth + 5,
                rectRightY + rectRightHeight);
        gc.strokeLine(rectRightX + rectRightWidth + 8, rectRightY, rectRightX + rectRightWidth + 2, rectRightY);
        gc.strokeLine(rectRightX + rectRightWidth + 8, rectRightY + rectRightHeight,
                rectRightX + rectRightWidth + 2, rectRightY + rectRightHeight);

        // 标注右侧宽 8 cm
        gc.fillText(String.format("%.0f cm", 8.0), rectRightX + rectRightWidth / 2 - 15, rectRightY - 10);
        gc.strokeLine(rectRightX, rectRightY - 5, rectRightX + rectRightWidth, rectRightY - 5);
        gc.strokeLine(rectRightX, rectRightY - 8, rectRightX, rectRightY - 2);
        gc.strokeLine(rectRightX + rectRightWidth, rectRightY - 8, rectRightX + rectRightWidth, rectRightY - 2);
    }

    @Override
    public double calculateArea() {
        return 174.0; // 第九个图形：楼梯形状
    }
}