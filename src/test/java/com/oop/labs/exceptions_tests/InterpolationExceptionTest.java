package com.oop.labs.exceptions_tests;

import com.oop.labs.exceptions.InterpolationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InterpolationExceptionTest {
    @Test
    void testInterpolationException() {
        assertThrows(InterpolationException.class, () -> {
            throw new InterpolationException();
        });
    }
}