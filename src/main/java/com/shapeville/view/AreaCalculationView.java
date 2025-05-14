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
import com.shapeville.model.*;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.HashSet;
import java.util.Set;

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
    private ComboBox<String> shapeSelector;
    private Set<String> completedShapes = new HashSet<>();
    private static final String[] AVAILABLE_SHAPES = {"Rectangle", "Triangle", "Parallelogram", "Trapezium"};

    public AreaCalculationView(GameController gameController) {
        this.gameController = gameController;
        this.random = new Random();
        setupUI();
    }

    private void setupUI() {
        setSpacing(10);
        setPadding(new Insets(10));
        setAlignment(Pos.TOP_CENTER);

        // Style for all labels - ensuring black text
        String labelStyle = "-fx-text-fill: black; -fx-font-size: 14px;";

        // Title with clearer style
        Label titleLabel = new Label("Calculate the area of shapes:");
        titleLabel.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");

        // Shape selection with reduced size
        HBox selectorBox = new HBox(10);
        selectorBox.setAlignment(Pos.CENTER);
        Label selectLabel = new Label("Select shape:");
        selectLabel.setStyle(labelStyle);
        shapeSelector = new ComboBox<>();
        shapeSelector.getItems().addAll(AVAILABLE_SHAPES);
        shapeSelector.setPromptText("Select a shape");
        shapeSelector.setPrefWidth(150);
        shapeSelector.setOnAction(e -> {
            String selectedShape = shapeSelector.getValue();
            if (selectedShape != null) {
                // 清除上一个图形的结果提示
                messageLabel.setText("");
                
                // 检查并移除之前已完成的图形（从下拉菜单中）
                for (String shape : completedShapes.toArray(new String[0])) {
                    shapeSelector.getItems().remove(shape);
                }
                
                generateShape(selectedShape);
                answerField.setDisable(false); // 启用输入框
            } else {
                answerField.setDisable(true); // 禁用输入框
            }
        });
        selectorBox.getChildren().addAll(selectLabel, shapeSelector);

        // Canvas for shape drawing with slightly reduced height
        canvas = new Canvas(400, 250);
        gc = canvas.getGraphicsContext2D();
        // 绘制初始指导文本
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(16));
        gc.fillText("Please select a shape", 100, 120);

        // Timer label with improved style
        timerLabel = new Label("Time remaining: 3:00");
        timerLabel.setStyle(labelStyle);

        // Input area with more compact layout
        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER);
        answerField = new TextField();
        answerField.setPromptText("Enter area");
        answerField.setPrefWidth(100);
        answerField.setDisable(true); // 初始状态下禁用输入框
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> checkAnswer());
        Label areaLabel = new Label("Area:");
        areaLabel.setStyle(labelStyle);
        inputBox.getChildren().addAll(areaLabel, answerField, submitButton);

        // Message label with improved style
        messageLabel = new Label("");
        messageLabel.setStyle("-fx-font-size: 14px;");

        // 进度指示器
        Label progressLabel = new Label("Progress: 0/4 shapes completed");
        progressLabel.setStyle(labelStyle);

        getChildren().addAll(
                titleLabel,
                selectorBox,
                canvas,
                timerLabel,
                inputBox,
                messageLabel,
                progressLabel);
                
        // 更新进度显示
        shapeSelector.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateProgressLabel(progressLabel);
        });
    }

    private void updateProgressLabel(Label progressLabel) {
        progressLabel.setText(String.format("Progress: %d/4 shapes completed", completedShapes.size()));
        
        // 当所有形状都完成后，提示用户
        if (completedShapes.size() >= 4) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Task Completed");
            alert.setHeaderText("Great Job!");
            alert.setContentText("You have completed all shape area calculations!");
            alert.showAndWait();
            
            // 返回主菜单
            MainView mainView = new MainView();
            mainView.getGameController().setCurrentScore(gameController.getCurrentScore());
            getScene().setRoot(mainView);
        }
    }

    private void generateShape(String shapeType) {
        // Generate random dimensions between 1 and 20, but multiply by 2 to make shapes larger
        double dim1 = random.nextInt(20) + 1; // 1 到 20 范围
        double dim2 = random.nextInt(20) + 1; // 1 到 20 范围
        double dim3 = random.nextInt(20) + 1; // 1 到 20 范围

        // 重置尝试次数和清除消息
        attempts = 0;
        messageLabel.setText("");

        currentShape = switch (shapeType) {
            case "Rectangle" -> new Rectangle(dim1, dim2);
            case "Triangle" -> new Triangle(dim1, dim2);
            case "Parallelogram" -> new Parallelogram(dim1, dim2);
            case "Trapezium" -> new Trapezium(dim1, dim3, dim2);
            default -> new Rectangle(dim1, dim2);
        };

        // 设置形状位置，使其居中显示
        currentShape.setPosition(
                (canvas.getWidth() - currentShape.getWidth()) / 2,
                (canvas.getHeight() - currentShape.getHeight()) / 2);

        // Clear canvas and draw new shape
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        currentShape.draw(gc);

        // 标注尺寸信息
        drawDimensions();

        // Reset attempts and start timer
        attempts = 0;
        startTimer();
    }

    private void drawDimensions() {
        // Clear the canvas
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        double imgX = 60;
        double imgY = 50;
        double imgWidth = 180;
        double imgHeight = 120;
        if (currentShape != null) {
            // 统一调用各自的绘制方法
            if (currentShape instanceof Trapezium t) {
                t.draw(gc, imgX, imgY, imgWidth, imgHeight);
            } else if (currentShape instanceof Rectangle r) {
                r.draw(gc, imgX, imgY, imgWidth, imgHeight);
            } else if (currentShape instanceof Parallelogram p) {
                p.draw(gc, imgX, imgY, imgWidth, imgHeight);
            } else if (currentShape instanceof Triangle tri) {
                tri.draw(gc, imgX, imgY, imgWidth, imgHeight);
            }
        }
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
        if (timer != null) {
            timer.cancel();
        }
        messageLabel.setText("Time's up! The correct area is: " +
                String.format("%.2f", currentShape.calculateArea()));
        messageLabel.setTextFill(Color.RED);

        showSolution();
    }

    private void showSolution() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        currentShape.draw(gc);
        drawDimensions();
        
        // 显示公式和带入值
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(14));
        String formula = "";
        String formulaWithValues = "";
        if (currentShape instanceof Rectangle) {
            formula = ((Rectangle) currentShape).getFormula();
            formulaWithValues = ((Rectangle) currentShape).getFormulaWithValues();
        } else if (currentShape instanceof Triangle) {
            formula = ((Triangle) currentShape).getFormula();
            formulaWithValues = ((Triangle) currentShape).getFormulaWithValues();
        } else if (currentShape instanceof Parallelogram) {
            formula = ((Parallelogram) currentShape).getFormula();
            formulaWithValues = ((Parallelogram) currentShape).getFormulaWithValues();
        } else if (currentShape instanceof Trapezium) {
            formula = ((Trapezium) currentShape).getFormula();
            formulaWithValues = ((Trapezium) currentShape).getFormulaWithValues();
        }
        gc.fillText(formula, 10, canvas.getHeight() - 30);
        gc.fillText(formulaWithValues, 10, canvas.getHeight() - 10);
        
        // 标记当前形状为已完成，但不立即从下拉菜单移除
        if (currentShape != null) {
            String currentShapeName = shapeSelector.getValue();
            if (!completedShapes.contains(currentShapeName)) {
                completedShapes.add(currentShapeName);
            
                // 更新进度显示
            for (Label node : getChildrenOfType(Label.class)) {
                if (node.getText().startsWith("Progress:")) {
                    updateProgressLabel(node);
                    break;
                }
            }
                
                // 检查是否已完成所有形状
                if (completedShapes.size() >= 4) {
                    // 使用延迟显示完成提示，避免界面更新冲突
                    new Thread(() -> {
                        try {
                            Thread.sleep(1000);
                            javafx.application.Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Task Completed");
                                alert.setHeaderText("Great Job!");
                                alert.setContentText("You have completed all shape area calculations!");
                                alert.showAndWait();
                                
                                // 返回主菜单
                                MainView mainView = new MainView();
                                mainView.getGameController().setCurrentScore(gameController.getCurrentScore());
                                getScene().setRoot(mainView);
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            }
        }
        
        // 重置输入字段，准备用户下一步操作
        answerField.clear();
    }

    private <T> Set<T> getChildrenOfType(Class<T> type) {
        Set<T> result = new HashSet<>();
        for (javafx.scene.Node node : getChildren()) {
            if (type.isInstance(node)) {
                result.add((T) node);
            }
        }
        return result;
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
                if (timer != null) {
                    timer.cancel();
                }
                // 播放答对音效
                AudioPlayer.playEffect("/audio/correct.wav");
                messageLabel.setText("Correct! Well done!");
                messageLabel.setTextFill(Color.GREEN);
                gameController.addPoints(attempts, false);

                showSolution();
            } else {
                // 播放答错音效
                AudioPlayer.playEffect("/audio/wrong.wav");
                if (attempts >= 3) {
                    if (timer != null) {
                        timer.cancel();
                    }
                    messageLabel.setText("The correct area is: " + String.format("%.2f", correctAnswer));
                    messageLabel.setTextFill(Color.RED);

                    showSolution();
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