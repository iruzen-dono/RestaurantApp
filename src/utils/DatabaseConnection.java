package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * CLASSE DE GESTION DE CONNEXION À LA BASE DE DONNÉES
 * 
 * Cette classe gère l'accès à la base de données MySQL.
 * Elle utilise le pattern Singleton pour assurer qu'une seule connexion
 * est créée et réutilisée pendant toute l'exécution de l'application.
 * 
 * ⚠️ IMPORTANT: À MODIFIER POUR VOTRE CONFIGURATION MYSQL
 * 
 * Responsabilités:
 * - Créer et maintenir la connexion à MySQL
 * - Tester la disponibilité de la base de données
 * - Fermer proprement la connexion
 * 
 * @author Développeur
 * @version 1.0
 */
public class DatabaseConnection {
    
    // ═══════════════════════════════════════════════════════════════
    // ⚠️ CONFIGURATION À MODIFIER SELON VOTRE ENVIRONNEMENT
    // ═══════════════════════════════════════════════════════════════
    
    /**
     * URL de connexion à MySQL
     * Format: jdbc:mysql://[HOST]:[PORT]/[DATABASE]
     * 
     * localhost:3306 = serveur local, port SQL standard
     * restaurant = nom de la base de données
     * Paramètres: ?useUnicode=true&characterEncoding=utf8...
     * 
     * ✏️ À MODIFIER: Remplacez localhost par l'adresse du serveur MySQL
     */
    private static final String URL = "jdbc:mysql://localhost:3306/restaurant?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC";
    
    /**
     * Utilisateur MySQL
     * ✏️ À MODIFIER: Remplacez par votre utilisateur MySQL (ex: "root", "admin", etc)
     */
    private static final String USER = "root";
    
    /**
     * Mot de passe MySQL
     * ✏️ À MODIFIER: Remplacez par votre mot de passe (vide "" si pas de password)
     */
    private static final String PASSWORD = "0000";
    
    /**
     * Driver JDBC pour MySQL
     * Ce driver permet à Java de communiquer avec MySQL
     */
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    /**
     * Variable de classe pour stocker la connexion (Singleton)
     * Une seule connexion pour toute l'application
     */
    private static Connection connection = null;

    // ═══════════════════════════════════════════════════════════════
    // MÉTHODES PUBLIQUES
    // ═══════════════════════════════════════════════════════════════

    /**
     * Obtient la connexion à la base de données
     * 
     * Pattern Singleton: Si la connexion n'existe pas ou est fermée,
     * elle est créée. Sinon, la connexion existante est retournée.
     * 
     * Flux:
     * 1. Vérifier si connection == null ou connection.isClosed()
     * 2. Si oui: Charger le driver MySQL et créer une nouvelle connexion
     * 3. Retourner la connexion
     * 
     * @return Connection l'objet de connexion à MySQL (ou null si erreur)
     */
    public static Connection getConnection() {
        try {
            // Vérifier si la connexion n'existe pas ou est fermée
            if (connection == null || connection.isClosed()) {
                // Charger le driver MySQL (obligatoire avant de se connecter)
                Class.forName(DRIVER);
                
                // Créer la connexion avec DriverManager
                // DriverManager utilise URL, USER, PASSWORD pour se connecter
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✓ Connexion à la base de données établie");
            }
        } catch (ClassNotFoundException e) {
            // Le driver MySQL n'est pas trouvé
            // Cause: mysql-connector.jar manquant dans le dossier lib/
            System.err.println("❌ Driver MySQL non trouvé: " + e.getMessage());
            System.err.println("Assurez-vous que le fichier mysql-connector.jar est dans le répertoire 'lib'");
        } catch (SQLException e) {
            // Erreur lors de la connexion à MySQL
            // Causes possibles: serveur hors ligne, identifiants incorrects, BD inexistante
            System.err.println("❌ Erreur de connexion à la base de données: " + e.getMessage());
            System.err.println("Vérifiez:");
            System.err.println("  - MySQL est-il en cours d'exécution?");
            System.err.println("  - Les identifiants (USER, PASSWORD) sont-ils corrects?");
            System.err.println("  - La base de données 'restaurant' existe-t-elle?");
        }
        return connection;
    }

    /**
     * Ferme la connexion à la base de données proprement
     * 
     * À appeler avant de fermer l'application pour libérer les ressources.
     * 
     * Flux:
     * 1. Vérifier que connection ne soit pas null et ouverte
     * 2. Appeler connection.close() pour fermer la connexion
     * 3. Afficher un message de confirmation
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Connexion fermée");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la fermeture de la connexion: " + e.getMessage());
        }
    }

    /**
     * Teste la connexion à la base de données
     * 
     * Utile pour vérifier si MySQL est accessible avant de démarrer l'app.
     * (Appelé dans Main.java au démarrage)
     * 
     * @return true si la connexion fonctionne, false sinon
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
