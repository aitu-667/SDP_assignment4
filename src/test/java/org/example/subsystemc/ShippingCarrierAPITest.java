package org.example.subsystemc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

public class ShippingCarrierAPITest {
    private ShippingRepository repository;
    private ShippingCarrierAPI carrierAPI;

    @BeforeEach
    void setUp() {
        File db = new File("test_ship.db");
        if (db.exists()) db.delete();
        repository = new ShippingRepository("jdbc:sqlite:test_ship.db");
        PaymentGateway gateway = new PaymentGateway();
        carrierAPI = new ShippingCarrierAPI(repository, gateway);
    }

    @Test
    void testDispatchAndCancel() {
        String tracking = carrierAPI.dispatchShipment("ORD-500", 250.0);
        assertNotNull(tracking);
        assertTrue(tracking.startsWith("TRACK-"));

        ShipmentRecord record = repository.findByOrderId("ORD-500");
        assertEquals(ShipmentRecord.Status.DISPATCHED, record.getStatus());

        carrierAPI.cancelShipment("ORD-500");
        assertEquals(ShipmentRecord.Status.CANCELLED, repository.findByOrderId("ORD-500").getStatus());
    }
}
