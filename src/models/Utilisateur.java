package models;

/**
 * Classe représentant un utilisateur
 */
public class Utilisateur {
    private int id;
    private String login;
    private String motDePasse;

    public Utilisateur() {
    }

    public Utilisateur(int id, String login, String motDePasse) {
        this.id = id;
        this.login = login;
        this.motDePasse = motDePasse;
    }

    public Utilisateur(String login, String motDePasse) {
        this.login = login;
        this.motDePasse = motDePasse;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }
}
