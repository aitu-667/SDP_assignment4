package org.example.facade;

import org.example.subsystema.*;
import org.example.subsystemb.*;
import org.example.subsystemc.*;
import org.example.workflow.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

public class FulfillmentFacadeTest {
    private FulfillmentFacade facade;
    private InventoryRepository invRepo;
    private PackingRepository packRepo;
    private ShippingRepository shipRepo;
    private ChainProgressRepository progressRepo;

    @BeforeEach
    void setUp() {
        File db = new File("test_full.db");
        if (db.exists()) db.delete();
        String dbUrl = "jdbc:sqlite:test_full.db";

        invRepo = new InventoryRepository(dbUrl);
        packRepo = new PackingRepository(dbUrl);
        shipRepo = new ShippingRepository(dbUrl);
        progressRepo = new ChainProgressRepository(dbUrl);

        facade = new FulfillmentFacade(
                new InventoryManager(invRepo, new OrderValidator()),
                new PackingStation(packRepo, new WarehousePicker()),
                new ShippingCarrierAPI(shipRepo, new PaymentGateway()),
                progressRepo
        );
        FailureSimulator.reset();
    }

    @Test
    void testHappyPath() {
        boolean result = facade.processFulfillment("ORD-1", "SKU-99", 3, 100.0);
        assertTrue(result);
        assertEquals(ChainProgress.Status.COMPLETED, progressRepo.findByOrderId("ORD-1").getStatus());
    }

    @Test
    void testFailureAtStepB() {
        FailureSimulator.failAtB = true;
        boolean result = facade.processFulfillment("ORD-2", "SKU-99", 3, 100.0);
        assertFalse(result);
        assertEquals(InventoryItem.Status.CANCELLED, invRepo.findByOrderId("ORD-2").getStatus());
        assertEquals(ChainProgress.Status.COMPENSATED, progressRepo.findByOrderId("ORD-2").getStatus());
    }

    @Test
    void testFailureAtStepC() {
        FailureSimulator.failAtC = true;
        boolean result = facade.processFulfillment("ORD-3", "SKU-99", 3, 100.0);
        assertFalse(result);
        assertEquals(InventoryItem.Status.CANCELLED, invRepo.findByOrderId("ORD-3").getStatus());
        assertEquals(PackingTask.Status.CANCELLED, packRepo.findByOrderId("ORD-3").getStatus());
        assertEquals(ChainProgress.Status.COMPENSATED, progressRepo.findByOrderId("ORD-3").getStatus());
    }
}
