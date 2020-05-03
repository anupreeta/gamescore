package com.game.gamescore.controller;

import com.game.gamescore.exception.SessionNotFoundException;
import com.game.gamescore.model.SessionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
public class HTTPServerControllerTest
{

    @InjectMocks
    HTTPServerController httpController;
    String sessionIDforUSER1 = "";
    String sessionIDforUSER2 = "";
    private Map sessionMap = new ConcurrentHashMap<String, SessionInfo>();

    @Test
    public void testLogin() {

       // verify our client code
       URL url = null;
       try {
           url = new URL("http://localhost:8500/login?1234");
           URLConnection conn = url.openConnection();
           BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
           sessionIDforUSER1 = in.readLine();
           sessionMap.put(sessionIDforUSER1, new SessionInfo(1234, new Date(), new Date()));
           assertEquals(10, sessionIDforUSER1.length());
       } catch (MalformedURLException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }

   }

    @Test
    public void testInvalidLogin() {
        // verify our client code
        URL url = null;
        try {
            url = new URL("http://localhost:8500/login?-1234");
            URLConnection conn = url.openConnection();
            assertEquals("HTTP/1.1 401 Unauthorized", conn.getHeaderField(null));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testScore() {

        // verify our client code
        URL url = null;
        try {
            url = new URL("http://localhost:8500/login?1234");
            URLConnection conn = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            sessionIDforUSER1 = in.readLine();
            sessionMap.put(sessionIDforUSER1, new SessionInfo(1234, new Date(), new Date()));

            url = new URL("http://localhost:8500/score?levelid=1&sessionkey="+sessionIDforUSER1+"&score=100");
            conn = url.openConnection();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            assertEquals("OK", in.readLine());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testInvalidScoreSession() {

        // verify our client code
        URL url = null;
        try {
            url = new URL("http://localhost:8500/score?levelid=1&sessionkey="+sessionIDforUSER1+"&score=100");
            URLConnection conn = url.openConnection();
            assertEquals("HTTP/1.1 401 Unauthorized", conn.getHeaderField(null));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testInvalidSession() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        try {
            Util.validateSession(sessionMap, "abc");
        }
        catch (SessionNotFoundException ex) {
            assertEquals("Session not created. Please login to register", ex.getMessage());
        }
    }

    // integration test
    @Test
    public void testHighScore() {

        // verify our client code
        URL url = null;
        try {
            url = new URL("http://localhost:8500/login?1234");
            URLConnection conn = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            sessionIDforUSER1 = in.readLine();
            sessionMap.put(sessionIDforUSER1, new SessionInfo(1234, new Date(), new Date()));

            url = new URL("http://localhost:8500/login?4567");
            conn = url.openConnection();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            sessionIDforUSER2 = in.readLine();
            sessionMap.put(sessionIDforUSER2, new SessionInfo(4567, new Date(), new Date()));

            url = new URL("http://localhost:8500/score?levelid=1&sessionkey="+sessionIDforUSER1 + "&score=150");
            conn = url.openConnection();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            url = new URL("http://localhost:8500/score?levelid=2&sessionkey="+sessionIDforUSER1 + "&score=160");
            conn = url.openConnection();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            url = new URL("http://localhost:8500/score?levelid=2&sessionkey="+sessionIDforUSER2 + "&score=190");
            conn = url.openConnection();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            url = new URL("http://localhost:8500/score?levelid=1&sessionkey="+sessionIDforUSER2 + "&score=80");
            conn = url.openConnection();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            url = new URL("http://localhost:8500/score?levelid=2&sessionkey="+sessionIDforUSER2 + "&score=230");
            conn = url.openConnection();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            url = new URL("http://localhost:8500/score?levelid=2&sessionkey="+sessionIDforUSER1 + "&score=230");
            url.openConnection();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            url = new URL("http://localhost:8500/highscorelist?1");
            conn = url.openConnection();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            assertEquals("1234->150,4567->80,", in.readLine());

            url = new URL("http://localhost:8500/highscorelist?2");
            conn = url.openConnection();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            assertEquals("1234->160,4567->230,", in.readLine());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
