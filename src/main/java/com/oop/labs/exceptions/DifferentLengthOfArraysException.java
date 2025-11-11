package com.oop.labs.exceptions;

public class DifferentLengthOfArraysException extends RuntimeException {
    public DifferentLengthOfArraysException(String message) {
        super(message);
    }
    public DifferentLengthOfArraysException(){super();}
}
