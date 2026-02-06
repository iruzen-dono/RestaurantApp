import ui.frames.LoginFrame;
import utils.DatabaseConnection;

/**
 * Classe principale - Point d'entrée de l'application RestaurantApp
 * 
 * Responsabilités:
 * - Tester la connexion à la base de données
 * - Lancer l'interface graphique
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("=== Démarrage de RestaurantApp ===");
        System.out.println("Vérification de la connexion à la base de données...");
        
        // Tester la connexion à la base de données
        if (!DatabaseConnection.testConnection()) {
            System.err.println("Erreur: Impossible de se connecter à la base de données!");
            System.err.println("Vérifiez:");
            System.err.println("- MySQL est-il en cours d'exécution?");
            System.err.println("- La base de données 'restaurant' existe-t-elle?");
            System.err.println("- Les identifiants dans DatabaseConnection.java sont-ils corrects?");
            System.exit(1);
        }

        System.out.println("Connexion établie avec succès");
        System.out.println("Lancement de l'interface graphique...");

        // Lancer l'écran de connexion dans le thread Swing
        java.awt.EventQueue.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
