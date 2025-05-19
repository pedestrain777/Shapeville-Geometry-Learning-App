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

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class CompoundShapeView extends VBox {
    private GameController gameController;
    private Canvas canvas;
    private GraphicsContext gc;
    private TextField answerField;
    private Label messageLabel;
    private Label timerLabel;
    private Label progressLabel;
    private CompoundShape currentShape;
    private int attempts;
    private Timer timer;
    private int timeRemaining;
    private int currentShapeIndex = -1;
    private Set<Integer> completedShapes = new HashSet<>();
    private ComboBox<String> shapeSelector;

    // 进度跟踪和回调
    private Runnable onExit = null;
    private ProgressBar progressBar;
    private Label scoreLabel;
    private boolean completionAlertShown = false;

    public CompoundShapeView(GameController gameController) {
        this.gameController = gameController;
        setupUI();
    }

    // 设置返回主界面回调的方法
    public void setOnExit(Runnable onExit) {
        this.onExit = onExit;
    }

    private void setupUI() {
        setSpacing(20);
        setPadding(new Insets(20));
        setAlignment(Pos.CENTER);

        // 标题
        Label titleLabel = new Label("Calculate the total area of the compound shape:");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black;");

        // 添加分数和进度显示区域
        scoreLabel = new Label("Score: 0");
        scoreLabel.setStyle("-fx-text-fill: black; -fx-font-size: 14px;");

        // 创建进度条
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(200);
        progressBar.setPrefHeight(20); // 增加高度使进度条更明显
        progressBar.setMinHeight(20);
        progressBar.setStyle("-fx-accent: #56B4E9; -fx-control-inner-background: #EEEEEE;"); // 增加背景色对比

        progressLabel = new Label("Progress: 0/9 shapes completed");
        progressLabel.setStyle("-fx-text-fill: black; -fx-font-size: 14px;");

        // 修改进度显示区域为水平布局
        HBox progressBox = new HBox(10);
        progressBox.setAlignment(Pos.CENTER);
        progressBox.getChildren().addAll(progressBar, progressLabel);

        VBox statsBox = new VBox(5);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.getChildren().addAll(scoreLabel, progressBox);
        statsBox.setStyle(
                "-fx-padding: 5; -fx-border-color: #CCCCCC; -fx-border-width: 1; -fx-background-color: #F8F8F8;");

        // Shape selection
        HBox selectorBox = new HBox(10);
        selectorBox.setAlignment(Pos.CENTER);
        shapeSelector = new ComboBox<>();
        shapeSelector.getItems().addAll(CompoundShapeFactory.getAllShapeNames());
        shapeSelector.setPromptText("Select a compound shape");
        shapeSelector.setPrefWidth(200);
        shapeSelector.setOnAction(e -> {
            if (shapeSelector.getValue() != null) {
                // 在选择新图形时清除消息
                messageLabel.setText("");

                currentShapeIndex = shapeSelector.getSelectionModel().getSelectedIndex();
                generateCompoundShape(currentShapeIndex);
            }
        });
        selectorBox.getChildren().addAll(new Label("Select shape:"), shapeSelector);

        // Canvas
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

        getChildren().addAll(
                titleLabel,
                statsBox,
                selectorBox,
                canvas,
                timerLabel,
                inputBox,
                messageLabel);

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
        // 更新进度标签和进度条
        int completedCount = completedShapes.size();
        double progress = (double) completedCount / 9.0;
        progressBar.setProgress(progress);
        progressLabel.setText(String.format("Progress: %d/9 shapes completed", completedCount));

        // 更新分数显示
        scoreLabel.setText("Score: " + gameController.getCurrentScore());

        // 当所有形状都完成后，提示用户
        if (completedCount >= 9 && !completionAlertShown) {
            completionAlertShown = true;

            // 标记关卡完成
            gameController.taskCompleted(5);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Task Completed");
            alert.setHeaderText("Great Job!");
            alert.setContentText("You have completed all compound shapes!");

            ButtonType returnButton = new ButtonType("Return to Main Menu");
            ButtonType stayButton = new ButtonType("Stay Here");
            alert.getButtonTypes().setAll(returnButton, stayButton);

            alert.showAndWait().ifPresent(response -> {
                if (response == returnButton) {
                    // 使用回调返回主界面
                    if (onExit != null) {
                        onExit.run();
                    } else {
                        // 向后兼容，如果没有设置回调则使用原有方式
                        MainView mainView = new MainView();
                        mainView.getGameController().setCurrentScore(gameController.getCurrentScore());
                        getScene().setRoot(mainView);
                    }
                }
            });
        }
    }

    private void generateCompoundShape(int index) {
        // 使用工厂创建相应的复合形状
        currentShape = CompoundShapeFactory.createShape(index);

        // 清空画布
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // 设置形状的统一颜色
        gc.setFill(Color.LIGHTBLUE);

        // 绘制复合形状
        drawCompoundShape();

        // 启动计时器
        startTimer();
    }

    private void drawCompoundShape() {
        if (currentShape == null) {
            return;
        }

        // 获取Canvas中心点
        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2;

        // 由复合形状对象负责绘制自身
        currentShape.draw(gc, centerX, centerY);

        // 添加尺寸标注
        currentShape.addDimensionLabels(gc);
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
                String.format("%.2f", currentShape.calculateArea()));
        messageLabel.setTextFill(Color.RED);

        showSolution();
    }

    private void showSolution() {
        // 重绘形状和尺寸标注
        drawCompoundShape();

        // 显示解答图片
        if (currentShapeIndex >= 0 && currentShapeIndex < 9) {
            String imagePath = String.format("/images/sol%d.png", currentShapeIndex + 1);
            try {
                javafx.scene.image.Image solutionImage = new javafx.scene.image.Image(
                        getClass().getResourceAsStream(imagePath));
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

        // 重置计时器显示为初始状态
        timerLabel.setText("Time remaining: 5:00");

        // 保存当前选择的形状名称（如果有）
        String currentShapeName = shapeSelector.getValue();

        // 如果已完成所有形状，但没有显示完成对话框，则通过更新进度标签显示完成对话框
        if (completedShapes.size() >= 9 && !completionAlertShown) {
            updateProgressLabel();
            return;
        } else if (completionAlertShown) {
            // 如果已经显示过完成对话框，只清空当前选择
            shapeSelector.getSelectionModel().clearSelection();
            currentShapeIndex = -1; // 重置索引
            answerField.setDisable(true); // 禁用输入框
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setFill(Color.BLACK);
            gc.setFont(javafx.scene.text.Font.font(16));
            gc.fillText("Please select a composite shape", 120, 200);
        } else {
            // 重新填充下拉菜单，排除已完成的形状
            shapeSelector.getItems().clear();
            java.util.List<String> allShapeNames = CompoundShapeFactory.getAllShapeNames();

            // 添加未完成的形状到下拉菜单
            for (int i = 0; i < allShapeNames.size() && i < 9; i++) {
                if (!completedShapes.contains(i)) {
                    shapeSelector.getItems().add(allShapeNames.get(i));
                }
            }

            // 清空当前选择
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
            double correctAnswer = currentShape.calculateArea();

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

                // 更新分数显示
                scoreLabel.setText("Score: " + gameController.getCurrentScore());

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