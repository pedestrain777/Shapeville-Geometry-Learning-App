package com.shapeville.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import com.shapeville.controller.GameController;

public class MainView extends VBox {
    private GameController gameController;
    private ProgressBar progressBar;
    private Label scoreLabel;
    private ScrollPane scrollPane;
    private VBox contentArea;

    public MainView() {
        gameController = new GameController();
        initializeUI();
    }

    public GameController getGameController() {
        return gameController;
    }

    private void initializeUI() {
        setSpacing(10);
        setPadding(new Insets(10));
        setAlignment(Pos.TOP_CENTER);

        // Common style for all labels - ensuring black text
        String labelStyle = "-fx-text-fill: black;";

        // Header
        Label titleLabel = new Label("Welcome to Shapeville!");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
        titleLabel.setPadding(new Insets(0, 0, 5, 0));
        titleLabel.setStyle(labelStyle);

        // Progress section
        HBox progressBox = new HBox(10);
        progressBox.setAlignment(Pos.CENTER);
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(200);
        scoreLabel = new Label("Score: 0");
        scoreLabel.setStyle(labelStyle);
        Label progressTextLabel = new Label("Progress:");
        progressTextLabel.setStyle(labelStyle);
        progressBox.getChildren().addAll(progressTextLabel, progressBar, scoreLabel);
        progressBox.setPadding(new Insets(0, 0, 5, 0));

        // Navigation buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button homeButton = new Button("Home");
        Button endSessionButton = new Button("End Session");
        buttonBox.getChildren().addAll(homeButton, endSessionButton);
        buttonBox.setPadding(new Insets(0, 0, 5, 0));

        // Content area with ScrollPane for scrolling capability
        contentArea = new VBox(5);
        contentArea.setAlignment(Pos.TOP_CENTER);
        contentArea.setPrefHeight(400);

        scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefViewportHeight(400);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        scrollPane.setStyle(
                "-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

        // Main menu
        createMainMenu();

        // Add all components
        getChildren().addAll(titleLabel, progressBox, buttonBox, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Button handlers
        homeButton.setOnAction(e -> createMainMenu());
        endSessionButton.setOnAction(e -> endSession());
    }

    private void createMainMenu() {
        contentArea.getChildren().clear();

        VBox menuBox = new VBox(8);
        menuBox.setAlignment(Pos.CENTER);

        Button task1Button = new Button("Task 1: Shape Identification");
        Button task2Button = new Button("Task 2: Angle Types");
        Button task3Button = new Button("Task 3: Area Calculation");
        Button task4Button = new Button("Task 4: Circle Calculations");
        Button bonus1Button = new Button("Extra: Compound Shapes");
        Button bonus2Button = new Button("Extra: Circle Sectors");

        menuBox.getChildren().addAll(
                task1Button, task2Button, task3Button,
                task4Button, bonus1Button, bonus2Button);

        // Style buttons
        for (javafx.scene.Node node : menuBox.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                btn.setPrefWidth(250);
                btn.setMinHeight(30);
                btn.setStyle("-fx-font-size: 14px;");
            }
        }

        // Add button handlers
        task1Button.setOnAction(e -> startTask1());
        task2Button.setOnAction(e -> startTask2());
        task3Button.setOnAction(e -> startTask3());
        task4Button.setOnAction(e -> startTask4());
        bonus1Button.setOnAction(e -> startExtra1());
        bonus2Button.setOnAction(e -> startExtra2());

        contentArea.getChildren().add(menuBox);
    }

    private void startTask1() {
        contentArea.getChildren().clear();
        ShapeIdentificationView shapeView = new ShapeIdentificationView(gameController);
        contentArea.getChildren().add(shapeView);
    }

    private void startTask2() {
        contentArea.getChildren().clear();
        AngleIdentificationView angleView = new AngleIdentificationView(gameController);
        contentArea.getChildren().add(angleView);
    }

    private void startTask3() {
        contentArea.getChildren().clear();
        AreaCalculationView areaView = new AreaCalculationView(gameController);
        contentArea.getChildren().add(areaView);
    }

    private void startTask4() {
        contentArea.getChildren().clear();
        CircleCalculationView circleView = new CircleCalculationView(gameController);
        contentArea.getChildren().add(circleView);
    }

    private void startExtra1() {
        contentArea.getChildren().clear();
        CompoundShapeView compoundView = new CompoundShapeView(gameController);
        contentArea.getChildren().add(compoundView);
    }

    private void startExtra2() {
        contentArea.getChildren().clear();
        SectorCalculationView sectorView = new SectorCalculationView(gameController);
        contentArea.getChildren().add(sectorView);
    }

    private void endSession() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Session Ended");
        alert.setHeaderText("Great job!");
        alert.setContentText("You scored " + gameController.getCurrentScore() + " points!");
        alert.showAndWait();

        // Reset game
        gameController = new GameController();
        createMainMenu();
    }
}