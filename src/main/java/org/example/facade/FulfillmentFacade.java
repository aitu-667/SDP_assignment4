package org.example.facade;

import org.example.subsystema.InventoryManager;
import org.example.subsystemb.PackingStation;
import org.example.subsystemc.ShippingCarrierAPI;
import org.example.workflow.ChainProgress;
import org.example.workflow.ChainProgressRepository;
import org.example.workflow.FailureSimulator;

public class FulfillmentFacade {
    private final InventoryManager inventoryManager;
    private final PackingStation packingStation;
    private final ShippingCarrierAPI shippingCarrierAPI;
    private final ChainProgressRepository progressRepository;

    public FulfillmentFacade(InventoryManager inventoryManager,
                             PackingStation packingStation,
                             ShippingCarrierAPI shippingCarrierAPI,
                             ChainProgressRepository progressRepository) {
        this.inventoryManager = inventoryManager;
        this.packingStation = packingStation;
        this.shippingCarrierAPI = shippingCarrierAPI;
        this.progressRepository = progressRepository;
    }

    public boolean processFulfillment(String orderId, String itemSku, int quantity, double price) {
        // Step A: Reserve Inventory
        inventoryManager.reserveInventory(orderId, itemSku, quantity);
        progressRepository.save(new ChainProgress(orderId, ChainProgress.Status.PENDING_B));

        // Step B: Pack Order
        try {
            if (FailureSimulator.failAtB) {
                throw new RuntimeException("Simulated failure at Warehouse Packing (Step B)");
            }
            packingStation.packOrder(orderId, quantity);
            progressRepository.save(new ChainProgress(orderId, ChainProgress.Status.PENDING_C));
        } catch (Exception e) {
            rollback(orderId);
            return false;
        }

        // Step C: Dispatch Shipping
        try {
            if (FailureSimulator.failAtC) {
                throw new RuntimeException("Simulated failure at Shipping Carrier (Step C)");
            }
            shippingCarrierAPI.dispatchShipment(orderId, price);
            progressRepository.save(new ChainProgress(orderId, ChainProgress.Status.COMPLETED));
            return true;
        } catch (Exception e) {
            rollback(orderId);
            return false;
        }
    }

    public void rollback(String orderId) {
        ChainProgress progress = progressRepository.findByOrderId(orderId);
        if (progress == null) return;

        if (progress.getStatus() == ChainProgress.Status.PENDING_C) {
            // Rollback C -> B -> A
            shippingCarrierAPI.cancelShipment(orderId);
            packingStation.unpackOrder(orderId);
            inventoryManager.cancelReservation(orderId);
        } else if (progress.getStatus() == ChainProgress.Status.PENDING_B) {
            // Rollback B -> A
            inventoryManager.cancelReservation(orderId);
        }

        progressRepository.save(new ChainProgress(orderId, ChainProgress.Status.COMPENSATED));
    }
}
