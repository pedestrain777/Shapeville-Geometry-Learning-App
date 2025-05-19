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
import com.shapeville.model.*;
import javafx.scene.paint.Color;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
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
    private ProgressBar progressBar;
    private Slider rotationSliderY;
    private Slider rotationSliderX;
    private int completedShapesCount = 0;
    private Button restartButton;
    // ---- 新增：旋转标签 ----
    private Label rotationYLabel;
    private Label rotationXLabel;

    // ----------- 本轮题库与索引，防止重复 -----------
    private List<Shape> currentRoundShapes = new ArrayList<>();
    private int currentShapeIndex = 0;
    // 添加跟踪已回答形状的集合
    private List<Shape> answeredShapes = new ArrayList<>();

    public ShapeIdentificationView(GameController gameController) {
        this.gameController = gameController;
        this.random = new Random();
        initializeShapes();
        setupUI();
        prepareNewRoundShapes();
        showNextShape();
    }

    // 新增方法设置返回主界面的回调
    public void setOnExit(Runnable onExit) {
        this.onExit = onExit;
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

        scoreLabel = new Label("Score: 0");
        scoreLabel.setStyle(labelStyle);

        // 创建更加明显的进度条
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

        // 确保进度组件在顶部且明显可见
        getChildren().add(statsBox);

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
            prepareNewRoundShapes();
            restartButton.setVisible(false);
            updateRotationControls();
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

        // 重新创建重启按钮
        restartButton = new Button("Restart");
        restartButton.setVisible(false);
        restartButton.setOnAction(e -> restartGame());

        // 旋转控制组件初始化移到这里
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

        // 新增：分别声明标签，并存为成员变量
        rotationYLabel = new Label("Rotate Y:");
        rotationXLabel = new Label("Rotate X:");
        HBox rotationBox = new HBox(10,
                rotationYLabel, rotationSliderY,
                rotationXLabel, rotationSliderX);
        rotationBox.setAlignment(Pos.CENTER);

        // 重新排列UI组件，确保进度条在顶部，旋转控件在下方
        getChildren().addAll(
                modeBox,
                taskLabel,
                canvas,
                inputBox,
                messageLabel,
                rotationBox, // 旋转控件放在最下方
                restartButton);

        // 确保初始状态下正确设置旋转控件的可见性
        updateRotationControls();

        updateScoreAndProgress();
    }

    private void prepareNewRoundShapes() {
        List<Shape> sourceList = is3DMode ? shapes3D : shapes2D;

        // 创建副本并排除已回答过的形状
        List<Shape> availableShapes = new ArrayList<>();
        for (Shape shape : sourceList) {
            boolean alreadyAnswered = false;
            for (Shape answered : answeredShapes) {
                // 通过比较名称判断是否是相同类型的形状
                if (shape.getClass().equals(answered.getClass())) {
                    alreadyAnswered = true;
                    break;
                }
            }
            if (!alreadyAnswered) {
                availableShapes.add(shape);
            }
        }

        // 如果所有形状都已回答过，则显示完成对话框
        if (availableShapes.isEmpty()) {
            Platform.runLater(this::showCompletionDialog);
            return;
        }

        // 随机选择未回答过的形状
        Collections.shuffle(availableShapes, random);
        int numShapes = Math.min(4, availableShapes.size());
        currentRoundShapes = availableShapes.subList(0, numShapes);
        currentShapeIndex = 0;
    }

    private void restartGame() {
        gameController.reset();
        completedShapesCount = 0;
        updateScoreAndProgress();
        prepareNewRoundShapes();
        restartButton.setVisible(false);
        updateRotationControls();
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

    private void updateScoreAndProgress() {
        double progress = Math.min((double) completedShapesCount / 4, 1.0);
        progressBar.setProgress(progress);
        progressLabel.setText("Progress: " + (int) (progress * 100) + "%");
        scoreLabel.setText("Score: " + gameController.getCurrentScore());
    }

    private void showNextShape() {
        // 检查是否完成4个形状
        if (completedShapesCount >= 4) {
            showCompletionDialog();
            return;
        }

        // ==每轮只需要判断索引==
        if (currentShapeIndex >= currentRoundShapes.size()) {
            showCompletionDialog();
            return;
        }

        // 实际出题
        attempts = 0;
        if (currentRoundShapes.isEmpty()) {
            messageLabel.setText("No shapes loaded!");
            return;
        }
        Shape template = currentRoundShapes.get(currentShapeIndex);
        currentShapeIndex++;
        currentShape = template.copy();

        currentShape.setPosition(
                (canvas.getWidth() - 100) / 2,
                (canvas.getHeight() - 100) / 2);
        if (!is3DMode && currentShape != null) {
            currentShape.setRotationX(0);
            currentShape.setRotationY(0);
        }
        rotationSliderY.setValue(0);
        rotationSliderX.setValue(0);
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        if (currentShape instanceof Circle) {
            ((Circle) currentShape).drawSimple(gc);
        } else {
            currentShape.draw(gc);
        }
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
                    // 重新一局（分数清零）
                    restartGame();
                }
            });
        });
    }

    private void checkAnswer() {
        attempts++;
        String ans = answerField.getText().trim();
        if (currentShape.isCorrectName(ans)) {
            // 播放答对音效
            AudioPlayer.playEffect("/audio/correct.wav");

            messageLabel.setText("Correct! Well done!");
            messageLabel.setTextFill(Color.GREEN);
            gameController.addPoints(attempts, is3DMode);
            gameController.taskCompleted(1);

            // 将回答正确的形状添加到已回答集合中
            answeredShapes.add(currentShape);

            completedShapesCount++;

            updateScoreAndProgress();

            PauseTransition pt = new PauseTransition(Duration.millis(800));
            pt.setOnFinished(e -> showNextShape());
            pt.play();

        } else {
            // 播放答错音效
            AudioPlayer.playEffect("/audio/wrong.wav");

            if (attempts >= 3) {
                messageLabel.setText("Correct answer is: " + currentShape.getName());
                messageLabel.setTextFill(Color.RED);

                PauseTransition pt = new PauseTransition(Duration.millis(2000));
                pt.setOnFinished(e -> showNextShape());
                pt.play();
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
            if (currentShape instanceof Circle) {
                ((Circle) currentShape).drawSimple(gc);
            } else {
                currentShape.draw(gc);
            }
        }
    }

    // 切换2D/3D时控制旋转滑块和标签的显示隐藏，并在切换到2D模式时归零旋转。

    private void updateRotationControls() {
        boolean enable = is3DMode;
        rotationSliderX.setDisable(!enable);
        rotationSliderY.setDisable(!enable);
        rotationSliderX.setVisible(enable);
        rotationSliderY.setVisible(enable);
        // 新增：标签同步隐藏
        if (rotationYLabel != null)
            rotationYLabel.setVisible(enable);
        if (rotationXLabel != null)
            rotationXLabel.setVisible(enable);

    }
}
