package org.example.subsystemc;

import java.sql.*;

public class ShippingRepository {
    private final String dbUrl;

    public ShippingRepository(String dbUrl) {
        this.dbUrl = dbUrl;
        initDb();
    }

    private void initDb() {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS shipment_records (" +
                    "order_id TEXT PRIMARY KEY, tracking_number TEXT, status TEXT)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(ShipmentRecord record) {
        String sql = "INSERT INTO shipment_records(order_id, tracking_number, status) VALUES(?,?,?) " +
                "ON CONFLICT(order_id) DO UPDATE SET status=excluded.status";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, record.getOrderId());
            pstmt.setString(2, record.getTrackingNumber());
            pstmt.setString(3, record.getStatus().name());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ShipmentRecord findByOrderId(String orderId) {
        String sql = "SELECT * FROM shipment_records WHERE order_id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new ShipmentRecord(
                        rs.getString("order_id"),
                        rs.getString("tracking_number"),
                        ShipmentRecord.Status.valueOf(rs.getString("status"))
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
