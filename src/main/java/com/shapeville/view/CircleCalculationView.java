package com.shapeville.view;

import com.shapeville.util.AudioPlayer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import com.shapeville.controller.GameController;
import com.shapeville.model.Circle;
import javafx.scene.paint.Color;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.HashSet;
import java.util.Set;

public class CircleCalculationView extends VBox {
    private GameController gameController;
    private Canvas canvas;
    private GraphicsContext gc;
    private TextField answerField;
    private Label messageLabel;
    private Label timerLabel;
    private Circle currentCircle;
    private int attempts;
    private Random random;
    private Timer timer;
    private int timeRemaining;
    private boolean isCalculatingArea;
    private boolean useDiameter;

    // 添加进度跟踪和回调
    private Runnable onExit = null;
    private ProgressBar progressBar;
    private Label progressLabel;
    private Label scoreLabel;
    private boolean areaCompleted = false;
    private boolean circumferenceCompleted = false;
    private boolean completionAlertShown = false;

    public CircleCalculationView(GameController gameController) {
        this.gameController = gameController;
        this.random = new Random();
        setupUI();
        showNextCircle();
    }

    // 添加设置返回主界面回调的方法
    public void setOnExit(Runnable onExit) {
        this.onExit = onExit;
    }

    private void setupUI() {
        setSpacing(10);
        setPadding(new Insets(10));
        setAlignment(Pos.TOP_CENTER);

        String labelStyle = "-fx-text-fill: black; -fx-font-size: 12px;";

        // 添加分数和进度显示区域
        scoreLabel = new Label("Score: 0");
        scoreLabel.setStyle("-fx-text-fill: black; -fx-font-size: 14px;");

        // 创建进度条
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(200);
        progressBar.setPrefHeight(20); // 增加高度使进度条更明显
        progressBar.setMinHeight(20);
        progressBar.setStyle("-fx-accent: #56B4E9; -fx-control-inner-background: #EEEEEE;"); // 增加背景色对比

        progressLabel = new Label("Progress: 0%");
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

        // 添加到主布局最顶部
        getChildren().add(statsBox);

        ToggleGroup calculationType = new ToggleGroup();
        RadioButton areaButton = new RadioButton("Area");
        areaButton.setStyle(labelStyle);
        RadioButton circumferenceButton = new RadioButton("Circumference");
        circumferenceButton.setStyle(labelStyle);
        areaButton.setToggleGroup(calculationType);
        circumferenceButton.setToggleGroup(calculationType);
        areaButton.setSelected(true);
        isCalculatingArea = true; // 明确设置初始状态

        HBox radioBox = new HBox(20);
        radioBox.setAlignment(Pos.CENTER);
        radioBox.getChildren().addAll(areaButton, circumferenceButton);

        calculationType.selectedToggleProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                isCalculatingArea = (newVal == areaButton);
                showNextCircle();
            }
        });

        canvas = new Canvas(400, 250);
        gc = canvas.getGraphicsContext2D();

        timerLabel = new Label("Time remaining: 3:00");
        timerLabel.setStyle(labelStyle);

        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER);
        answerField = new TextField();
        answerField.setPromptText("Enter your answer");
        answerField.setPrefWidth(100);
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> checkAnswer());
        Label answerLabel = new Label("Answer:");
        answerLabel.setStyle(labelStyle);
        inputBox.getChildren().addAll(answerLabel, answerField, submitButton);

        messageLabel = new Label("");
        messageLabel.setStyle(labelStyle);

        Label instructionsLabel = new Label(
                "Area formula: A = πr² = πd²/4\n" +
                        "Circumference formula: C = 2πr = πd\n" +
                        "Use π = 3.14, round to 2 decimal places");
        instructionsLabel.setStyle(labelStyle);

        Label titleLabel = new Label("Circle Calculations:");
        titleLabel.setStyle(labelStyle);

        getChildren().addAll(
                titleLabel,
                radioBox,
                canvas,
                instructionsLabel,
                timerLabel,
                inputBox,
                messageLabel);

        // 初始化进度显示
        updateProgressDisplay();
    }

    private void updateProgressDisplay() {
        int completedCount = 0;
        if (areaCompleted)
            completedCount++;
        if (circumferenceCompleted)
            completedCount++;

        double progress = (double) completedCount / 2;
        progressBar.setProgress(progress);
        progressLabel.setText("Progress: " + (int) (progress * 100) + "%");
        scoreLabel.setText("Score: " + gameController.getCurrentScore());
    }

    private void showNextCircle() {
        attempts = 0;

        double radius = random.nextInt(20) + 1;
        useDiameter = random.nextBoolean();
        double inputValue = useDiameter ? radius * 2 : radius;

        currentCircle = new Circle(inputValue, useDiameter);

        currentCircle.setPosition(
                (canvas.getWidth() - currentCircle.getWidth()) / 2,
                (canvas.getHeight() - currentCircle.getHeight()) / 2);

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        currentCircle.draw(gc);

        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(14));

        // 显示输入值
        gc.fillText(currentCircle.getInputText(), 10, 20);

        // 在底部显示当前公式
        String formula = isCalculatingArea
                ? currentCircle.getAreaFormulaWithValues()
                : currentCircle.getCircumferenceFormulaWithValues();
        gc.fillText(formula, 10, canvas.getHeight() - 20);

        answerField.clear();
        messageLabel.setText("");
        answerField.requestFocus();

        startTimer();
    }

    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timeRemaining = 180;

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
        AudioPlayer.playEffect("/audio/wrong.wav");
        timer.cancel();
        double correctAnswer = isCalculatingArea ? currentCircle.calculateArea()
                : currentCircle.calculateCircumference();

        // 显示完整公式和答案
        String formula = isCalculatingArea
                ? currentCircle.getAreaFormulaWithValues()
                : currentCircle.getCircumferenceFormulaWithValues();

        messageLabel.setText("Time's up! " + formula + " = " + String.format("%.2f", correctAnswer));
        messageLabel.setTextFill(Color.RED);

        new Thread(() -> {
            try {
                Thread.sleep(2000);
                javafx.application.Platform.runLater(this::showNextCircle);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void checkAnswer() {
        attempts++;
        try {
            double answer = Double.parseDouble(answerField.getText().trim());
            double correctAnswer = isCalculatingArea ? currentCircle.calculateArea()
                    : currentCircle.calculateCircumference();

            // 准备完整的公式和答案
            String formula = isCalculatingArea
                    ? currentCircle.getAreaFormulaWithValues()
                    : currentCircle.getCircumferenceFormulaWithValues();

            // 答案误差范围为0.1
            if (Math.abs(answer - correctAnswer) < 0.1) {
                AudioPlayer.playEffect("/audio/correct.wav");
                timer.cancel();
                messageLabel.setText("Correct! Well done! " + formula + " = " + String.format("%.2f", correctAnswer));
                messageLabel.setTextFill(Color.GREEN);
                gameController.addPoints(attempts, false);

                // 标记计算类型为已完成
                if (isCalculatingArea) {
                    areaCompleted = true;
                } else {
                    circumferenceCompleted = true;
                }

                // 更新进度显示
                updateProgressDisplay();

                // 检查是否已完成所有计算类型
                checkCompletion();

                gc.setFill(Color.BLACK);
                gc.setFont(javafx.scene.text.Font.font(14));
                gc.fillText(formula, 10, canvas.getHeight() - 20);

                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(this::showNextCircle);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                AudioPlayer.playEffect("/audio/wrong.wav");
                if (attempts >= 3) {
                    timer.cancel();
                    messageLabel.setText(
                            "The correct answer is: " + formula + " = " + String.format("%.2f", correctAnswer));
                    messageLabel.setTextFill(Color.RED);

                    new Thread(() -> {
                        try {
                            Thread.sleep(2000);
                            javafx.application.Platform.runLater(this::showNextCircle);
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
            attempts--;
        }
    }

    private void checkCompletion() {
        if (areaCompleted && circumferenceCompleted && !completionAlertShown) {
            completionAlertShown = true;

            // 使用延迟显示完成提示，避免界面更新冲突
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Task Completed");
                        alert.setHeaderText("Great Job!");
                        alert.setContentText("You have completed both area and circumference calculations!");

                        ButtonType returnButton = new ButtonType("Return to Main Menu");
                        ButtonType stayButton = new ButtonType("Stay Here");
                        alert.getButtonTypes().setAll(returnButton, stayButton);

                        alert.showAndWait().ifPresent(response -> {
                            if (response == returnButton) {
                                // 使用回调返回主界面
                                if (onExit != null) {
                                    onExit.run();
                                }
                            }
                        });
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
