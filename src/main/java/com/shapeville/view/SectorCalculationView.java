package com.shapeville.view;

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

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class SectorCalculationView extends VBox {
    private GameController gameController;
    private Canvas canvas;
    private GraphicsContext gc;
    private TextField answerField;
    private Label messageLabel;
    private Label timerLabel;
    private double currentRadius;
    private double currentAngle;
    private int attempts;
    private Random random;
    private Timer timer;
    private int timeRemaining;
    private boolean isCalculatingArea;

    public SectorCalculationView(GameController gameController) {
        this.gameController = gameController;
        this.random = new Random();
        setupUI();
        showNextSector();
    }

    private void setupUI() {
        setSpacing(20);
        setPadding(new Insets(20));
        setAlignment(Pos.CENTER);

        // Calculation type selection
        ToggleGroup calculationType = new ToggleGroup();
        RadioButton areaButton = new RadioButton("Sector Area");
        RadioButton arcButton = new RadioButton("Arc Length");
        areaButton.setToggleGroup(calculationType);
        arcButton.setToggleGroup(calculationType);
        areaButton.setSelected(true);

        HBox radioBox = new HBox(20);
        radioBox.setAlignment(Pos.CENTER);
        radioBox.getChildren().addAll(areaButton, arcButton);

        calculationType.selectedToggleProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                isCalculatingArea = (newVal == areaButton);
                showNextSector();
            }
        });

        // Canvas for sector drawing
        canvas = new Canvas(400, 400);
        gc = canvas.getGraphicsContext2D();

        // Timer label
        timerLabel = new Label("Time remaining: 5:00");

        // Input area
        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER);
        answerField = new TextField();
        answerField.setPromptText("Enter your answer");
        answerField.setPrefWidth(100);
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> checkAnswer());
        inputBox.getChildren().addAll(new Label("Answer:"), answerField, submitButton);

        // Message label
        messageLabel = new Label("");
        messageLabel.setStyle("-fx-font-size: 14px;");

        // Instructions
        Label instructionsLabel = new Label(
                "Area of sector = (θ/360°) × πr²\n" +
                        "Length of arc = (θ/360°) × 2πr\n" +
                        "where θ is the angle in degrees");
        instructionsLabel.setStyle("-fx-font-size: 12px;");

        getChildren().addAll(
                new Label("Circle Sector Calculations:"),
                radioBox,
                canvas,
                instructionsLabel,
                timerLabel,
                inputBox,
                messageLabel);
    }

    private void showNextSector() {
        attempts = 0;
        // Generate random radius and angle
        currentRadius = random.nextInt(15) + 5; // radius between 5 and 20
        currentAngle = (random.nextInt(27) + 3) * 10; // angle between 30 and 300 in steps of 10

        drawSector();

        // Reset UI
        answerField.clear();
        messageLabel.setText("");
        answerField.requestFocus();

        startTimer();
    }

    private void drawSector() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2;
        double radius = currentRadius * 10; // Scale up for display

        // Draw full circle outline
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(1);
        gc.strokeOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

        // Draw sector
        gc.setFill(Color.LIGHTBLUE);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(2);

        // Draw arc
        gc.strokeArc(centerX - radius, centerY - radius, radius * 2, radius * 2,
                0, -currentAngle, ArcType.ROUND);

        // Draw radii
        gc.strokeLine(centerX, centerY,
                centerX + radius * Math.cos(Math.toRadians(0)),
                centerY - radius * Math.sin(Math.toRadians(0)));
        gc.strokeLine(centerX, centerY,
                centerX + radius * Math.cos(Math.toRadians(currentAngle)),
                centerY - radius * Math.sin(Math.toRadians(currentAngle)));

        // Label dimensions
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(14));
        gc.fillText("r = " + currentRadius + " units", 10, 20);
        gc.fillText("θ = " + currentAngle + "°", 10, 40);
    }

    private double calculateSectorArea() {
        return (currentAngle / 360.0) * Math.PI * currentRadius * currentRadius;
    }

    private double calculateArcLength() {
        return (currentAngle / 360.0) * 2 * Math.PI * currentRadius;
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
        timer.cancel();
        double correctAnswer = isCalculatingArea ? calculateSectorArea() : calculateArcLength();
        messageLabel.setText("Time's up! The correct answer is: " +
                String.format("%.2f", correctAnswer));
        messageLabel.setTextFill(Color.RED);

        showSolution();
    }

    private void showSolution() {
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(14));
        double y = canvas.getHeight() - 60;

        if (isCalculatingArea) {
            gc.fillText(String.format("Area = (%.0f/360°) × π × %.1f² = %.2f",
                    currentAngle, currentRadius, calculateSectorArea()), 10, y);
        } else {
            gc.fillText(String.format("Arc Length = (%.0f/360°) × 2π × %.1f = %.2f",
                    currentAngle, currentRadius, calculateArcLength()), 10, y);
        }
    }

    private void checkAnswer() {
        attempts++;
        try {
            double answer = Double.parseDouble(answerField.getText().trim());
            double correctAnswer = isCalculatingArea ? calculateSectorArea() : calculateArcLength();

            // Allow for small rounding differences
            if (Math.abs(answer - correctAnswer) < 0.1) {
                timer.cancel();
                messageLabel.setText("Correct! Well done!");
                messageLabel.setTextFill(Color.GREEN);
                gameController.addPoints(attempts, true); // Advanced level scoring

                showSolution();

                // Show next sector after a delay
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(this::showNextSector);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                if (attempts >= 3) {
                    timer.cancel();
                    messageLabel.setText("The correct answer is: " + String.format("%.2f", correctAnswer));
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