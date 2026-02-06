package utils;

/**
 * Petit utilitaire pour exécuter les exports sans lancer l'UI.
 */
public class RunExport {
    public static void main(String[] args) {
        try {
            System.out.println("Démarrage de l'export...");
            ExportUtils.exportDefaultExports();
            System.out.println("Export terminé. Fichiers dans le dossier 'exports'.");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'export : " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
