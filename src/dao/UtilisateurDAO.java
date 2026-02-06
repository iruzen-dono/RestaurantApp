package dao;

import models.Utilisateur;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des utilisateurs
 */
public class UtilisateurDAO implements IGenericDAO<Utilisateur> {

    @Override
    public void create(Utilisateur utilisateur) throws Exception {
        String sql = "INSERT INTO utilisateur (login, motDePasse) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, utilisateur.getLogin());
            pstmt.setString(2, utilisateur.getMotDePasse());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    utilisateur.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public Utilisateur read(int id) throws Exception {
        String sql = "SELECT * FROM utilisateur WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Utilisateur(rs.getInt("id"), rs.getString("login"), rs.getString("motDePasse"));
                }
            }
        }
        return null;
    }

    @Override
    public List<Utilisateur> readAll() throws Exception {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur ORDER BY login";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                utilisateurs.add(new Utilisateur(rs.getInt("id"), rs.getString("login"), rs.getString("motDePasse")));
            }
        }
        return utilisateurs;
    }

    @Override
    public void update(Utilisateur utilisateur) throws Exception {
        String sql = "UPDATE utilisateur SET login = ?, motDePasse = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, utilisateur.getLogin());
            pstmt.setString(2, utilisateur.getMotDePasse());
            pstmt.setInt(3, utilisateur.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM utilisateur WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    /**
     * Authentifie un utilisateur avec login et mot de passe
     */
    public Utilisateur authenticate(String login, String motDePasse) throws Exception {
        String sql = "SELECT * FROM utilisateur WHERE login = ? AND motDePasse = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            pstmt.setString(2, motDePasse);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Utilisateur(rs.getInt("id"), rs.getString("login"), rs.getString("motDePasse"));
                }
            }
        }
        return null;
    }

    /**
     * Vérifie si un login existe déjà
     */
    public boolean existsByLogin(String login) throws Exception {
        String sql = "SELECT COUNT(*) FROM utilisateur WHERE login = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}
