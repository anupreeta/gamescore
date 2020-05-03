package com.game.gamescore.model;

public class UserInfo {

    private final long id;
    private final String sessionkey;

    public long getId() {
        return id;
    }

    public String getSessionkey() {
        return sessionkey;
    }

    public UserInfo(long id, String sessionkey) {
        this.id = id;
        this.sessionkey = sessionkey;
    }



}
