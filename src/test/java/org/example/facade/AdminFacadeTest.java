package org.example.facade;

import org.example.subsystema.*;
import org.example.subsystemb.*;
import org.example.subsystemc.*;
import org.example.workflow.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

public class AdminFacadeTest {
    private AdminFacade adminFacade;
    private InventoryRepository invRepo;

    @BeforeEach
    void setUp() {
        File db = new File("test_admin.db");
        if (db.exists()) db.delete();
        String dbUrl = "jdbc:sqlite:test_admin.db";

        invRepo = new InventoryRepository(dbUrl);
        PackingRepository packRepo = new PackingRepository(dbUrl);
        ShippingRepository shipRepo = new ShippingRepository(dbUrl);
        ChainProgressRepository progressRepo = new ChainProgressRepository(dbUrl);

        FulfillmentFacade fulfillmentFacade = new FulfillmentFacade(
                new InventoryManager(invRepo, new OrderValidator()),
                new PackingStation(packRepo, new WarehousePicker()),
                new ShippingCarrierAPI(shipRepo, new PaymentGateway()),
                progressRepo
        );

        adminFacade = new AdminFacade(invRepo, packRepo, shipRepo, progressRepo, fulfillmentFacade);
    }

    @Test
    void testAdminInspection() {
        invRepo.save(new InventoryItem("ORD-ADMIN", "SKU-1", 2, InventoryItem.Status.RESERVED));
        assertDoesNotThrow(() -> adminFacade.printOrderStatus("ORD-ADMIN"));
    }
}
