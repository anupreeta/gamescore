package com.game.gamescore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class SessionNotFoundAdvice {

    @ResponseBody
    @ExceptionHandler(SessionNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String sessionNotFoundHandler(SessionNotFoundException ex)
    {
        return ex.getMessage();
    }
}
