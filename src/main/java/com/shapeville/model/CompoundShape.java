package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;

/**
 * 复合形状接口，定义了所有复合形状需要实现的方法
 */
public interface CompoundShape {
    /**
     * 获取复合形状的名称
     * 
     * @return 名称
     */
    String getName();

    /**
     * 计算复合形状的面积
     * 
     * @return 面积
     */
    double calculateArea();

    /**
     * 在画布上绘制复合形状
     * 
     * @param gc      GraphicsContext对象
     * @param centerX 中心X坐标
     * @param centerY 中心Y坐标
     */
    void draw(GraphicsContext gc, double centerX, double centerY);

    /**
     * 在画布上添加尺寸标注
     * 
     * @param gc GraphicsContext对象
     */
    void addDimensionLabels(GraphicsContext gc);

    /**
     * 获取形状索引
     * 
     * @return 形状索引
     */
    int getIndex();
}