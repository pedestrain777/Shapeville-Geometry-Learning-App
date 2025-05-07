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

public class ShapeIdentificationView extends VBox {
    private GameController gameController;
    private Canvas canvas;
    private GraphicsContext gc;
    private TextField answerField;
    private Label messageLabel;
    private Shape currentShape;
    private int attempts;
    private List<Shape> shapes2D;
    private List<Shape> shapes3D;
    private boolean is3DMode = false;
    private ToggleGroup modeToggleGroup;
    private Random random;
    private Label scoreLabel;
    private Label progressLabel;

    public ShapeIdentificationView(GameController gameController) {
        this.gameController = gameController;
        this.random = new Random();
        initializeShapes();
        setupUI();
        showNextShape();
    }

    private void initializeShapes() {
        shapes2D = new ArrayList<>();
        shapes2D.add(new Rectangle(100, 60));
        shapes2D.add(new Square(80));
        shapes2D.add(new Circle(50));
        shapes2D.add(new Triangle(100, 80));
        shapes2D.add(new Oval(100, 60));
        shapes2D.add(new Pentagon(60));
        shapes2D.add(new Hexagon(60));
        shapes2D.add(new Heptagon(60));
        shapes2D.add(new Octagon(60));
        shapes2D.add(new Rhombus(90, 50));
        shapes2D.add(new Kite(90, 50));

        shapes3D = new ArrayList<>();
        shapes3D.add(new Cube(60));
        shapes3D.add(new Cuboid(80, 60, 40));
        shapes3D.add(new Cylinder(40, 80));
        shapes3D.add(new Sphere(50));
        shapes3D.add(new TriangularPrism(60, 40, 80));
        shapes3D.add(new SquarePyramid(60, 80));
        shapes3D.add(new Cone(40, 80));
        shapes3D.add(new Tetrahedron(60));
    }

    private void setupUI() {
        setSpacing(20);
        setPadding(new Insets(20));
        setAlignment(Pos.CENTER);

        // Style for all labels - ensuring black text
        String labelStyle = "-fx-text-fill: black; -fx-font-size: 14px;";

        // Score and progress display
        HBox infoBox = new HBox(20);
        infoBox.setAlignment(Pos.CENTER);
        scoreLabel = new Label("Score: 0");
        scoreLabel.setStyle(labelStyle);
        progressLabel = new Label("Progress: 0%");
        progressLabel.setStyle(labelStyle);
        infoBox.getChildren().addAll(scoreLabel, progressLabel);

        // Mode toggle buttons
        modeToggleGroup = new ToggleGroup();
        RadioButton btn2D = new RadioButton("2D Shapes");
        btn2D.setStyle(labelStyle);
        btn2D.setToggleGroup(modeToggleGroup);
        btn2D.setSelected(true);
        RadioButton btn3D = new RadioButton("3D Shapes");
        btn3D.setStyle(labelStyle);
        btn3D.setToggleGroup(modeToggleGroup);
        HBox modeBox = new HBox(10, btn2D, btn3D);
        modeBox.setAlignment(Pos.CENTER);
        modeToggleGroup.selectedToggleProperty().addListener((obs, old, sel) -> {
            is3DMode = btn3D.isSelected();
            showNextShape();
        });

        // Canvas
        canvas = new Canvas(300, 300);
        gc = canvas.getGraphicsContext2D();

        // Input area
        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER);
        answerField = new TextField();
        answerField.setMaxWidth(200);
        answerField.setPromptText("Enter shape name");
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> checkAnswer());
        inputBox.getChildren().addAll(answerField, submitButton);

        // Message label
        messageLabel = new Label("");
        messageLabel.setStyle(labelStyle);

        // Task label
        Label taskLabel = new Label("Identify the shape:");
        taskLabel.setStyle(labelStyle);

        // Add all components
        getChildren().addAll(
                infoBox,
                modeBox,
                taskLabel,
                canvas,
                inputBox,
                messageLabel);

        // Update score and progress
        updateScoreAndProgress();
    }

    private void updateScoreAndProgress() {
        scoreLabel.setText("Score: " + gameController.getCurrentScore());
        progressLabel.setText("Progress: " + (int) (gameController.getProgress() * 100) + "%");
    }

    private void showNextShape() {
        attempts = 0;
        List<Shape> shapeList = is3DMode ? shapes3D : shapes2D;
        currentShape = shapeList.get(random.nextInt(shapeList.size()));
        currentShape.setPosition(
                (canvas.getWidth() - 100) / 2,
                (canvas.getHeight() - 100) / 2);
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        currentShape.draw(gc);
        answerField.clear();
        messageLabel.setText("");
        answerField.requestFocus();
    }

    private void checkAnswer() {
        attempts++;
        String answer = answerField.getText().trim();
        if (currentShape.isCorrectName(answer)) {
            messageLabel.setText("Correct! Well done!");
            messageLabel.setTextFill(Color.GREEN);
            gameController.addPoints(attempts, is3DMode);
            gameController.taskCompleted(1); // Shape identification is task 1
            updateScoreAndProgress();

            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(this::showNextShape);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            if (attempts >= 3) {
                messageLabel.setText("Correct answer is: " + currentShape.getName());
                messageLabel.setTextFill(Color.RED);
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
    }
}