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
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class SectorCalculationView extends VBox {
    private GameController gameController;
    private Canvas canvas;
    private GraphicsContext gc;
    private TextField answerField;
    private Label messageLabel;
    private Label timerLabel;
    private Label progressLabel;
    private int attempts;
    private Timer timer;
    private int timeRemaining;
    private boolean isCalculatingArea = true; // 默认计算面积
    private ComboBox<String> sectorSelector;
    private int currentSectorIndex = -1;
    private Set<Integer> completedSectors = new HashSet<>();

    // 进度跟踪和回调
    private Runnable onExit = null;
    private ProgressBar progressBar;
    private Label scoreLabel;
    private boolean completionAlertShown = false;

    // 预设的8个扇形选项
    private static final int SECTOR_COUNT = 8;
    private static final double[] SECTOR_RADII = { 8.0, 18.0, 18.0, 22.0, 3.5, 8.0, 12.0, 15.0 };
    private static final double[] SECTOR_ANGLES = { 90.0, 130.0, 240.0, 110.0, 100.0, 270.0, 280.0, 250.0 };
    private static final String[] SECTOR_UNITS = { "cm", "ft", "cm", "ft", "m", "in", "yd", "mm" };
    private static final String[] SECTOR_NAMES = {
            "1: Circle Sector (r=8cm, θ=90°)",
            "2: Circle Sector (r=18ft, θ=130°)",
            "3: Circle Sector (r=18cm, θ=240°)",
            "4: Circle Sector (r=22ft, θ=110°)",
            "5: Circle Sector (r=3.5m, θ=100°)",
            "6: Circle Sector (r=8in, θ=270°)",
            "7: Circle Sector (r=12yd, θ=280°)",
            "8: Circle Sector (r=15mm, θ=250°)"
    };

    // 添加固定的π值为3.14
    private static final double PI = 3.14;

    public SectorCalculationView(GameController gameController) {
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
        String labelStyle = "-fx-text-fill: black; -fx-font-size: 12px;";

        // 标题
        Label titleLabel = new Label("Circle Sector Calculations");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black;");

        // 添加分数和进度显示区域
        scoreLabel = new Label("Score: 0");
        scoreLabel.setStyle("-fx-text-fill: black; -fx-font-size: 14px;");

        // 创建进度条
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(200);
        progressBar.setPrefHeight(20);
        progressBar.setMinHeight(20);
        progressBar.setStyle("-fx-accent: #56B4E9; -fx-control-inner-background: #EEEEEE;");

        progressLabel = new Label("Progress: 0/8 sectors completed");
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

        // Sector selection
        HBox selectorBox = new HBox(10);
        selectorBox.setAlignment(Pos.CENTER);
        sectorSelector = new ComboBox<>();
        sectorSelector.getItems().addAll(SECTOR_NAMES);
        sectorSelector.setPromptText("Select a sector option");
        sectorSelector.setPrefWidth(250);
        sectorSelector.setOnAction(e -> {
            if (sectorSelector.getValue() != null) {
                // 在选择新扇形时清除消息
                messageLabel.setText("");

                currentSectorIndex = sectorSelector.getSelectionModel().getSelectedIndex();
                showSelectedSector(currentSectorIndex);
            }
        });
        selectorBox.getChildren().addAll(new Label("Select sector:"), sectorSelector);

        // Calculation type selection
        ToggleGroup calculationType = new ToggleGroup();
        RadioButton areaButton = new RadioButton("Sector Area");
        areaButton.setStyle(labelStyle);
        RadioButton arcButton = new RadioButton("Arc Length");
        arcButton.setStyle(labelStyle);
        areaButton.setToggleGroup(calculationType);
        arcButton.setToggleGroup(calculationType);
        areaButton.setSelected(true);

        HBox radioBox = new HBox(20);
        radioBox.setAlignment(Pos.CENTER);
        radioBox.getChildren().addAll(areaButton, arcButton);

        calculationType.selectedToggleProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                isCalculatingArea = (newVal == areaButton);
                if (currentSectorIndex >= 0) {
                    showSelectedSector(currentSectorIndex);
                }
            }
        });

        // Canvas
        canvas = new Canvas(400, 400);
        gc = canvas.getGraphicsContext2D();
        // 初始提示
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(16));
        gc.fillText("Please select a sector option from the dropdown menu", 40, 200);

        // Timer label
        timerLabel = new Label("Time remaining: 5:00");
        timerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");

        // Input area
        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER);
        answerField = new TextField();
        answerField.setPromptText("Enter your answer");
        answerField.setPrefWidth(100);
        // 根据currentSectorIndex判断是否禁用输入框
        answerField.setDisable(currentSectorIndex == -1);
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> checkAnswer());
        inputBox.getChildren().addAll(new Label("Answer:"), answerField, submitButton);

        // Instructions
        Label instructionsLabel = new Label(
                "Area of sector = (θ/360°) × πr²      " +
                        "Length of arc = (θ/360°) × 2πr");
        instructionsLabel.setStyle(labelStyle);

        // 将π值提示放在单独的标签中，使其更加明显
        Label piLabel = new Label("Use π = 3.14, round to 2 decimal places");
        piLabel.setStyle("-fx-text-fill: black; -fx-font-size: 14px; -fx-font-weight: bold;");

        // Message label
        messageLabel = new Label("");
        messageLabel.setStyle("-fx-font-size: 14px;");

        getChildren().addAll(
                titleLabel,
                statsBox,
                selectorBox,
                radioBox,
                canvas,
                instructionsLabel,
                piLabel, // 添加新的π值提示标签
                timerLabel,
                inputBox,
                messageLabel);

        // 更新进度显示
        sectorSelector.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateProgressLabel();
        });

        // 监听currentSectorIndex，控制输入框的禁用状态
        sectorSelector.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                currentSectorIndex = newVal.intValue();
                answerField.setDisable(currentSectorIndex == -1);
            }
        });
    }

    private void updateProgressLabel() {
        // 更新进度标签和进度条
        int completedCount = completedSectors.size();
        double progress = (double) completedCount / SECTOR_COUNT;
        progressBar.setProgress(progress);
        progressLabel.setText(String.format("Progress: %d/%d sectors completed", completedCount, SECTOR_COUNT));

        // 更新分数显示
        scoreLabel.setText("Score: " + gameController.getCurrentScore());

        // 当所有扇形都完成后，提示用户
        if (completedCount >= SECTOR_COUNT && !completionAlertShown) {
            completionAlertShown = true;

            // 标记关卡完成
            gameController.taskCompleted(6);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Task Completed");
            alert.setHeaderText("Great Job!");
            alert.setContentText("You have completed all sector calculations!");

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

    private void showSelectedSector(int index) {
        if (index < 0 || index >= SECTOR_COUNT) {
            return;
        }

        attempts = 0;
        answerField.clear();
        messageLabel.setText("");

        // 绘制选定的扇形
        drawSector(index);

        // 启动计时器
        startTimer();
    }

    private void drawSector(int index) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2;

        double radius = SECTOR_RADII[index];
        double angle = SECTOR_ANGLES[index];
        String unit = SECTOR_UNITS[index];

        // 将半径缩放到画布上的合适大小（像素）
        double displayRadius = normalizeRadius(radius, unit);

        // 1) 画全圆轮廓
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(1);
        gc.strokeOval(centerX - displayRadius, centerY - displayRadius,
                displayRadius * 2, displayRadius * 2);

        // 2) 画扇形
        gc.setFill(Color.LIGHTBLUE);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(2);
        gc.fillArc(centerX - displayRadius, centerY - displayRadius,
                displayRadius * 2, displayRadius * 2,
                0, -angle, ArcType.ROUND);

        // 3) 画弧线标记
        double markerRadius = displayRadius * 0.3;
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        gc.strokeArc(centerX - markerRadius, centerY - markerRadius,
                markerRadius * 2, markerRadius * 2,
                0, -angle, ArcType.OPEN);

        // 4) 在扇形内部居中显示角度文字
        double midAngleRad = Math.toRadians(angle / 2);
        double textRadius = displayRadius * 0.5;
        double textX = centerX + textRadius * Math.cos(midAngleRad);
        double textY = centerY - textRadius * Math.sin(midAngleRad);
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(16));
        gc.fillText(String.format("%.0f°", angle), textX - 15, textY + 5);

        // 5) 显示半径文字
        double radiusTextX = centerX + (displayRadius / 2) * Math.cos(Math.toRadians(angle / 4));
        double radiusTextY = centerY - (displayRadius / 2) * Math.sin(Math.toRadians(angle / 4));
        gc.setFill(Color.BLACK);
        gc.fillText(String.format("%.1f %s", radius, unit), radiusTextX - 20, radiusTextY);

        // 6) 标题和任务说明
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(14));
        String calculation = isCalculatingArea ? "Area" : "Arc Length";
        gc.fillText("Calculate the " + calculation + " of the sector shown:", 10, 20);
    }

    // 根据不同的单位将半径标准化为合适的像素大小
    private double normalizeRadius(double radius, String unit) {
        double baseSize = 120; // 基础显示大小，以便在画布上看起来合适

        switch (unit) {
            case "mm":
                return baseSize * 0.5; // 很小的单位，缩小显示
            case "cm":
                return baseSize * 0.8;
            case "in":
                return baseSize * 0.9;
            case "m":
                return baseSize * 1.0;
            case "ft":
                return baseSize * 0.9;
            case "yd":
                return baseSize * 0.8;
            default:
                return baseSize;
        }
    }

    private double calculateSectorArea(int index) {
        if (index < 0 || index >= SECTOR_COUNT) {
            return 0;
        }
        double radius = SECTOR_RADII[index];
        double angle = SECTOR_ANGLES[index];
        // 使用固定的π值为3.14
        return (angle / 360.0) * PI * radius * radius;
    }

    private double calculateArcLength(int index) {
        if (index < 0 || index >= SECTOR_COUNT) {
            return 0;
        }
        double radius = SECTOR_RADII[index];
        double angle = SECTOR_ANGLES[index];
        // 使用固定的π值为3.14
        return (angle / 360.0) * 2 * PI * radius;
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
        // 播放答错音效
        AudioPlayer.playEffect("/audio/wrong.wav");
        timer.cancel();
        double correctAnswer = isCalculatingArea ? calculateSectorArea(currentSectorIndex)
                : calculateArcLength(currentSectorIndex);
        messageLabel.setText("Time's up! The correct answer is: " +
                String.format("%.2f", correctAnswer) + " " + SECTOR_UNITS[currentSectorIndex] +
                (isCalculatingArea ? "²" : ""));
        messageLabel.setTextFill(Color.RED);

        showSolution();
    }

    private void showSolution() {
        // 重绘扇形
        drawSector(currentSectorIndex);

        // 在底部显示计算过程
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(14));
        double y = canvas.getHeight() - 60;
        double radius = SECTOR_RADII[currentSectorIndex];
        double angle = SECTOR_ANGLES[currentSectorIndex];
        String unit = SECTOR_UNITS[currentSectorIndex];

        if (isCalculatingArea) {
            gc.fillText(String.format("Area = (%.0f/360°) × %.2f × %.1f² %s² = %.2f %s²",
                    angle, PI, radius, unit, calculateSectorArea(currentSectorIndex), unit), 10, y);
        } else {
            gc.fillText(String.format("Arc Length = (%.0f/360°) × 2 × %.2f × %.1f %s = %.2f %s",
                    angle, PI, radius, unit, calculateArcLength(currentSectorIndex), unit), 10, y);
        }

        // 记录此扇形已完成
        if (currentSectorIndex >= 0 && currentSectorIndex < SECTOR_COUNT) {
            if (!completedSectors.contains(currentSectorIndex)) {
                completedSectors.add(currentSectorIndex);
                updateProgressLabel();
            }
        }
    }

    private void showNextSector() {
        attempts = 0;
        answerField.clear();
        messageLabel.setText("");
        if (timer != null) {
            timer.cancel();
        }

        // 保存当前选择的扇形名称（如果有）
        String currentSectorName = sectorSelector.getValue();

        // 如果已完成所有扇形，但没有显示完成对话框，则通过更新进度标签显示完成对话框
        if (completedSectors.size() >= SECTOR_COUNT && !completionAlertShown) {
            updateProgressLabel();
            return;
        } else if (completionAlertShown) {
            // 如果已经显示过完成对话框，只清空当前选择
            sectorSelector.getSelectionModel().clearSelection();
            currentSectorIndex = -1; // 重置索引
            answerField.setDisable(true); // 禁用输入框
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setFill(Color.BLACK);
            gc.setFont(javafx.scene.text.Font.font(16));
            gc.fillText("Please select a sector option from the dropdown menu", 40, 200);
        } else {
            // 重新填充下拉菜单，排除已完成的扇形
            sectorSelector.getItems().clear();

            // 添加未完成的扇形到下拉菜单
            for (int i = 0; i < SECTOR_COUNT; i++) {
                if (!completedSectors.contains(i)) {
                    sectorSelector.getItems().add(SECTOR_NAMES[i]);
                }
            }

            // 清空当前选择
            sectorSelector.getSelectionModel().clearSelection();
            currentSectorIndex = -1; // 重置索引
            answerField.setDisable(true); // 禁用输入框

            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setFill(Color.BLACK);
            gc.setFont(javafx.scene.text.Font.font(16));
            gc.fillText("Please select a sector option from the dropdown menu", 40, 200);
        }
    }

    private void checkAnswer() {
        // 检查是否选择了扇形(根据索引)
        if (currentSectorIndex == -1) {
            messageLabel.setText("Please select a sector first");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        attempts++;
        try {
            double answer = Double.parseDouble(answerField.getText().trim());
            double correctAnswer = isCalculatingArea ? calculateSectorArea(currentSectorIndex)
                    : calculateArcLength(currentSectorIndex);
            String unit = SECTOR_UNITS[currentSectorIndex];

            // Allow for small rounding differences (0.1误差范围)
            if (Math.abs(answer - correctAnswer) < 0.1) {
                // 播放答对音效
                AudioPlayer.playEffect("/audio/correct.wav");
                timer.cancel();
                messageLabel.setText("Correct! Well done! The answer is: " +
                        String.format("%.2f", correctAnswer) + " " + unit +
                        (isCalculatingArea ? "²" : ""));
                messageLabel.setTextFill(Color.GREEN);
                gameController.addPoints(attempts, true); // Advanced level scoring

                // 更新分数显示
                scoreLabel.setText("Score: " + gameController.getCurrentScore());

                showSolution();

                // Show next shape after a delay
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(this::showNextSector);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                // 播放答错音效
                AudioPlayer.playEffect("/audio/wrong.wav");
                if (attempts >= 3) {
                    timer.cancel();
                    messageLabel.setText("The correct answer is: " +
                            String.format("%.2f", correctAnswer) + " " + unit +
                            (isCalculatingArea ? "²" : ""));
                    messageLabel.setTextFill(Color.RED);

                    showSolution();

                    // Show next sector after a delay
                    new Thread(() -> {
                        try {
                            Thread.sleep(3000);
                            javafx.application.Platform.runLater(this::showNextSector);
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
