package com.shapeville.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import com.shapeville.controller.GameController;
import com.shapeville.model.Angle;
import javafx.scene.paint.Color;

import java.util.Random;

public class AngleIdentificationView extends VBox {
    private GameController gameController;
    private Canvas canvas;
    private GraphicsContext gc;
    private ComboBox<String> answerBox;
    private Label messageLabel;
    private Angle currentAngle;
    private int attempts;
    private Random random;
    private TextField angleInputField;

    public AngleIdentificationView(GameController gameController) {
        this.gameController = gameController;
        this.random = new Random();
        setupUI();
        showNextAngle();
    }
    private void setupUI() {
        setSpacing(20);
        setPadding(new Insets(20));
        setAlignment(Pos.CENTER);

        // Common style for labels - ensuring black text
        String labelStyle = "-fx-text-fill: black; -fx-font-size: 14px;";
        String smallLabelStyle = "-fx-text-fill: black; -fx-font-size: 12px;";

        // Canvas for angle drawing
        canvas = new Canvas(200, 100);
        gc = canvas.getGraphicsContext2D();


        angleInputField = new TextField();
        angleInputField.setPromptText("Enter angle (0-360)");
        angleInputField.setMaxWidth(150);


        Button setAngleButton = new Button("Set Angle");
        setAngleButton.setOnAction(e -> {
            try {
                int customAngle = Integer.parseInt(angleInputField.getText());
                if (customAngle < 0 || customAngle > 360) {
                    messageLabel.setText("Angle must be between 0 and 360.");
                    messageLabel.setTextFill(Color.RED);
                    return;
                }
                showAngle(customAngle);
            } catch (NumberFormatException ex) {
                messageLabel.setText("Please enter a valid number.");
                messageLabel.setTextFill(Color.RED);
            }
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
        getChildren().addAll(
                titleLabel,
                canvas,
                angleInputField,
                setAngleButton,
                instructionsLabel,
                answerBox,
                submitButton,
                messageLabel);
    }

private void showNextAngle() {
    int angleValue = 0;
    showAngle(angleValue);
}

    private void showAngle(int angleValue) {
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
            case "Acute Angle":    enumFormat = "ACUTE";    break;
            case "Right Angle":    enumFormat = "RIGHT";    break;
            case "Obtuse Angle":   enumFormat = "OBTUSE";   break;
            case "Straight Angle": enumFormat = "STRAIGHT"; break;
            case "Reflex Angle":   enumFormat = "REFLEX";   break;
            default:
                // 保底——如果你以后加了新类型
                enumFormat = answer.toUpperCase().replace(" ", "_");
        }

        if (currentAngle.isCorrectType(enumFormat)) {
            messageLabel.setText("Correct! Well done!");
            messageLabel.setTextFill(Color.GREEN);
            gameController.addPoints(attempts, false);

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
            if (attempts >= 3) {
                String correctDisplay;
                switch (currentAngle.getType()) {
                    case ACUTE:    correctDisplay = "Acute Angle";    break;
                    case RIGHT:    correctDisplay = "Right Angle";    break;
                    case OBTUSE:   correctDisplay = "Obtuse Angle";   break;
                    case STRAIGHT: correctDisplay = "Straight Angle"; break;
                    case REFLEX:   correctDisplay = "Reflex Angle";   break;
                    default:       correctDisplay = currentAngle.getType().toString();
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
