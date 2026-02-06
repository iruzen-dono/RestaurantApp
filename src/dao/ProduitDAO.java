package dao;

import models.Produit;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des produits dans la base de données
 * 
 * Implémente les opérations CRUD (Create, Read, Update, Delete)
 * et fournit des méthodes utilitaires pour le stock.
 */
public class ProduitDAO implements IGenericDAO<Produit> {

    @Override
    public void create(Produit produit) throws Exception {
        String sql = "INSERT INTO produit (nom, categorie_id, prix_vente, stock_actuel, seuil_alerte) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, produit.getNom());
            pstmt.setInt(2, produit.getCategorieId());
            pstmt.setDouble(3, produit.getPrixVente());
            pstmt.setInt(4, produit.getStockActuel());
            pstmt.setInt(5, produit.getSeuilAlerte());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    produit.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public Produit read(int id) throws Exception {
        String sql = "SELECT * FROM produit WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Produit(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getInt("categorie_id"),
                            rs.getDouble("prix_vente"),
                            rs.getInt("stock_actuel"),
                            rs.getInt("seuil_alerte")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<Produit> readAll() throws Exception {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produit ORDER BY nom";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                produits.add(new Produit(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("categorie_id"),
                        rs.getDouble("prix_vente"),
                        rs.getInt("stock_actuel"),
                        rs.getInt("seuil_alerte")
                ));
            }
        }
        return produits;
    }

    @Override
    public void update(Produit produit) throws Exception {
        String sql = "UPDATE produit SET nom = ?, categorie_id = ?, prix_vente = ?, stock_actuel = ?, seuil_alerte = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, produit.getNom());
            pstmt.setInt(2, produit.getCategorieId());
            pstmt.setDouble(3, produit.getPrixVente());
            pstmt.setInt(4, produit.getStockActuel());
            pstmt.setInt(5, produit.getSeuilAlerte());
            pstmt.setInt(6, produit.getId());
            
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM produit WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    /**
     * Récupère les produits d'une catégorie spécifique
     */
    public List<Produit> readByCategorie(int categorieId) throws Exception {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produit WHERE categorie_id = ? ORDER BY nom";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, categorieId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    produits.add(new Produit(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getInt("categorie_id"),
                            rs.getDouble("prix_vente"),
                            rs.getInt("stock_actuel"),
                            rs.getInt("seuil_alerte")
                    ));
                }
            }
        }
        return produits;
    }

    /**
     * Récupère les produits en rupture ou sous alerte
     */
    public List<Produit> readAlertProducts() throws Exception {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT * FROM produit WHERE stock_actuel < seuil_alerte ORDER BY nom";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                produits.add(new Produit(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("categorie_id"),
                        rs.getDouble("prix_vente"),
                        rs.getInt("stock_actuel"),
                        rs.getInt("seuil_alerte")
                ));
            }
        }
        return produits;
    }

    /**
     * Augmente le stock d'un produit
     */
    public void increaseStock(int produitId, int quantite) throws Exception {
        String sql = "UPDATE produit SET stock_actuel = stock_actuel + ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quantite);
            pstmt.setInt(2, produitId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Diminue le stock d'un produit
     */
    public void decreaseStock(int produitId, int quantite) throws Exception {
        String sql = "UPDATE produit SET stock_actuel = stock_actuel - ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quantite);
            pstmt.setInt(2, produitId);
            pstmt.executeUpdate();
        }
    }
}
