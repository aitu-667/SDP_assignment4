package org.example.subsystemb;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WarehousePickerTest {

    @Test
    void testCalculateBoxSize() {
        WarehousePicker picker = new WarehousePicker();
        assertEquals("SMALL", picker.calculateBoxSize(1));
        assertEquals("SMALL", picker.calculateBoxSize(2));
        assertEquals("MEDIUM", picker.calculateBoxSize(5));
        assertEquals("LARGE", picker.calculateBoxSize(15));
    }
}
