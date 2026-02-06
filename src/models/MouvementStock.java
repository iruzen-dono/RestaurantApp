package models;

import java.time.LocalDate;

/**
 * Classe représentant un mouvement de stock
 */
public class MouvementStock {
    public enum TypeMouvement {
        ENTREE, SORTIE
    }

    private int id;
    private int produitId;
    private TypeMouvement type;
    private int quantite;
    private LocalDate dateMouvement;
    private String motif;

    public MouvementStock() {
    }

    public MouvementStock(int id, int produitId, TypeMouvement type, int quantite, LocalDate dateMouvement, String motif) {
        this.id = id;
        this.produitId = produitId;
        this.type = type;
        this.quantite = quantite;
        this.dateMouvement = dateMouvement;
        this.motif = motif;
    }

    public MouvementStock(int produitId, TypeMouvement type, int quantite, LocalDate dateMouvement, String motif) {
        this.produitId = produitId;
        this.type = type;
        this.quantite = quantite;
        this.dateMouvement = dateMouvement;
        this.motif = motif;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProduitId() {
        return produitId;
    }

    public void setProduitId(int produitId) {
        this.produitId = produitId;
    }

    public TypeMouvement getType() {
        return type;
    }

    public void setType(TypeMouvement type) {
        this.type = type;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public LocalDate getDateMouvement() {
        return dateMouvement;
    }

    public void setDateMouvement(LocalDate dateMouvement) {
        this.dateMouvement = dateMouvement;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }
}
