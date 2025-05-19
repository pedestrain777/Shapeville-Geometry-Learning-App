package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.ArrayList;

/**
 * 复合形状1: 矩形+三角形组合
 */
public class CompoundShape1 extends AbstractCompoundShape {
    private Rectangle rect;
    private Triangle tri;

    public CompoundShape1() {
        super(0, "Compound Shape 1");
    }

    @Override
    protected void createComponents() {
        // 图1：矩形+三角形
        rect = new Rectangle(14 * scaleFactor, 14 * scaleFactor); // 图片中的矩形尺寸
        // 三角形的垂直底边与矩形右侧等高，水平高为5*scaleFactor
        tri = new Triangle(14 * scaleFactor, 5 * scaleFactor); // 底边(垂直), 高(水平)
        components.add(rect);
        components.add(tri);
    }

    @Override
    protected void positionComponents(double centerX, double centerY) {
        // 计算整体的宽度和高度
        double totalWidth = rect.getWidth() + tri.getHeight();
        double totalHeight = rect.getHeight();

        // 计算整体起始位置，使其居中
        double startX = centerX - totalWidth / 2;
        double startY = centerY - totalHeight / 2;

        // 设置矩形位置
        rect.setPosition(startX, startY);

        // 设置三角形位置，使其垂直底边与矩形右侧对齐，且垂直居中
        tri.setPosition(startX + rect.getWidth(), startY + rect.getHeight() / 2 - tri.getBase() / 2);
    }

    @Override
    protected void drawShape(GraphicsContext gc) {
        // 统一颜色，只绘制外轮廓
        double rectX = rect.getX();
        double rectY = rect.getY();
        double rectWidth = rect.getWidth();
        double rectHeight = rect.getHeight();

        double triX = tri.getX();
        double triY = tri.getY();
        double triBase = tri.getBase(); // 垂直高度
        double triHeight = tri.getHeight(); // 水平长度

        // 填充整个复合图形
        ArrayList<Double> xPointsList = new ArrayList<>();
        ArrayList<Double> yPointsList = new ArrayList<>();

        // 添加矩形左上、左下、右下顶点
        xPointsList.add(rectX);
        yPointsList.add(rectY);
        xPointsList.add(rectX);
        yPointsList.add(rectY + rectHeight);
        xPointsList.add(rectX + rectWidth);
        yPointsList.add(rectY + rectHeight);

        // 添加三角形右下顶点，右侧尖点，右上顶点
        xPointsList.add(triX);
        yPointsList.add(triY + triBase);
        xPointsList.add(triX + triHeight);
        yPointsList.add(triY + triBase / 2);
        xPointsList.add(triX);
        yPointsList.add(triY);

        // 排序顶点 (简单排序，可能需要更复杂的几何排序方法)
        double[] sortedX = xPointsList.stream().mapToDouble(Double::doubleValue).toArray();
        double[] sortedY = yPointsList.stream().mapToDouble(Double::doubleValue).toArray();

        // 简单的填充多边形
        gc.fillPolygon(sortedX, sortedY, sortedX.length);

        // 绘制外轮廓
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);

        // 绘制矩形左、下、上边
        gc.strokeLine(rectX, rectY, rectX, rectY + rectHeight); // 左边
        gc.strokeLine(rectX, rectY + rectHeight, rectX + rectWidth, rectY + rectHeight); // 下边
        gc.strokeLine(rectX, rectY, rectX + rectWidth, rectY); // 上边

        // 绘制连接三角形的边 (矩形右下到三角形右下，三角形右下到尖点，尖点到三角形右上，三角形右上到矩形右上)
        gc.strokeLine(rectX + rectWidth, rectY + rectHeight, triX, triY + triBase); // 矩形右下到三角形右下
        gc.strokeLine(triX, triY + triBase, triX + triHeight, triY + triBase / 2); // 三角形右下到尖点
        gc.strokeLine(triX + triHeight, triY + triBase / 2, triX, triY); // 尖点到三角形右上
        gc.strokeLine(triX, triY, rectX + rectWidth, rectY); // 三角形右上到矩形右上
    }

    @Override
    public void addDimensionLabels(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(14));
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);

        double rectX = rect.getX();
        double rectY = rect.getY();
        double rectWidth = rect.getWidth();
        double rectHeight = rect.getHeight();

        double triX = tri.getX();
        double triY = tri.getY();
        double triBase = tri.getBase(); // 垂直高度
        double triHeight = tri.getHeight(); // 水平长度

        // 标注矩形高度 (左侧)
        gc.fillText(String.format("%.0f cm", 14.0), rectX - 30, rectY + rectHeight / 2);
        gc.strokeLine(rectX - 5, rectY, rectX - 5, rectY + rectHeight);
        gc.strokeLine(rectX - 2, rectY, rectX - 8, rectY);
        gc.strokeLine(rectX - 2, rectY + rectHeight, rectX - 8, rectY + rectHeight);

        // 标注矩形底部宽度 (底部)
        gc.fillText(String.format("%.0f cm", 14.0), rectX + rectWidth / 2 - 15, rectY + rectHeight + 15);
        gc.strokeLine(rectX, rectY + rectHeight + 5, rectX + rectWidth, rectY + rectHeight + 5);
        gc.strokeLine(rectX, rectY + rectHeight + 2, rectX, rectY + rectHeight + 8);
        gc.strokeLine(rectX + rectWidth, rectY + rectHeight + 2, rectX + rectWidth, rectY + rectHeight + 8);

        // 添加新的水平长度标注 (22cm)
        double totalWidth = rectWidth + triHeight; // 总宽度
        gc.fillText(String.format("%.0f cm", 22.0), rectX + totalWidth / 2 - 15, rectY - 25);
        gc.strokeLine(rectX, rectY - 15, rectX + totalWidth, rectY - 15);
        gc.strokeLine(rectX, rectY - 12, rectX, rectY - 18);
        gc.strokeLine(rectX + totalWidth, rectY - 12, rectX + totalWidth, rectY - 18);
    }

    @Override
    public double calculateArea() {
        return 252.0; // 矩形(14x14) + 三角形的面积
    }
}