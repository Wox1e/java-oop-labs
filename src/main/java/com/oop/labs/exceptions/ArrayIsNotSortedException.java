package com.oop.labs.exceptions;

import java.util.Arrays;

public class ArrayIsNotSortedException extends RuntimeException{
    public ArrayIsNotSortedException(String message) {
        super(message);
    }
    public ArrayIsNotSortedException(){super();}
}
