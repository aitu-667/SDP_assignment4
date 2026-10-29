package org.example.subsystema;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

public class InventoryRepositoryTest {
    private InventoryRepository repository;
    private OrderValidator validator;

    @BeforeEach
    void setUp() {
        File db = new File("test_inv.db");
        if (db.exists()) db.delete();
        repository = new InventoryRepository("jdbc:sqlite:test_inv.db");
        validator = new OrderValidator();
    }

    @Test
    void testSaveAndFind() {
        assertTrue(validator.validate("SKU-123", 5));
        InventoryItem item = new InventoryItem("ORD-100", "SKU-123", 5, InventoryItem.Status.RESERVED);
        repository.save(item);

        InventoryItem found = repository.findByOrderId("ORD-100");
        assertNotNull(found);
        assertEquals("SKU-123", found.getItemSku());
        assertEquals(InventoryItem.Status.RESERVED, found.getStatus());
    }
}
