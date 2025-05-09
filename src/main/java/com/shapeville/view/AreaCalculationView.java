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

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.HashSet;
import java.util.Set;

public class AreaCalculationView extends VBox {
    private GameController gameController;
    private Canvas canvas;
    private GraphicsContext gc;
    private TextField answerField;
    private Label messageLabel;
    private Label timerLabel;
    private Shape2D currentShape;
    private int attempts;
    private Random random;
    private Timer timer;
    private int timeRemaining;
    private ComboBox<String> shapeSelector;
    private Set<String> completedShapes = new HashSet<>();
    private static final String[] AVAILABLE_SHAPES = {"Rectangle", "Triangle", "Parallelogram", "Trapezium"};

    public AreaCalculationView(GameController gameController) {
        this.gameController = gameController;
        this.random = new Random();
        setupUI();
    }

    private void setupUI() {
        setSpacing(10);
        setPadding(new Insets(10));
        setAlignment(Pos.TOP_CENTER);

        // Style for all labels - ensuring black text
        String labelStyle = "-fx-text-fill: black; -fx-font-size: 14px;";

        // Title with clearer style
        Label titleLabel = new Label("Calculate the area of shapes:");
        titleLabel.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");

        // Shape selection with reduced size
        HBox selectorBox = new HBox(10);
        selectorBox.setAlignment(Pos.CENTER);
        Label selectLabel = new Label("Select shape:");
        selectLabel.setStyle(labelStyle);
        shapeSelector = new ComboBox<>();
        shapeSelector.getItems().addAll(AVAILABLE_SHAPES);
        shapeSelector.setPromptText("Select a shape");
        shapeSelector.setPrefWidth(150);
        shapeSelector.setOnAction(e -> {
            if (shapeSelector.getValue() != null) {
                generateShape(shapeSelector.getValue());
            }
        });
        selectorBox.getChildren().addAll(selectLabel, shapeSelector);

        // Canvas for shape drawing with slightly reduced height
        canvas = new Canvas(400, 250);
        gc = canvas.getGraphicsContext2D();
        // 绘制初始指导文本
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(16));
        gc.fillText("Please select a shape", 100, 120);

        // Timer label with improved style
        timerLabel = new Label("Time remaining: 3:00");
        timerLabel.setStyle(labelStyle);

