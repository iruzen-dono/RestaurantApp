package utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckLignes {
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT id, commande_id, produit_id, quantite, prix_unitaire, montant_ligne FROM ligne_commande ORDER BY commande_id, id";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                System.out.println("Lignes de commande:");
                while (rs.next()) {
                    System.out.printf("%d | cmd=%d | prod=%d | q=%d | pu=%.2f | montant=%.2f\n",
                            rs.getInt("id"), rs.getInt("commande_id"), rs.getInt("produit_id"), rs.getInt("quantite"), rs.getDouble("prix_unitaire"), rs.getDouble("montant_ligne")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
