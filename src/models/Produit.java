package models;

/**
 * CLASSE MODÈLE - Produit
 * 
 * Représente une entité "Produit" du restaurant.
 * C'est une simple classe de données (POJO: Plain Old Java Object)
 * avec des getters/setters pour accéder aux propriétés.
 * 
 * ✅ Utilisation:
 * - Stockage en base de données (table 'produit')
 * - Communication entre DAO et UI
 * - Manipulation en mémoire
 * 
 * 📊 Attributs:
 * - id: Identifiant unique (clé primaire en BD) - auto-généré par MySQL
 * - nom: Nom du produit (ex: "Coca Cola 33cl")
 * - categorieId: Référence à la catégorie (clé étrangère)
 * - prixVente: Prix de vente au client (en euros)
 * - stockActuel: Quantité en stock maintenant
 * - seuilAlerte: Stock minimum avant alerte (ex: 10 unités)
 * 
 * 🗄️ Correspond à la table BD:
 *   CREATE TABLE produit (
 *       id INT PRIMARY KEY AUTO_INCREMENT,
 *       nom VARCHAR(150),
 *       categorie_id INT,
 *       prix_vente DECIMAL(10, 2),
 *       stock_actuel INT,
 *       seuil_alerte INT,
 *       FOREIGN KEY (categorie_id) REFERENCES categorie(id)
 *   );
 * 
 * @author Développeur
 * @version 1.0
 */
public class Produit {
    //═══════════════════════════════════════════════════════════════
    // ATTRIBUTS (Propriétés du produit)
    //═══════════════════════════════════════════════════════════════
    
    private int id;              // Identifiant unique (PK)
    private String nom;          // Nom du produit
    private int categorieId;     // Référence à Categorie (FK)
    private double prixVente;    // Prix de vente
    private int stockActuel;     // Quantité actuelle
    private int seuilAlerte;     // Quantité minimale avant alerte

    //═══════════════════════════════════════════════════════════════
    // CONSTRUCTEURS
    //═══════════════════════════════════════════════════════════════

    /**
     * Constructeur vide
     * Utilisé par certains frameworks ou sérialisation
     */
    public Produit() {
    }

    /**
     * Constructeur complet avec ID
     * Utilisé pour créer un Produit depuis la BD (avec ID)
     * 
     * @param id identifiant du produit (vient de la BD)
     * @param nom nom du produit
     * @param categorieId ID de la catégorie
     * @param prixVente prix de vente
     * @param stockActuel quantité en stock
     * @param seuilAlerte seuil d'alerte
     */
    public Produit(int id, String nom, int categorieId, double prixVente, int stockActuel, int seuilAlerte) {
        this.id = id;
        this.nom = nom;
        this.categorieId = categorieId;
        this.prixVente = prixVente;
        this.stockActuel = stockActuel;
        this.seuilAlerte = seuilAlerte;
    }

    /**
     * Constructeur sans ID
     * Utilisé pour créer un nouveau Produit avant insertion en BD
     * L'ID sera généré par MySQL et assigné après insertion
     * 
     * @param nom nom du produit
     * @param categorieId ID de la catégorie
     * @param prixVente prix de vente
     * @param stockActuel quantité en stock
     * @param seuilAlerte seuil d'alerte
     * 
     * Exemple:
     *   Produit p = new Produit("Coca 33cl", 1, 2.50, 50, 10);
     *   dao.create(p);  // MySQL génère et assigne un ID
     */
    public Produit(String nom, int categorieId, double prixVente, int stockActuel, int seuilAlerte) {
        this.nom = nom;
        this.categorieId = categorieId;
        this.prixVente = prixVente;
        this.stockActuel = stockActuel;
        this.seuilAlerte = seuilAlerte;
    }

    //═══════════════════════════════════════════════════════════════
    // GETTERS et SETTERS
    //═══════════════════════════════════════════════════════════════

    /**
     * Récupère l'ID du produit
     * @return l'identifiant unique
     */
    public int getId() {
        return id;
    }

    /**
     * Assigne l'ID du produit
     * Appelé par ProduitDAO après insertion en BD
     * @param id l'identifiant unique
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Récupère le nom du produit
     * @return le nom (ex: "Burger Classique")
     */
    public String getNom() {
        return nom;
    }

    /**
     * Assigne un nouveau nom au produit
     * @param nom le nouveau nom
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Récupère l'ID de la catégorie (clé étrangère)
     * @return ID de la catégorie (ex: 1 = Boissons)
     */
    public int getCategorieId() {
        return categorieId;
    }

    /**
     * Assigne une nouvelle catégorie
     * @param categorieId nouvel ID de catégorie
     */
    public void setCategorieId(int categorieId) {
        this.categorieId = categorieId;
    }

    /**
     * Récupère le prix de vente
     * @return prix en euros (ex: 2.50)
     */
    public double getPrixVente() {
        return prixVente;
    }

    /**
     * Assigne un nouveau prix de vente
     * @param prixVente nouveau prix
     */
    public void setPrixVente(double prixVente) {
        this.prixVente = prixVente;
    }

    /**
     * Récupère le stock actuel
     * @return nombre de produits en stock
     */
    public int getStockActuel() {
        return stockActuel;
    }

    /**
     * Assigne un nouveau stock
     * @param stockActuel nouvelle quantité
     */
    public void setStockActuel(int stockActuel) {
        this.stockActuel = stockActuel;
    }

    /**
     * Récupère le seuil d'alerte
     * @return quantité minimale avant alerte
     */
    public int getSeuilAlerte() {
        return seuilAlerte;
    }

    /**
     * Assigne un nouveau seuil d'alerte
     * @param seuilAlerte nouvelle quantité d'alerte
     */
    public void setSeuilAlerte(int seuilAlerte) {
        this.seuilAlerte = seuilAlerte;
    }

    //═══════════════════════════════════════════════════════════════
    // MÉTHODES UTILITAIRES
    //═══════════════════════════════════════════════════════════════

    /**
     * Vérifier si le produit est en rupture de stock
     * @return true si stock < seuil d'alerte
     */
    public boolean isStockBas() {
        return stockActuel < seuilAlerte;
    }

    /**
     * Représentation textuelle du produit (pour affichage)
     * @return chaîne de caractères (ex: "Coca Cola 33cl (2.50€)")
     */
    @Override
    public String toString() {
        return nom + " (" + prixVente + "€)";
    }
}