        // Input area with more compact layout
        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER);
        answerField = new TextField();
        answerField.setPromptText("Enter area");
        answerField.setPrefWidth(100);
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> checkAnswer());
        Label areaLabel = new Label("Area:");
        areaLabel.setStyle(labelStyle);
        inputBox.getChildren().addAll(areaLabel, answerField, submitButton);

        // Message label with improved style
        messageLabel = new Label("");
        messageLabel.setStyle("-fx-font-size: 14px;");

        // 进度指示器
        Label progressLabel = new Label("Progress: 0/4 shapes completed");
        progressLabel.setStyle(labelStyle);

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
            updateProgressLabel(progressLabel);
        });
    }

    private void updateProgressLabel(Label progressLabel) {
        progressLabel.setText(String.format("Progress: %d/4 shapes completed", completedShapes.size()));
        
        // 当所有形状都完成后，提示用户
        if (completedShapes.size() >= 4) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Task Completed");
            alert.setHeaderText("Great Job!");
            alert.setContentText("You have completed all shape area calculations!");
            alert.showAndWait();
            
            // 返回主菜单
            MainView mainView = new MainView();
            mainView.getGameController().setCurrentScore(gameController.getCurrentScore());
            getScene().setRoot(mainView);
        }
    }

    private void generateShape(String shapeType) {
        // Generate random dimensions between 1 and 20, but multiply by 2 to make shapes larger
        double dim1 = (random.nextInt(15) + 5) * 2; // 10-40范围
        double dim2 = (random.nextInt(15) + 5) * 2; // 10-40范围
        double dim3 = (random.nextInt(10) + 5) * 2; // 10-30范围

        currentShape = switch (shapeType) {
            case "Rectangle" -> new Rectangle(dim1, dim2);
            case "Triangle" -> new Triangle(dim1, dim2);
            case "Parallelogram" -> new Parallelogram(dim1, dim2);
            case "Trapezium" -> new Trapezium(dim1, dim3, dim2);
            default -> new Rectangle(dim1, dim2);
        };

        // 设置形状位置，使其居中显示
        currentShape.setPosition(
                (canvas.getWidth() - currentShape.getWidth()) / 2,
                (canvas.getHeight() - currentShape.getHeight()) / 2);

        // Clear canvas and draw new shape
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        currentShape.draw(gc);

        // 标注尺寸信息
        drawDimensions();

        // Reset attempts and start timer
        attempts = 0;
        startTimer();
    }

    private void drawDimensions() {
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(14));  // 增大字体
        
        // 获取形状的位置
        double x = currentShape.getX();
        double y = currentShape.getY();
        
        if (currentShape instanceof Rectangle) {
            Rectangle rect = (Rectangle) currentShape;
            // 在形状上方和左侧显示标签
            gc.fillText(String.format("Width: %.1f", rect.getWidth()), 
                       x + rect.getWidth()/2 - 40, 
                       y - 15);
            gc.fillText(String.format("Height: %.1f", rect.getHeight()), 
                       x - 80, 
                       y + rect.getHeight()/2);
                       
            // 绘制尺寸指示线
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            // 宽度指示线
            gc.strokeLine(x, y - 10, x + rect.getWidth(), y - 10);
            gc.strokeLine(x, y - 5, x, y - 15);
            gc.strokeLine(x + rect.getWidth(), y - 5, x + rect.getWidth(), y - 15);
            // 高度指示线
            gc.strokeLine(x - 10, y, x - 10, y + rect.getHeight());
            gc.strokeLine(x - 5, y, x - 15, y);
            gc.strokeLine(x - 5, y + rect.getHeight(), x - 15, y + rect.getHeight());
        } 
        else if (currentShape instanceof Triangle) {
            Triangle tri = (Triangle) currentShape;
            // 在三角形底部和左侧显示标签
            gc.fillText(String.format("Base: %.1f", tri.getBase()), 
                       x + tri.getWidth()/2 - 30, 
                       y + tri.getHeight() + 20);
            gc.fillText(String.format("Height: %.1f", tri.getHeight()), 
                       x - 80, 
                       y + tri.getHeight()/2);
                       
            // 绘制尺寸指示线
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            // 底边指示线
            gc.strokeLine(x, y + tri.getHeight() + 10, x + tri.getWidth(), y + tri.getHeight() + 10);
            gc.strokeLine(x, y + tri.getHeight() + 5, x, y + tri.getHeight() + 15);
            gc.strokeLine(x + tri.getWidth(), y + tri.getHeight() + 5, x + tri.getWidth(), y + tri.getHeight() + 15);
            // 高度指示线
            gc.strokeLine(x - 10, y, x - 10, y + tri.getHeight());
            gc.strokeLine(x - 5, y, x - 15, y);
            gc.strokeLine(x - 5, y + tri.getHeight(), x - 15, y + tri.getHeight());
        }
        else if (currentShape instanceof Parallelogram) {
            Parallelogram para = (Parallelogram) currentShape;
            // 在平行四边形底部和左侧显示标签
            gc.fillText(String.format("Base: %.1f", para.getBase()), 
                       x + para.getWidth()/2 - 30, 
                       y + para.getHeight() + 20);
            gc.fillText(String.format("Height: %.1f", para.getHeight()), 
                       x - 80, 
                       y + para.getHeight()/2);
                       
            // 绘制尺寸指示线
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            // 底边指示线
            gc.strokeLine(x, y + para.getHeight() + 10, x + para.getWidth(), y + para.getHeight() + 10);
            gc.strokeLine(x, y + para.getHeight() + 5, x, y + para.getHeight() + 15);
            gc.strokeLine(x + para.getWidth(), y + para.getHeight() + 5, x + para.getWidth(), y + para.getHeight() + 15);
            // 高度指示线
            gc.strokeLine(x - 10, y, x - 10, y + para.getHeight());
            gc.strokeLine(x - 5, y, x - 15, y);
            gc.strokeLine(x - 5, y + para.getHeight(), x - 15, y + para.getHeight());
        }
        else if (currentShape instanceof Trapezium) {
            Trapezium trap = (Trapezium) currentShape;
            // 在梯形上方、底部和左侧显示标签
            double offset = (trap.getBottomWidth() - trap.getTopWidth()) / 2;
            
            gc.fillText(String.format("a: %.1f", trap.getTopWidth()), 
                       x + offset + trap.getTopWidth()/2 - 20, 
                       y - 15);
            gc.fillText(String.format("c: %.1f", trap.getBottomWidth()), 
                       x + trap.getBottomWidth()/2 - 20, 
                       y + trap.getHeight() + 20);
            gc.fillText(String.format("h: %.1f", trap.getHeight()), 
                       x - 50, 
                       y + trap.getHeight()/2);
                       
            // 绘制尺寸指示线
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            // 上底指示线
            gc.strokeLine(x + offset, y - 10, x + offset + trap.getTopWidth(), y - 10);
            gc.strokeLine(x + offset, y - 5, x + offset, y - 15);
            gc.strokeLine(x + offset + trap.getTopWidth(), y - 5, x + offset + trap.getTopWidth(), y - 15);
            // 下底指示线
            gc.strokeLine(x, y + trap.getHeight() + 10, x + trap.getBottomWidth(), y + trap.getHeight() + 10);
            gc.strokeLine(x, y + trap.getHeight() + 5, x, y + trap.getHeight() + 15);
            gc.strokeLine(x + trap.getBottomWidth(), y + trap.getHeight() + 5, x + trap.getBottomWidth(), y + trap.getHeight() + 15);
            // 高度指示线
            gc.strokeLine(x - 10, y, x - 10, y + trap.getHeight());
            gc.strokeLine(x - 5, y, x - 15, y);
            gc.strokeLine(x - 5, y + trap.getHeight(), x - 15, y + trap.getHeight());
        }
    }

    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timeRemaining = 180; // 3 minutes

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
                String.format("%.2f", currentShape.calculateArea()));
        messageLabel.setTextFill(Color.RED);

        showSolution();
    }

    private void showSolution() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        currentShape.draw(gc);
        drawDimensions();
        
        // 显示公式
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(14));
        
        String formula = "";
        if (currentShape instanceof Rectangle) {
            formula = ((Rectangle) currentShape).getFormulaWithValues();
        } else if (currentShape instanceof Triangle) {
            formula = ((Triangle) currentShape).getFormulaWithValues();
        } else if (currentShape instanceof Parallelogram) {
            formula = ((Parallelogram) currentShape).getFormulaWithValues();
        } else if (currentShape instanceof Trapezium) {
            formula = ((Trapezium) currentShape).getFormulaWithValues();
        }
        
        gc.fillText(formula, 10, canvas.getHeight() - 10);
        
        // 记录此形状已完成
        if (currentShape != null) {
            completedShapes.add(shapeSelector.getValue());
            
            // 从下拉菜单中移除已完成的形状
            for (Label node : getChildrenOfType(Label.class)) {
                if (node.getText().startsWith("Progress:")) {
                    updateProgressLabel(node);
                    break;
                }
            }
        }
    }

    private <T> Set<T> getChildrenOfType(Class<T> type) {
        Set<T> result = new HashSet<>();
        for (javafx.scene.Node node : getChildren()) {
            if (type.isInstance(node)) {
                result.add((T) node);
            }
        }
        return result;
    }

    private void showNextShape() {
        attempts = 0;
        answerField.clear();
        messageLabel.setText("");
        if (timer != null) {
            timer.cancel();
        }
        
        // 如果已完成所有形状，返回主界面
        if (completedShapes.size() >= 4) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Task Completed");
            alert.setHeaderText("Great Job!");
            alert.setContentText("You have completed all shape area calculations!");
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
            gc.fillText("请从上方下拉菜单选择一个形状", 100, 120);
        }
    }

    private void checkAnswer() {
        if (currentShape == null) {
            messageLabel.setText("Please select a shape first");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        attempts++;
        try {
            double answer = Double.parseDouble(answerField.getText().trim());
            double correctAnswer = currentShape.calculateArea();

            // Allow for small rounding differences
            if (Math.abs(answer - correctAnswer) < 0.1) {
                if (timer != null) {
                    timer.cancel();
                }
                messageLabel.setText("Correct! Well done!");
                messageLabel.setTextFill(Color.GREEN);
                gameController.addPoints(attempts, false);

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
                    messageLabel.setText("The correct area is: " + String.format("%.2f", correctAnswer));
                    messageLabel.setTextFill(Color.RED);

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