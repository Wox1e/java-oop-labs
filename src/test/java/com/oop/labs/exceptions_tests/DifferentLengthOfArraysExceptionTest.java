package com.oop.labs.exceptions_tests;

import com.oop.labs.exceptions.DifferentLengthOfArraysException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DifferentLengthOfArraysExceptionTest {
    @Test
    void testException() {
        assertThrows(DifferentLengthOfArraysException.class, () -> {
            throw new DifferentLengthOfArraysException();
        });
    }

}