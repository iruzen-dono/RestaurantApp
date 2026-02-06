package dao;

import models.Produit;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO PRODUIT - Data Access Object pour la table 'produit'
 * 
 * Cette classe gère TOUTES les opérations sur la table MySQL 'produit'.
 * Elle implémente l'interface IGenericDAO<Produit> pour les opérations CRUD.
 * 
 * 🔍 FONCTIONNEMENT:
 * - PreparedStatement: Protège contre l'injection SQL (paramètres liés avec ?)
 * - try-with-resources: Ferme automatiquement les ressources (Connection, Statement, ResultSet)
 * - ResultSet: Résultat des requêtes SELECT
 * - executeUpdate(): Pour INSERT/UPDATE/DELETE
 * - executeQuery(): Pour SELECT
 * 
 * 💡 EXEMPLE D'UTILISATION:
 *   ProduitDAO dao = new ProduitDAO();
 *   List<Produit> produits = dao.readAll();           // Récupère tous les produits
 *   Produit p = dao.read(5);                          // Récupère le produit avec id=5
 *   Produit nouveau = new Produit("Coca", 1, 2.50, 50, 10);
 *   dao.create(nouveau);                              // Ajoute le produit
 *   p.setPrixVente(3.00);
 *   dao.update(p);                                     // Modifie le produit
 *   dao.delete(5);                                     // Supprime le produit
 * 
 * @author Développeur
 * @version 1.0
 */
public class ProduitDAO implements IGenericDAO<Produit> {

