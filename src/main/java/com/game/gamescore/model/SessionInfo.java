package com.game.gamescore.model;

import java.util.Date;

public class SessionInfo {
    private final Integer userID;
    private final Date sessionStart;
    private final Date sessionEnd;

    public SessionInfo(Integer userID, Date sessionStart, Date sessionEnd) {
        this.userID = userID;
        this.sessionStart = sessionStart;
        this.sessionEnd = sessionEnd;
    }

    public Integer getUserID() {
        return userID;
    }

    public Date getSessionStart() {
        return sessionStart;
    }

    public Date getSessionEnd() {
        return sessionEnd;
    }
}
