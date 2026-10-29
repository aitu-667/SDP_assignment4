package org.example.subsystemc;

public class ShipmentRecord {
    public enum Status { NEW, DISPATCHED, CANCELLED }

    private final String orderId;
    private final String trackingNumber;
    private Status status;

    public ShipmentRecord(String orderId, String trackingNumber, Status status) {
        this.orderId = orderId;
        this.trackingNumber = trackingNumber;
        this.status = status;
    }

    public String getOrderId() { return orderId; }
    public String getTrackingNumber() { return trackingNumber; }
    public Status getStatus() { return status; }

    public void setStatus(Status status) {
        this.status = status;
    }
}
