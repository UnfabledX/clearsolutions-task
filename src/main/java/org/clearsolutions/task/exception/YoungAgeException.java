package org.clearsolutions.task.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class YoungAgeException extends RuntimeException {

    private final HttpStatus status;

    public YoungAgeException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
