package org.example.facade;

import org.example.subsystema.InventoryItem;
import org.example.subsystema.InventoryRepository;
import org.example.subsystemb.PackingRepository;
import org.example.subsystemb.PackingTask;
import org.example.subsystemc.ShipmentRecord;
import org.example.subsystemc.ShippingRepository;
import org.example.workflow.ChainProgress;
import org.example.workflow.ChainProgressRepository;

public class AdminFacade {
    private final InventoryRepository inventoryRepo;
    private final PackingRepository packingRepo;
    private final ShippingRepository shippingRepo;
    private final ChainProgressRepository progressRepo;
    private final FulfillmentFacade fulfillmentFacade;

    public AdminFacade(InventoryRepository inventoryRepo, PackingRepository packingRepo,
                       ShippingRepository shippingRepo, ChainProgressRepository progressRepo,
                       FulfillmentFacade fulfillmentFacade) {
        this.inventoryRepo = inventoryRepo;
        this.packingRepo = packingRepo;
        this.shippingRepo = shippingRepo;
        this.progressRepo = progressRepo;
        this.fulfillmentFacade = fulfillmentFacade;
    }

    public void printOrderStatus(String orderId) {
        System.out.println("=== Order Status for: " + orderId + " ===");
        ChainProgress cp = progressRepo.findByOrderId(orderId);
        System.out.println("Chain Status: " + (cp != null ? cp.getStatus() : "NOT_FOUND"));

        InventoryItem item = inventoryRepo.findByOrderId(orderId);
        System.out.println("Subsystem A (Inventory): " + (item != null ? item.getStatus() : "NONE"));

        PackingTask task = packingRepo.findByOrderId(orderId);
        System.out.println("Subsystem B (Packing): " + (task != null ? task.getStatus() : "NONE"));

        ShipmentRecord ship = shippingRepo.findByOrderId(orderId);
        System.out.println("Subsystem C (Shipping): " + (ship != null ? ship.getStatus() : "NONE"));
        System.out.println("=================================");
    }

    public void manualCompensation(String orderId) {
        fulfillmentFacade.rollback(orderId);
    }
}
