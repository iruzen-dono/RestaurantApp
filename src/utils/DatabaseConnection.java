package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe de gestion de la connexion à la base de données
 * 
 * Utilise le pattern Singleton pour garantir une seule connexion
 * réutilisée pendant toute l'exécution de l'application.
 */
public class DatabaseConnection {
    
    // Configuration de la base de données - À modifier selon votre environnement
    private static final String URL = "jdbc:mysql://localhost:3306/restaurant?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC";
    private static final String USER = "root";          // À remplacer par votre utilisateur MySQL
    private static final String PASSWORD = "0000";      // À remplacer par votre mot de passe
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    private static Connection connection = null;

    /**
     * Récupère la connexion à la base de données
     * Si la connexion n'existe pas ou est fermée, crée une nouvelle connexion
     * 
     * @return Connection l'objet de connexion
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName(DRIVER);
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connexion à la base de données établie");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur: Driver MySQL non trouvé");
            System.err.println("Assurez-vous que mysql-connector.jar est dans le dossier lib/");
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données: " + e.getMessage());
        }
        return connection;
    }

    /**
     * Ferme la connexion à la base de données
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connexion fermée");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture: " + e.getMessage());
        }
    }

    /**
     * Teste la connexion à la base de données
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
