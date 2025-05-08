package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Cone extends Shape {
    private double radius, height;
    private static final int SEGMENTS = 32; // 分段数，越大圆越平滑

    public Cone(double radius, double height) {
        super("Cone", Color.web("#009E73"));  // 色盲友好绿色
        this.radius = radius;
        this.height = height;
        // 默认等角视角：X 35.264°, Y 0°
        setRotationX(35.264);
        setRotationY(0);
    }

    @Override
    public double calculateArea() {
        return Math.PI * radius * (radius + Math.sqrt(height * height + radius * radius));
    }

    @Override
    public void draw(GraphicsContext gc) {
        double centerX = x + radius;
        double centerY = y + height / 2;

        // 旋转弧度
        double ry = Math.toRadians(getRotationY());
        double rx = Math.toRadians(getRotationX());
        double cosY = Math.cos(ry), sinY = Math.sin(ry);
        double cosX = Math.cos(rx), sinX = Math.sin(rx);

        // 生成基底圆周点
        double[][] base3D = new double[SEGMENTS][3];
        for (int i = 0; i < SEGMENTS; i++) {
            double theta = 2 * Math.PI * i / SEGMENTS;
            base3D[i][0] = radius * Math.cos(theta);
            base3D[i][1] = height / 2;
            base3D[i][2] = radius * Math.sin(theta);
        }
        // 旋转顶点
        double[][] rBase = new double[SEGMENTS][3];
        for (int i = 0; i < SEGMENTS; i++) {
            rBase[i] = rotatePoint(base3D[i], cosY, sinY, cosX, sinX);
        }
        // 旋转后的顶点
        double[] apex3D = rotatePoint(new double[]{0, -height / 2, 0}, cosY, sinY, cosX, sinX);

        // 投影到 2D
        double[][] pBase = new double[SEGMENTS][2];
        for (int i = 0; i < SEGMENTS; i++) {
            pBase[i][0] = rBase[i][0] + centerX;
            pBase[i][1] = rBase[i][1] + centerY;
        }
        double apexX = apex3D[0] + centerX;
        double apexY = apex3D[1] + centerY;

        // 填充所有侧面为绿色，保持高光
        gc.setLineWidth(1);
        for (int i = 0; i < SEGMENTS; i++) {
            int next = (i + 1) % SEGMENTS;
            double[] normal = computeNormal(apex3D, rBase[i], rBase[next]);
            double bright = Math.max(0, normal[2] * -1);
            Color fillCol = color.deriveColor(0, 1, 0.5 + 0.5 * bright, 1);
            gc.setFill(fillCol);
            double[] xs = {apexX, pBase[i][0], pBase[next][0]};
            double[] ys = {apexY, pBase[i][1], pBase[next][1]};
            gc.fillPolygon(xs, ys, 3);
        }

        // 绘制基底
        gc.setFill(color);
        gc.beginPath();
        gc.moveTo(pBase[0][0], pBase[0][1]);
        for (int i = 1; i < SEGMENTS; i++) gc.lineTo(pBase[i][0], pBase[i][1]);
        gc.closePath();
        gc.fill();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.beginPath();
        gc.moveTo(pBase[0][0], pBase[0][1]);
        for (int i = 1; i < SEGMENTS; i++) gc.lineTo(pBase[i][0], pBase[i][1]);
        gc.closePath();
        gc.stroke();

        // 仅绘制两条侧棱（黑色），其余不描边
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.5);
        // 左侧棱 (i=0)
        gc.strokeLine(apexX, apexY, pBase[0][0], pBase[0][1]);
        // 右侧棱 (i=SEGMENTS/2)
        int mid = SEGMENTS / 2;
        gc.strokeLine(apexX, apexY, pBase[mid][0], pBase[mid][1]);
    }

    private double[] rotatePoint(double[] p, double cosY, double sinY, double cosX, double sinX) {
        double x1 = p[0] * cosY - p[2] * sinY;
        double z1 = p[0] * sinY + p[2] * cosY;
        double y2 = p[1] * cosX - z1 * sinX;
        double z2 = p[1] * sinX + z1 * cosX;
        return new double[]{x1, y2, z2};
    }

    private double[] computeNormal(double[] p0, double[] p1, double[] p2) {
        double[] v1 = {p1[0] - p0[0], p1[1] - p0[1], p1[2] - p0[2]};
        double[] v2 = {p2[0] - p0[0], p2[1] - p0[1], p2[2] - p0[2]};
        double[] n = {
                v1[1] * v2[2] - v1[2] * v2[1],
                v1[2] * v2[0] - v1[0] * v2[2],
                v1[0] * v2[1] - v1[1] * v2[0]
        };
        double len = Math.hypot(Math.hypot(n[0], n[1]), n[2]);
        return new double[]{n[0] / len, n[1] / len, n[2] / len};
    }
}
