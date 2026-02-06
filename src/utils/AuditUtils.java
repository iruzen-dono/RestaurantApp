package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utilitaire simple pour enregistrer des entrées d'historique dans la base.
 */
public class AuditUtils {

    public static void ensureTableExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS historique (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "action VARCHAR(50), " +
                "table_name VARCHAR(100), " +
                "record_id INT, " +
                "user VARCHAR(100), " +
                "details TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    public static void log(String action, String tableName, Integer recordId, String user, String details) throws SQLException {
        ensureTableExists();
        String sql = "INSERT INTO historique (action, table_name, record_id, user, details) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, action);
            pstmt.setString(2, tableName);
            if (recordId == null) pstmt.setNull(3, java.sql.Types.INTEGER); else pstmt.setInt(3, recordId);
            pstmt.setString(4, user != null ? user : "system");
            pstmt.setString(5, details != null ? details : "");
            pstmt.executeUpdate();
        }
    }
}
