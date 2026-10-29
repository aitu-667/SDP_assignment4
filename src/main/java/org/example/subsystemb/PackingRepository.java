package org.example.subsystemb;

import java.sql.*;

public class PackingRepository {
    private final String dbUrl;

    public PackingRepository(String dbUrl) {
        this.dbUrl = dbUrl;
        initDb();
    }

    private void initDb() {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS packing_tasks (" +
                    "order_id TEXT PRIMARY KEY, box_size TEXT, status TEXT)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(PackingTask task) {
        String sql = "INSERT INTO packing_tasks(order_id, box_size, status) VALUES(?,?,?) " +
                "ON CONFLICT(order_id) DO UPDATE SET status=excluded.status";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, task.getOrderId());
            pstmt.setString(2, task.getBoxSize());
            pstmt.setString(3, task.getStatus().name());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public PackingTask findByOrderId(String orderId) {
        String sql = "SELECT * FROM packing_tasks WHERE order_id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new PackingTask(
                        rs.getString("order_id"),
                        rs.getString("box_size"),
                        PackingTask.Status.valueOf(rs.getString("status"))
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
