package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * 复合形状5: 直角梯形
 */
public class CompoundShape5 extends AbstractCompoundShape {

    public CompoundShape5() {
        super(4, "Compound Shape 5");
    }

    @Override
    protected void createComponents() {
        // 图5：直角梯形 - 创建基本形状（用点集描述）
        // 下底4cm，左边6cm，右边2cm，上底斜边5cm
        components.add(new Shape2D("rightTrapezium", Color.LIGHTBLUE, 8 * scaleFactor, 12 * scaleFactor) {
            @Override
            public double calculateArea() {
                return 0.5 * (4 + 5) * 6; // 梯形面积公式
            }

            @Override
            public void draw(GraphicsContext gc) {
                double x = this.x;
                double y = this.y;
                double scale = scaleFactor;
                double[] xPoints = { x, x, x + 8 * scale, x + 8 * scale };
                double[] yPoints = { y, y - 12 * scale, y - 4 * scale, y };
                gc.setFill(this.getColor());
                gc.fillPolygon(xPoints, yPoints, 4);
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(2.0);
                gc.strokePolygon(xPoints, yPoints, 4);
            }

            @Override
            public Shape2D copy() {
                Shape2D that = this;
                return new Shape2D("rightTrapezium", that.getColor(), 8 * scaleFactor, 12 * scaleFactor) {
                    @Override
                    public double calculateArea() {
                        return that.calculateArea();
                    }

                    @Override
                    public void draw(GraphicsContext gc) {
                        that.draw(gc);
                    }

                    @Override
                    public Shape2D copy() {
                        return this;
                    }
                };
            }
        });
    }

    @Override
    protected void positionComponents(double centerX, double centerY) {
        // 图5：直角梯形 - 定位基本形状
        Shape2D trap = components.get(0);
        double width = 8 * scaleFactor;
        double height = 12 * scaleFactor;
        double startX = centerX - width / 2;
        double startY = centerY + height / 2;
        trap.setPosition(startX, startY);
    }

    @Override
    protected void drawShape(GraphicsContext gc) {
        // 图5的特殊绘制：直角梯形，填充浅蓝色，仅绘制外轮廓
        components.get(0).draw(gc);
    }

    @Override
    public void addDimensionLabels(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(14));
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);

        Shape2D trap = components.get(0);
        double x = trap.getX();
        double y = trap.getY();
        double scale = scaleFactor;

        // 直接在此处计算顶点
        double[] xPoints = { x, x, x + 8 * scale, x + 8 * scale };
        double[] yPoints = { y, y - 12 * scale, y - 4 * scale, y };

        // 下底 4cm
        gc.fillText("4 cm", x + 4 * scale - 10, y + 20);
        gc.strokeLine(x, y + 10, x + 8 * scale, y + 10);
        gc.strokeLine(x, y + 7, x, y + 13);
        gc.strokeLine(x + 8 * scale, y + 7, x + 8 * scale, y + 13);

        // 左边 6cm
        gc.fillText("6 cm", x - 35, y - 6 * scale);
        gc.strokeLine(x - 10, y, x - 10, y - 12 * scale);
        gc.strokeLine(x - 7, y, x - 13, y);
        gc.strokeLine(x - 7, y - 12 * scale, x - 13, y - 12 * scale);

        // 右边 2cm
        gc.fillText("2 cm", x + 8 * scale + 10, y - scale);
        gc.strokeLine(x + 8 * scale + 5, y, x + 8 * scale + 5, y - 4 * scale);
        gc.strokeLine(x + 8 * scale + 2, y, x + 8 * scale + 8, y);
        gc.strokeLine(x + 8 * scale + 2, y - 4 * scale, x + 8 * scale + 8, y - 4 * scale);
    }

    @Override
    public double calculateArea() {
        return 16.0; // 第五个图形：直角梯形
    }
}