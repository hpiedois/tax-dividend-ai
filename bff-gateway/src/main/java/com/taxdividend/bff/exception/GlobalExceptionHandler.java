package com.taxdividend.bff.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleIllegalArgumentException(IllegalArgumentException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(WebClientResponseException.class)
    ProblemDetail handleWebClientResponseException(WebClientResponseException e) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(e.getStatusCode(), e.getStatusText());
        pd.setTitle("Upstream Service Error");
        return pd;
    }

    // Add more handlers as needed
}
