package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Sphere extends Shape {
    private double radius;
    private static final int LATITUDES = 20; // 纬度分段

    public Sphere(double radius) {
        super("Sphere", Color.web("#F0E442"));  // 色盲友好黄色
        this.radius = radius;
        // 旋转影响光照和视角
        setRotationX(0);
        setRotationY(0);
    }

    @Override
    public double calculateArea() {
        return 4 * Math.PI * radius * radius;
    }

    @Override
    public void draw(GraphicsContext gc) {
        double cx = x + radius;
        double cy = y + radius;

        // 清空并绘制球外轮廓
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.setFill(color);
        gc.fillOval(x, y, radius * 2, radius * 2);
        gc.strokeOval(x, y, radius * 2, radius * 2);

        // 获取旋转弧度
        double rx = Math.toRadians(getRotationX());
        double ry = Math.toRadians(getRotationY());

        // 依次绘制纬线
        for (int i = 1; i < LATITUDES; i++) {
            double t = (double) i / LATITUDES;
            double lat = (t - 0.5) * Math.PI; // -π/2 到 π/2

            int segments = 40;  // 每个纬线细分段数
            double[] xPoints = new double[segments];
            double[] yPoints = new double[segments];

            for (int j = 0; j < segments; j++) {
                double lon = (double) j / segments * 2 * Math.PI; // 经度
                double x3 = radius * Math.cos(lat) * Math.cos(lon);
                double y3 = radius * Math.sin(lat);
                double z3 = radius * Math.cos(lat) * Math.sin(lon);

                // 先绕X轴旋转
                double y2 = y3 * Math.cos(rx) - z3 * Math.sin(rx);
                double z2 = y3 * Math.sin(rx) + z3 * Math.cos(rx);

                // 再绕Y轴旋转
                double x2 = x3 * Math.cos(ry) + z2 * Math.sin(ry);
                double zFinal = -x3 * Math.sin(ry) + z2 * Math.cos(ry);

                // 投影到2D屏幕
                xPoints[j] = cx + x2;
                yPoints[j] = cy + y2;
            }

            // 计算亮度（简单根据纬度）
            double bright = Math.max(0, Math.cos(lat) * Math.cos(rx));
            Color fillCol = color.deriveColor(0, 1, 0.5 + 0.5 * bright, 1);
            gc.setStroke(fillCol);
            gc.setLineWidth(1);

            // 绘制纬线
            for (int j = 0; j < segments - 1; j++) {
                gc.strokeLine(xPoints[j], yPoints[j], xPoints[j + 1], yPoints[j + 1]);
            }
        }
    }
    @Override
    public Sphere copy() {
        Sphere newSphere = new Sphere(this.radius);
        newSphere.setColor(this.getColor());
        newSphere.setPosition(this.x, this.y);
        newSphere.setRotationX(this.rotationX);
        newSphere.setRotationY(this.rotationY);
        return newSphere;
    }
}
