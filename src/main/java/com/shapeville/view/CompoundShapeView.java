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

public class CompoundShapeView extends VBox {
    private GameController gameController;
    private Canvas canvas;
    private GraphicsContext gc;
    private TextField answerField;
    private Label messageLabel;
    private Label timerLabel;
    private List<Shape2D> currentShapes;
    private int attempts;
    private Timer timer;
    private int timeRemaining;
    private int currentShapeIndex;

    public CompoundShapeView(GameController gameController) {
        this.gameController = gameController;
        this.random = new Random();
        this.currentShapes = new ArrayList<>();
        setupUI();
        showNextShape();
    }

    private void setupUI() {
        setSpacing(20);
        setPadding(new Insets(20));
        setAlignment(Pos.CENTER);

        // Shape selection
        ComboBox<String> shapeSelector = new ComboBox<>();
        shapeSelector.getItems().addAll(
                "Shape 1", "Shape 2", "Shape 3", "Shape 4",
                "Shape 5", "Shape 6", "Shape 7", "Shape 8", "Shape 9");
        shapeSelector.setPromptText("Select a compound shape");
        shapeSelector.setOnAction(e -> {
            if (shapeSelector.getValue() != null) {
                currentShapeIndex = shapeSelector.getSelectionModel().getSelectedIndex();
                generateCompoundShape(currentShapeIndex);
            }
        });

        // Canvas for shape drawing
        canvas = new Canvas(500, 400);
        gc = canvas.getGraphicsContext2D();

        // Timer label
        timerLabel = new Label("Time remaining: 5:00");

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

        getChildren().addAll(
                new Label("Calculate the total area of the compound shape:"),
                shapeSelector,
                canvas,
                timerLabel,
                inputBox,
                messageLabel);
    }

    private void generateCompoundShape(int index) {
        currentShapes.clear();
        // Example of compound shape generation (you'll need to implement all 9 shapes)
        switch (index) {
            case 0 -> {
                // Rectangle with triangle on top (house shape)
                Rectangle base = new Rectangle(14, 14);
                Triangle roof = new Triangle(14, 5);
                currentShapes.add(base);
                currentShapes.add(roof);
            }
            // Add other cases for different compound shapes
            default -> {
                Rectangle defaultShape = new Rectangle(10, 10);
                currentShapes.add(defaultShape);
            }
        }

        drawCompoundShape();
        startTimer();
    }

    private void drawCompoundShape() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Calculate total bounds
        double totalWidth = 0;
        double totalHeight = 0;
        for (Shape2D shape : currentShapes) {
            totalWidth = Math.max(totalWidth, shape.getWidth());
            totalHeight += shape.getHeight();
        }

        // Center the compound shape
        double startX = (canvas.getWidth() - totalWidth) / 2;
        double startY = (canvas.getHeight() - totalHeight) / 2;

        double currentY = startY;
        for (Shape2D shape : currentShapes) {
            shape.setPosition(startX, currentY);
            shape.draw(gc);
            currentY += shape.getHeight();
        }

        // Draw dimensions
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        // Add dimension lines and measurements here
    }

    private double calculateTotalArea() {
        return currentShapes.stream()
                .mapToDouble(Shape2D::calculateArea)
                .sum();
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
        messageLabel.setText("Time's up! The correct area is: " +
                String.format("%.2f", calculateTotalArea()));
        messageLabel.setTextFill(Color.RED);

        showSolution();
    }

    private void showSolution() {
        // Draw solution steps on canvas
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(14));
        double y = 20;
        double totalArea = 0;

        for (Shape2D shape : currentShapes) {
            String areaText = String.format("%s area: %.2f",
                    shape.getClass().getSimpleName(),
                    shape.calculateArea());
            gc.fillText(areaText, 10, y);
            totalArea += shape.calculateArea();
            y += 20;
        }

        gc.fillText(String.format("Total area: %.2f", totalArea), 10, y + 20);
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
                timer.cancel();
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
                    timer.cancel();
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

    private void showNextShape() {
        attempts = 0;
        answerField.clear();
        messageLabel.setText("");
        if (timer != null) {
            timer.cancel();
        }
        currentShapes.clear();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}