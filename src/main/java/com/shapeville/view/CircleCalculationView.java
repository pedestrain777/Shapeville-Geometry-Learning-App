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
import com.shapeville.model.Circle;
import javafx.scene.paint.Color;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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
    private boolean useDiameter;  // 新字段


    public CircleCalculationView(GameController gameController) {
        this.gameController = gameController;
        this.random = new Random();
        setupUI();
        showNextCircle();
    }

    private void setupUI() {
        setSpacing(10);
        setPadding(new Insets(10));
        setAlignment(Pos.TOP_CENTER);

        String labelStyle = "-fx-text-fill: black; -fx-font-size: 12px;";

        ToggleGroup calculationType = new ToggleGroup();
        RadioButton areaButton = new RadioButton("Area");
        areaButton.setStyle(labelStyle);
        RadioButton circumferenceButton = new RadioButton("Circumference");
        circumferenceButton.setStyle(labelStyle);
        areaButton.setToggleGroup(calculationType);
        circumferenceButton.setToggleGroup(calculationType);
        areaButton.setSelected(true);

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
                        "Use π = 3.14159");
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
    }

    private void showNextCircle() {
        attempts = 0;

        // 随机生成一个“半径”值，再决定是否用直径显示
        double radius = random.nextInt(20) + 1;
        boolean useDiameter = random.nextBoolean();
        double inputValue = useDiameter ? radius * 2 : radius;

        // 用新的构造器
        currentCircle = new Circle(inputValue, useDiameter);
        currentCircle.setPosition(
                (canvas.getWidth() - currentCircle.getWidth()) / 2,
                (canvas.getHeight() - currentCircle.getHeight()) / 2);

        // 清屏并画圆
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        currentCircle.draw(gc);

        // 显示“Radius = …” 或 “Diameter = …”
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(14));
        gc.fillText(currentCircle.getInputText(), 10, 20);
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
        // 播放答错音效
        AudioPlayer.playEffect("/audio/wrong.wav");
        timer.cancel();
        double correctAnswer = isCalculatingArea ? currentCircle.calculateArea()
                : currentCircle.calculateCircumference();
        messageLabel.setText("Time's up! The correct answer is: " +
                String.format("%.2f", correctAnswer));
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

            if (Math.abs(answer - correctAnswer) < 0.1) {
                // 播放答对音效
                AudioPlayer.playEffect("/audio/correct.wav");
                timer.cancel();
                messageLabel.setText("Correct! Well done!");
                messageLabel.setTextFill(Color.GREEN);
                gameController.addPoints(attempts, false);

                gc.setFill(Color.BLACK);
                gc.setFont(javafx.scene.text.Font.font(14));
                String formula = isCalculatingArea ? currentCircle.getAreaFormulaWithValues()
                        : currentCircle.getCircumferenceFormulaWithValues();
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
                // 播放答错音效
                AudioPlayer.playEffect("/audio/wrong.wav");
                if (attempts >= 3) {
                    timer.cancel();
                    messageLabel.setText("The correct answer is: " + String.format("%.2f", correctAnswer));
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
}
