package dao;

import models.Categorie;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des catégories
 */
public class CategorieDAO implements IGenericDAO<Categorie> {

    @Override
    public void create(Categorie categorie) throws Exception {
        String sql = "INSERT INTO categorie (libelle) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, categorie.getLibelle());
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    categorie.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public Categorie read(int id) throws Exception {
        String sql = "SELECT * FROM categorie WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Categorie(rs.getInt("id"), rs.getString("libelle"));
                }
            }
        }
        return null;
    }

    @Override
    public List<Categorie> readAll() throws Exception {
        List<Categorie> categories = new ArrayList<>();
        String sql = "SELECT * FROM categorie ORDER BY libelle";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(new Categorie(rs.getInt("id"), rs.getString("libelle")));
            }
        }
        return categories;
    }

    @Override
    public void update(Categorie categorie) throws Exception {
        String sql = "UPDATE categorie SET libelle = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, categorie.getLibelle());
            pstmt.setInt(2, categorie.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM categorie WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    /**
     * Vérifie si une catégorie avec ce libellé existe déjà
     */
    public boolean existsByLibelle(String libelle) throws Exception {
        String sql = "SELECT COUNT(*) FROM categorie WHERE libelle = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, libelle);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}
