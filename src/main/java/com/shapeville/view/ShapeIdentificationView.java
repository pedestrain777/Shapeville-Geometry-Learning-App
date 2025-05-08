package com.shapeville.view;

import javafx.application.Platform;
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
import java.util.function.Consumer;

public class ShapeIdentificationView extends VBox {
    private final GameController gameController;
    private Runnable onExit = null;
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
    private Slider rotationSliderY;
    private Slider rotationSliderX;
    private int completed2DCount = 0;
    private int completed3DCount = 0;
    private Button restartButton;

    public ShapeIdentificationView(GameController gameController) {
        this.gameController = gameController;
        this.onExit = onExit;
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

        String labelStyle = "-fx-text-fill: black; -fx-font-size: 14px;";

        HBox infoBox = new HBox(20);
        infoBox.setAlignment(Pos.CENTER);
        scoreLabel = new Label("Score: 0");
        scoreLabel.setStyle(labelStyle);
        progressLabel = new Label("Progress: 0%");
        progressLabel.setStyle(labelStyle);
        infoBox.getChildren().addAll(scoreLabel, progressLabel);

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
            completed2DCount = 0;
            completed3DCount = 0;
            restartButton.setVisible(false);
            showNextShape();
        });

        canvas = new Canvas(300, 300);
        gc = canvas.getGraphicsContext2D();

        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER);
        answerField = new TextField();
        answerField.setMaxWidth(200);
        answerField.setPromptText("Enter shape name");
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> checkAnswer());
        inputBox.getChildren().addAll(answerField, submitButton);

        messageLabel = new Label("");
        messageLabel.setStyle(labelStyle);

        Label taskLabel = new Label("Identify the shape:");
        taskLabel.setStyle(labelStyle);

        getChildren().addAll(
                infoBox,
                modeBox,
                taskLabel,
                canvas,
                inputBox,
                messageLabel
        );

        updateScoreAndProgress();

        rotationSliderY = createRotationSlider(val -> {
            if (currentShape != null) {
                currentShape.setRotationY(val);
                redrawShape();
            }
        });

        rotationSliderX = createRotationSlider(val -> {
            if (currentShape != null) {
                currentShape.setRotationX(val);
                redrawShape();
            }
        });

        HBox rotationBox = new HBox(10,
                new Label("Rotate Y:"), rotationSliderY,
                new Label("Rotate X:"), rotationSliderX
        );
        rotationBox.setAlignment(Pos.CENTER);
        getChildren().add(rotationBox);

        restartButton = new Button("Restart");
        restartButton.setVisible(false);
        restartButton.setOnAction(e -> restartGame());
        getChildren().add(restartButton);
    }

    private void restartGame() {
        gameController.reset();
        completed2DCount = 0;
        completed3DCount = 0;
        updateScoreAndProgress();
        restartButton.setVisible(false);
        showNextShape();
    }

    private Slider createRotationSlider(Consumer<Double> listener) {
        Slider slider = new Slider(0, 360, 0);
        slider.setBlockIncrement(1);
        slider.setMajorTickUnit(90);
        slider.setMinorTickCount(9);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.valueProperty().addListener((obs, oldVal, newVal) -> listener.accept(newVal.doubleValue()));
        return slider;
    }

    //每答对一题增加25%；进度条，最多100%
    private void updateScoreAndProgress() {
        int completed = is3DMode ? completed3DCount : completed2DCount;
        double progress = Math.min((double) completed / 4, 1.0);
        progressLabel.setText("Progress: " + (int) (progress * 100) + "%");
        scoreLabel.setText("Score: " + gameController.getCurrentScore());
    }

    private void showNextShape() {
        if ((!is3DMode && completed2DCount >= 4) || (is3DMode && completed3DCount >= 4)) {
            // 弹出选择对话框
            showCompletionDialog();
            return;
        }

        attempts = 0;
        List<Shape> list = is3DMode ? shapes3D : shapes2D;
        currentShape = list.get(random.nextInt(list.size()));
        currentShape.setPosition(
                (canvas.getWidth() - 100) / 2,
                (canvas.getHeight() - 100) / 2
        );
        rotationSliderY.setValue(0);
        rotationSliderX.setValue(0);
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        currentShape.draw(gc);
        answerField.clear();
        messageLabel.setText("");
        answerField.requestFocus();
    }

    private void showCompletionDialog() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Congratulations!");
            alert.setHeaderText("You've completed 4 shapes!");
            alert.setContentText("Do you want to continue or restart?");

            ButtonType continueButton = new ButtonType("Continue");
            ButtonType restartButton = new ButtonType("Restart");

            alert.getButtonTypes().setAll(continueButton, restartButton);

            alert.showAndWait().ifPresent(response -> {
                if (response == continueButton) {
                    // 继续答题，分数保持
                    if (is3DMode) {
                        completed3DCount = 0;
                    } else {
                        completed2DCount = 0;
                    }
                    showNextShape();
                } else {
                    // 重置一局
                    restartGame();
                }
            });
        });
    }

    private void checkAnswer() {
        attempts++;
        String ans = answerField.getText().trim();
        if (currentShape.isCorrectName(ans)) {
            messageLabel.setText("Correct! Well done!");
            messageLabel.setTextFill(Color.GREEN);
            gameController.addPoints(attempts, is3DMode);
            gameController.taskCompleted(1);

            if (is3DMode) {
                completed3DCount++;
            } else {
                completed2DCount++;
            }

            updateScoreAndProgress();  // ⬅️ 把这个放在计数递增后

            new Thread(() -> {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException ignored) {
                }
                Platform.runLater(this::showNextShape);
            }).start();

        } else {
            if (attempts >= 3) {
                messageLabel.setText("Correct answer is: " + currentShape.getName());
                messageLabel.setTextFill(Color.RED);
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ignored) {
                    }
                    Platform.runLater(this::showNextShape);
                }).start();
            } else {
                messageLabel.setText("Try again! Attempt " + attempts + " of 3");
                messageLabel.setTextFill(Color.RED);
                answerField.clear();
                answerField.requestFocus();
            }
        }
    }

    private void redrawShape() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        if (currentShape != null) {
            currentShape.draw(gc);
        }
    }
}
