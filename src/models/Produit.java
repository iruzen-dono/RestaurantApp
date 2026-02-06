package models;

/**
 * Classe représentant un produit du restaurant
 */
public class Produit {
    private int id;
    private String nom;
    private int categorieId;
    private double prixVente;
    private int stockActuel;
    private int seuilAlerte;

    public Produit() {
    }

    public Produit(int id, String nom, int categorieId, double prixVente, int stockActuel, int seuilAlerte) {
        this.id = id;
        this.nom = nom;
        this.categorieId = categorieId;
        this.prixVente = prixVente;
        this.stockActuel = stockActuel;
        this.seuilAlerte = seuilAlerte;
    }

    public Produit(String nom, int categorieId, double prixVente, int stockActuel, int seuilAlerte) {
        this.nom = nom;
        this.categorieId = categorieId;
        this.prixVente = prixVente;
        this.stockActuel = stockActuel;
        this.seuilAlerte = seuilAlerte;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getCategorieId() {
        return categorieId;
    }

    public void setCategorieId(int categorieId) {
        this.categorieId = categorieId;
    }

    public double getPrixVente() {
        return prixVente;
    }

    public void setPrixVente(double prixVente) {
        this.prixVente = prixVente;
    }

    public int getStockActuel() {
        return stockActuel;
    }

    public void setStockActuel(int stockActuel) {
        this.stockActuel = stockActuel;
    }

    public int getSeuilAlerte() {
        return seuilAlerte;
    }

    public void setSeuilAlerte(int seuilAlerte) {
        this.seuilAlerte = seuilAlerte;
    }

    @Override
    public String toString() {
        return nom;
    }
}
