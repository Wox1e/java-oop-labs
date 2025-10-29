package exceptions_tests;

import exceptions.InterpolationException;
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