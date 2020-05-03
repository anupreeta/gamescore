package com.game.gamescore.controller;

import com.game.gamescore.exception.SessionNotFoundException;
import com.game.gamescore.model.SessionInfo;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.Map;
import java.util.Random;

public class Util {

    public static String getSessionID(int n)
    {
        // limit for uppercase Letters
        int lowerLimit = 65;
        int upperLimit = 90;
        Random random = new Random();
        // Create a StringBuffer to store the result
        StringBuffer r = new StringBuffer(n);
        for (int i = 0; i < n; i++) {
            // take a random value between 65 & 90
            int nextRandomChar = lowerLimit
                    + (int)(random.nextFloat()
                    * (upperLimit - lowerLimit + 1));
            r.append((char)nextRandomChar);
        }
        return r.toString();
    }

    public static SessionInfo validateSession(Map sessionMap, @RequestParam("sessionkey") String sessionKey) {
                // If session does not exist or if session has expired, throw SessionNotfoundException
            SessionInfo sessionDetails = (SessionInfo)sessionMap.get(sessionKey);
            if(sessionDetails !=null) {
                Date now = new Date();
                if (now.after(sessionDetails.getSessionEnd()))
                    throw new SessionNotFoundException("Session has Expired. Please login to continue");
            }
            else
                throw new SessionNotFoundException("Session not created. Please login to register");
            return sessionDetails;
        }
}
