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
import com.shapeville.model.Angle;
import javafx.scene.paint.Color;

import java.util.Random;

public class AngleIdentificationView extends VBox {
    private GameController gameController;
    private Runnable onExit = null;
    private Canvas canvas;
    private GraphicsContext gc;
    private ComboBox<String> answerBox;
    private Label messageLabel;
    private Angle currentAngle;
    private int attempts;
    private Random random;
    private ComboBox<Integer> angleSelector;

    // 添加进度跟踪
    private Label scoreLabel;
    private Label progressLabel;
    private ProgressBar progressBar;
    private int completedAnglesCount = 0;

    public AngleIdentificationView(GameController gameController) {
        this.gameController = gameController;
        this.random = new Random();
        setupUI();
        showNextAngle();
    }

    // 添加设置返回主界面回调的方法
    public void setOnExit(Runnable onExit) {
        this.onExit = onExit;
    }

    private void setupUI() {
        setSpacing(20);
        setPadding(new Insets(20));
        setAlignment(Pos.CENTER);

        // Common style for labels - ensuring black text
        String labelStyle = "-fx-text-fill: black; -fx-font-size: 14px;";
        String smallLabelStyle = "-fx-text-fill: black; -fx-font-size: 12px;";

        // 添加分数和进度显示区域
        scoreLabel = new Label("Score: 0");
        scoreLabel.setStyle(labelStyle);

        // 创建进度条
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(200);
        progressBar.setPrefHeight(20); // 增加高度使进度条更明显
        progressBar.setMinHeight(20);
        progressBar.setStyle("-fx-accent: #56B4E9; -fx-control-inner-background: #EEEEEE;"); // 增加背景色对比

        progressLabel = new Label("Progress: 0%");
        progressLabel.setStyle(labelStyle);

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

        // Canvas for angle drawing
        canvas = new Canvas(200, 100);
        gc = canvas.getGraphicsContext2D();

        angleSelector = new ComboBox<>();
        angleSelector.setPromptText("Select angle (10-350)");
        for (int i = 10; i < 360; i += 10) {
            angleSelector.getItems().add(i);
        }
        angleSelector.setPrefWidth(150);

        Button setAngleButton = new Button("Set Angle");
        setAngleButton.setOnAction(e -> {
            Integer selectedAngle = angleSelector.getValue();
            if (selectedAngle == null) {
                messageLabel.setText("Please select an angle");
                messageLabel.setTextFill(Color.RED);
                return;
            }
            showAngle(selectedAngle);
        });

        // Answer selection
        answerBox = new ComboBox<>();
        answerBox.getItems().addAll(
                "Acute Angle",
                "Right Angle",
                "Obtuse Angle",
                "Straight Angle",
                "Reflex Angle");
        answerBox.setPromptText("Select angle type");

        // Submit button
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> checkAnswer());

        // Message label
        messageLabel = new Label("");
        messageLabel.setStyle(labelStyle);

        // Title label
        Label titleLabel = new Label("Identify the angle type:");
        titleLabel.setStyle(labelStyle);

        // Instructions
        Label instructionsLabel = new Label(
                "Identify the type of angle shown.\n" +
                        "Remember:\n" +
                        "- Acute: less than 90°\n" +
                        "- Right: exactly 90°\n" +
                        "- Obtuse: between 90° and 180°\n" +
                        "- Straight: exactly 180°\n" +
                        "- Reflex: greater than 180°");
        instructionsLabel.setStyle(smallLabelStyle);

        Label angleSelectLabel = new Label("Set custom angle (optional):");
        angleSelectLabel.setStyle(labelStyle);

        HBox angleSelectionBox = new HBox(10);
        angleSelectionBox.setAlignment(Pos.CENTER);
        angleSelectionBox.getChildren().addAll(angleSelector, setAngleButton);

        getChildren().addAll(
                titleLabel,
                canvas,
                angleSelectLabel,
                angleSelectionBox,
                instructionsLabel,
                answerBox,
                submitButton,
                messageLabel);

