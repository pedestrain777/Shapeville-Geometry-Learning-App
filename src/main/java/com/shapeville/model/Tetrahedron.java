package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Tetrahedron extends Shape {
    private double size;

    public Tetrahedron(double size) {
        super("Tetrahedron", Color.web("#56B4E9"));  // 色盲友好蓝色
        this.size = size;

        // 默认旋转
        setRotationX(120);
        setRotationY(120);
    }

    @Override
    public double calculateArea() {
        return Math.sqrt(3) * size * size;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setLineWidth(2);

        double angleX = Math.toRadians(getRotationX());
        double angleY = Math.toRadians(getRotationY());

        // 3D坐标：一个正四面体的4个顶点
        double[][] points3D = {
                {0, 0, 0},                         // 点0
                {size, 0, 0},                      // 点1
                {size / 2, 0, Math.sqrt(3) / 2 * size},  // 点2
                {size / 2, Math.sqrt(6) / 3 * size, Math.sqrt(3) / 6 * size} // 顶点3
        };

        double[][] points2D = new double[4][2];

        for (int i = 0; i < points3D.length; i++) {
            double x3 = points3D[i][0] - size / 2;  // 居中
            double y3 = points3D[i][1] - size / 3;  // 居中
            double z3 = points3D[i][2] - size / 3;  // 居中

            // 旋转X轴
            double y2 = y3 * Math.cos(angleX) - z3 * Math.sin(angleX);
            double z2 = y3 * Math.sin(angleX) + z3 * Math.cos(angleX);

            // 旋转Y轴
            double x2 = x3 * Math.cos(angleY) + z2 * Math.sin(angleY);
            double zFinal = -x3 * Math.sin(angleY) + z2 * Math.cos(angleY);

            // 投影
            points2D[i][0] = this.x + size + x2;
            points2D[i][1] = this.y + size + y2;

            // 存回 zFinal 方便光照
            points3D[i][2] = zFinal;
        }

        // 🔥 先画3个侧面（每个面统一颜色，但有明暗变化）
        int[][] faces = {
                {0, 1, 3},
                {1, 2, 3},
                {2, 0, 3}
        };

        for (int i = 0; i < faces.length; i++) {
            int[] face = faces[i];

            // 光照计算：法线向量
            double[] p1 = points3D[face[0]];
            double[] p2 = points3D[face[1]];
            double[] p3 = points3D[face[2]];

            double[] u = {p2[0] - p1[0], p2[1] - p1[1], p2[2] - p1[2]};
            double[] v = {p3[0] - p1[0], p3[1] - p1[1], p3[2] - p1[2]};
            double nx = u[1] * v[2] - u[2] * v[1];
            double ny = u[2] * v[0] - u[0] * v[2];
            double nz = u[0] * v[1] - u[1] * v[0];

            // 计算光照强度（法线与视角方向的夹角）
            double bright = Math.max(0.2, -nz / Math.sqrt(nx * nx + ny * ny + nz * nz));
            Color faceColor = color.deriveColor(0, 1, 0.4 + 0.6 * bright, 1);

            // 画面
            double[] xPts = {points2D[face[0]][0], points2D[face[1]][0], points2D[face[2]][0]};
            double[] yPts = {points2D[face[0]][1], points2D[face[1]][1], points2D[face[2]][1]};
            gc.setFill(faceColor);
            gc.fillPolygon(xPts, yPts, 3);
            gc.setStroke(Color.BLACK);
            gc.strokePolygon(xPts, yPts, 3);
        }

        // 🔥 最后画底面 (固定暗色)
        int[] base = {0, 1, 2};
        double[] xBase = {points2D[base[0]][0], points2D[base[1]][0], points2D[base[2]][0]};
        double[] yBase = {points2D[base[0]][1], points2D[base[1]][1], points2D[base[2]][1]};
        gc.setFill(color.deriveColor(0, 1, 0.5, 1));  // 底面颜色
        gc.fillPolygon(xBase, yBase, 3);
        gc.setStroke(Color.DARKGRAY);
        gc.strokePolygon(xBase, yBase, 3);
    }
    @Override
    public Tetrahedron copy() {
        Tetrahedron newTetra = new Tetrahedron(this.size);
        newTetra.setColor(this.getColor());
        newTetra.setPosition(this.x, this.y);
        newTetra.setRotationX(this.rotationX);
        newTetra.setRotationY(this.rotationY);
        return newTetra;
    }
}
