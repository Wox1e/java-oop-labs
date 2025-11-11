package com.oop.labs.exceptions;

public class InconsistentFunctionsException extends RuntimeException {
    public InconsistentFunctionsException(String message) {
        super(message);
    }
    public InconsistentFunctionsException(){super();}
}
