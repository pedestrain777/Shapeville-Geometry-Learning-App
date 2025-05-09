package com.shapeville.controller;

public class GameController {
    private int currentScore;
    private int totalTasks;
    private int completedTasks;
    private int currentLevel;
    private boolean[] completedLevels;

    public GameController() {
        this.currentScore = 0;
        this.totalTasks = 6; // 4个主要任务 + 2个额外任务
        this.completedTasks = 0;
        this.currentLevel = 1;
        this.completedLevels = new boolean[6];
    }

    public void addPoints(int attempts, boolean isAdvanced) {
        int points;
        if (isAdvanced) {
            points = switch (attempts) {
                case 1 -> 6;
                case 2 -> 4;
                case 3 -> 2;
                default -> 0;
            };
        } else {
            points = switch (attempts) {
                case 1 -> 3;
                case 2 -> 2;
                case 3 -> 1;
                default -> 0;
            };
        }
        currentScore += points;
    }

    public void taskCompleted(int level) {
        if (level >= 1 && level <= 6 && !completedLevels[level - 1]) {
            completedTasks++;
            completedLevels[level - 1] = true;
        }
    }

    public int getCurrentScore() {
        return currentScore;
    }
    
    public void setCurrentScore(int score) {
        this.currentScore = score;
    }

    public double getProgress() {
        return (double) completedTasks / totalTasks;
    }

    public boolean isLevelCompleted(int level) {
        return level >= 1 && level <= 6 && completedLevels[level - 1];
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int level) {
        if (level >= 1 && level <= 6) {
            this.currentLevel = level;
        }
    }

    public void reset() {
        currentScore = 0;
        completedTasks = 0;
        currentLevel = 1;
        completedLevels = new boolean[6];
    }
}