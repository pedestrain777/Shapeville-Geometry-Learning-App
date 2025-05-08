package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SquarePyramid extends Shape {
    private double base, height;

    public SquarePyramid(double base, double height) {
        super("Square-based Pyramid", Color.web("#F0E442"));  // 色盲友好黄色
        this.base = base;
        this.height = height;

        // 默认初始旋转，避免正方形死板感
        setRotationX(0);
        setRotationY(10);
    }

    @Override
    public double calculateArea() {
        // 近似表面积
        return base * base + 2 * base * Math.sqrt((base / 2) * (base / 2) + height * height);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setLineWidth(2);

        double angleX = Math.toRadians(getRotationX());
        double angleY = Math.toRadians(getRotationY());

        double[][] points3D = {
                {-base / 2, 0, -base / 2}, // 0: 左后
                {base / 2, 0, -base / 2},  // 1: 右后
                {base / 2, 0, base / 2},   // 2: 右前
                {-base / 2, 0, base / 2},  // 3: 左前
                {0, -height, 0}           // 4: 顶点
        };

        double[][] points2D = new double[5][2];

        for (int i = 0; i < points3D.length; i++) {
            double x3 = points3D[i][0];
            double y3 = points3D[i][1];
            double z3 = points3D[i][2];

            // 旋转X轴
            double y2 = y3 * Math.cos(angleX) - z3 * Math.sin(angleX);
            double z2 = y3 * Math.sin(angleX) + z3 * Math.cos(angleX);

            // 旋转Y轴
            double x2 = x3 * Math.cos(angleY) + z2 * Math.sin(angleY);
            double zFinal = -x3 * Math.sin(angleY) + z2 * Math.cos(angleY);

            // 投影
            points2D[i][0] = this.x + base + x2;
            points2D[i][1] = this.y + base + y2;

            // 存回 zFinal 方便光照
            points3D[i][2] = zFinal;
        }

        // 🔥 先画底面 (固定较暗色)
        gc.setFill(color.deriveColor(0, 1, 0.5, 1));
        double[] xBase = {points2D[0][0], points2D[1][0], points2D[2][0], points2D[3][0]};
        double[] yBase = {points2D[0][1], points2D[1][1], points2D[2][1], points2D[3][1]};
        gc.fillPolygon(xBase, yBase, 4);
        gc.setStroke(Color.DARKGRAY);
        gc.strokePolygon(xBase, yBase, 4);

        // 🔥 四个侧面（统一颜色，但根据朝向变亮变暗）
        int[][] faces = {
                {0, 1, 4},
                {1, 2, 4},
                {2, 3, 4},
                {3, 0, 4}
        };

        for (int i = 0; i < 4; i++) {
            int[] face = faces[i];

            // 光照简化：根据面法线Z值调整亮度
            double[] p1 = points3D[face[0]];
            double[] p2 = points3D[face[1]];
            double[] p3 = points3D[face[2]];

            // 计算面法线
            double[] u = {p2[0] - p1[0], p2[1] - p1[1], p2[2] - p1[2]};
            double[] v = {p3[0] - p1[0], p3[1] - p1[1], p3[2] - p1[2]};
            double nx = u[1] * v[2] - u[2] * v[1];
            double ny = u[2] * v[0] - u[0] * v[2];
            double nz = u[0] * v[1] - u[1] * v[0];

            // 最暗 0.6，最亮 1.0
            double bright = Math.max(0.3, -nz / Math.sqrt(nx*nx + ny*ny + nz*nz));
            Color faceColor = color.deriveColor(0, 1, 0.6 + 0.4 * bright, 1);

            double[] xPts = {points2D[face[0]][0], points2D[face[1]][0], points2D[face[2]][0]};
            double[] yPts = {points2D[face[0]][1], points2D[face[1]][1], points2D[face[2]][1]};
            gc.setFill(faceColor);
            gc.fillPolygon(xPts, yPts, 3);
            gc.setStroke(Color.BLACK);
            gc.strokePolygon(xPts, yPts, 3);
        }
    }

}
