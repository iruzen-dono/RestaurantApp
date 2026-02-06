package dao;

import models.MouvementStock;
import utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import utils.AuditUtils;

/**
 * DAO pour la gestion des mouvements de stock
 */
public class MouvementStockDAO implements IGenericDAO<MouvementStock> {

    @Override
    public void create(MouvementStock mouvement) throws Exception {
        String sql = "INSERT INTO mouvement_stock (produit_id, type, quantite, date_mouvement, motif) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, mouvement.getProduitId());
            pstmt.setString(2, mouvement.getType().toString());
            pstmt.setInt(3, mouvement.getQuantite());
            pstmt.setDate(4, java.sql.Date.valueOf(mouvement.getDateMouvement()));
            pstmt.setString(5, mouvement.getMotif());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    mouvement.setId(generatedKeys.getInt(1));
                }
            }
            try { AuditUtils.log("CREATE", "mouvement_stock", mouvement.getId(), "system", "prod="+mouvement.getProduitId()+" type="+mouvement.getType()); } catch (Exception ignore) {}
        }
    }

    @Override
    public MouvementStock read(int id) throws Exception {
        String sql = "SELECT * FROM mouvement_stock WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new MouvementStock(
                            rs.getInt("id"),
                            rs.getInt("produit_id"),
                            MouvementStock.TypeMouvement.valueOf(rs.getString("type")),
                            rs.getInt("quantite"),
                            rs.getDate("date_mouvement").toLocalDate(),
                            rs.getString("motif")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<MouvementStock> readAll() throws Exception {
        List<MouvementStock> mouvements = new ArrayList<>();
        String sql = "SELECT * FROM mouvement_stock ORDER BY date_mouvement DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                mouvements.add(new MouvementStock(
                        rs.getInt("id"),
                        rs.getInt("produit_id"),
                        MouvementStock.TypeMouvement.valueOf(rs.getString("type")),
                        rs.getInt("quantite"),
                        rs.getDate("date_mouvement").toLocalDate(),
                        rs.getString("motif")
                ));
            }
        }
        return mouvements;
    }

    @Override
    public void update(MouvementStock mouvement) throws Exception {
        String sql = "UPDATE mouvement_stock SET produit_id = ?, type = ?, quantite = ?, date_mouvement = ?, motif = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, mouvement.getProduitId());
            pstmt.setString(2, mouvement.getType().toString());
            pstmt.setInt(3, mouvement.getQuantite());
            pstmt.setDate(4, java.sql.Date.valueOf(mouvement.getDateMouvement()));
            pstmt.setString(5, mouvement.getMotif());
            pstmt.setInt(6, mouvement.getId());
            pstmt.executeUpdate();
        }
        try { AuditUtils.log("UPDATE", "mouvement_stock", mouvement.getId(), "system", "prod="+mouvement.getProduitId()+" type="+mouvement.getType()); } catch (Exception ignore) {}
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM mouvement_stock WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
        try { AuditUtils.log("DELETE", "mouvement_stock", id, "system", null); } catch (Exception ignore) {}
    }

    /**
     * Récupère les mouvements d'un produit
     */
    public List<MouvementStock> readByProduit(int produitId) throws Exception {
        List<MouvementStock> mouvements = new ArrayList<>();
        String sql = "SELECT * FROM mouvement_stock WHERE produit_id = ? ORDER BY date_mouvement DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, produitId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    mouvements.add(new MouvementStock(
                            rs.getInt("id"),
                            rs.getInt("produit_id"),
                            MouvementStock.TypeMouvement.valueOf(rs.getString("type")),
                            rs.getInt("quantite"),
                            rs.getDate("date_mouvement").toLocalDate(),
                            rs.getString("motif")
                    ));
                }
            }
        }
        return mouvements;
    }

    /**
     * Récupère les mouvements pour une date donnée
     */
    public List<MouvementStock> readByDate(LocalDate date) throws Exception {
        List<MouvementStock> mouvements = new ArrayList<>();
        String sql = "SELECT * FROM mouvement_stock WHERE date_mouvement = ? ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, java.sql.Date.valueOf(date));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    mouvements.add(new MouvementStock(
                            rs.getInt("id"),
                            rs.getInt("produit_id"),
                            MouvementStock.TypeMouvement.valueOf(rs.getString("type")),
                            rs.getInt("quantite"),
                            rs.getDate("date_mouvement").toLocalDate(),
                            rs.getString("motif")
                    ));
                }
            }
        }
        return mouvements;
    }
}
