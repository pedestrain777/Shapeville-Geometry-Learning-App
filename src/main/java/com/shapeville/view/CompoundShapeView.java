package com.shapeville.view;

import com.shapeville.util.AudioPlayer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import com.shapeville.controller.GameController;
import com.shapeville.model.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.HashSet;
import java.util.Set;

public class CompoundShapeView extends VBox {
    private GameController gameController;
    private Canvas canvas;
    private GraphicsContext gc;
    private TextField answerField;
    private Label messageLabel;
    private Label timerLabel;
    private Label progressLabel;
    private List<Shape2D> currentShapes;
    private int attempts;
    private Timer timer;
    private int timeRemaining;
    private int currentShapeIndex = -1;
    private Random random;
    private Set<Integer> completedShapes = new HashSet<>();
    private ComboBox<String> shapeSelector;

    public CompoundShapeView(GameController gameController) {
        this.gameController = gameController;
        this.random = new Random();
        this.currentShapes = new ArrayList<>();
        setupUI();
    }

    private void setupUI() {
        setSpacing(20);
        setPadding(new Insets(20));
        setAlignment(Pos.CENTER);

        // 标题
        Label titleLabel = new Label("Calculate the total area of the compound shape:");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black;");

        // Shape selection
        HBox selectorBox = new HBox(10);
        selectorBox.setAlignment(Pos.CENTER);
        shapeSelector = new ComboBox<>();
        for (int i = 1; i <= 9; i++) {
            shapeSelector.getItems().add("Shape " + i);
        }
        shapeSelector.setPromptText("Select a compound shape");
        shapeSelector.setPrefWidth(200);
        shapeSelector.setOnAction(e -> {
            if (shapeSelector.getValue() != null) {
                currentShapeIndex = shapeSelector.getSelectionModel().getSelectedIndex();
                generateCompoundShape(currentShapeIndex);
                // 在选择新图形时清除消息
                messageLabel.setText("");
            }
        });
        selectorBox.getChildren().addAll(new Label("Select shape:"), shapeSelector);

        // 恢复Canvas
        canvas = new Canvas(500, 400);
        gc = canvas.getGraphicsContext2D();
        // 初始提示
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(16));
        gc.fillText("Please select a composite shape", 120, 200);

        // Timer label
        timerLabel = new Label("Time remaining: 5:00");
        timerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");

