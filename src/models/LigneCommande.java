package models;

/**
 * Classe représentant une ligne de commande
 */
public class LigneCommande {
    private int id;
    private int commandeId;
    private int produitId;
    private int quantite;
    private double prixUnitaire;
    private double montantLigne;

    public LigneCommande() {
    }

    public LigneCommande(int id, int commandeId, int produitId, int quantite, double prixUnitaire) {
        this.id = id;
        this.commandeId = commandeId;
        this.produitId = produitId;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.montantLigne = quantite * prixUnitaire;
    }

    public LigneCommande(int commandeId, int produitId, int quantite, double prixUnitaire) {
        this.commandeId = commandeId;
        this.produitId = produitId;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.montantLigne = quantite * prixUnitaire;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCommandeId() {
        return commandeId;
    }

    public void setCommandeId(int commandeId) {
        this.commandeId = commandeId;
    }

    public int getProduitId() {
        return produitId;
    }

    public void setProduitId(int produitId) {
        this.produitId = produitId;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
        recalculMontant();
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
        recalculMontant();
    }

    public double getMontantLigne() {
        return montantLigne;
    }

    private void recalculMontant() {
        this.montantLigne = quantite * prixUnitaire;
    }
}
