package com.shapeville;

import com.shapeville.util.AudioPlayer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.shapeville.view.MainView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

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
//        String musicFile = getClass().getResource("/audio/bg_music.mp3").toExternalForm();
//        Media media = new Media(musicFile);
//        MediaPlayer mediaPlayer = new MediaPlayer(media);
//        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // 循环播放
//        mediaPlayer.play();
        AudioPlayer.playWav("/audio/bg_music.wav");
    }

    public static void main(String[] args) {
        launch(args);
    }
}