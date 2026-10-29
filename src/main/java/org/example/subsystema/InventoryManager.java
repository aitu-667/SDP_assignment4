package org.example.subsystema;

public class InventoryManager {
    private final InventoryRepository repository;
    private final OrderValidator validator;

    public InventoryManager(InventoryRepository repository, OrderValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    public void reserveInventory(String orderId, String itemSku, int quantity) {
        if (!validator.validate(itemSku, quantity)) {
            throw new IllegalArgumentException("Invalid order input");
        }
        InventoryItem item = new InventoryItem(orderId, itemSku, quantity, InventoryItem.Status.NEW);
        item.setStatus(InventoryItem.Status.RESERVED);
        repository.save(item);
    }

    public void cancelReservation(String orderId) {
        InventoryItem item = repository.findByOrderId(orderId);
        if (item != null) {
            item.setStatus(InventoryItem.Status.CANCELLED);
            repository.save(item);
        }
    }
}
