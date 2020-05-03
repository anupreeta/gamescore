package com.game.gamescore.controller;

import com.game.gamescore.model.LevelScore;
import com.game.gamescore.model.SessionInfo;
import com.game.gamescore.model.UserInfo;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@RestController
public class GameController {

    public static final int SESSION_TIMEOUT = 10;
    public static final int MAX_ENTRIES = 15;
    public static Map sessionMap = new ConcurrentHashMap<String, SessionInfo>();
        private static Queue<LevelScore> scoreQueue = new ConcurrentLinkedQueue<LevelScore>();

        @GetMapping("/{userID}/login")
        public UserInfo login(@PathVariable("userID") Integer userID) {
            if(userID > 0) {
                String sessionkey = Util.getSessionID(SESSION_TIMEOUT);
                LocalDateTime dateTime = LocalDateTime.now().plus(Duration.of(SESSION_TIMEOUT, ChronoUnit.MINUTES));
                Date tenminutes = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
                sessionMap.put(sessionkey, new SessionInfo(userID, new Date(), tenminutes));
                return new UserInfo(userID, sessionkey);
            }
            return null;
        }

        @PostMapping("/{levelID}/score")
        public void score(@PathVariable("levelID") Integer levelID, @RequestParam("sessionkey") String sessionKey, @RequestBody Integer score) {
            // extract user id based on session id
            SessionInfo sessionDetails = Util.validateSession(sessionMap, sessionKey);
            LevelScore levelScore = new LevelScore(sessionDetails.getUserID(), score, levelID);
            scoreQueue.add(levelScore);
        }

        @GetMapping("/{levelID}/highscorelist")
        public String highscorelist(@PathVariable("levelID") Integer levelID) {
            Map<Integer, List<LevelScore>> scoreMap = new ConcurrentHashMap<Integer, List<LevelScore>>();
            Map<Integer, Integer> sortedResult = new ConcurrentHashMap<>();
            List<LevelScore> selectedList = new ArrayList<LevelScore>();
            for(LevelScore score: scoreQueue) {
                // group the score by level first
                if(score.getLevel()==levelID) {
                    selectedList.add(score);
                }
            }
            if(selectedList.size() ==0)
                return "No scores for the selected level";
            // for the selected level, for each user, sort the scores in descending order
            selectedList.sort((LevelScore o1, LevelScore o2)->o1.getUserId().compareTo(o2.getUserId()));
            selectedList.sort((LevelScore o1, LevelScore o2)->o1.getScore().compareTo(o2.getScore()));

            for(LevelScore sortedScore: selectedList) {
                sortedResult.put(sortedScore.getUserId(), sortedScore.getScore());
            }

            StringBuilder builder = new StringBuilder();
            int i=0;
            for(Map.Entry<Integer, Integer> e : sortedResult.entrySet())
            {
                if(i< MAX_ENTRIES) {
                    Integer key = e.getKey();
                    Integer value = e.getValue();
                    builder.append(key);
                    builder.append("->");
                    builder.append(value);
                    builder.append(",");
                    i++;
                }
            }

            return builder.toString();
        }

}
