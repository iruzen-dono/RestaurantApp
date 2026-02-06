package dao;

import models.LigneCommande;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import utils.AuditUtils;

/**
 * DAO pour la gestion des lignes de commande
 */
public class LigneCommandeDAO implements IGenericDAO<LigneCommande> {

    @Override
    public void create(LigneCommande ligne) throws Exception {
        String sql = "INSERT INTO ligne_commande (commande_id, produit_id, quantite, prix_unitaire, montant_ligne) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, ligne.getCommandeId());
            pstmt.setInt(2, ligne.getProduitId());
            pstmt.setInt(3, ligne.getQuantite());
            pstmt.setDouble(4, ligne.getPrixUnitaire());
            pstmt.setDouble(5, ligne.getMontantLigne());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ligne.setId(generatedKeys.getInt(1));
                }
            }
            try { AuditUtils.log("CREATE", "ligne_commande", ligne.getId(), "system", "cmd="+ligne.getCommandeId()+" prod="+ligne.getProduitId()); } catch (Exception ignore) {}
        }
    }

    @Override
    public LigneCommande read(int id) throws Exception {
        String sql = "SELECT * FROM ligne_commande WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new LigneCommande(
                            rs.getInt("id"),
                            rs.getInt("commande_id"),
                            rs.getInt("produit_id"),
                            rs.getInt("quantite"),
                            rs.getDouble("prix_unitaire")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<LigneCommande> readAll() throws Exception {
        List<LigneCommande> lignes = new ArrayList<>();
        String sql = "SELECT * FROM ligne_commande ORDER BY commande_id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lignes.add(new LigneCommande(
                        rs.getInt("id"),
                        rs.getInt("commande_id"),
                        rs.getInt("produit_id"),
                        rs.getInt("quantite"),
                        rs.getDouble("prix_unitaire")
                ));
            }
        }
        return lignes;
    }

    @Override
    public void update(LigneCommande ligne) throws Exception {
        String sql = "UPDATE ligne_commande SET commande_id = ?, produit_id = ?, quantite = ?, prix_unitaire = ?, montant_ligne = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ligne.getCommandeId());
            pstmt.setInt(2, ligne.getProduitId());
            pstmt.setInt(3, ligne.getQuantite());
            pstmt.setDouble(4, ligne.getPrixUnitaire());
            pstmt.setDouble(5, ligne.getMontantLigne());
            pstmt.setInt(6, ligne.getId());
            pstmt.executeUpdate();
        }
        try { AuditUtils.log("UPDATE", "ligne_commande", ligne.getId(), "system", "cmd="+ligne.getCommandeId()+" prod="+ligne.getProduitId()); } catch (Exception ignore) {}
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM ligne_commande WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
        try { AuditUtils.log("DELETE", "ligne_commande", id, "system", null); } catch (Exception ignore) {}
    }

    /**
     * Récupère les lignes d'une commande
     */
    public List<LigneCommande> readByCommande(int commandeId) throws Exception {
        List<LigneCommande> lignes = new ArrayList<>();
        String sql = "SELECT * FROM ligne_commande WHERE commande_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, commandeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lignes.add(new LigneCommande(
                            rs.getInt("id"),
                            rs.getInt("commande_id"),
                            rs.getInt("produit_id"),
                            rs.getInt("quantite"),
                            rs.getDouble("prix_unitaire")
                    ));
                }
            }
        }
        return lignes;
    }

    /**
     * Supprime toutes les lignes d'une commande
     */
    public void deleteByCommande(int commandeId) throws Exception {
        String sql = "DELETE FROM ligne_commande WHERE commande_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, commandeId);
            pstmt.executeUpdate();
        }
        try { AuditUtils.log("DELETE", "ligne_commande", null, "system", "deleteByCommande="+commandeId); } catch (Exception ignore) {}
    }
}
