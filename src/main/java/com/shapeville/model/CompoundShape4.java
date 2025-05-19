package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * 复合形状4: T形（上方正方形和下方矩形的组合）
 */
public class CompoundShape4 extends AbstractCompoundShape {
    private Rectangle rectTop; // 上方正方形
    private Rectangle rectBottom; // 下方长方形

    public CompoundShape4() {
        super(3, "Compound Shape 4");
    }

    @Override
    protected void createComponents() {
        // 图4：由两个矩形组成，上方正方形12x12，下方矩形24x6
        rectTop = new Rectangle(12 * scaleFactor, 12 * scaleFactor); // 上方正方形
        rectBottom = new Rectangle(24 * scaleFactor, 6 * scaleFactor); // 下方长方形
        components.add(rectTop);
        components.add(rectBottom);
    }

    @Override
    protected void positionComponents(double centerX, double centerY) {
        double totalWidth = 24 * scaleFactor;
        double totalHeight = 12 * scaleFactor + 6 * scaleFactor;
        double startX = centerX - totalWidth / 2;
        double startY = centerY - totalHeight / 2;

        // 更新：使上方正方形位于新的位置以匹配2cm和10cm宽度
        rectTop.setPosition(startX + 2 * scaleFactor, startY);

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
                x0 + 2 * w, // 上方正方形左上
                x0 + 2 * w, // 上方正方形左下边
                x0 + 14 * w, // 上方正方形右上边
                x0 + 14 * w, // 上方正方形右下
                x0 + 24 * w, // 右上
                x0 + 24 * w, // 右下
                x0 // 回到左下
        };
        double[] yPoints = {
                y0 + 6 * h, // 左下
                y0, // 左上
                y0, // 上方正方形左上
                y0 - 12 * h, // 上方正方形左下边
                y0 - 12 * h, // 上方正方形右上边
                y0, // 上方正方形右下
                y0, // 右上
                y0 + 6 * h, // 右下
                y0 + 6 * h // 回到左下
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

        // 获取底部矩形的坐标
        double x0 = rectBottom.getX();
        double y0 = rectBottom.getY();
        double rectBottomWidth = rectBottom.getWidth();
        double rectBottomHeight = rectBottom.getHeight();

        // 获取顶部正方形的坐标
        double topX = rectTop.getX();
        double topY = rectTop.getY();
        double topWidth = rectTop.getWidth();
        double topHeight = rectTop.getHeight();

        // 计算关键点坐标
        double x1 = x0 + rectBottomWidth; // 底部矩形右上角X
        double y1 = y0; // 底部矩形右上角Y
        double x2 = x1; // 底部矩形右下角X
        double y2 = y0 + rectBottomHeight; // 底部矩形右下角Y
        double x3 = x0; // 底部矩形左下角X
        double y3 = y0 + rectBottomHeight; // 底部矩形左下角Y

        // 顶部正方形右上和右下点
        double topRightX = topX + topWidth;
        double topRightTopY = topY;
        double topRightBottomY = topY + topHeight;

        // 顶部宽 12cm
        gc.fillText("12 cm", topX + topWidth / 2 - 15, topY - 10);
        gc.strokeLine(topX, topY - 5, topRightX, topY - 5);
        gc.strokeLine(topX, topY - 2, topX, topY - 8);
        gc.strokeLine(topRightX, topY - 2, topRightX, topY - 8);

        // 左侧高 12cm
        gc.fillText("12 cm", topX - 30, topY + topHeight / 2);
        gc.strokeLine(topX - 5, topY, topX - 5, topRightBottomY);
        gc.strokeLine(topX - 2, topY, topX - 8, topY);
        gc.strokeLine(topX - 2, topRightBottomY, topX - 8, topRightBottomY);

        // 右侧高 18cm
        gc.fillText("18 cm", topRightX + 10, topRightTopY + (y2 - topRightTopY) / 2);
        gc.strokeLine(topRightX + 5, topRightTopY, topRightX + 5, y2);
        gc.strokeLine(topRightX + 2, topRightTopY, topRightX + 8, topRightTopY);
        gc.strokeLine(topRightX + 2, y2, topRightX + 8, y2);

        // 底部宽 24cm
        gc.fillText("24 cm", x0 + rectBottomWidth / 2 - 15, y2 + 35);
        gc.strokeLine(x0, y2 + 20, x2, y2 + 20);
        gc.strokeLine(x0, y2 + 17, x0, y2 + 23);
        gc.strokeLine(x2, y2 + 17, x2, y2 + 23);

        // 左下宽 2cm - 调整位置，居中于2cm宽的段上
        gc.fillText("2 cm", x0 + scaleFactor, y3 + 15);
        gc.strokeLine(x0, y3 + 5, topX, y3 + 5);
        gc.strokeLine(x0, y3 + 2, x0, y3 + 8);
        gc.strokeLine(topX, y3 + 2, topX, y3 + 8);

        // 右下宽 10cm - 调整位置，居中于10cm宽的段上
        gc.fillText("10 cm", topRightX + 5 * scaleFactor, y2 + 15);
        gc.strokeLine(topRightX, y2 + 5, x2, y2 + 5);
        gc.strokeLine(topRightX, y2 + 2, topRightX, y2 + 8);
        gc.strokeLine(x2, y2 + 2, x2, y2 + 8);

        // 下方高 6cm
        gc.fillText("6 cm", x1 + 10, y1 + rectBottomHeight / 2);
        gc.strokeLine(x1 + 5, y1, x1 + 5, y2);
        gc.strokeLine(x1 + 2, y1, x1 + 8, y1);
        gc.strokeLine(x1 + 2, y2, x1 + 8, y2);
    }

    @Override
    public double calculateArea() {
        return 288.0; // 第四个图形：T形
    }
}