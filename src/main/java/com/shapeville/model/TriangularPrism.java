package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TriangularPrism extends Shape {
    private double base, height, length;

    public TriangularPrism(double base, double height, double length) {
        super("Triangular Prism", Color.web("#F0E442"));  // 色盲友好淡黄
        this.base = base;
        this.height = height;
        this.length = length;

        // 默认旋转角度
        setRotationX(30);
        setRotationY(30);
    }

    @Override
    public double calculateArea() {
        // 表面积：2个三角形 + 3个矩形
        return base * height + 2 * length * base + 2 * length * height;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setLineWidth(1);

        double angleX = Math.toRadians(getRotationX());
        double angleY = Math.toRadians(getRotationY());

        // 1. 定义三棱柱的 6 个顶点（3 个前面 + 3 个后面）
        //    我们让“前面”在 z=+length/2，“后面”在 z=-length/2
        double halfB = base / 2.0;
        double halfL = length / 2.0;
        double[][] pts3d = {
                // 前面三角形 (z = +halfL)
                {-halfB, 0,  halfL},    // 0
                { halfB, 0,  halfL},    // 1
                {    0, height, halfL}, // 2
                // 后面三角形 (z = -halfL)
                {-halfB, 0, -halfL},    // 3
                { halfB, 0, -halfL},    // 4
                {    0, height, -halfL} // 5
        };

        // 2. 旋转 + 投影 → 得到屏幕坐标
        double[][] pts2d = new double[6][2];
        double[] zs      = new double[6];  // 保存最终的 z 用于光照
        for (int i = 0; i < pts3d.length; i++) {
            double x3 = pts3d[i][0];
            double y3 = pts3d[i][1];
            double z3 = pts3d[i][2];

            // 绕 X 轴旋转
            double y2 = y3 * Math.cos(angleX) - z3 * Math.sin(angleX);
            double z2 = y3 * Math.sin(angleX) + z3 * Math.cos(angleX);

            // 绕 Y 轴旋转
            double x2 = x3 * Math.cos(angleY) + z2 * Math.sin(angleY);
            double zF = -x3 * Math.sin(angleY) + z2 * Math.cos(angleY);

            // 简单正交投影
            pts2d[i][0] = x + length + x2;
            pts2d[i][1] = y + length + y2;
            zs[i]        = zF;
        }

        // 3. 定义 5 个面：2 个三角面 + 3 个矩形面
        int[][] faces = {
                {0,1,2},  // 前三角面
                {3,5,4},  // 后三角面 (注意顶点顺序保持逆时针/顺时针以正确计算法线方向)
                {0,3,4,1},// 底面矩形（前后下边连线）
                {1,4,5,2},// 右侧矩形
                {2,5,3,0} // 左侧矩形
        };

        // 4. 逐面计算光照并绘制
        for (int[] face : faces) {
            // 4.1 计算法线向量（取前三个点）
            double[] p1 = new double[]{ pts3d[face[0]][0], pts3d[face[0]][1], zs[face[0]] };
            double[] p2 = new double[]{ pts3d[face[1]][0], pts3d[face[1]][1], zs[face[1]] };
            double[] p3 = new double[]{ pts3d[face[2]][0], pts3d[face[2]][1], zs[face[2]] };

            double[] u = { p2[0]-p1[0], p2[1]-p1[1], p2[2]-p1[2] };
            double[] v = { p3[0]-p1[0], p3[1]-p1[1], p3[2]-p1[2] };
            // 叉积
            double nx = u[1]*v[2] - u[2]*v[1];
            double ny = u[2]*v[0] - u[0]*v[2];
            double nz = u[0]*v[1] - u[1]*v[0];

            // 4.2 光源方向 (这里取前方)
            double lx = 0.5, ly = -0.5, lz = -1;
            double dot = nx*lx + ny*ly + nz*lz;
            double norm = Math.sqrt(nx*nx + ny*ny + nz*nz);
            double lint = dot / (norm * Math.sqrt(lx*lx+ly*ly+lz*lz));
            // 保证最暗也不是全黑
            double bright = Math.max(0.3, lint);

            // 4.3 计算颜色
            Color faceColor = color.deriveColor(0, 1, 0.5 + 0.5*bright, 1);

            // 4.4 准备二维点数组
            double[] xs = new double[face.length];
            double[] ys = new double[face.length];
            for (int i = 0; i < face.length; i++) {
                xs[i] = pts2d[face[i]][0];
                ys[i] = pts2d[face[i]][1];
            }

            // 4.5 填充并描边
            gc.setFill(faceColor);
            gc.fillPolygon(xs, ys, face.length);
            gc.setStroke(Color.BLACK);
            gc.strokePolygon(xs, ys, face.length);
        }
    }

}
