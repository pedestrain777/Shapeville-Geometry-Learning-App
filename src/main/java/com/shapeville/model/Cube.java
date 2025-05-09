package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Cube extends Shape {
    private double size;

    public Cube(double size) {
        super("Cube", Color.LIGHTBLUE);
        this.size = size;

        // 默认初始旋转，让立方体一开始就有立体感
        setRotationX(30);
        setRotationY(30);
    }

    @Override
    public double calculateArea() {
        return 6 * size * size;
    }

    @Override
    public void draw(GraphicsContext gc) {
        double centerX = x + size / 2;
        double centerY = y + size / 2;
        double half = size / 2;

        // 旋转角度
        double angleY = Math.toRadians(getRotationY());
        double angleX = Math.toRadians(getRotationX());
        double sinY = Math.sin(angleY), cosY = Math.cos(angleY);
        double sinX = Math.sin(angleX), cosX = Math.cos(angleX);

        // 8个顶点
        double[][] pts = {
                {-half, -half, -half},
                {half, -half, -half},
                {half, half, -half},
                {-half, half, -half},
                {-half, -half, half},
                {half, -half, half},
                {half, half, half},
                {-half, half, half}
        };

        // 面定义
        int[][] faces = {
                {0, 1, 2, 3}, // back
                {4, 5, 6, 7}, // front
                {0, 1, 5, 4}, // bottom
                {2, 3, 7, 6}, // top
                {1, 2, 6, 5}, // right
                {0, 3, 7, 4}  // left
        };

        double viewerDistance = 500;

        // 投影点
        double[][] proj = new double[8][2];

        // 旋转 + 透视投影
        for (int i = 0; i < 8; i++) {
            double X = pts[i][0], Y = pts[i][1], Z = pts[i][2];
            // Y轴旋转
            double x1 = X * cosY - Z * sinY;
            double z1 = X * sinY + Z * cosY;
            // X轴旋转
            double y2 = Y * cosX - z1 * sinX;
            double z2 = Y * sinX + z1 * cosX;

            double f = viewerDistance / (viewerDistance + z2);
            proj[i][0] = x1 * f + centerX;
            proj[i][1] = y2 * f + centerY;

            // 存回新Z，方便后面深度排序
            pts[i][0] = x1;
            pts[i][1] = y2;
            pts[i][2] = z2;
        }

        // 每个面的平均Z值
        double[] depths = new double[6];
        for (int i = 0; i < 6; i++) {
            double sum = 0;
            for (int j = 0; j < 4; j++) {
                int idx = faces[i][j];
                sum += pts[idx][2];
            }
            depths[i] = sum / 4;
        }

        Integer[] order = {0, 1, 2, 3, 4, 5};
        java.util.Arrays.sort(order, (a, b) -> Double.compare(depths[b], depths[a])); // 从远到近

        // 光源方向：来自右上方、稍微往外一点
        double[] light = {0.5, -0.5, -1};

        for (int fIdx : order) {
            int[] face = faces[fIdx];
            double[] p0 = pts[face[0]];
            double[] p1 = pts[face[1]];
            double[] p2 = pts[face[2]];

            // 计算法线
            double[] v1 = {p1[0] - p0[0], p1[1] - p0[1], p1[2] - p0[2]};
            double[] v2 = {p2[0] - p0[0], p2[1] - p0[1], p2[2] - p0[2]};
            double[] normal = {
                    v1[1] * v2[2] - v1[2] * v2[1],
                    v1[2] * v2[0] - v1[0] * v2[2],
                    v1[0] * v2[1] - v1[1] * v2[0]
            };
            double len = Math.sqrt(normal[0] * normal[0] + normal[1] * normal[1] + normal[2] * normal[2]);
            for (int k = 0; k < 3; k++) normal[k] /= len;

            double brightness = Math.max(0.1, normal[0] * light[0] + normal[1] * light[1] + normal[2] * light[2]);
            Color faceColor = color.deriveColor(0, 1, 0.6 + 0.4 * brightness, 1);

            double[] xs = new double[4], ys = new double[4];
            for (int k = 0; k < 4; k++) {
                xs[k] = proj[face[k]][0];
                ys[k] = proj[face[k]][1];
            }

            gc.setFill(faceColor);
            gc.fillPolygon(xs, ys, 4);
            gc.setStroke(Color.BLACK);
            gc.strokePolygon(xs, ys, 4);
        }
    }
    @Override
    public Cube copy() {
        Cube newCube = new Cube(this.size);
        newCube.setColor(this.getColor());
        newCube.setPosition(this.x, this.y);
        newCube.setRotationX(this.rotationX);
        newCube.setRotationY(this.rotationY);
        return newCube;
    }
}
