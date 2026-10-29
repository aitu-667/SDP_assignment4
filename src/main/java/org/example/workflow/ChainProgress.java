package org.example.workflow;

public class ChainProgress {
    public enum Status { PENDING_B, PENDING_C, COMPLETED, COMPENSATED }

    private final String orderId;
    private Status status;

    public ChainProgress(String orderId, Status status) {
        this.orderId = orderId;
        this.status = status;
    }

    public String getOrderId() { return orderId; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
