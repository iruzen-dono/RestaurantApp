package dao;

import java.util.List;

/**
 * INTERFACE GÉNÉRIQUE DAO - Pattern Data Access Object
 * 
 * Cette interface définit les opérations CRUD (Create, Read, Update, Delete)
 * communes à TOUS les DAO du projet. Elle utilise la généricité Java <T>
 * pour fonctionner avec n'importe quel type d'entité.
 * 
 * ✨ AVANTAGES:
 * - Code réutilisable: Une seule interface pour tous les DAO
 * - Cohérence: Tous les DAO ont les mêmes méthodes CRUD
 * - Contrat: Force chaque DAO à implémenter les 5 méthodes obligatoires
 * - Généricité: Fonctionne avec Produit, Utilisateur, Commande, etc
 * 
 * 🔄 CYCLE DE VIE DES DONNÉES:
 * 
 *    UI (ProduitPanel)
 *       ↓
 *    DAO (ProduitDAO implements IGenericDAO<Produit>)
 *       ├─ CREATE: Ajouter un nouveau produit en BD
 *       ├─ READ:   Récupérer un produit spécifique par ID
 *       ├─ READ ALL: Récupérer tous les produits
 *       ├─ UPDATE: Modifier un produit existant
 *       └─ DELETE: Supprimer un produit
 *       ↓
 *    BD (MySQL table produit)
 * 
 * @param <T> Type générique (Produit, Utilisateur, Commande, etc)
 * 
 * @author Développeur
 * @version 1.0
 */
public interface IGenericDAO<T> {
    
    /**
     * OPÉRATION CREATE - Créer / Ajouter un enregistrement
     * 
     * Équivalent SQL: INSERT INTO table VALUES (...)
     * 
     * Exemple:
     *   Produit produit = new Produit("Coca", 1, 2.50, 50, 10);
     *   dao.create(produit);  // Ajoute le produit en BD
     * 
     * @param t l'objet à créer en base de données
     * @throws Exception si erreur lors de l'insertion
     */
    void create(T t) throws Exception;
    
    /**
     * OPÉRATION READ - Lire / Récupérer UN enregistrement par ID
     * 
     * Équivalent SQL: SELECT * FROM table WHERE id = ?
     * 
     * Exemple:
     *   Produit produit = dao.read(5);  // Récupère le produit avec id=5
     *   if (produit != null) { ... }
     * 
     * @param id l'identifiant de l'objet à récupérer
     * @return l'objet trouvé, ou null si non trouvé
     * @throws Exception si erreur de base de données
     */
    T read(int id) throws Exception;
    
    /**
     * OPÉRATION READ ALL - Lire / Récupérer TOUS les enregistrements
     * 
     * Équivalent SQL: SELECT * FROM table
     * 
     * Exemple:
     *   List<Produit> produits = dao.readAll();
     *   for (Produit p : produits) { ... }
     * 
     * @return une liste de tous les objets dans la table
     * @throws Exception si erreur de base de données
     */
    List<T> readAll() throws Exception;
    
    /**
     * OPÉRATION UPDATE - Modifier / Mettre à jour un enregistrement
     * 
     * Équivalent SQL: UPDATE table SET col1=val1, col2=val2 WHERE id=?
     * 
     * Note: L'objet T doit avoir un ID pour savoir quel enregistrement modifier
     * 
     * Exemple:
     *   Produit produit = dao.read(5);     // Récupère le produit
     *   produit.setPrixVente(3.50);         // Modifie le prix
     *   dao.update(produit);                // Enregistre en BD
     * 
     * @param t l'objet contenant les modifications (doit avoir un ID)
     * @throws Exception si erreur lors de la mise à jour
     */
    void update(T t) throws Exception;
    
    /**
     * OPÉRATION DELETE - Supprimer / Supprimer un enregistrement
     * 
     * Équivalent SQL: DELETE FROM table WHERE id = ?
     * 
     * ⚠️ ATTENTION: Cette opération est IRRÉVERSIBLE!
     * 
     * Exemple:
     *   dao.delete(5);  // Supprime le produit avec id=5
     * 
     * @param id l'identifiant de l'objet à supprimer
     * @throws Exception si erreur lors de la suppression (ex: contrainte FK)
     */
    void delete(int id) throws Exception;
}
