package com.shapeville.view;

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
    private int currentShapeIndex;
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
        selectorBox.getChildren().addAll(new Label("Select shape:"), shapeSelector);
        
        shapeSelector.setOnAction(e -> {
            if (shapeSelector.getValue() != null) {
                currentShapeIndex = shapeSelector.getSelectionModel().getSelectedIndex();
                generateCompoundShape(currentShapeIndex);
            }
        });

        // Canvas for shape drawing
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
                canvas,
                timerLabel,
                inputBox,
                messageLabel,
                progressLabel);
                
        // 更新进度显示
        shapeSelector.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateProgressLabel();
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
        
        // 放大所有尺寸至原来的两倍
        switch (index) {
            case 0 -> {
                // 形状1: L形（矩形+矩形）
                double width1 = 28;  // 原来的14 * 2
                double height1 = 28; // 原来的14 * 2
                double width2 = 28;  // 原来的14 * 2
                double height2 = 10; // 原来的5 * 2
                
                Rectangle rect1 = new Rectangle(width1, height1);
                Rectangle rect2 = new Rectangle(width2, height2);
                
                currentShapes.add(rect1);
                currentShapes.add(rect2);
            }
            case 1 -> {
                // 形状2: T形（矩形+矩形）
                double width1 = 42;  // 原来的21 * 2
                double height1 = 20; // 原来的10 * 2
                double width2 = 22;  // 原来的11 * 2
                double height2 = 22; // 原来的11 * 2
                
                Rectangle rect1 = new Rectangle(width1, height1);
                Rectangle rect2 = new Rectangle(width2, height2);
                
                currentShapes.add(rect1);
                currentShapes.add(rect2);
            }
            case 2 -> {
                // 形状3: 凹形（矩形+2个小矩形）
                double width1 = 36;  // 原来的18 * 2
                double height1 = 32; // 原来的16 * 2
                double width2 = 32;  // 原来的16 * 2
                double height2 = 38; // 原来的19 * 2
                
                Rectangle rect1 = new Rectangle(width1, height1);
                Rectangle rect2 = new Rectangle(width2, height2);
                
                currentShapes.add(rect1);
                currentShapes.add(rect2);
            }
            case 3 -> {
                // 形状4: 复杂阶梯形（3个矩形）
                double width1 = 24;  // 原来的12 * 2
                double height1 = 24; // 原来的12 * 2
                double width2 = 20;  // 原来的10 * 2
                double height2 = 4;  // 原来的2 * 2
                double width3 = 48;  // 原来的24 * 2
                double height3 = 12; // 原来的6 * 2
                
                Rectangle rect1 = new Rectangle(width1, height1);
                Rectangle rect2 = new Rectangle(width2, height2);
                Rectangle rect3 = new Rectangle(width3, height3);
                
                currentShapes.add(rect1);
                currentShapes.add(rect2);
                currentShapes.add(rect3);
            }
            case 4 -> {
                // 形状5: 三角形
                double triangleBase = 32;  // 原来的16 * 2
                double height = 8;         // 原来的4 * 2
                double hypotenuse = 8;     // 原来的4 * 2
                
                Triangle triangle = new Triangle(triangleBase, height);
                Rectangle rect = new Rectangle(triangleBase, hypotenuse);
                
                currentShapes.add(triangle);
                currentShapes.add(rect);
            }
            case 5 -> {
                // 形状6: 梯形和直角三角形
                double width = 40;      // 原来的20 * 2
                double height = 22;     // 原来的11 * 2
                double topWidth = 18;   // 原来的9 * 2
                double diagonal = 28;   // 原来的14 * 2
                
                Trapezium trapezium = new Trapezium(topWidth, width, height);
                currentShapes.add(trapezium);
            }
            case 6 -> {
                // 形状7: 复合形状（五边形）
                double rectBase = 28;   // 原来的14 * 2
                double height1 = 32;    // 原来的16 * 2
                double height2 = 24;    // 原来的12 * 2
                double width = 10;      // 原来的5 * 2
                
                Rectangle rect = new Rectangle(rectBase, height1);
                Triangle triangle = new Triangle(rectBase, height2);
                
                currentShapes.add(rect);
                currentShapes.add(triangle);
            }
            case 7 -> {
                // 形状8: 复杂矩形组合
                double width1 = 72;     // 原来的36 * 2
                double height1 = 72;    // 原来的36 * 2
                double width2 = 120;    // 原来的60 * 2
                double height2 = 72;    // 原来的36 * 2
                
                Rectangle rect1 = new Rectangle(width1, height1);
                Rectangle rect2 = new Rectangle(width2, height2);
                
                currentShapes.add(rect1);
                currentShapes.add(rect2);
            }
            case 8 -> {
                // 形状9: 楼梯形状
                double width1 = 20;     // 原来的10 * 2
                double height1 = 22;    // 原来的11 * 2
                double width2 = 16;     // 原来的8 * 2
                double height2 = 16;    // 原来的8 * 2
                
                Rectangle rect1 = new Rectangle(width1, height1);
                Rectangle rect2 = new Rectangle(width2, height2);
                
                currentShapes.add(rect1);
                currentShapes.add(rect2);
            }
            default -> {
                // 默认形状：简单矩形
                Rectangle defaultShape = new Rectangle(20, 20);  // 原来的10 * 2
                currentShapes.add(defaultShape);
            }
        }

        drawCompoundShape();
        startTimer();
    }

    private void drawCompoundShape() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // 没有形状时直接返回
        if (currentShapes.isEmpty()) {
            return;
        }

        // 计算每个形状的位置，根据不同的复合形状类型
        positionShapes();

        // 绘制所有形状，使用不同的颜色来区分不同的部分
        Color[] colors = {
            Color.LIGHTBLUE, Color.LIGHTGREEN, Color.LIGHTSALMON, 
            Color.LIGHTCORAL, Color.LIGHTSTEELBLUE, Color.LIGHTYELLOW
        };
        
        for (int i = 0; i < currentShapes.size(); i++) {
            Shape2D shape = currentShapes.get(i);
            // 设置不同的填充颜色
            gc.setFill(colors[i % colors.length]);
            shape.draw(gc);
            
            // 绘制边框
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2.0);
            if (shape instanceof Rectangle) {
                Rectangle rect = (Rectangle) shape;
                gc.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
            } else if (shape instanceof Triangle) {
                Triangle tri = (Triangle) shape;
                double[] xPoints = {tri.getX(), tri.getX() + tri.getWidth(), tri.getX() + tri.getWidth()/2};
                double[] yPoints = {tri.getY() + tri.getHeight(), tri.getY() + tri.getHeight(), tri.getY()};
                gc.strokePolygon(xPoints, yPoints, 3);
            } else if (shape instanceof Trapezium) {
                Trapezium trap = (Trapezium) shape;
                double offset = (trap.getBottomWidth() - trap.getTopWidth()) / 2;
                double[] xPoints = {
                    trap.getX() + offset, trap.getX() + offset + trap.getTopWidth(), 
                    trap.getX() + trap.getBottomWidth(), trap.getX()
                };
                double[] yPoints = {
                    trap.getY(), trap.getY(), 
                    trap.getY() + trap.getHeight(), trap.getY() + trap.getHeight()
                };
                gc.strokePolygon(xPoints, yPoints, 4);
            }
        }

        // 添加尺寸标注
        addDimensionLabels();
    }
    
    private void positionShapes() {
        // 基本位置
        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2;
        
        switch (currentShapeIndex) {
            case 0 -> {
                // L形：第一个矩形作为L的底部，第二个矩形作为L的右侧竖直部分
                Rectangle rect1 = (Rectangle) currentShapes.get(0);
                Rectangle rect2 = (Rectangle) currentShapes.get(1);
                
                double startX = centerX - rect1.getWidth()/2;
                double startY = centerY - rect1.getHeight()/2;
                
                rect1.setPosition(startX, startY);
                rect2.setPosition(startX + rect1.getWidth() - rect2.getWidth(), 
                                 startY - rect2.getHeight());
            }
            case 1 -> {
                // T形：第一个矩形作为T的横线，第二个矩形作为T的竖线
                Rectangle rect1 = (Rectangle) currentShapes.get(0);
                Rectangle rect2 = (Rectangle) currentShapes.get(1);
                
                double startX = centerX - rect1.getWidth()/2;
                double startY = centerY - rect2.getHeight()/2;
                
                rect1.setPosition(startX, startY);
                rect2.setPosition(startX + (rect1.getWidth() - rect2.getWidth())/2, 
                                 startY - rect2.getHeight());
            }
            case 2 -> {
                // 凹形：两个矩形合并成一个凹形
                Rectangle rect1 = (Rectangle) currentShapes.get(0);
                Rectangle rect2 = (Rectangle) currentShapes.get(1);
                
                double startX = centerX - rect1.getWidth()/2;
                double startY = centerY - (rect1.getHeight() + rect2.getHeight())/2;
                
                rect1.setPosition(startX, startY);
                rect2.setPosition(startX + (rect1.getWidth() - rect2.getWidth())/2, 
                                 startY + rect1.getHeight());
            }
            case 3 -> {
                // 复杂阶梯形：三个矩形组合
                Rectangle rect1 = (Rectangle) currentShapes.get(0);
                Rectangle rect2 = (Rectangle) currentShapes.get(1);
                Rectangle rect3 = (Rectangle) currentShapes.get(2);
                
                double startX = centerX - rect3.getWidth()/2;
                double startY = centerY - (rect1.getHeight() + rect3.getHeight())/2;
                
                rect1.setPosition(startX, startY);
                rect2.setPosition(startX + rect1.getWidth(), startY);
                rect3.setPosition(startX, startY + rect1.getHeight());
            }
            case 4 -> {
                // 三角形和矩形
                Triangle triangle = (Triangle) currentShapes.get(0);
                Rectangle rect = (Rectangle) currentShapes.get(1);
                
                double startX = centerX - triangle.getWidth()/2;
                double startY = centerY - (triangle.getHeight() + rect.getHeight())/2;
                
                triangle.setPosition(startX, startY);
                rect.setPosition(startX, startY + triangle.getHeight());
            }
            case 5 -> {
                // 梯形
                Trapezium trapezium = (Trapezium) currentShapes.get(0);
                trapezium.setPosition(centerX - trapezium.getWidth()/2, centerY - trapezium.getHeight()/2);
            }
            case 6 -> {
                // 五边形（矩形和三角形）
                Rectangle rect = (Rectangle) currentShapes.get(0);
                Triangle triangle = (Triangle) currentShapes.get(1);
                
                double startX = centerX - rect.getWidth()/2;
                double startY = centerY - (rect.getHeight() + triangle.getHeight())/2;
                
                rect.setPosition(startX, startY + triangle.getHeight());
                triangle.setPosition(startX, startY);
            }
            case 7 -> {
                // 复杂矩形组合
                Rectangle rect1 = (Rectangle) currentShapes.get(0);
                Rectangle rect2 = (Rectangle) currentShapes.get(1);
                
                double startX = centerX - rect2.getWidth()/2;
                double startY = centerY - (rect1.getHeight() + rect2.getHeight())/2;
                
                rect1.setPosition(startX, startY);
                rect2.setPosition(startX, startY + rect1.getHeight());
            }
            case 8 -> {
                // 楼梯形状
                Rectangle rect1 = (Rectangle) currentShapes.get(0);
                Rectangle rect2 = (Rectangle) currentShapes.get(1);
                
                double startX = centerX - rect1.getWidth()/2;
                double startY = centerY - (rect1.getHeight() + rect2.getHeight()/2)/2;
                
                rect1.setPosition(startX, startY);
                rect2.setPosition(startX + rect1.getWidth() - rect2.getWidth(), 
                                 startY - rect2.getHeight());
            }
            default -> {
                // 默认居中显示
                if (!currentShapes.isEmpty()) {
                    Shape2D shape = currentShapes.get(0);
                    shape.setPosition(centerX - shape.getWidth()/2, centerY - shape.getHeight()/2);
                }
            }
        }
    }
    
    private void addDimensionLabels() {
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(14));  // 增大字体
        gc.setLineWidth(1.0);
        
        for (Shape2D shape : currentShapes) {
            // 获取当前形状的位置
            double x = 0, y = 0;
            
            // 根据形状类型添加不同的标签
            if (shape instanceof Rectangle) {
                Rectangle rect = (Rectangle) shape;
                x = rect.getX();
                y = rect.getY();
                
                // 添加宽度标签
                gc.fillText(String.format("%.1f", rect.getWidth()), 
                           x + rect.getWidth()/2 - 15, 
                           y - 10);
                
                // 添加高度标签
                gc.fillText(String.format("%.1f", rect.getHeight()), 
                           x - 30, 
                           y + rect.getHeight()/2);
                           
                // 绘制指示线
                gc.setStroke(Color.BLACK);
                // 宽度指示线
                gc.strokeLine(x, y - 5, x + rect.getWidth(), y - 5);
                gc.strokeLine(x, y - 2, x, y - 8);
                gc.strokeLine(x + rect.getWidth(), y - 2, x + rect.getWidth(), y - 8);
                // 高度指示线
                gc.strokeLine(x - 5, y, x - 5, y + rect.getHeight());
                gc.strokeLine(x - 2, y, x - 8, y);
                gc.strokeLine(x - 2, y + rect.getHeight(), x - 8, y + rect.getHeight());
            } 
            else if (shape instanceof Triangle) {
                Triangle tri = (Triangle) shape;
                x = tri.getX();
                y = tri.getY();
                
                // 添加底边标签
                gc.fillText(String.format("%.1f", tri.getBase()), 
                           x + tri.getWidth()/2 - 15, 
                           y + tri.getHeight() + 15);
                
                // 添加高度标签
                gc.fillText(String.format("%.1f", tri.getHeight()), 
                           x - 30, 
                           y + tri.getHeight()/2);
                           
                // 绘制指示线
                gc.setStroke(Color.BLACK);
                // 底边指示线
                gc.strokeLine(x, y + tri.getHeight() + 5, x + tri.getWidth(), y + tri.getHeight() + 5);
                gc.strokeLine(x, y + tri.getHeight() + 2, x, y + tri.getHeight() + 8);
                gc.strokeLine(x + tri.getWidth(), y + tri.getHeight() + 2, x + tri.getWidth(), y + tri.getHeight() + 8);
                // 高度指示线
                gc.strokeLine(x - 5, y, x - 5, y + tri.getHeight());
                gc.strokeLine(x - 2, y, x - 8, y);
                gc.strokeLine(x - 2, y + tri.getHeight(), x - 8, y + tri.getHeight());
            }
            else if (shape instanceof Trapezium) {
                Trapezium trap = (Trapezium) shape;
                x = trap.getX();
                y = trap.getY();
                double offset = (trap.getBottomWidth() - trap.getTopWidth()) / 2;
                
                // 添加上底标签
                gc.fillText(String.format("%.1f", trap.getTopWidth()), 
                           x + offset + trap.getTopWidth()/2 - 15, 
                           y - 10);
                
                // 添加下底标签
                gc.fillText(String.format("%.1f", trap.getBottomWidth()), 
                           x + trap.getBottomWidth()/2 - 15, 
                           y + trap.getHeight() + 15);
                
                // 添加高度标签
                gc.fillText(String.format("%.1f", trap.getHeight()), 
                           x - 30, 
                           y + trap.getHeight()/2);
                           
                // 绘制指示线
                gc.setStroke(Color.BLACK);
                // 上底指示线
                gc.strokeLine(x + offset, y - 5, x + offset + trap.getTopWidth(), y - 5);
                gc.strokeLine(x + offset, y - 2, x + offset, y - 8);
                gc.strokeLine(x + offset + trap.getTopWidth(), y - 2, x + offset + trap.getTopWidth(), y - 8);
                // 下底指示线
                gc.strokeLine(x, y + trap.getHeight() + 5, x + trap.getBottomWidth(), y + trap.getHeight() + 5);
                gc.strokeLine(x, y + trap.getHeight() + 2, x, y + trap.getHeight() + 8);
                gc.strokeLine(x + trap.getBottomWidth(), y + trap.getHeight() + 2, x + trap.getBottomWidth(), y + trap.getHeight() + 8);
                // 高度指示线
                gc.strokeLine(x - 5, y, x - 5, y + trap.getHeight());
                gc.strokeLine(x - 2, y, x - 8, y);
                gc.strokeLine(x - 2, y + trap.getHeight(), x - 8, y + trap.getHeight());
            }
        }
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
        // 重绘形状
        drawCompoundShape();
        
        // 绘制解决方案
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(14));
        
        double y = 30;
        double totalArea = 0;
        
        // 显示每个形状的面积
        for (int i = 0; i < currentShapes.size(); i++) {
            Shape2D shape = currentShapes.get(i);
            String shapeType = shape.getClass().getSimpleName();
            double area = shape.calculateArea();
            String formula = "";
            
            if (shape instanceof Rectangle) {
                formula = ((Rectangle)shape).getFormula();
            } else if (shape instanceof Triangle) {
                formula = ((Triangle)shape).getFormula();
            } else if (shape instanceof Trapezium) {
                formula = ((Trapezium)shape).getFormula();
            }
            
            String areaText = String.format("Shape %d (%s): %.2f - %s",
                              i + 1, shapeType, area, formula);
            gc.fillText(areaText, 20, y);
            totalArea += area;
            y += 20;
        }
        
        // 显示总面积
        gc.setFont(javafx.scene.text.Font.font(16));
        gc.fillText(String.format("Total area: %.2f", totalArea), 20, y + 20);
        
        // 记录此形状已完成
        if (currentShapeIndex >= 0 && currentShapeIndex < 9) {
            completedShapes.add(currentShapeIndex);
            updateProgressLabel();
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
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setFill(Color.BLACK);
            gc.setFont(javafx.scene.text.Font.font(16));
            gc.fillText("请从上方下拉菜单选择一个复合形状", 120, 200);
        }
    }

    private void checkAnswer() {
        if (currentShapes.isEmpty()) {
            messageLabel.setText("Please select a shape first");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        attempts++;
        try {
            double answer = Double.parseDouble(answerField.getText().trim());
            double correctAnswer = calculateTotalArea();

            // Allow for small rounding differences
            if (Math.abs(answer - correctAnswer) < 0.1) {
                if (timer != null) {
                    timer.cancel();
                }
                messageLabel.setText("Correct! Well done!");
                messageLabel.setTextFill(Color.GREEN);
                gameController.addPoints(attempts, true); // Advanced level scoring

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