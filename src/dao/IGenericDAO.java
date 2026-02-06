package dao;

import java.util.List;

/**
 * Interface générique pour les opérations CRUD (Create, Read, Update, Delete)
 * 
 * Cette interface définit le contrat que doit respecter chaque DAO.
 * Elle utilise les Generics (<T>) pour être réutilisable avec n'importe quel type d'objet.
 * 
 * Les classes implémentant cette interface:
 * - UtilisateurDAO
 * - CategorieDAO
 * - ProduitDAO
 * - CommandeDAO
 * - LigneCommandeDAO
 * - MouvementStockDAO
 * 
 * @param <T> Le type d'entité gérée par le DAO
 */
public interface IGenericDAO<T> {
    
    /**
     * Crée une nouvelle entité en base de données
     * 
     * @param t L'objet à insérer
     * @throws Exception Si erreur lors de l'insertion
     */
    void create(T t) throws Exception;
    
    /**
     * Récupère une entité par son ID
     * 
     * @param id L'identifiant de l'entité
     * @return L'objet trouvé, ou null s'il n'existe pas
     * @throws Exception Si erreur de base de données
     */
    T read(int id) throws Exception;
    
    /**
     * Récupère toutes les entités du type
     * 
     * @return Une liste de tous les objets
     * @throws Exception Si erreur de base de données
     */
    List<T> readAll() throws Exception;
    
    /**
     * Modifie une entité existante
     * 
     * @param t L'objet contenant les données mises à jour
     * @throws Exception Si erreur lors de la mise à jour
     */
    void update(T t) throws Exception;
    
    /**
     * Supprime une entité par son ID
     * 
     * @param id L'identifiant de l'entité à supprimer
     * @throws Exception Si erreur lors de la suppression
     */
    void delete(int id) throws Exception;
}