        // 初始化进度显示
        updateScoreAndProgress();
    }

    // 添加更新分数和进度的方法
    private void updateScoreAndProgress() {
        double progress = Math.min((double) completedAnglesCount / 5, 1.0);
        progressBar.setProgress(progress);
        progressLabel.setText("Progress: " + (int) (progress * 100) + "%");
        scoreLabel.setText("Score: " + gameController.getCurrentScore());
    }

    private void showNextAngle() {
        // 检查是否已完成所有5种角度
        if (completedAnglesCount >= 5) {
            showCompletionDialog();
            return;
        }

        int angleValue = (random.nextInt(35) + 1) * 10;
        showAngle(angleValue);
    }

    // 添加完成对话框
    private void showCompletionDialog() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Congratulations!");
            alert.setHeaderText("You've identified all 5 angle types!");
            alert.setContentText("Do you want to continue or return to main menu?");

            ButtonType continueButton = new ButtonType("Return to Main Menu");
            ButtonType restartButton = new ButtonType("Restart");

            alert.getButtonTypes().setAll(continueButton, restartButton);

            alert.showAndWait().ifPresent(response -> {
                if (response == continueButton) {
                    // 返回主界面
                    if (onExit != null) {
                        onExit.run();
                    }
                } else {
                    // 重新开始
                    restartGame();
                }
            });
        });
    }

    // 添加重新开始游戏的方法
    private void restartGame() {
        gameController.reset();
        completedAnglesCount = 0;
        updateScoreAndProgress();
        showNextAngle();
    }

    private void showAngle(int angleValue) {
        if (angleValue <= 0 || angleValue >= 360) {
            messageLabel.setText("Angle must be between 0 and 360 degrees (excluding 0 and 360)");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        attempts = 0;
        currentAngle = new Angle(angleValue);
        currentAngle.setPosition(canvas.getWidth() / 2, canvas.getHeight() / 2);

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        currentAngle.draw(gc);

        answerBox.setValue(null);
        messageLabel.setText("");
        answerBox.requestFocus();
    }

    private void checkAnswer() {
        attempts++;
        String answer = answerBox.getValue();
        if (answer == null) {
            messageLabel.setText("Please select an angle type");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        // Convert answer to enum format
        String enumFormat;
        switch (answer) {
            case "Acute Angle":
                enumFormat = "ACUTE";
                break;
            case "Right Angle":
                enumFormat = "RIGHT";
                break;
            case "Obtuse Angle":
                enumFormat = "OBTUSE";
                break;
            case "Straight Angle":
                enumFormat = "STRAIGHT";
                break;
            case "Reflex Angle":
                enumFormat = "REFLEX";
                break;
            default:
                // 保底——如果你以后加了新类型
                enumFormat = answer.toUpperCase().replace(" ", "_");
        }

        if (currentAngle.isCorrectType(enumFormat)) {
            // 播放答对音效
            AudioPlayer.playEffect("/audio/correct.wav");

            messageLabel.setText("Correct! Well done!");
            messageLabel.setTextFill(Color.GREEN);
            gameController.addPoints(attempts, false);
            gameController.taskCompleted(1);

            // 增加完成角度计数
            completedAnglesCount++;
            updateScoreAndProgress();

            // Show next angle after a delay
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(this::showNextAngle);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {

            // 播放答错音效
            AudioPlayer.playEffect("/audio/wrong.wav");
            if (attempts >= 3) {
                String correctDisplay;
                switch (currentAngle.getType()) {
                    case ACUTE:
                        correctDisplay = "Acute Angle";
                        break;
                    case RIGHT:
                        correctDisplay = "Right Angle";
                        break;
                    case OBTUSE:
                        correctDisplay = "Obtuse Angle";
                        break;
                    case STRAIGHT:
                        correctDisplay = "Straight Angle";
                        break;
                    case REFLEX:
                        correctDisplay = "Reflex Angle";
                        break;
                    default:
                        correctDisplay = currentAngle.getType().toString();
                }
                messageLabel.setText("The correct answer is: " + correctDisplay +
                        " (" + currentAngle.getTypeDescription() + ")");
                messageLabel.setTextFill(Color.RED);

                // Show next angle after a delay
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(this::showNextAngle);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                messageLabel.setText("Try again! Attempt " + attempts + " of 3");
                messageLabel.setTextFill(Color.RED);
                answerBox.setValue(null);
                answerBox.requestFocus();
            }
        }
    }
}
