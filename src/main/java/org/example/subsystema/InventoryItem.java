package org.example.subsystema;

public class InventoryItem {
    public enum Status { NEW, RESERVED, CANCELLED }

    private final String orderId;
    private final String itemSku;
    private final int quantity;
    private Status status;

    public InventoryItem(String orderId, String itemSku, int quantity, Status status) {
        this.orderId = orderId;
        this.itemSku = itemSku;
        this.quantity = quantity;
        this.status = status;
    }

    public String getOrderId() { return orderId; }
    public String getItemSku() { return itemSku; }
    public int getQuantity() { return quantity; }
    public Status getStatus() { return status; }

    public void setStatus(Status newStatus) {
        if (this.status == Status.CANCELLED && newStatus == Status.RESERVED) {
            throw new IllegalStateException("Cannot reserve cancelled inventory");
        }
        this.status = newStatus;
    }
}
