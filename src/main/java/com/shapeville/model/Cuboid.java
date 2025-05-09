package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Cuboid extends Shape {
    private double width, height, depth;

    public Cuboid(double width, double height, double depth) {
        super("Cuboid", Color.web("#E69F00"));
        this.width = width;
        this.height = height;
        this.depth = depth;

        // 等角投影常用角度：绕 Y 轴 45°，绕 X 轴 35.264°（arcsin(tan30°)）
        setRotationY(45);
        setRotationX(35.264);
    }

    @Override
    public double calculateArea() {
        return 2 * (width * height + width * depth + height * depth);
    }

    @Override
    public void draw(GraphicsContext gc) {
        double centerX = x + width / 2;
        double centerY = y + height / 2;
        double hw = width  / 2;
        double hh = height / 2;
        double hd = depth  / 2;

        // 旋转弧度
        double ry = Math.toRadians(getRotationY());
        double rx = Math.toRadians(getRotationX());
        double sinY = Math.sin(ry), cosY = Math.cos(ry);
        double sinX = Math.sin(rx), cosX = Math.cos(rx);

        double[][] pts = {
                {-hw, -hh, -hd}, { hw, -hh, -hd},
                { hw,  hh, -hd}, {-hw,  hh, -hd},
                {-hw, -hh,  hd}, { hw, -hh,  hd},
                { hw,  hh,  hd}, {-hw,  hh,  hd}
        };
        int[][] faces = {
                {0,1,2,3}, {4,5,6,7},
                {0,1,5,4}, {2,3,7,6},
                {1,2,6,5}, {0,3,7,4}
        };

        // 1. 旋转顶点（不做透视缩放）
        double[][] rot = new double[8][3];
        for (int i = 0; i < 8; i++) {
            double X = pts[i][0], Y = pts[i][1], Z = pts[i][2];
            // 绕 Y 轴
            double x1 = X * cosY - Z * sinY;
            double z1 = X * sinY + Z * cosY;
            // 绕 X 轴
            double y2 = Y * cosX - z1 * sinX;
            double z2 = Y * sinX + z1 * cosX;
            rot[i][0] = x1;
            rot[i][1] = y2;
            rot[i][2] = z2;
        }

        // 2. 正投影到屏幕（直接丢弃 Z ）
        double[][] proj = new double[8][2];
        for (int i = 0; i < 8; i++) {
            proj[i][0] = rot[i][0] + centerX;
            proj[i][1] = rot[i][1] + centerY;
        }

        // 3. 按平均 Z 排序（Painter’s Algorithm）
        Double[] avgZ = new Double[faces.length];
        for (int f = 0; f < faces.length; f++) {
            double sum = 0;
            for (int idx : faces[f]) sum += rot[idx][2];
            avgZ[f] = sum / faces[f].length;
        }
        Integer[] order = {0,1,2,3,4,5};
        java.util.Arrays.sort(order, (a,b)-> Double.compare(avgZ[a], avgZ[b]));

        // 4. 填色 + 面描边
        double[] light = {0.5, -0.5, -1};
        for (int fi : order) {
            int[] fidx = faces[fi];
            // 计算法线
            double[] p0 = rot[fidx[0]], p1 = rot[fidx[1]], p2 = rot[fidx[2]];
            double[] v1 = {p1[0]-p0[0], p1[1]-p0[1], p1[2]-p0[2]};
            double[] v2 = {p2[0]-p0[0], p2[1]-p0[1], p2[2]-p0[2]};
            double[] n = {
                    v1[1]*v2[2] - v1[2]*v2[1],
                    v1[2]*v2[0] - v1[0]*v2[2],
                    v1[0]*v2[1] - v1[1]*v2[0]
            };
            double nl = Math.hypot(Math.hypot(n[0], n[1]), n[2]);
            for (int k = 0; k < 3; k++) n[k] /= nl;
            double bright = Math.max(0, n[0]*light[0] + n[1]*light[1] + n[2]*light[2]);

            double[] xs = new double[4], ys = new double[4];
            for (int k = 0; k < 4; k++) {
                xs[k] = proj[fidx[k]][0];
                ys[k] = proj[fidx[k]][1];
            }

            gc.setFill(color.deriveColor(0,1,0.5+0.5*bright,1));
            gc.fillPolygon(xs, ys, 4);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.strokePolygon(xs, ys, 4);
        }

        // 5. 最后画所有棱
        int[][] edges = {
                {0,1},{1,2},{2,3},{3,0},
                {4,5},{5,6},{6,7},{7,4},
                {0,4},{1,5},{2,6},{3,7}
        };
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.5);
        for (int[] e : edges) {
            gc.strokeLine(proj[e[0]][0], proj[e[0]][1],
                    proj[e[1]][0], proj[e[1]][1]);
        }
    }
    @Override
    public Cuboid copy() {
        Cuboid newCuboid = new Cuboid(this.width, this.height, this.depth);
        newCuboid.setColor(this.getColor());
        newCuboid.setPosition(this.x, this.y);
        newCuboid.setRotationX(this.rotationX);
        newCuboid.setRotationY(this.rotationY);
        return newCuboid;
    }
}