    /**
     * CREATE - Ajouter un nouveau produit en base de données
     * 
     * SQL généré: INSERT INTO produit (nom, categorie_id, prix_vente, stock_actuel, seuil_alerte)
     *             VALUES (?, ?, ?, ?, ?)
     *             
     * Flux:
     * 1. Récupérer une connexion à la BD
     * 2. Créer un PreparedStatement avec RETURN_GENERATED_KEYS
     *    (pour récupérer l'ID auto-généré)
     * 3. Remplir les paramètres (?) avec les valeurs du produit
     * 4. Exécuter l'insertion (executeUpdate)
     * 5. Récupérer l'ID généré et l'assigner au produit
     * 6. Fermer automatiquement la connexion et statement (try-with-resources)
     * 
     * @param produit le produit à ajouter
     * @throws Exception si erreur d'insertion ou de base de données
     */
    @Override
    public void create(Produit produit) throws Exception {
        // Requête SQL avec paramètres liés (?)
        String sql = "INSERT INTO produit (nom, categorie_id, prix_vente, stock_actuel, seuil_alerte) VALUES (?, ?, ?, ?, ?)";
        
        // try-with-resources: Ferme automatiquement connection et pstmt
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
             
            // Remplir les paramètres (?) de la requête SQL
            pstmt.setString(1, produit.getNom());              // ? position 1 = nom
            pstmt.setInt(2, produit.getCategorieId());         // ? position 2 = categorie_id
            pstmt.setDouble(3, produit.getPrixVente());        // ? position 3 = prix_vente
            pstmt.setInt(4, produit.getStockActuel());         // ? position 4 = stock_actuel
            pstmt.setInt(5, produit.getSeuilAlerte());         // ? position 5 = seuil_alerte
            
            // Exécuter la requête INSERT
            pstmt.executeUpdate();

            // Récupérer l'ID auto-généré par MySQL (AUTO_INCREMENT)
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    // Assigner l'ID généré au produit objet
                    produit.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    /**
     * READ - Récupérer UN produit par son ID
     * 
     * SQL généré: SELECT * FROM produit WHERE id = ?
     * 
     * Flux:
     * 1. Créer la requête SELECT avec WHERE id = ?
     * 2. Définir le paramètre id
     * 3. Exécuter la requête (executeQuery)
     * 4. Si un résultat existe (rs.next()), créer l'objet Produit
     * 5. Retourner le produit ou null si non trouvé
     * 
     * @param id l'ID du produit recherché
     * @return le Produit trouvé, ou null si non trouvé
     * @throws Exception si erreur de base de données
     */
    @Override
    public Produit read(int id) throws Exception {
        String sql = "SELECT * FROM produit WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            // Définir le paramètre id
            pstmt.setInt(1, id);
            
            // Exécuter la requête SELECT
            try (ResultSet rs = pstmt.executeQuery()) {
                // Si une ligne est retournée (produit trouvé)
                if (rs.next()) {
                    // Créer l'objet Produit avec les données de la ligne
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
        // Si aucun produit trouvé
        return null;
    }

    /**
     * READ ALL - Récupérer TOUS les produits
     * 
     * SQL généré: SELECT * FROM produit ORDER BY nom
     * 
     * Flux:
     * 1. Créer une liste vide pour stocker les produits
     * 2. Exécuter la requête SELECT sans WHERE
     * 3. Boucler sur chaque ligne du ResultSet
     * 4. Pour chaque ligne, créer un objet Produit et l'ajouter à la liste
     * 5. Retourner la liste complète
     * 
     * @return liste de tous les produits (vide si aucun)
     * @throws Exception si erreur de base de données
     */
    @Override
    public List<Produit> readAll() throws Exception {
        List<Produit> produits = new ArrayList<>();  // Liste pour stocker les résultats
        String sql = "SELECT * FROM produit ORDER BY nom";  // Trié par nom
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
             
            // Boucler sur toutes les lignes retournées
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
        return produits;  // Retourner la liste (vide si aucun résultat)
    }

    /**
     * UPDATE - Modifier un produit existant
     * 
     * SQL généré: UPDATE produit SET nom = ?, categorie_id = ?, ... WHERE id = ?
     * 
     * Flux:
     * 1. Créer la requête UPDATE avec WHERE id = ?
     * 2. Remplir TOUS les champs du produit
     * 3. Créer la condition WHERE id = ?
     * 4. Exécuter la mise à jour (executeUpdate)
     * 
     * ⚠️ IMPORTANT: Le produit DOIT avoir un ID pour identifier quelle ligne modifier!
     * 
     * @param produit le produit modifié (doit avoir un ID)
     * @throws Exception si erreur de base de données
     */
    @Override
    public void update(Produit produit) throws Exception {
        String sql = "UPDATE produit SET nom = ?, categorie_id = ?, prix_vente = ?, stock_actuel = ?, seuil_alerte = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            // Remplir les paramètres UPDATE SET
            pstmt.setString(1, produit.getNom());
            pstmt.setInt(2, produit.getCategorieId());
            pstmt.setDouble(3, produit.getPrixVente());
            pstmt.setInt(4, produit.getStockActuel());
            pstmt.setInt(5, produit.getSeuilAlerte());
            
            // Remplir le paramètre WHERE id = ?
            pstmt.setInt(6, produit.getId());
            
            // Exécuter la mise à jour
            pstmt.executeUpdate();
        }
    }

    /**
     * DELETE - Supprimer un produit
     * 
     * SQL généré: DELETE FROM produit WHERE id = ?
     * 
     * ⚠️ ATTENTION: Cette opération est IRRÉVERSIBLE!
     * 
     * Flux:
     * 1. Créer la requête DELETE
     * 2. Définir le paramètre id
     * 3. Exécuter la suppression
     * 
     * @param id l'ID du produit à supprimer
     * @throws Exception si erreur de base de données ou contrainte FK
     */
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
     * MÉTHODE BONUS - Récupère les produits d'une catégorie spécifique
     * 
     * SQL: SELECT * FROM produit WHERE categorie_id = ? ORDER BY nom
     * 
     * Utilisé dans ProduitPanel pour filtrer par catégorie
     * 
     * @param categorieId l'ID de la catégorie à filtrer
     * @return liste des produits de cette catégorie
     * @throws Exception si erreur de base de données
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
     * MÉTHODE BONUS - Récupère les produits en rupture ou sous alerte
     * 
     * SQL: SELECT * FROM produit WHERE stock_actuel < seuil_alerte
     * 
     * Utile pour identifier les produits à réapprovisionner
     * 
     * @return liste des produits en rupture/alerte
     * @throws Exception si erreur de base de données
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
     * MÉTHODE BONUS - Augmente le stock d'un produit
     * 
     * SQL: UPDATE produit SET stock_actuel = stock_actuel + ? WHERE id = ?
     * 
     * Utilisé lors d'une entrée de stock (MouvementStock type ENTREE)
     * 
     * @param produitId l'ID du produit
     * @param quantite la quantité à ajouter
     * @throws Exception si erreur de base de données
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
     * MÉTHODE BONUS - Diminue le stock d'un produit
     * 
     * SQL: UPDATE produit SET stock_actuel = stock_actuel - ? WHERE id = ?
     * 
     * Utilisé lors d'une sortie de stock ou d'une commande
     * 
     * @param produitId l'ID du produit
     * @param quantite la quantité à retirer
     * @throws Exception si erreur de base de données
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
