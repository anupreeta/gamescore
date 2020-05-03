package com.game.gamescore.controller;

import com.game.gamescore.exception.SessionNotFoundException;
import com.game.gamescore.model.UserInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static com.game.gamescore.controller.GameController.sessionMap;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GameControllerTest {
    @InjectMocks
    GameController gameController;

    String sessionIDforUSER1 = "";
    String sessionIDforUSER2 = "";


    @Test
    void getSessionID() {
        String str = Util.getSessionID(10);
        assertEquals(10,str.length());
    }

    @Test
    public void testLogin()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        UserInfo responseEntity = gameController.login(1234);
        sessionIDforUSER1 = responseEntity.getSessionkey();
        assertEquals(10,responseEntity.getSessionkey().length());
    }

    @Test
    public void testInvalidLogin() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        UserInfo responseEntity = gameController.login(-1234);
        assertNull(responseEntity);
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

    @Test
    public void testHighScoreforNonExistingLevel() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        assertEquals("No scores for the selected level", gameController.highscorelist(1));
    }

    @Test
    public void testHighScore() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        UserInfo responseEntity = gameController.login(1234);
        sessionIDforUSER1 = responseEntity.getSessionkey();
        assertEquals(10,responseEntity.getSessionkey().length());

        responseEntity = gameController.login(4567);
        sessionIDforUSER2 = responseEntity.getSessionkey();
        assertEquals(10,responseEntity.getSessionkey().length());

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        gameController.score(1, sessionIDforUSER1,150);
        gameController.score(2, sessionIDforUSER1,160);
        gameController.score(2, sessionIDforUSER2,190);
        gameController.score(1, sessionIDforUSER2,80);
        gameController.score(2, sessionIDforUSER2,230);
        gameController.score(2, sessionIDforUSER1,230);

        assertEquals("1234->150,4567->80," ,gameController.highscorelist(1) );
        assertEquals("1234->230,4567->230," ,gameController.highscorelist(2) );
    }
}