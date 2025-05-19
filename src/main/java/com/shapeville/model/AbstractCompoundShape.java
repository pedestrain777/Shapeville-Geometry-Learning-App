package com.shapeville.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * 复合形状抽象基类，实现了CompoundShape接口的基本功能
 */
public abstract class AbstractCompoundShape implements CompoundShape {
    protected List<Shape2D> components = new ArrayList<>();
    protected final int index;
    protected final String name;
    protected final double scaleFactor = 10.0;

    /**
     * 构造函数
     * 
     * @param index 形状索引
     * @param name  形状名称
     */
    public AbstractCompoundShape(int index, String name) {
        this.index = index;
        this.name = name;
        createComponents();
    }

    /**
     * 创建组成复合形状的基本形状组件
     */
    protected abstract void createComponents();

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getIndex() {
        return index;
    }

    /**
     * 重置组件列表
     */
    protected void clearComponents() {
        components.clear();
    }

    /**
     * 获取当前组件列表
     */
    public List<Shape2D> getComponents() {
        return components;
    }

    /**
     * 设置形状的位置
     * 
     * @param centerX 画布中心X坐标
     * @param centerY 画布中心Y坐标
     */
    protected abstract void positionComponents(double centerX, double centerY);

    @Override
    public void draw(GraphicsContext gc, double centerX, double centerY) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        // 先定位组件
        positionComponents(centerX, centerY);

        // 统一设置填充颜色
        gc.setFill(Color.LIGHTBLUE);

        // 子类特定的绘制方法
        drawShape(gc);

        // 添加尺寸标注
        addDimensionLabels(gc);
    }

    /**
     * 特定形状的绘制方法
     * 
     * @param gc GraphicsContext对象
     */
    protected abstract void drawShape(GraphicsContext gc);
}