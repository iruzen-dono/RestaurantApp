package utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckHistorique {
    public static void main(String[] args) {
        try {
            AuditUtils.ensureTableExists();
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT id, action, table_name, record_id, user, details, created_at FROM historique ORDER BY created_at DESC LIMIT 20";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                System.out.println("Dernières entrées historique:");
                while (rs.next()) {
                    System.out.printf("%d | %s | %s | %s | %s | %s | %s\n",
                            rs.getInt("id"), rs.getString("action"), rs.getString("table_name"),
                            rs.getObject("record_id"), rs.getString("user"), rs.getString("details"), rs.getTimestamp("created_at")
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
