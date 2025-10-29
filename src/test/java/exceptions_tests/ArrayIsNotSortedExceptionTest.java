package exceptions_tests;

import exceptions.ArrayIsNotSortedException;
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