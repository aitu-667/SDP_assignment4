package org.example.subsystemc;

import java.util.UUID;

public class ShippingCarrierAPI {
    private final ShippingRepository repository;
    private final PaymentGateway paymentGateway;

    public ShippingCarrierAPI(ShippingRepository repository, PaymentGateway paymentGateway) {
        this.repository = repository;
        this.paymentGateway = paymentGateway;
    }

    public String dispatchShipment(String orderId, double price) {
        if (!paymentGateway.processPayment(price)) {
            throw new IllegalArgumentException("Payment failed");
        }
        String tracking = "TRACK-" + UUID.randomUUID().toString().substring(0, 8);
        ShipmentRecord record = new ShipmentRecord(orderId, tracking, ShipmentRecord.Status.NEW);
        record.setStatus(ShipmentRecord.Status.DISPATCHED);
        repository.save(record);
        return tracking;
    }

    public void cancelShipment(String orderId) {
        ShipmentRecord record = repository.findByOrderId(orderId);
        if (record != null) {
            record.setStatus(ShipmentRecord.Status.CANCELLED);
            repository.save(record);
        }
    }
}
