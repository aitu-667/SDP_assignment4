package org.example.subsystema;

import java.sql.*;

public class InventoryRepository {
    private final String dbUrl;

    public InventoryRepository(String dbUrl) {
        this.dbUrl = dbUrl;
        initDb();
    }

    private void initDb() {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS inventory_reservations (" +
                    "order_id TEXT PRIMARY KEY, item_sku TEXT, quantity INT, status TEXT)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(InventoryItem item) {
        String sql = "INSERT INTO inventory_reservations(order_id, item_sku, quantity, status) VALUES(?,?,?,?) " +
                "ON CONFLICT(order_id) DO UPDATE SET status=excluded.status";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.getOrderId());
            pstmt.setString(2, item.getItemSku());
            pstmt.setInt(3, item.getQuantity());
            pstmt.setString(4, item.getStatus().name());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public InventoryItem findByOrderId(String orderId) {
        String sql = "SELECT * FROM inventory_reservations WHERE order_id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new InventoryItem(
                        rs.getString("order_id"),
                        rs.getString("item_sku"),
                        rs.getInt("quantity"),
                        InventoryItem.Status.valueOf(rs.getString("status"))
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
