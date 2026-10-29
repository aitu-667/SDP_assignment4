package org.example.workflow;

import java.sql.*;

public class ChainProgressRepository {
    private final String dbUrl;

    public ChainProgressRepository(String dbUrl) {
        this.dbUrl = dbUrl;
        initDb();
    }

    private void initDb() {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS chain_progress (" +
                    "order_id TEXT PRIMARY KEY, status TEXT)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(ChainProgress progress) {
        String sql = "INSERT INTO chain_progress(order_id, status) VALUES(?,?) " +
                "ON CONFLICT(order_id) DO UPDATE SET status=excluded.status";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, progress.getOrderId());
            pstmt.setString(2, progress.getStatus().name());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ChainProgress findByOrderId(String orderId) {
        String sql = "SELECT * FROM chain_progress WHERE order_id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new ChainProgress(
                        rs.getString("order_id"),
                        ChainProgress.Status.valueOf(rs.getString("status"))
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
