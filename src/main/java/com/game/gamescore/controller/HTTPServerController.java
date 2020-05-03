package com.game.gamescore.controller;

import com.game.gamescore.exception.SessionNotFoundException;
import com.game.gamescore.model.LevelScore;
import com.game.gamescore.model.SessionInfo;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HTTPServerController {

    public static final int SESSION_TIMEOUT = 10;
    public static final int MAX_ENTRIES = 15;
    private static Map sessionMap = new ConcurrentHashMap<String, SessionInfo>();
    private static Queue<LevelScore> scoreQueue = new ConcurrentLinkedQueue<LevelScore>();

    public static void startServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8500), 0);
        HttpContext context = server.createContext("/login");
        context.setHandler(HTTPServerController::login);

        HttpContext context2 = server.createContext("/score");
        context2.setHandler(HTTPServerController::score);

        HttpContext context3 = server.createContext("/highscorelist");
        context3.setHandler(HTTPServerController::highscorelist);

        server.start();
        System.out.println("Server is running");
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8500), 0);
        HttpContext context = server.createContext("/login");
        context.setHandler(HTTPServerController::login);

        HttpContext context2 = server.createContext("/score");
        context2.setHandler(HTTPServerController::score);

        HttpContext context3 = server.createContext("/highscorelist");
        context3.setHandler(HTTPServerController::highscorelist);

        server.start();
        System.out.println("Server is running");
    }

   public static void login(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        Integer userID  =  Integer.parseInt(requestURI.getQuery());
        if(userID < 0 ) {
            exchange.sendResponseHeaders(401, "Invalid User ID".getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write("Invalid User ID".getBytes());
            os.close();
        }
        String sessionkey = Util.getSessionID(SESSION_TIMEOUT);
        LocalDateTime dateTime = LocalDateTime.now().plus(Duration.of(SESSION_TIMEOUT, ChronoUnit.MINUTES));
        Date tenminutes = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        sessionMap.put(sessionkey, new SessionInfo(userID, new Date(), tenminutes));


       exchange.sendResponseHeaders(200, sessionkey.getBytes().length);
       OutputStream os = exchange.getResponseBody();
       os.write(sessionkey.getBytes());
       os.close();

    }

    public static void score(HttpExchange exchange) throws IOException {
        Map <String,String>params =queryToMap(exchange.getRequestURI().getQuery());

        Integer levelID = Integer.parseInt(params.get("levelid"));
        String sessionKey = params.get("sessionkey");
        Integer score = Integer.parseInt(params.get("score"));

        // extract user id based on session id
        try {
            SessionInfo sessionDetails = Util.validateSession(sessionMap, sessionKey);
            LevelScore levelScore = new LevelScore(sessionDetails.getUserID(), score, levelID);
            scoreQueue.add(levelScore);
            exchange.sendResponseHeaders(200, 0);
            OutputStream os = exchange.getResponseBody();
            os.write("OK".getBytes());
            os.close();

        }catch(SessionNotFoundException ex) {
            exchange.sendResponseHeaders(401, ex.getMessage().getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(ex.getMessage().getBytes());
            os.close();
        }
    }

    public static void highscorelist(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        Integer levelID  =  Integer.parseInt(requestURI.getQuery());

        Map<Integer, List<LevelScore>> scoreMap = new ConcurrentHashMap<Integer, List<LevelScore>>();
        Map<Integer, Integer> sortedResult = new ConcurrentHashMap<>();
        List<LevelScore> selectedList = new ArrayList<LevelScore>();
        for(LevelScore score: scoreQueue) {
            // group the score by level first
            if(score.getLevel()==levelID) {
                selectedList.add(score);
            }
        }

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

        String outStr = builder.toString();
        exchange.sendResponseHeaders(200, outStr.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(outStr.getBytes());
        os.close();

    }

    /**
     * returns the url parameters in a map
     * @param query
     * @return map
     */
    public static Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }
}