        // Input area
        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER);
        answerField = new TextField();
        answerField.setPromptText("Enter total area");
        answerField.setPrefWidth(100);
        // 根据currentShapeIndex判断是否禁用输入框
        answerField.setDisable(currentShapeIndex == -1);
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> checkAnswer());
        inputBox.getChildren().addAll(new Label("Total Area:"), answerField, submitButton);

        // Message label
        messageLabel = new Label("");
        messageLabel.setStyle("-fx-font-size: 14px;");
        
        // 进度标签
        progressLabel = new Label("Progress: 0/9 shapes completed");
        progressLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");

        getChildren().addAll(
                titleLabel,
                selectorBox,
                canvas, // 恢复显示Canvas
                timerLabel,
                inputBox,
                messageLabel,
                progressLabel);
                
        // 更新进度显示
        shapeSelector.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateProgressLabel();
        });
        
        // 监听currentShapeIndex，控制输入框的禁用状态
        shapeSelector.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            currentShapeIndex = newVal.intValue();
            answerField.setDisable(currentShapeIndex == -1);
        });
    }
    
    private void updateProgressLabel() {
        progressLabel.setText(String.format("Progress: %d/9 shapes completed", completedShapes.size()));
        
        // 当所有形状都完成后，提示用户
        if (completedShapes.size() >= 9) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Task Completed");
            alert.setHeaderText("Great Job!");
            alert.setContentText("You have completed all compound shapes!");
            alert.showAndWait();
            
            // 返回主菜单
            MainView mainView = new MainView();
            mainView.getGameController().setCurrentScore(gameController.getCurrentScore());
            getScene().setRoot(mainView);
        }
    }

    private void generateCompoundShape(int index) {
        currentShapes.clear();
        // 根据图片中的图形结构来创建基本形状
        double scaleFactor = 10.0; // 放大系数
        if (index == 0) {
            // 图1：矩形+三角形
            Rectangle rect = new Rectangle(14 * scaleFactor, 14 * scaleFactor); // 图片中的矩形尺寸
            // 根据图片，三角形的垂直底边与矩形右侧等高 (14*scaleFactor)，水平高（为了视觉还原5cm斜边）
            // 为了让斜边大约是 5*scaleFactor，根据勾股定理估算水平高：sqrt((5*s)^2 - (14*s/2)^2) = s * sqrt(25 - 49)，还是虚数
            // 按照视觉比例，估算三角形的水平高大约是垂直高的一半或者更少。暂定水平高为 5 * scaleFactor
            Triangle tri = new Triangle(14 * scaleFactor, 5 * scaleFactor); // 底边(垂直), 高(水平)
                currentShapes.add(rect);
            currentShapes.add(tri);
        } else if (index == 1) {
            // 图2：两个矩形拼L形
            // 根据图片，左上角矩形尺寸 11x11，下方矩形尺寸 20x10
            Rectangle rect1 = new Rectangle(11 * scaleFactor, 11 * scaleFactor); // 左上角矩形 (宽x高)
            Rectangle rect2 = new Rectangle(20 * scaleFactor, 10 * scaleFactor); // 下方矩形 (宽x高)
                currentShapes.add(rect1);
                currentShapes.add(rect2);
        } else if (index == 2) {
            // 图3：凹形（矩形+小矩形） - 创建基本形状
            // 根据图片，左侧矩形尺寸 18x19，右侧上方矩形尺寸 16x16
            Rectangle rect1 = new Rectangle(18 * scaleFactor, 19 * scaleFactor); // 左侧矩形 (宽x高)
            Rectangle rect2 = new Rectangle(16 * scaleFactor, 16 * scaleFactor); // 右侧上方矩形 (宽x高)
                currentShapes.add(rect1);
                currentShapes.add(rect2);
        } else if (index == 3) {
            // 图4：由两个矩形组成，上方正方形12x12，下方矩形24x6
            Rectangle rectTop = new Rectangle(12 * scaleFactor, 12 * scaleFactor); // 上方正方形
            Rectangle rectBottom = new Rectangle(24 * scaleFactor, 6 * scaleFactor); // 下方长方形
            currentShapes.add(rectTop);
            currentShapes.add(rectBottom);
        } else if (index == 4) {
            // 图5：直角梯形 - 创建基本形状（用点集描述）
            // 下底4cm，左边6cm，右边2cm，上底斜边5cm
            // 用自定义Shape2D实现
            currentShapes.clear();
            currentShapes.add(new Shape2D("rightTrapezium", javafx.scene.paint.Color.LIGHTBLUE, 8 * 10.0, 12 * 10.0) {
                @Override
                public double calculateArea() {
                    return 0.5 * (4 + 5) * 6; // 梯形面积公式
                }
                @Override
                public void draw(javafx.scene.canvas.GraphicsContext gc) {
                    double x = this.x;
                    double y = this.y;
                    double scale = 10.0;
                    double[] xPoints = {x, x, x + 8*scale, x + 8*scale};
                    double[] yPoints = {y, y - 12*scale, y - 4*scale, y};
                    gc.setFill(this.getColor());
                    gc.fillPolygon(xPoints, yPoints, 4);
                    gc.setStroke(javafx.scene.paint.Color.BLACK);
                    gc.setLineWidth(2.0);
                    gc.strokePolygon(xPoints, yPoints, 4);
                }
                @Override
                public Shape2D copy() {
                    Shape2D that = this;
                    return new Shape2D("rightTrapezium", that.getColor(), 8 * 10.0, 12 * 10.0) {
                        @Override
                        public double calculateArea() { return that.calculateArea(); }
                        @Override
                        public void draw(javafx.scene.canvas.GraphicsContext gc) { that.draw(gc); }
                        @Override
                        public Shape2D copy() { return this; }
                    };
                }
            });
        } else if (index == 5) {
            // 图6：直角梯形（矩形+直角三角形）
            Rectangle rect = new Rectangle(9 * scaleFactor, 11 * scaleFactor); // 左侧矩形
            Triangle tri = new Triangle(11 * scaleFactor, 11 * scaleFactor);   // 右侧直角三角形
            currentShapes.add(rect);
            currentShapes.add(tri);
        } else if (index == 6) {
            // 图7：复合形状（五边形，矩形+三角形） - 创建基本形状
            // TODO: 添加图7的基本形状创建代码
        } else if (index == 7) {
            // 图8：复杂矩形组合（T形） - 创建基本形状
            // TODO: 添加图8的基本形状创建代码
        } else if (index == 8) {
            // 图9：楼梯形状（两个矩形） - 创建基本形状
            // 根据图片，左侧矩形长11，宽10，右侧矩形长宽都为8
            Rectangle rectLeft = new Rectangle(10 * scaleFactor, 11 * scaleFactor); // 左侧矩形 (宽x高)
            Rectangle rectRight = new Rectangle(8 * scaleFactor, 8 * scaleFactor); // 右侧矩形 (宽x高)
            currentShapes.add(rectLeft);
            currentShapes.add(rectRight);
        }
        else {
            // 对于其他图形，可以暂时添加一个默认形状或者留空
            currentShapes.add(new Rectangle(50, 50));
        }

        drawCompoundShape();
        startTimer();
    }

    private void drawCompoundShape() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (currentShapes.isEmpty()) {
            return;
        }

        positionShapes();
        
        if (currentShapeIndex == 0) {
            // 图1的特殊绘制：统一颜色，只绘制外轮廓
            gc.setFill(Color.LIGHTBLUE); // 修改为浅蓝色
            
            Rectangle rect = (Rectangle) currentShapes.get(0);
            Triangle tri = (Triangle) currentShapes.get(1);
            
            double rectX = rect.getX();
            double rectY = rect.getY();
            double rectWidth = rect.getWidth();
            double rectHeight = rect.getHeight();
            
            double triX = tri.getX();
            double triY = tri.getY();
            double triBase = tri.getBase(); // 垂直高度
            double triHeight = tri.getHeight(); // 水平长度
            
            // 填充整个复合图形
            List<Double> xPointsList = new ArrayList<>();
            List<Double> yPointsList = new ArrayList<>();

            // 添加矩形左上、左下、右下顶点
            xPointsList.add(rectX);
            yPointsList.add(rectY);
            xPointsList.add(rectX);
            yPointsList.add(rectY + rectHeight);
            xPointsList.add(rectX + rectWidth);
            yPointsList.add(rectY + rectHeight);
            
            // 添加三角形右下顶点，右侧尖点，右上顶点
            xPointsList.add(triX);
            yPointsList.add(triY + triBase);
            xPointsList.add(triX + triHeight);
            yPointsList.add(triY + triBase/2);
             xPointsList.add(triX);
            yPointsList.add(triY);

             // 排序顶点 (简单排序，可能需要更复杂的几何排序方法)
            double[] sortedX = xPointsList.stream().mapToDouble(Double::doubleValue).toArray();
            double[] sortedY = yPointsList.stream().mapToDouble(Double::doubleValue).toArray();

             // 简单的填充多边形
             gc.fillPolygon(sortedX, sortedY, sortedX.length);

            // 绘制外轮廓
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2.0);
            
            // 绘制矩形左、下、上边
            gc.strokeLine(rectX, rectY, rectX, rectY + rectHeight); // 左边
            gc.strokeLine(rectX, rectY + rectHeight, rectX + rectWidth, rectY + rectHeight); // 下边
            gc.strokeLine(rectX, rectY, rectX + rectWidth, rectY); // 上边

            // 绘制连接三角形的边 (矩形右下到三角形右下，三角形右下到尖点，尖点到三角形右上，三角形右上到矩形右上)
            gc.strokeLine(rectX + rectWidth, rectY + rectHeight, triX, triY + triBase); // 矩形右下到三角形右下
            gc.strokeLine(triX, triY + triBase, triX + triHeight, triY + triBase/2); // 三角形右下到尖点
            gc.strokeLine(triX + triHeight, triY + triBase/2, triX, triY); // 尖点到三角形右上
            gc.strokeLine(triX, triY, rectX + rectWidth, rectY); // 三角形右上到矩形右上

        } else if (currentShapeIndex == 1) {
             // 图2的特殊绘制：统一颜色，只绘制外轮廓
            gc.setFill(Color.LIGHTBLUE); // 统一设置为浅蓝色

            Rectangle rect1 = (Rectangle) currentShapes.get(0); // 左上角矩形
            Rectangle rect2 = (Rectangle) currentShapes.get(1); // 下方矩形

            double rect1X = rect1.getX();
            double rect1Y = rect1.getY();
            double rect1Width = rect1.getWidth();
            double rect1Height = rect1.getHeight();

            double rect2X = rect2.getX();
            double rect2Y = rect2.getY();
            double rect2Width = rect2.getWidth();
            double rect2Height = rect2.getHeight();

            // 填充整个复合图形 (L形)
            double[] xPointsFill = {rect1X, rect1X + rect1Width, rect1X + rect1Width, rect2X + rect2Width, rect2X + rect2Width, rect2X};
            double[] yPointsFill = {rect1Y, rect1Y, rect1Y + rect1Height, rect1Y + rect1Height, rect2Y + rect2Height, rect2Y + rect2Height};
            gc.fillPolygon(xPointsFill, yPointsFill, 6);
            
            // 绘制外轮廓
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2.0);

            // 绘制外围的六条边
            gc.strokeLine(rect1X, rect1Y, rect1X + rect1Width, rect1Y); // 左上横边
            gc.strokeLine(rect1X + rect1Width, rect1Y, rect1X + rect1Width, rect1Y + rect1Height); // 右上竖边
            gc.strokeLine(rect1X + rect1Width, rect1Y + rect1Height, rect2X + rect2Width, rect1Y + rect1Height); // 连接两矩形的横边
            gc.strokeLine(rect2X + rect2Width, rect1Y + rect1Height, rect2X + rect2Width, rect2Y + rect2Height); // 右下竖边
            gc.strokeLine(rect2X + rect2Width, rect2Y + rect2Height, rect2X, rect2Y + rect2Height); // 下横边
            gc.strokeLine(rect2X, rect2Y + rect2Height, rect2X, rect1Y); // 左侧总竖边

        } else if (currentShapeIndex == 2) {
            // 图3的特殊绘制：统一颜色，只绘制外轮廓
            gc.setFill(Color.LIGHTBLUE); // 修改为浅蓝色
            
            Rectangle rect1 = (Rectangle) currentShapes.get(0); // 左侧矩形
            Rectangle rect2 = (Rectangle) currentShapes.get(1); // 右侧上方矩形
            
            double rect1X = rect1.getX();
            double rect1Y = rect1.getY();
            double rect1Width = rect1.getWidth();
            double rect1Height = rect1.getHeight();
            
            double rect2X = rect2.getX();
            double rect2Y = rect2.getY();
            double rect2Width = rect2.getWidth();
            double rect2Height = rect2.getHeight();
            
            // 填充整个复合图形
            double[] xPointsFill = {rect1X, rect1X + rect1Width, rect1X + rect1Width, rect2X + rect2Width, rect2X + rect2Width, rect1X};
            double[] yPointsFill = {rect1Y, rect1Y, rect1Y + (rect1Height - rect2Height), rect1Y + (rect1Height - rect2Height), rect1Y + rect1Height, rect1Y + rect1Height};
            gc.fillPolygon(xPointsFill, yPointsFill, 6);

            // 绘制外轮廓
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2.0);
            
            // 绘制外围六条边
            gc.strokeLine(rect1X, rect1Y, rect1X + rect1Width, rect1Y); // 左上横边
            gc.strokeLine(rect1X + rect1Width, rect1Y, rect1X + rect1Width, rect1Y + (rect1Height - rect2Height)); // 右侧上部竖边
            gc.strokeLine(rect1X + rect1Width, rect1Y + (rect1Height - rect2Height), rect2X + rect2Width, rect1Y + (rect1Height - rect2Height)); // 右侧连接横边
            gc.strokeLine(rect2X + rect2Width, rect1Y + (rect1Height - rect2Height), rect2X + rect2Width, rect1Y + rect1Height); // 右下竖边
            gc.strokeLine(rect2X + rect2Width, rect1Y + rect1Height, rect1X, rect1Y + rect1Height); // 下横边
            gc.strokeLine(rect1X, rect1Y + rect1Height, rect1X, rect1Y); // 左侧竖边
            
        } else if (currentShapeIndex == 3) {
            // 图4的特殊绘制：统一颜色，只绘制外轮廓
            gc.setFill(Color.LIGHTBLUE); // 统一设置为浅蓝色

            Rectangle rectTop = (Rectangle) currentShapes.get(0);
            Rectangle rectBottom = (Rectangle) currentShapes.get(1);

            double x0 = rectBottom.getX();
            double y0 = rectBottom.getY();
            double w = 10.0;
            double h = 10.0;

            // 计算顶点，顺时针
            double[] xPoints = {
                x0,                        // 左下
                x0,                        // 左上
                x0 + 6 * w,                // 上方正方形左上
                x0 + 6 * w,                // 上方正方形右上
                x0 + 18 * w,               // 上方正方形右上
                x0 + 18 * w,               // 上方正方形右下
                x0 + 24 * w,               // 右上
                x0 + 24 * w,               // 右下
                x0                         // 回到左下
            };
            double[] yPoints = {
                y0 + 6 * h,                // 左下
                y0,                        // 左上
                y0,                        // 上方正方形左上
                y0 - 12 * h,               // 上方正方形右上
                y0 - 12 * h,               // 上方正方形右上
                y0,                        // 上方正方形右下
                y0,                        // 右上
                y0 + 6 * h,                // 右下
                y0 + 6 * h                 // 回到左下
            };

            gc.fillPolygon(xPoints, yPoints, 8);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2.0);
            gc.strokePolygon(xPoints, yPoints, 8);

        } else if (currentShapeIndex == 4) {
            // 图5的特殊绘制：直角梯形，填充浅蓝色，仅绘制外轮廓
            Shape2D trap = currentShapes.get(0);
            trap.draw(gc);
        } else if (currentShapeIndex == 5) {
            // 图6的特殊绘制：统一颜色，只绘制外轮廓
            gc.setFill(Color.LIGHTBLUE);

            Rectangle rect = (Rectangle) currentShapes.get(0);
            Triangle tri = (Triangle) currentShapes.get(1);

            double x0 = rect.getX();
            double y0 = rect.getY();
            double x1 = x0 + rect.getWidth();
            double y1 = y0;
            double x2 = x1 + tri.getBase();
            double y2 = y1 + tri.getHeight();
            double x3 = x0;
            double y3 = y0 + rect.getHeight();

            double[] xPoints = {x0, x1, x2, x3};
            double[] yPoints = {y0, y1, y2, y3};

            gc.fillPolygon(xPoints, yPoints, 4);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2.0);
            gc.strokePolygon(xPoints, yPoints, 4);
        } else if (currentShapeIndex == 6) {
             // 图7的特殊绘制：统一颜色，只绘制外轮廓
            // TODO: 添加图7的填充颜色和外轮廓绘制代码
        } else if (currentShapeIndex == 7) {
             // 图8的特殊绘制：统一颜色，只绘制外轮廓
            // TODO: 添加图8的填充颜色和外轮廓绘制代码
        } else if (currentShapeIndex == 8) {
             // 图9的特殊绘制：统一颜色，只绘制外轮廓
            gc.setFill(Color.LIGHTBLUE); // 统一设置为浅蓝色

            Rectangle rectLeft = (Rectangle) currentShapes.get(0); // 左侧矩形
            Rectangle rectRight = (Rectangle) currentShapes.get(1); // 右侧矩形

            double rectLeftX = rectLeft.getX();
            double rectLeftY = rectLeft.getY();
            double rectLeftWidth = rectLeft.getWidth();
            double rectLeftHeight = rectLeft.getHeight();

            double rectRightX = rectRight.getX();
            double rectRightY = rectRight.getY();
            double rectRightWidth = rectRight.getWidth();
            double rectRightHeight = rectRight.getHeight();

            // 填充整个复合图形
            double[] xPointsFill = {
                rectLeftX,                                         // 左上
                rectLeftX + rectLeftWidth,                        // 左上到右上（连接）
                rectLeftX + rectLeftWidth,                        // 转折点，左矩形右上
                rectRightX + rectRightWidth,                      // 右矩形右上
                rectRightX + rectRightWidth,                      // 右矩形右下
                rectLeftX + rectLeftWidth,                        // 左矩形右下
                rectLeftX,                                         // 左矩形左下
            };
            
            double[] yPointsFill = {
                rectLeftY,                                         // 左上
                rectLeftY,                                         // 左矩形上边
                rectRightY,                                        // 连接点（右矩形顶部）
                rectRightY,                                        // 右矩形右上
                rectRightY + rectRightHeight,                     // 右矩形右下
                rectLeftY + rectLeftHeight,                       // 左矩形右下
                rectLeftY + rectLeftHeight                        // 左下
            };
            gc.fillPolygon(xPointsFill, yPointsFill, xPointsFill.length);

            // 绘制外轮廓
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2.0);

            // 绘制外围六条边
            gc.strokeLine(rectLeftX, rectLeftY, rectLeftX + rectLeftWidth, rectLeftY); // 左上横边
            gc.strokeLine(rectLeftX + rectLeftWidth, rectLeftY + rectLeftHeight, rectRightX + rectRightWidth, rectLeftY + rectLeftHeight); // 连接两矩形的横边
            gc.strokeLine(rectRightX + rectRightWidth, rectLeftY + rectLeftHeight, rectRightX + rectRightWidth, rectRightY + rectRightHeight); // 右下竖边
            gc.strokeLine(rectRightX + rectRightWidth, rectRightY + rectRightHeight, rectLeftX, rectRightY + rectRightHeight); // 下横边
            gc.strokeLine(rectLeftX, rectRightY + rectRightHeight, rectLeftX, rectLeftY); // 左侧竖边

        }

        addDimensionLabels();
    }
    
    private void positionShapes() {
        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2;
        
        if (currentShapeIndex == 0) {
            // 图1：矩形+三角形
            Rectangle rect = (Rectangle) currentShapes.get(0);
            Triangle tri = (Triangle) currentShapes.get(1);
            
            // 计算整体的宽度和高度
            double totalWidth = rect.getWidth() + tri.getHeight(); 
            double totalHeight = rect.getHeight();
            
            // 计算整体起始位置，使其居中
            double startX = centerX - totalWidth / 2;
            double startY = centerY - totalHeight / 2;
            
            // 设置矩形位置
            rect.setPosition(startX, startY);
            
            // 设置三角形位置，使其垂直底边与矩形右侧对齐，且垂直居中
            tri.setPosition(startX + rect.getWidth(), startY + rect.getHeight()/2 - tri.getBase()/2);
            
        } else if (currentShapeIndex == 1) {
            // 图2：两个矩形拼L形
            Rectangle rect1 = (Rectangle) currentShapes.get(0); // 左上角矩形
            Rectangle rect2 = (Rectangle) currentShapes.get(1); // 下方矩形
            
            // 根据图片结构，下方矩形宽度 20，高度 10。左上角矩形宽度 11，高度 11。总高度 21。
            // 整体宽度 20，整体高度 21
            double totalWidth = 20 * 10.0;
            double totalHeight = (11 + 10) * 10.0; // 11(左上高)+10(下方高)
            
            // 计算整体起始位置，使其居中
            double startX = centerX - totalWidth / 2;
            double startY = centerY - totalHeight / 2;
            
            // 设置下方矩形位置 (左下角)
            rect2.setPosition(startX, startY + 11 * 10.0); // 在左上角矩形下方
            
            // 设置左上角矩形位置 (左上角)
                rect1.setPosition(startX, startY);
            
        } else if (currentShapeIndex == 2) {
            // 图3：凹形 - 定位基本形状
            Rectangle rect1 = (Rectangle) currentShapes.get(0); // 左侧矩形
            Rectangle rect2 = (Rectangle) currentShapes.get(1); // 右侧上方矩形
            
            // 根据图片结构，左侧矩形宽18高19，右侧矩形宽16高16
            // 整体宽度为 18 + 16 = 34
            // 整体高度为 19
            double totalWidth = (18 + 16) * 10.0;
            double totalHeight = 19 * 10.0;
            
            // 计算整体起始位置，使其居中
            double startX = centerX - totalWidth / 2;
            double startY = centerY - totalHeight / 2;
            
            // 设置左侧矩形位置 (左上角)
                rect1.setPosition(startX, startY);
            
            // 设置右侧上方矩形位置 (右侧上方)
            rect2.setPosition(startX + rect1.getWidth(), startY); // 在左侧矩形右侧，顶部对齐
            
        } else if (currentShapeIndex == 3) {
            // 图4：定位
            double totalWidth = 24 * 10.0;
            double totalHeight = 12 * 10.0 + 6 * 10.0;
            double startX = centerX - totalWidth / 2;
            double startY = centerY - totalHeight / 2;

            // 上方正方形居中
            Rectangle rectTop = (Rectangle) currentShapes.get(0);
            rectTop.setPosition(startX + 6 * 10.0, startY);

            // 下方长方形
            Rectangle rectBottom = (Rectangle) currentShapes.get(1);
            rectBottom.setPosition(startX, startY + 12 * 10.0);
        } else if (currentShapeIndex == 4) {
            // 图5：直角梯形 - 定位基本形状
            Shape2D trap = currentShapes.get(0);
            double width = 8 * 10.0;
            double height = 12 * 10.0;
            double startX = centerX - width / 2;
            double startY = centerY + height / 2;
            trap.setPosition(startX, startY);
        } else if (currentShapeIndex == 5) {
            Rectangle rect = (Rectangle) currentShapes.get(0);
            Triangle tri = (Triangle) currentShapes.get(1);
            double totalWidth = 9 * 10.0 + 11 * 10.0;
            double totalHeight = 11 * 10.0;
            double startX = centerX - totalWidth / 2;
            double startY = centerY - totalHeight / 2;
            rect.setPosition(startX, startY);
            tri.setPosition(startX + rect.getWidth(), startY);
        } else if (currentShapeIndex == 6) {
            // 图7：复合形状（五边形） - 定位基本形状
            // TODO: 添加图7的基本形状定位代码
        } else if (currentShapeIndex == 7) {
            // 图8：复杂矩形组合（T形） - 定位基本形状
            // TODO: 添加图8的基本形状定位代码
        } else if (currentShapeIndex == 8) {
            // 图9：楼梯形状 - 定位基本形状
            // 根据图片结构，左侧矩形宽10高11，右侧矩形宽8高8
            // 整体宽度为 10 + 8 = 18
            // 整体高度为 11
            double totalWidth = (10 + 8) * 10.0;
            double totalHeight = 11 * 10.0;

            // 计算整体起始位置，使其居中
            double startX = centerX - totalWidth / 2;
            double startY = centerY - totalHeight / 2;

            // 设置左侧矩形位置 (左上角)
            Rectangle rectLeft = (Rectangle) currentShapes.get(0);
            rectLeft.setPosition(startX, startY);

            // 设置右侧矩形位置 (右侧上方)
            Rectangle rectRight = (Rectangle) currentShapes.get(1);
            rectRight.setPosition(startX + rectLeft.getWidth(), startY + (rectLeft.getHeight() - rectRight.getHeight())); // 在左侧矩形右侧，底部对齐
        }
        else {
            // 其他图形的定位逻辑，暂时留空或默认处理
                if (!currentShapes.isEmpty()) {
                    Shape2D shape = currentShapes.get(0);
                shape.setPosition(centerX - shape.getWidth() / 2, centerY - shape.getHeight() / 2);
            }
        }
    }
    
    // addDimensionLabels方法现在实现第一个和第二个图形的尺寸标注
    private void addDimensionLabels() {
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(14));
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);
        
        double scaleFactor = 10.0; // 使用相同的放大系数
        
        if (currentShapeIndex == 0) {
            // 图1：矩形+三角形的尺寸标注
            Rectangle rect = (Rectangle) currentShapes.get(0);
            Triangle tri = (Triangle) currentShapes.get(1);
            
            double rectX = rect.getX();
            double rectY = rect.getY();
            double rectWidth = rect.getWidth();
            double rectHeight = rect.getHeight();
            
            double triX = tri.getX();
            double triY = tri.getY();
            double triBase = tri.getBase(); // 垂直高度
            double triHeight = tri.getHeight(); // 水平长度
            
            // 标注矩形高度 (左侧)
            gc.fillText(String.format("%.0f cm", 14.0), rectX - 30, rectY + rectHeight/2);
            gc.strokeLine(rectX - 5, rectY, rectX - 5, rectY + rectHeight);
            gc.strokeLine(rectX - 2, rectY, rectX - 8, rectY);
            gc.strokeLine(rectX - 2, rectY + rectHeight, rectX - 8, rectY + rectHeight);
            
            // 标注矩形底部宽度 (底部)
            gc.fillText(String.format("%.0f cm", 14.0), rectX + rectWidth/2 - 15, rectY + rectHeight + 15);
            gc.strokeLine(rectX, rectY + rectHeight + 5, rectX + rectWidth, rectY + rectHeight + 5);
            gc.strokeLine(rectX, rectY + rectHeight + 2, rectX, rectY + rectHeight + 8);
            gc.strokeLine(rectX + rectWidth, rectY + rectHeight + 2, rectX + rectWidth, rectY + rectHeight + 8);
            
            // 标注三角形斜边 (右上和右下)
            // 右上斜边中点
            double upperTriMidX = triX + triHeight/2;
            double upperTriMidY = triY + triBase/4;
            gc.fillText(String.format("%.0f cm", 5.0), upperTriMidX + 10, upperTriMidY - 5);
            // 右下斜边中点
            double lowerTriMidX = triX + triHeight/2;
            double lowerTriMidY = triY + triBase * 3/4;
             gc.fillText(String.format("%.0f cm", 5.0), lowerTriMidX + 10, lowerTriMidY + 15);
             
             // 绘制斜边指示线 (简化处理，只画短线)
             gc.strokeLine(upperTriMidX, upperTriMidY, upperTriMidX + 5, upperTriMidY - 5);
             gc.strokeLine(lowerTriMidX, lowerTriMidY, lowerTriMidX + 5, lowerTriMidY + 5);

            // 添加新的水平长度标注 (22cm)
            double totalWidth = rectWidth + triHeight; // 总宽度
            gc.fillText(String.format("%.0f cm", 22.0), rectX + totalWidth/2 - 15, rectY - 25);
            gc.strokeLine(rectX, rectY - 15, rectX + totalWidth, rectY - 15);
            gc.strokeLine(rectX, rectY - 12, rectX, rectY - 18);
            gc.strokeLine(rectX + totalWidth, rectY - 12, rectX + totalWidth, rectY - 18);

        } else if (currentShapeIndex == 1) {
            // 图2：L形的尺寸标注
            Rectangle rect1 = (Rectangle) currentShapes.get(0); // 左上角矩形
            Rectangle rect2 = (Rectangle) currentShapes.get(1); // 下方矩形
            
            double rect1X = rect1.getX();
            double rect1Y = rect1.getY();
            double rect1Width = rect1.getWidth();
            double rect1Height = rect1.getHeight();
            
            double rect2X = rect2.getX();
            double rect2Y = rect2.getY();
            double rect2Width = rect2.getWidth();
            double rect2Height = rect2.getHeight();
            
            // 标注尺寸 (根据图片从左到右，从上到下)
            // 左侧总高 21 cm
            gc.fillText(String.format("%.0f cm", 21.0), rect1X - 30, rect1Y + (rect1Height + rect2Height)/2);
            gc.strokeLine(rect1X - 5, rect1Y, rect1X - 5, rect2Y + rect2Height);
            gc.strokeLine(rect1X - 2, rect1Y, rect1X - 8, rect1Y);
            gc.strokeLine(rect1X - 2, rect2Y + rect2Height, rect1X - 8, rect2Y + rect2Height);
            
            // 左上横边 11 cm
            gc.fillText(String.format("%.0f cm", 11.0), rect1X + rect1Width/2 - 15, rect1Y - 10);
            gc.strokeLine(rect1X, rect1Y - 5, rect1X + rect1Width, rect1Y - 5);
            gc.strokeLine(rect1X, rect1Y - 2, rect1X, rect1Y - 8);
            gc.strokeLine(rect1X + rect1Width, rect1Y - 2, rect1X + rect1Width, rect1Y - 8);
                
            // 右上竖边 11 cm
            gc.fillText(String.format("%.0f cm", 11.0), rect1X + rect1Width + 10, rect1Y + rect1Height/2);
            gc.strokeLine(rect1X + rect1Width + 5, rect1Y, rect1X + rect1Width + 5, rect1Y + rect1Height);
            gc.strokeLine(rect1X + rect1Width + 2, rect1Y, rect1X + rect1Width + 8, rect1Y);
            gc.strokeLine(rect1X + rect1Width + 2, rect1Y + rect1Height, rect1X + rect1Width + 8, rect1Y + rect1Height);
                
            // 连接两矩形的横边 10 cm
            gc.fillText(String.format("%.0f cm", 10.0), rect1X + rect1Width + (rect2Width - rect1Width)/2, rect1Y + rect1Height + 15);
            gc.strokeLine(rect1X + rect1Width, rect1Y + rect1Height + 5, rect2X + rect2Width, rect1Y + rect1Height + 5);
            gc.strokeLine(rect1X + rect1Width, rect1Y + rect1Height + 2, rect1X + rect1Width, rect1Y + rect1Height + 8);
            gc.strokeLine(rect2X + rect2Width, rect1Y + rect1Height + 2, rect2X + rect2Width, rect1Y + rect1Height + 8);
            
            // 右下竖边 10 cm
            gc.fillText(String.format("%.0f cm", 10.0), rect2X + rect2Width + 10, rect2Y + rect2Height/2);
            gc.strokeLine(rect2X + rect2Width + 5, rect2Y, rect2X + rect2Width + 5, rect2Y + rect2Height);
            gc.strokeLine(rect2X + rect2Width + 2, rect2Y, rect2X + rect2Width + 8, rect2Y);
            gc.strokeLine(rect2X + rect2Width + 2, rect2Y + rect2Height, rect2X + rect2Width + 8, rect2Y + rect2Height);
            
            // 下横边 20 cm
            gc.fillText(String.format("%.0f cm", 20.0), rect2X + rect2Width/2 - 15, rect2Y + rect2Height + 15);
             gc.strokeLine(rect2X, rect2Y + rect2Height + 5, rect2X + rect2Width, rect2Y + rect2Height + 5);
             gc.strokeLine(rect2X, rect2Y + rect2Height + 2, rect2X, rect2Y + rect2Height + 8);
             gc.strokeLine(rect2X + rect2Width, rect2Y + rect2Height + 2, rect2X + rect2Width, rect2Y + rect2Height + 8);

        } else if (currentShapeIndex == 2) {
             // 图3：凹形 - 尺寸标注
            Rectangle rect1 = (Rectangle) currentShapes.get(0); // 左侧矩形
            Rectangle rect2 = (Rectangle) currentShapes.get(1); // 右侧上方矩形

            double rect1X = rect1.getX();
            double rect1Y = rect1.getY();
            double rect1Width = rect1.getWidth();
            double rect1Height = rect1.getHeight();

            double rect2X = rect2.getX();
            double rect2Y = rect2.getY();
            double rect2Width = rect2.getWidth();
            double rect2Height = rect2.getHeight();

            // 标注左侧总高 19 cm
            gc.fillText(String.format("%.0f cm", 19.0), rect1X - 30, rect1Y + rect1Height/2);
            gc.strokeLine(rect1X - 5, rect1Y, rect1X - 5, rect1Y + rect1Height);
            gc.strokeLine(rect1X - 8, rect1Y, rect1X - 2, rect1Y);
            gc.strokeLine(rect1X - 8, rect1Y + rect1Height, rect1X - 2, rect1Y + rect1Height);

            // 标注顶部左侧 18 cm 宽
            gc.fillText(String.format("%.0f cm", 18.0), rect1X + rect1Width/2 - 15, rect1Y - 10);
            gc.strokeLine(rect1X, rect1Y - 5, rect1X + rect1Width, rect1Y - 5);
            gc.strokeLine(rect1X, rect1Y - 8, rect1X, rect1Y - 2);
            gc.strokeLine(rect1X + rect1Width, rect1Y - 8, rect1X + rect1Width, rect1Y - 2);

            // 标注顶部右侧 16 cm 宽
            gc.fillText(String.format("%.0f cm", 16.0), rect2X + rect2Width/2 - 15, rect2Y - 10);
            gc.strokeLine(rect2X, rect2Y - 5, rect2X + rect2Width, rect2Y - 5);
            gc.strokeLine(rect2X, rect2Y - 8, rect2X, rect2Y - 2);
            gc.strokeLine(rect2X + rect2Width, rect2Y - 8, rect2X + rect2Width, rect2Y - 2);

            // 标注右侧 16 cm 高
            gc.fillText(String.format("%.0f cm", 16.0), rect2X + rect2Width + 10, rect1Y + rect1Height - rect2Height/2);
            gc.strokeLine(rect2X + rect2Width + 5, rect1Y + rect1Height - rect2Height, rect2X + rect2Width + 5, rect1Y + rect1Height);
            gc.strokeLine(rect2X + rect2Width + 8, rect1Y + rect1Height - rect2Height, rect2X + rect2Width + 2, rect1Y + rect1Height - rect2Height);
            gc.strokeLine(rect2X + rect2Width + 8, rect1Y + rect1Height, rect2X + rect2Width + 2, rect1Y + rect1Height);

        } else if (currentShapeIndex == 3) {
            // 图4：尺寸标注
            Rectangle rectTop = (Rectangle) currentShapes.get(0);
            Rectangle rectBottom = (Rectangle) currentShapes.get(1);

            double x0 = rectBottom.getX();
            double y0 = rectBottom.getY();
            double x1 = rectBottom.getX() + rectBottom.getWidth();
            double y1 = rectBottom.getY();
            double x2 = rectBottom.getX() + rectBottom.getWidth();
            double y2 = rectBottom.getY() + rectBottom.getHeight();
            double x3 = rectBottom.getX();
            double y3 = rectBottom.getY() + rectBottom.getHeight();
            double x4 = rectTop.getX();
            double y4 = rectTop.getY();
            double x5 = rectTop.getX() + rectTop.getWidth();
            double y5 = rectTop.getY();
            double x6 = rectTop.getX() + rectTop.getWidth();
            double y6 = rectTop.getY() + rectTop.getHeight();
            double x7 = rectTop.getX();
            double y7 = rectTop.getY() + rectTop.getHeight();

            // 顶部宽 12m
            gc.fillText("12 cm", x4 + rectTop.getWidth() / 2 - 15, y4 - 10);
            gc.strokeLine(x4, y4 - 5, x5, y4 - 5);
            gc.strokeLine(x4, y4 - 2, x4, y4 - 8);
            gc.strokeLine(x5, y4 - 2, x5, y4 - 8);

            // 左侧高 12m
            gc.fillText("12 cm", x4 - 30, y4 + rectTop.getHeight() / 2);
            gc.strokeLine(x4 - 5, y4, x4 - 5, y7);
            gc.strokeLine(x4 - 2, y4, x4 - 8, y4);
            gc.strokeLine(x4 - 2, y7, x4 - 8, y7);

            // 右侧高 18m
            gc.fillText("18 cm", x6 + 10, y5 + (y2 - y5) / 2);
            gc.strokeLine(x6 + 5, y5, x6 + 5, y2);
            gc.strokeLine(x6 + 2, y5, x6 + 8, y5);
            gc.strokeLine(x6 + 2, y2, x6 + 8, y2);

            // 底部宽 24m
            gc.fillText("24 cm", x0 + rectBottom.getWidth() / 2 - 15, y2 + 35);
            gc.strokeLine(x0, y2 + 20, x2, y2 + 20);
            gc.strokeLine(x0, y2 + 17, x0, y2 + 23);
            gc.strokeLine(x2, y2 + 17, x2, y2 + 23);

            // 左下宽 2m
            gc.fillText("2 cm", x0 + 10, y3 + 15);
            gc.strokeLine(x0, y3 + 5, x4, y3 + 5);
            gc.strokeLine(x0, y3 + 2, x0, y3 + 8);
            gc.strokeLine(x4, y3 + 2, x4, y3 + 8);

            // 右下宽 10m
            gc.fillText("10 cm", x5 + 10, y2 + 15);
            gc.strokeLine(x5, y2 + 5, x2, y2 + 5);
            gc.strokeLine(x5, y2 + 2, x5, y2 + 8);
            gc.strokeLine(x2, y2 + 2, x2, y2 + 8);

            // 下方高 6m
            gc.fillText("6 cm", x1 + 10, y1 + rectBottom.getHeight() / 2);
            gc.strokeLine(x1 + 5, y1, x1 + 5, y2);
            gc.strokeLine(x1 + 2, y1, x1 + 8, y1);
            gc.strokeLine(x1 + 2, y2, x1 + 8, y2);
        } else if (currentShapeIndex == 4) {
            // 图5：直角梯形 - 尺寸标注
            Shape2D trap = currentShapes.get(0);
            double x = trap.getX();
            double y = trap.getY();
            double scale = 10.0;
            // 直接在此处计算顶点
            double[] xPoints = {x, x, x + 8*scale, x + 8*scale};
            double[] yPoints = {y, y - 12*scale, y - 4*scale, y};
            // 下底 4cm
            gc.fillText("4 cm", x + 4*scale - 10, y + 20);
            gc.strokeLine(x, y + 10, x + 8*scale, y + 10);
            gc.strokeLine(x, y + 7, x, y + 13);
            gc.strokeLine(x + 8*scale, y + 7, x + 8*scale, y + 13);
            // 左边 6cm
            gc.fillText("6 cm", x - 35, y - 6*scale);
            gc.strokeLine(x - 10, y, x - 10, y - 12*scale);
            gc.strokeLine(x - 7, y, x - 13, y);
            gc.strokeLine(x - 7, y - 12*scale, x - 13, y - 12*scale);
            // 右边 2cm
            gc.fillText("2 cm", x + 8*scale + 10, y - scale);
            gc.strokeLine(x + 8*scale + 5, y, x + 8*scale + 5, y - 4*scale);
            gc.strokeLine(x + 8*scale + 2, y, x + 8*scale + 8, y);
            gc.strokeLine(x + 8*scale + 2, y - 4*scale, x + 8*scale + 8, y - 4*scale);
//            // 上底斜边 5cm
//            double midX = (x + x + 4*scale) / 2;
//            double midY = (y - 6*scale + y - 2*scale) / 2;
//            gc.fillText("5 cm", midX - 10, midY - 10);
//            gc.strokeLine(x, y - 6*scale - 5, x + 4*scale, y - 2*scale - 5);
//            gc.strokeLine(x, y - 6*scale - 2, x, y - 6*scale - 8);
//            gc.strokeLine(x + 4*scale, y - 2*scale - 2, x + 4*scale, y - 2*scale - 8);
        } else if (currentShapeIndex == 5) {
            // 图6：直角梯形 - 尺寸标注
            Rectangle rect = (Rectangle) currentShapes.get(0);
            Triangle tri = (Triangle) currentShapes.get(1);

            double x0 = rect.getX();
            double y0 = rect.getY();
            double x1 = x0 + rect.getWidth();
            double y1 = y0;
            double x2 = x1 + tri.getBase();
            double y2 = y1 + tri.getHeight();
            double x3 = x0;
            double y3 = y0 + rect.getHeight();

            // 上边 9m
            gc.fillText("9 m", x0 + rect.getWidth() / 2 - 10, y0 - 10);
            gc.strokeLine(x0, y0 - 5, x1, y1 - 5);
            gc.strokeLine(x0, y0 - 2, x0, y0 - 8);
            gc.strokeLine(x1, y1 - 2, x1, y1 - 8);

            // 左边 11m
            gc.fillText("11 m", x0 - 30, y0 + rect.getHeight() / 2);
            gc.strokeLine(x0 - 5, y0, x0 - 5, y3);
            gc.strokeLine(x0 - 2, y0, x0 - 8, y0);
            gc.strokeLine(x0 - 2, y3, x0 - 8, y3);

            // 下边 20m
            gc.fillText("20 m", x0 + (x2 - x0) / 2 - 10, y2 + 20);
            gc.strokeLine(x3, y3 + 5, x2, y2 + 5);
            gc.strokeLine(x3, y3 + 2, x3, y3 + 8);
            gc.strokeLine(x2, y2 + 2, x2, y2 + 8);

            // 右斜边 14m
            double midX = (x1 + x2) / 2;
            double midY = (y1 + y2) / 2;
            double angle = Math.atan2(y2 - y1, x2 - x1);
            gc.save();
            gc.translate(midX, midY);
            gc.rotate(Math.toDegrees(angle));
            gc.setFill(Color.BLACK);
            gc.fillText("14 m", 10, -5); // 适当偏移
            gc.restore();
            // 可选：在斜边中点画一小段短线
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1.0);
            double shortLen = 20;
            double dx = shortLen * Math.cos(angle) / 2;
            double dy = shortLen * Math.sin(angle) / 2;
            gc.strokeLine(midX - dx, midY - dy, midX + dx, midY + dy);
        } else if (currentShapeIndex == 6) {
             // 图7：复合形状（五边形） - 尺寸标注
            // TODO: 添加图7的尺寸标注代码
        } else if (currentShapeIndex == 7) {
             // 图8：复杂矩形组合（T形） - 尺寸标注
            // TODO: 添加图8的尺寸标注代码
        } else if (currentShapeIndex == 8) {
             // 图9：楼梯形状 - 尺寸标注
            Rectangle rectLeft = (Rectangle) currentShapes.get(0); // 左侧矩形
            Rectangle rectRight = (Rectangle) currentShapes.get(1); // 右侧矩形

            double rectLeftX = rectLeft.getX();
            double rectLeftY = rectLeft.getY();
            double rectLeftWidth = rectLeft.getWidth();
            double rectLeftHeight = rectLeft.getHeight();

            double rectRightX = rectRight.getX();
            double rectRightY = rectRight.getY();
            double rectRightWidth = rectRight.getWidth();
            double rectRightHeight = rectRight.getHeight();

            // 标注左侧高 11 cm
            gc.fillText(String.format("%.0f cm", 11.0), rectLeftX - 30, rectLeftY + rectLeftHeight/2);
            gc.strokeLine(rectLeftX - 5, rectLeftY, rectLeftX - 5, rectLeftY + rectLeftHeight);
            gc.strokeLine(rectLeftX - 8, rectLeftY, rectLeftX - 2, rectLeftY);
            gc.strokeLine(rectLeftX - 8, rectLeftY + rectLeftHeight, rectLeftX - 2, rectLeftY + rectLeftHeight);

            // 标注底部总宽 18 cm
            gc.fillText(String.format("%.0f cm", 18.0), rectLeftX + (rectLeftWidth + rectRightWidth)/2 - 15, rectLeftY + rectLeftHeight + 15);
            gc.strokeLine(rectLeftX, rectLeftY + rectLeftHeight + 5, rectLeftX + rectLeftWidth + rectRightWidth, rectLeftY + rectLeftHeight + 5);
            gc.strokeLine(rectLeftX, rectLeftY + rectLeftHeight + 2, rectLeftX, rectLeftY + rectLeftHeight + 8);
            gc.strokeLine(rectLeftX + rectLeftWidth + rectRightWidth, rectLeftY + rectLeftHeight + 2, rectLeftX + rectLeftWidth + rectRightWidth, rectLeftY + rectLeftHeight + 8);

            // 标注左侧顶部宽 10 cm
            gc.fillText(String.format("%.0f cm", 10.0), rectLeftX + rectLeftWidth/2 - 15, rectLeftY - 10);
            gc.strokeLine(rectLeftX, rectLeftY - 5, rectLeftX + rectLeftWidth, rectLeftY - 5);
            gc.strokeLine(rectLeftX, rectLeftY - 8, rectLeftX, rectLeftY - 2);
            gc.strokeLine(rectLeftX + rectLeftWidth, rectLeftY - 8, rectLeftX + rectLeftWidth, rectLeftY - 2);

            // 标注右侧高 8 cm
            gc.fillText(String.format("%.0f cm", 8.0), rectRightX + rectRightWidth + 10, rectRightY + rectRightHeight/2);
            gc.strokeLine(rectRightX + rectRightWidth + 5, rectRightY, rectRightX + rectRightWidth + 5, rectRightY + rectRightHeight);
            gc.strokeLine(rectRightX + rectRightWidth + 8, rectRightY, rectRightX + rectRightWidth + 2, rectRightY);
            gc.strokeLine(rectRightX + rectRightWidth + 8, rectRightY + rectRightHeight, rectRightX + rectRightWidth + 2, rectRightY + rectRightHeight);

             // 标注右侧宽 8 cm
            gc.fillText(String.format("%.0f cm", 8.0), rectRightX + rectRightWidth/2 - 15, rectRightY - 10);
            gc.strokeLine(rectRightX, rectRightY - 5, rectRightX + rectRightWidth, rectRightY - 5);
            gc.strokeLine(rectRightX, rectRightY - 8, rectRightX, rectRightY - 2);
            gc.strokeLine(rectRightX + rectRightWidth, rectRightY - 8, rectRightX + rectRightWidth, rectRightY - 2);

        }
        // 其他图形的尺寸标注逻辑，待实现
    }

    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timeRemaining = 300; // 5 minutes

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                javafx.application.Platform.runLater(() -> {
                    timeRemaining--;
                    updateTimerLabel();
                    if (timeRemaining <= 0) {
                        timeUp();
                    }
                });
            }
        }, 0, 1000);
    }

    private void updateTimerLabel() {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        timerLabel.setText(String.format("Time remaining: %d:%02d", minutes, seconds));
    }

    private void timeUp() {
        if (timer != null) {
            timer.cancel();
        }
        // 播放答错音效
        AudioPlayer.playEffect("/audio/wrong.wav");
        messageLabel.setText("Time's up! The correct area is: " +
                String.format("%.2f", calculateTotalArea()));
        messageLabel.setTextFill(Color.RED);

        showSolution();
    }

    private double calculateTotalArea() {
        return currentShapes.stream()
                .mapToDouble(Shape2D::calculateArea)
                .sum();
    }

    private void showSolution() {
        // 重绘形状和尺寸标注
        drawCompoundShape();
        addDimensionLabels();
        
        // 显示解答图片
        if (currentShapeIndex >= 0 && currentShapeIndex < 9) {
            String imagePath = String.format("/images/sol%d.png", currentShapeIndex + 1);
            try {
                javafx.scene.image.Image solutionImage = new javafx.scene.image.Image(getClass().getResourceAsStream(imagePath));
                // 清除画布
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                // 图片覆盖整个画布
                gc.drawImage(solutionImage, 0, 0, canvas.getWidth(), canvas.getHeight());
            } catch (Exception e) {
                System.err.println("The solution image cannot be loaded: " + imagePath);
            }
        }
        
        // 记录此形状已完成
        if (currentShapeIndex >= 0 && currentShapeIndex < 9) {
            if (!completedShapes.contains(currentShapeIndex)) {
                completedShapes.add(currentShapeIndex);
                updateProgressLabel();
            }
        }
    }

    private void showNextShape() {
        attempts = 0;
        answerField.clear();
        messageLabel.setText("");
        if (timer != null) {
            timer.cancel();
        }
        
        // 如果已完成所有形状，返回主界面
        if (completedShapes.size() >= 9) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Task Completed");
            alert.setHeaderText("Great Job!");
            alert.setContentText("You have completed all compound shapes!");
            alert.showAndWait();
            
            // 返回主菜单
            MainView mainView = new MainView();
            mainView.getGameController().setCurrentScore(gameController.getCurrentScore());
            getScene().setRoot(mainView);
        } else {
            // 否则清空当前选择，等待用户选择下一个形状
            shapeSelector.getSelectionModel().clearSelection();
            currentShapeIndex = -1; // 重置索引
            answerField.setDisable(true); // 禁用输入框
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setFill(Color.BLACK);
            gc.setFont(javafx.scene.text.Font.font(16));
            gc.fillText("Please select a composite shape", 120, 200);
        }
    }

    private void checkAnswer() {
        // 修改判断条件，检查是否选择了图形(根据索引)
        if (currentShapeIndex == -1) {
            messageLabel.setText("Please select a shape first");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        attempts++;
        try {
            double answer = Double.parseDouble(answerField.getText().trim());
            double correctAnswer;
            
            // 根据不同图形计算正确答案
            switch (currentShapeIndex) {
                case 0:
                    correctAnswer = 252.0; // 第一个图形：矩形(14x14) + 三角形
                    break;
                case 1:
                    correctAnswer = 310.0; // 第二个图形：L形（11x11 + 20x10）
                    break;
                case 2:
                    correctAnswer = 598.0; // 第三个图形：凹形
                    break;
                case 3:
                    correctAnswer = 216.0; // 第四个图形：T形
                    break;
                case 4:
                    correctAnswer = 16.0; // 第五个图形：直角梯形
                    break;
                case 5:
                    correctAnswer = 159.5; // 第六个图形：梯形和直角三角形
                    break;
                case 6:
                    correctAnswer = 140.0; // 第七个图形：复合形状（五边形）
                    break;
                case 7:
                    correctAnswer = 3256.0; // 第八个图形：复杂矩形组合
                    break;
                case 8:
                    correctAnswer = 174.0; // 第九个图形：楼梯形状
                    break;
                default:
                    correctAnswer = calculateTotalArea();
                    break;
            }

            // Allow for small rounding differences (0.1)
            if (Math.abs(answer - correctAnswer) < 0.1) {
                if (timer != null) {
                    timer.cancel();
                }
                // 播放答对音效
                AudioPlayer.playEffect("/audio/correct.wav");
                messageLabel.setText("Correct! Well done!");
                messageLabel.setTextFill(Color.GREEN);
                gameController.addPoints(attempts, true);

                showSolution();

                // Show next shape after a delay
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(this::showNextShape);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                // 播放答错音效
                AudioPlayer.playEffect("/audio/wrong.wav");
                if (attempts >= 3) {
                    if (timer != null) {
                        timer.cancel();
                    }
                    messageLabel.setText("The correct answer is: " + String.format("%.2f", correctAnswer));
                    messageLabel.setTextFill(Color.RED);

                    showSolution();

                    // Show next shape after a delay
                    new Thread(() -> {
                        try {
                            Thread.sleep(3000);
                            javafx.application.Platform.runLater(this::showNextShape);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                } else {
                    messageLabel.setText("Try again! Attempt " + attempts + " of 3");
                    messageLabel.setTextFill(Color.RED);
                    answerField.clear();
                    answerField.requestFocus();
                }
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Please enter a valid number");
            messageLabel.setTextFill(Color.RED);
            attempts--; // Don't count invalid inputs
        }
    }
}