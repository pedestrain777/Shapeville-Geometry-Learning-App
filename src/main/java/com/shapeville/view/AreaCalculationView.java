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

    public AreaCalculationView(GameController gameController) {
        this.gameController = gameController;
        this.random = new Random();
        setupUI();
        showNextShape();
    }

    private void setupUI() {
        setSpacing(10);
        setPadding(new Insets(10));
        setAlignment(Pos.TOP_CENTER);

        // Style for all labels - ensuring black text
        String labelStyle = "-fx-text-fill: black; -fx-font-size: 12px;";

        // Shape selection with reduced size
        HBox selectorBox = new HBox(10);
        selectorBox.setAlignment(Pos.CENTER);
        Label selectLabel = new Label("Shape:");
        selectLabel.setStyle(labelStyle);
        ComboBox<String> shapeSelector = new ComboBox<>();
        shapeSelector.getItems().addAll(
                "Rectangle",
                "Triangle",
                "Parallelogram",
                "Trapezium");
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

        // Timer label with smaller style
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

        // Message label
        messageLabel = new Label("");
        messageLabel.setStyle(labelStyle);

        // Title with smaller padding
        Label titleLabel = new Label("Calculate the area:");
        titleLabel.setStyle(labelStyle);
        titleLabel.setPadding(new Insets(0, 0, 5, 0));

        getChildren().addAll(
                titleLabel,
                selectorBox,
                canvas,
                timerLabel,
                inputBox,
                messageLabel);
    }

    private void generateShape(String shapeType) {
        // Generate random dimensions between 1 and 20
        double dim1 = random.nextInt(20) + 1;
        double dim2 = random.nextInt(20) + 1;

        currentShape = switch (shapeType) {
            case "Rectangle" -> new Rectangle(dim1, dim2);
            case "Triangle" -> new Triangle(dim1, dim2);
            // Add other shapes here
            default -> new Rectangle(dim1, dim2);
        };

        currentShape.setPosition(
                (canvas.getWidth() - currentShape.getWidth()) / 2,
                (canvas.getHeight() - currentShape.getHeight()) / 2);

        // Clear canvas and draw new shape
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        currentShape.draw(gc);

        // Reset attempts and start timer
        attempts = 0;
        startTimer();
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
        timer.cancel();
        messageLabel.setText("Time's up! The correct area is: " +
                String.format("%.1f", currentShape.calculateArea()));
        messageLabel.setTextFill(Color.RED);

        // Show next shape after a delay
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                javafx.application.Platform.runLater(this::showNextShape);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showNextShape() {
        attempts = 0;
        answerField.clear();
        messageLabel.setText("");
        if (timer != null) {
            timer.cancel();
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
                timer.cancel();
                messageLabel.setText("Correct! Well done!");
                messageLabel.setTextFill(Color.GREEN);
                gameController.addPoints(attempts, false);

                // Show formula
                gc.setFill(Color.BLACK);
                gc.setFont(javafx.scene.text.Font.font(14));
                if (currentShape instanceof Rectangle) {
                    gc.fillText(((Rectangle) currentShape).getFormulaWithValues(), 10, canvas.getHeight() - 20);
                }
                // Add other shape formulas here

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
                    timer.cancel();
                    messageLabel.setText("The correct area is: " + String.format("%.1f", correctAnswer));
                    messageLabel.setTextFill(Color.RED);

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