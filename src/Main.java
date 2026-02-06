import ui.frames.LoginFrame;
import utils.DatabaseConnection;

/**
 * CLASSE PRINCIPALE - Point d'entrée de l'application RestaurantApp
 * 
 * Cette classe est le premier point d'exécution de l'application.
 * Elle effectue les vérifications initiales et lance l'interface graphique.
 * 
 * Flux d'exécution:
 * 1. Affiche le message de démarrage
 * 2. Teste la connexion à la base de données MySQL
 * 3. Si echec -> Affiche les erreurs possibles et quitte
 * 4. Si succès -> Lance l'écran de connexion (LoginFrame)
 * 
 * @author Développeur
 * @version 1.0
 */
public class Main {
    
    /**
     * Méthode main - Point d'entrée obligatoire d'une application Java
     * 
     * @param args Arguments de la ligne de commande (non utilisés ici)
     */
    public static void main(String[] args) {
        // ÉTAPE 1: Afficher le message de démarrage
        System.out.println("=== Démarrage de RestaurantApp ===");
        System.out.println("Vérification de la connexion à la base de données...");
        
        // ÉTAPE 2: Tester la connexion à la BD
        // DatabaseConnection.testConnection() retourne true si la connexion fonctionne
        if (!DatabaseConnection.testConnection()) {
            // ÉTAPE 3: Si la connexion échoue, afficher les causes possibles
            System.err.println("❌ Impossible de se connecter à la base de données!");
            System.err.println("Assurez-vous que:");
            System.err.println("1. MySQL est en cours d'exécution");
            System.err.println("2. La base de données 'restaurant' a été créée (exécutez restaurant.sql)");
            System.err.println("3. Le fichier mysql-connector.jar est dans le dossier 'lib'");
            System.err.println("4. Les identifiants dans DatabaseConnection.java sont corrects (USER, PASSWORD)");
            
            // Quitter l'application avec code d'erreur
            System.exit(1);
        }

        // ÉTAPE 4: Afficher le succès
        System.out.println("✓ Connexion établie avec succès");
        System.out.println("Lancement de l'interface graphique...");

        // ÉTAPE 5: Lancer l'écran de connexion dans le thread Swing
        // EventQueue.invokeLater() assure que la création d'interface se fait dans le bon thread
        java.awt.EventQueue.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
