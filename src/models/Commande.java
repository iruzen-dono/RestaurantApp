package models;

import java.time.LocalDate;

/**
 * Classe représentant une commande client
 */
public class Commande {
    public enum EtatCommande {
        EN_COURS, VALIDEE, ANNULEE
    }

    private int id;
    private LocalDate dateCommande;
    private EtatCommande etat;
    private double total;

    public Commande() {
        this.etat = EtatCommande.EN_COURS;
        this.total = 0;
    }

    public Commande(int id, LocalDate dateCommande, EtatCommande etat, double total) {
        this.id = id;
        this.dateCommande = dateCommande;
        this.etat = etat;
        this.total = total;
    }

    public Commande(LocalDate dateCommande) {
        this.dateCommande = dateCommande;
        this.etat = EtatCommande.EN_COURS;
        this.total = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDate dateCommande) {
        this.dateCommande = dateCommande;
    }

    public EtatCommande getEtat() {
        return etat;
    }

    public void setEtat(EtatCommande etat) {
        this.etat = etat;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
