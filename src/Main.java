import ui.frames.LoginFrame;
import utils.DatabaseConnection;

/**
 * Classe principale - Point d'entrée de l'application RestaurantApp
 */
public class Main {
    public static void main(String[] args) {
        // Test de la connexion à la base de données
        System.out.println("=== Démarrage de RestaurantApp ===");
        System.out.println("Vérification de la connexion à la base de données...");
        
        if (!DatabaseConnection.testConnection()) {
            System.err.println("❌ Impossible de se connecter à la base de données!");
            System.err.println("Assurez-vous que:");
            System.err.println("1. MySQL est en cours d'exécution");
            System.err.println("2. La base de données 'restaurant' a été créée (exécutez restaurant.sql)");
            System.err.println("3. Le fichier mysql-connector.jar est dans le dossier 'lib'");
            System.exit(1);
        }

        System.out.println("✓ Connexion établie avec succès");
        System.out.println("Lancement de l'interface graphique...");

        // Lancer l'écran de connexion
        java.awt.EventQueue.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
