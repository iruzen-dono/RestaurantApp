package dao;

import models.AuditEntry;
import utils.AuditUtils;
import utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HistoriqueDAO {

    public List<AuditEntry> readAll() throws Exception {
        // Ensure the historique table exists before attempting to read it
        AuditUtils.ensureTableExists();

        List<AuditEntry> list = new ArrayList<>();
        String sql = "SELECT * FROM historique ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new AuditEntry(
                        rs.getInt("id"),
                        rs.getString("action"),
                        rs.getString("table_name"),
                        rs.getObject("record_id") != null ? rs.getInt("record_id") : null,
                        rs.getString("user"),
                        rs.getString("details"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        }
        return list;
    }
}
