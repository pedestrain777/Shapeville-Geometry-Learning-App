package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Cylinder extends Shape {
    private double radius, height;
    private static final int SEGMENTS = 32; // 分段数，越大圆越圆滑

    public Cylinder(double radius, double height) {
        super("Cylinder", Color.web("#CC79A7"));  // 色盲友好紫色
        this.radius = radius;
        this.height = height;

        // 默认初始旋转，让形状一开始就有立体感
        setRotationX(15.264);  // 等角视角
        setRotationY(0);
    }

    @Override
    public double calculateArea() {
        return 2 * Math.PI * radius * (radius + height);
    }

    @Override
    public void draw(GraphicsContext gc) {
        double centerX = x + radius;
        double centerY = y + height / 2;

        // 旋转角度
        double ry = Math.toRadians(getRotationY());
        double rx = Math.toRadians(getRotationX());
        double sinY = Math.sin(ry), cosY = Math.cos(ry);
        double sinX = Math.sin(rx), cosX = Math.cos(rx);

        // 生成上下圆的 3D 点
        double[][] top = new double[SEGMENTS][3];
        double[][] bot = new double[SEGMENTS][3];
        for (int i = 0; i < SEGMENTS; i++) {
            double theta = 2 * Math.PI * i / SEGMENTS;
            double cx = radius * Math.cos(theta);
            double cz = radius * Math.sin(theta);
            top[i][0] = cx;
            top[i][1] = -height / 2;
            top[i][2] = cz;
            bot[i][0] = cx;
            bot[i][1] = height / 2;
            bot[i][2] = cz;
        }

        // 旋转后的点
        double[][] rTop = new double[SEGMENTS][3];
        double[][] rBot = new double[SEGMENTS][3];
        for (int i = 0; i < SEGMENTS; i++) {
            rTop[i] = rotatePoint(top[i], cosY, sinY, cosX, sinX);
            rBot[i] = rotatePoint(bot[i], cosY, sinY, cosX, sinX);
        }

        // 正投影到屏幕
        double[][] pTop = new double[SEGMENTS][2];
        double[][] pBot = new double[SEGMENTS][2];
        for (int i = 0; i < SEGMENTS; i++) {
            pTop[i][0] = rTop[i][0] + centerX;
            pTop[i][1] = rTop[i][1] + centerY;
            pBot[i][0] = rBot[i][0] + centerX;
            pBot[i][1] = rBot[i][1] + centerY;
        }

        // 绘制侧面（只填色，不描绘所有边线）
        int[][] faceOrder = new int[SEGMENTS][2];
        double[] avgZ = new double[SEGMENTS];
        for (int i = 0; i < SEGMENTS; i++) {
            int next = (i + 1) % SEGMENTS;
            avgZ[i] = (rTop[i][2] + rTop[next][2] + rBot[i][2] + rBot[next][2]) / 4;
            faceOrder[i] = new int[]{i, next};
        }
        java.util.Arrays.sort(faceOrder, (a, b) -> Double.compare(avgZ[a[0]], avgZ[b[0]]));

        for (int[] edge : faceOrder) {
            int i0 = edge[0], i1 = edge[1];
            double[] n = computeNormal(rTop[i0], rTop[i1], rBot[i0]);
            double bright = Math.max(0, n[2] * -1);
            gc.setFill(color.deriveColor(0, 1, 0.5 + 0.5 * bright, 1));
            double[] xs = { pTop[i0][0], pTop[i1][0], pBot[i1][0], pBot[i0][0] };
            double[] ys = { pTop[i0][1], pTop[i1][1], pBot[i1][1], pBot[i0][1] };
            gc.fillPolygon(xs, ys, 4);
        }

        // 顶面（填充 + 描边）
        gc.setFill(color);
        gc.beginPath();
        gc.moveTo(pTop[0][0], pTop[0][1]);
        for (int i = 1; i < SEGMENTS; i++) gc.lineTo(pTop[i][0], pTop[i][1]);
        gc.closePath();
        gc.fill();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.beginPath();
        gc.moveTo(pTop[0][0], pTop[0][1]);
        for (int i = 1; i < SEGMENTS; i++) gc.lineTo(pTop[i][0], pTop[i][1]);
        gc.closePath();
        gc.stroke();

        // 底面（简单背面剔除 + 描边）
        double topZsum = 0, botZsum = 0;
        for (int i = 0; i < SEGMENTS; i++) { topZsum += rTop[i][2]; botZsum += rBot[i][2]; }
        if (botZsum < topZsum) {
            gc.beginPath();
            gc.moveTo(pBot[0][0], pBot[0][1]);
            for (int i = 1; i < SEGMENTS; i++) gc.lineTo(pBot[i][0], pBot[i][1]);
            gc.closePath();
            gc.fill();
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.beginPath();
            gc.moveTo(pBot[0][0], pBot[0][1]);
            for (int i = 1; i < SEGMENTS; i++) gc.lineTo(pBot[i][0], pBot[i][1]);
            gc.closePath();
            gc.stroke();
        }

        // 仅绘制两条侧棱：最左和最右的直线
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.5);
        // 左侧: i=0
        gc.strokeLine(pTop[0][0], pTop[0][1], pBot[0][0], pBot[0][1]);
        // 右侧: i=SEGMENTS/2
        int mid = SEGMENTS / 2;
        gc.strokeLine(pTop[mid][0], pTop[mid][1], pBot[mid][0], pBot[mid][1]);
    }

    private double[] rotatePoint(double[] p, double cosY, double sinY, double cosX, double sinX) {
        double x1 = p[0] * cosY - p[2] * sinY;
        double z1 = p[0] * sinY + p[2] * cosY;
        double y2 = p[1] * cosX - z1 * sinX;
        double z2 = p[1] * sinX + z1 * cosX;
        return new double[]{x1, y2, z2};
    }

    private double[] computeNormal(double[] p0, double[] p1, double[] p2) {
        double[] v1 = { p1[0]-p0[0], p1[1]-p0[1], p1[2]-p0[2] };
        double[] v2 = { p2[0]-p0[0], p2[1]-p0[1], p2[2]-p0[2] };
        double[] n = {
                v1[1]*v2[2] - v1[2]*v2[1],
                v1[2]*v2[0] - v1[0]*v2[2],
                v1[0]*v2[1] - v1[1]*v2[0]
        };
        double len = Math.hypot(Math.hypot(n[0], n[1]), n[2]);
        return new double[]{ n[0]/len, n[1]/len, n[2]/len };
    }
    @Override
    public Cylinder copy() {
        Cylinder newCylinder = new Cylinder(this.radius, this.height);
        newCylinder.setColor(this.getColor());
        newCylinder.setPosition(this.x, this.y);
        newCylinder.setRotationX(this.rotationX);
        newCylinder.setRotationY(this.rotationY);
        return newCylinder;
    }
}
