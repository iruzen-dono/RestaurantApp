package dao;

import models.Commande;
import utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des commandes
 */
public class CommandeDAO implements IGenericDAO<Commande> {

    @Override
    public void create(Commande commande) throws Exception {
        String sql = "INSERT INTO commande (date_commande, etat, total) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setDate(1, java.sql.Date.valueOf(commande.getDateCommande()));
            pstmt.setString(2, commande.getEtat().toString());
            pstmt.setDouble(3, commande.getTotal());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    commande.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public Commande read(int id) throws Exception {
        String sql = "SELECT * FROM commande WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Commande(
                            rs.getInt("id"),
                            rs.getDate("date_commande").toLocalDate(),
                            Commande.EtatCommande.valueOf(rs.getString("etat")),
                            rs.getDouble("total")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<Commande> readAll() throws Exception {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commande ORDER BY date_commande DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                commandes.add(new Commande(
                        rs.getInt("id"),
                        rs.getDate("date_commande").toLocalDate(),
                        Commande.EtatCommande.valueOf(rs.getString("etat")),
                        rs.getDouble("total")
                ));
            }
        }
        return commandes;
    }

    @Override
    public void update(Commande commande) throws Exception {
        String sql = "UPDATE commande SET date_commande = ?, etat = ?, total = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, java.sql.Date.valueOf(commande.getDateCommande()));
            pstmt.setString(2, commande.getEtat().toString());
            pstmt.setDouble(3, commande.getTotal());
            pstmt.setInt(4, commande.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM commande WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    /**
     * Récupère les commandes du jour
     */
    public List<Commande> readByDate(LocalDate date) throws Exception {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commande WHERE date_commande = ? ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, java.sql.Date.valueOf(date));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    commandes.add(new Commande(
                            rs.getInt("id"),
                            rs.getDate("date_commande").toLocalDate(),
                            Commande.EtatCommande.valueOf(rs.getString("etat")),
                            rs.getDouble("total")
                    ));
                }
            }
        }
        return commandes;
    }

    /**
     * Calcule le chiffre d'affaires pour une date donnée
     */
    public double calculateTotalSalesByDate(LocalDate date) throws Exception {
        String sql = "SELECT COALESCE(SUM(total), 0) as total FROM commande WHERE date_commande = ? AND etat = 'VALIDEE'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, java.sql.Date.valueOf(date));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        }
        return 0;
    }

    /**
     * Calcule le chiffre d'affaires pour une période
     */
    public double calculateTotalSalesBetween(LocalDate dateDebut, LocalDate dateFin) throws Exception {
        String sql = "SELECT COALESCE(SUM(total), 0) as total FROM commande WHERE date_commande BETWEEN ? AND ? AND etat = 'VALIDEE'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, java.sql.Date.valueOf(dateDebut));
            pstmt.setDate(2, java.sql.Date.valueOf(dateFin));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        }
        return 0;
    }
}
