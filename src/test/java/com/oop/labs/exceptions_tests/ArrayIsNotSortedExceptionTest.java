package com.oop.labs.exceptions_tests;

import com.oop.labs.exceptions.ArrayIsNotSortedException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArrayIsNotSortedExceptionTest {

    @Test
    void testException() {
        assertThrows(ArrayIsNotSortedException.class, () -> {
            throw new ArrayIsNotSortedException();
        });
    }

}