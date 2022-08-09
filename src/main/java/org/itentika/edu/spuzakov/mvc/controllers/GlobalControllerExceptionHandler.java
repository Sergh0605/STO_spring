package org.itentika.edu.spuzakov.mvc.controllers;

import lombok.extern.slf4j.Slf4j;
import org.itentika.edu.spuzakov.mvc.dto.ExceptionDto;
import org.itentika.edu.spuzakov.mvc.exception.ConversionStoException;
import org.itentika.edu.spuzakov.mvc.exception.NotFoundStoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {
    @ExceptionHandler({MissingServletRequestParameterException.class,
            MissingServletRequestPartException.class,
            HttpRequestMethodNotSupportedException.class,
            ConversionStoException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<?> handleInvalidInputException(HttpServletRequest request, Exception e) {
        log.error("Request: " + request.getRequestURL() + " raised " + e.getMessage());
        HttpStatus errorStatus = HttpStatus.BAD_REQUEST;
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .timestamp(LocalDateTime.now())
                .status(errorStatus.value())
                .error(e.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(errorStatus).body(exceptionDto);
    }

    @ExceptionHandler({NotFoundStoException.class, NoHandlerFoundException.class})
    public ResponseEntity<?> handleNotFoundException(HttpServletRequest request, Exception e) {
        log.error("Request: " + request.getRequestURL() + " raised " + e.getMessage());
        HttpStatus errorStatus = HttpStatus.NOT_FOUND;
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .timestamp(LocalDateTime.now())
                .status(errorStatus.value())
                .error(e.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(errorStatus).body(exceptionDto);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<?> handleApplicationException(HttpServletRequest request, Exception e) {
        log.error("Request: " + request.getRequestURL() + " raised " + e.getMessage(), e);
        HttpStatus errorStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .timestamp(LocalDateTime.now())
                .status(errorStatus.value())
                .error("Internal Server Error")
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(errorStatus).body(exceptionDto);
    }
}
