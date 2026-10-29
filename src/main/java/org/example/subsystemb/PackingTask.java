package org.example.subsystemb;

public class PackingTask {
    public enum Status { NEW, PACKED, CANCELLED }

    private final String orderId;
    private final String boxSize;
    private Status status;

    public PackingTask(String orderId, String boxSize, Status status) {
        this.orderId = orderId;
        this.boxSize = boxSize;
        this.status = status;
    }

    public String getOrderId() { return orderId; }
    public String getBoxSize() { return boxSize; }
    public Status getStatus() { return status; }

    public void setStatus(Status status) {
        this.status = status;
    }
}
