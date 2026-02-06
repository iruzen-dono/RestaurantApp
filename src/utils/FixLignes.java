package utils;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Fix existing ligne_commande rows where prix_unitaire is 0 by copying produit.prix_vente
 */
public class FixLignes {
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "UPDATE ligne_commande lc JOIN produit p ON lc.produit_id = p.id " +
                    "SET lc.prix_unitaire = p.prix_vente, lc.montant_ligne = lc.quantite * p.prix_vente " +
                    "WHERE lc.prix_unitaire = 0 OR lc.prix_unitaire IS NULL";
            try (Statement stmt = conn.createStatement()) {
                int updated = stmt.executeUpdate(sql);
                System.out.println("Lignes mises à jour: " + updated);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
