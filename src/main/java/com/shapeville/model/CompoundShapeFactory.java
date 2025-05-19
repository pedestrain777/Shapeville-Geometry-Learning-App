package com.shapeville.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 复合形状工厂类，用于创建所有复合形状
 */
public class CompoundShapeFactory {

    /**
     * 创建指定索引的复合形状
     * 
     * @param index 形状索引 (0-8)
     * @return 复合形状
     */
    public static CompoundShape createShape(int index) {
        switch (index) {
            case 0:
                return new CompoundShape1();
            case 1:
                return new CompoundShape2();
            case 2:
                return new CompoundShape3();
            case 3:
                return new CompoundShape4();
            case 4:
                return new CompoundShape5();
            case 5:
                return new CompoundShape6();
            case 6:
                return new CompoundShape7();
            case 7:
                return new CompoundShape8();
            case 8:
                return new CompoundShape9();
            default:
                throw new IllegalArgumentException("Invalid shape index: " + index);
        }
    }

    /**
     * 获取所有复合形状的列表
     * 
     * @return 所有复合形状
     */
    public static List<CompoundShape> getAllShapes() {
        List<CompoundShape> shapes = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            shapes.add(createShape(i));
        }
        return shapes;
    }

    /**
     * 获取所有复合形状的名称
     * 
     * @return 所有复合形状名称
     */
    public static List<String> getAllShapeNames() {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            names.add("Shape " + (i + 1));
        }
        return names;
    }
}