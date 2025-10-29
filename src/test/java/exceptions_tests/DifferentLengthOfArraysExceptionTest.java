package exceptions_tests;

import exceptions.DifferentLengthOfArraysException;
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