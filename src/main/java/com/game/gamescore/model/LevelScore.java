package com.game.gamescore.model;

public class LevelScore{

    private final Integer userId;
    private final Integer score;
    private final Integer level;

    public LevelScore(Integer userId, Integer score, Integer level) {
        this.userId = userId;
        this.score = score;
        this.level = level;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getScore() {
        return score;
    }

    public Integer getLevel() {
        return level;
    }
}
