package utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utilitaire pour exporter les données de la base en CSV.
 */
public class ExportUtils {

    public static void exportTableToCSV(String tableName, String outputPath) throws SQLException, IOException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM " + tableName;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql);
             BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputPath))) {

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            // header
            for (int i = 1; i <= colCount; i++) {
                writer.write(meta.getColumnLabel(i));
                if (i < colCount) writer.write(",");
            }
            writer.newLine();

            // rows
            while (rs.next()) {
                for (int i = 1; i <= colCount; i++) {
                    String val = rs.getString(i);
                    if (val == null) val = "";
                    // Escape CSV if needed
                    val = val.replace("\"", "\"\"");
                    if (val.contains(",") || val.contains("\"") || val.contains("\n")) {
                        val = "\"" + val + "\"";
                    }
                    writer.write(val);
                    if (i < colCount) writer.write(",");
                }
                writer.newLine();
            }
        }
    }

    public static void exportDefaultExports() throws Exception {
        Files.createDirectories(Paths.get("exports"));
        exportTableToCSV("produit", "exports/produits.csv");
        exportTableToCSV("commande", "exports/commandes.csv");
    }
}
