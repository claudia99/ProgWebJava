package com.example.project.controller;

import com.example.project.dto.ErrorDto;
import com.example.project.exception.BadRequestException;
import com.example.project.exception.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionControllerAdvice {
    @ExceptionHandler
    protected ResponseEntity<ErrorDto> handleEntityNotFoundException(EntityNotFoundException ex) {
        return new ResponseEntity<>(
                ErrorDto.builder().code(404).message(ex.getMessage()).build(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorDto> handleBadRequestException(BadRequestException ex) {
        return new ResponseEntity<>(
                ErrorDto.builder().code(400).message(ex.getMessage()).build(),
                HttpStatus.BAD_REQUEST
        );
    }
}
