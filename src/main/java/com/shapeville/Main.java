package com.shapeville;

import com.shapeville.util.AudioPlayer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.shapeville.view.MainView;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        MainView mainView = new MainView();
        Scene scene = new Scene(mainView, 800, 700);

        primaryStage.setTitle("Shapeville - Learn Geometry");
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(650);
        primaryStage.setMinWidth(750);
        primaryStage.show();

        // 播放背景音乐
        AudioPlayer.playWav("/audio/bg_music.wav");

        // 添加窗口关闭事件处理
        primaryStage.setOnCloseRequest(event -> {
            AudioPlayer.stopAll();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}