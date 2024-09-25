package com.sergeineretin.cloudfilestorage.controller;

import com.sergeineretin.cloudfilestorage.exception.FileDownloadException;
import com.sergeineretin.cloudfilestorage.exception.StorageException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(StorageException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    private String handleStorageException(StorageException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleViolationException(ConstraintViolationException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(FileDownloadException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleFileDownloadException(FileDownloadException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error";
    }
}
