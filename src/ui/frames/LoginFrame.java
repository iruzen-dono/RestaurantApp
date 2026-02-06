package ui.frames;

import dao.UtilisateurDAO;
import models.Utilisateur;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ÉCRAN DE CONNEXION - LoginFrame
 * 
 * Cette fenêtre est la première écran que voit l'utilisateur à la démarrage.
 * Elle demande login + mot de passe, puis authentifie l'utilisateur.
 * 
 * 🔄 FLUX:
 * 1. Main.java crée LoginFrame
 * 2. L'utilisateur entre login et mot de passe
 * 3. Click "Connexion" -> authenticateUser()
 * 4. UtilisateurDAO.authenticate() vérifie en BD
 * 5. Si succès -> Ferme LoginFrame et ouvre MainFrame
 * 6. Si erreur -> Affiche message d'erreur
 * 
 * 🎨 COMPOSANTS SWING:
 * - JFrame: Fenêtre principale
 * - JPanel: Conteneur pour les composants
 * - JTextField: Champ texte pour le login
 * - JPasswordField: Champ masqué pour le mot de passe
 * - JButton: Boutons "Connexion" et "Quitter"
 * - JLabel: Textes et messages d'erreur
 * - GridBagLayout: Disposition en grille
 * 
 * @author Développeur
 * @version 1.0
 */
public class LoginFrame extends JFrame {
    
    //═══════════════════════════════════════════════════════════════
    // COMPOSANTS SWING (Éléments visuels)
    //═══════════════════════════════════════════════════════════════
    
    private JTextField loginField;           // Champ pour saisir le login
    private JPasswordField passwordField;    // Champ pour saisir le mot de passe (masqué)
    private JButton loginButton;             // Bouton "Connexion"
    private JButton exitButton;              // Bouton "Quitter"
    private JLabel messageLabel;             // Affiche les messages d'erreur
    
    //═══════════════════════════════════════════════════════════════
    // ACCÈS AUX DONNÉES
    //═══════════════════════════════════════════════════════════════
    
    private UtilisateurDAO utilisateurDAO;   // DAO pour accéder à la BD

    //═══════════════════════════════════════════════════════════════
    // CONSTRUCTEUR
    //═══════════════════════════════════════════════════════════════

    /**
     * Constructeur de LoginFrame
     * 
     * Flux:
     * 1. Créer l'instance du DAO
     * 2. Initialiser tous les composants Swing
     */
    public LoginFrame() {
        utilisateurDAO = new UtilisateurDAO();  // Créer le DAO
        initComponents();                       // Initialiser l'interface graphique
    }

    //═══════════════════════════════════════════════════════════════
    // INITIALISATION DE L'INTERFACE GRAPHIQUE
    //═══════════════════════════════════════════════════════════════

    /**
     * Initialise tous les composants Swing de la fenêtre de connexion
     * 
     * Étapes:
     * 1. Configurer la fenêtre (titre, taille, position)
     * 2. Créer un panel principal avec GridBagLayout
     * 3. Ajouter les composants (labels, champs, boutons)
     * 4. Ajouter les event listeners (action listeners)
     */
    private void initComponents() {
        // ═══════════════════════════════════════════════════════════════
        // CONFIGURATION DE LA FENÊTRE
        // ═══════════════════════════════════════════════════════════════
        
        setTitle("RestaurantApp - Connexion");              // Titre de la fenêtre
        setDefaultCloseOperation(EXIT_ON_CLOSE);           // Ferme l'app si X cliqué
        setSize(400, 250);                                 // Taille: 400x250 pixels
        setLocationRelativeTo(null);                       // Centrer sur l'écran
        setResizable(false);                               // Fenêtre non redimensionnable

        // ═══════════════════════════════════════════════════════════════
        // CRÉATION DU PANEL PRINCIPAL (avec GridBagLayout)
        // ═══════════════════════════════════════════════════════════════
        
        // GridBagLayout: Disposition flexible en grille (comme une feuille de calcul)
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();  // Contraintes de positionnement
        gbc.insets = new Insets(10, 10, 10, 10);           // Espaces autour des composants

        // ═══════════════════════════════════════════════════════════════
        // COMPOSANT 1: TITRE
        // ═══════════════════════════════════════════════════════════════
        
        JLabel titleLabel = new JLabel("Connexion RestaurantApp");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));  // Police: Arial, gras, 16pt
        gbc.gridx = 0;          // Colonne 0
        gbc.gridy = 0;          // Ligne 0
        gbc.gridwidth = 2;      // Occupe 2 colonnes
        panel.add(titleLabel, gbc);  // Ajouter au panel

        // ═══════════════════════════════════════════════════════════════
        // COMPOSANT 2: LABEL + CHAMP LOGIN
        // ═══════════════════════════════════════════════════════════════
        
        gbc.gridwidth = 1;      // Réinitialiser à 1 colonne
        gbc.gridy = 1;          // Ligne 1
        gbc.gridx = 0;          // Colonne 0 (label "Login:")
        panel.add(new JLabel("Login:"), gbc);
        
        gbc.gridx = 1;          // Colonne 1 (champ de saisie)
        loginField = new JTextField(15);  // Champ texte, largeur 15 caractères
        panel.add(loginField, gbc);

        // ═══════════════════════════════════════════════════════════════
        // COMPOSANT 3: LABEL + CHAMP MOT DE PASSE
        // ═══════════════════════════════════════════════════════════════
        
        gbc.gridy = 2;          // Ligne 2
        gbc.gridx = 0;          // Colonne 0 (label "Mot de passe:")
        panel.add(new JLabel("Mot de passe:"), gbc);
        
        gbc.gridx = 1;          // Colonne 1 (champ password)
        passwordField = new JPasswordField(15);  // Champ masqué (avec étoiles)
        panel.add(passwordField, gbc);

        // ═══════════════════════════════════════════════════════════════
        // COMPOSANT 4: LABEL DE MESSAGE (ERREUR)
        // ═══════════════════════════════════════════════════════════════
        
        gbc.gridy = 3;          // Ligne 3
        gbc.gridx = 0;          // Colonne 0
        gbc.gridwidth = 2;      // Occupe 2 colonnes
        messageLabel = new JLabel("");  // Vide au départ
        messageLabel.setForeground(Color.RED);  // Couleur rouge pour les erreurs
        panel.add(messageLabel, gbc);

        // ═══════════════════════════════════════════════════════════════
        // COMPOSANT 5: BOUTONS
        // ═══════════════════════════════════════════════════════════════
        
        gbc.gridy = 4;          // Ligne 4
        JPanel buttonPanel = new JPanel();  // Panel pour les boutons
        
        loginButton = new JButton("Connexion");
        exitButton = new JButton("Quitter");
        
        buttonPanel.add(loginButton);   // Ajouter bouton Connexion
        buttonPanel.add(exitButton);    // Ajouter bouton Quitter
        panel.add(buttonPanel, gbc);

        // ═══════════════════════════════════════════════════════════════
        // EVENT LISTENERS (Écouteurs d'événements)
        // ═══════════════════════════════════════════════════════════════
        
        // Bouton "Connexion" - Click
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authenticateUser();  // Appeler la méthode d'authentification
            }
        });

        // Bouton "Quitter" - Click
        exitButton.addActionListener(e -> System.exit(0));  // Quitter l'application

        // Touche ENTRÉE dans le champ login
        loginField.addActionListener(e -> authenticateUser());
        
        // Touche ENTRÉE dans le champ password
        passwordField.addActionListener(e -> authenticateUser());

        // ═══════════════════════════════════════════════════════════════
        // AJOUTER LE PANEL À LA FENÊTRE
        // ═══════════════════════════════════════════════════════════════
        
        add(panel);  // Ajouter le panel principal à la fenêtre
    }

    //═══════════════════════════════════════════════════════════════
    // MÉTHODE D'AUTHENTIFICATION
    //═══════════════════════════════════════════════════════════════

    /**
     * Authentifier l'utilisateur
     * 
     * Flux:
     * 1. Récupérer le login et le mot de passe saisis
     * 2. Valider qu'ils ne sont pas vides
     * 3. Appeler UtilisateurDAO.authenticate() pour vérifier en BD
     * 4. Si authentification OK:
     *    - Ouvrir MainFrame (menu principal)
     *    - Fermer LoginFrame (dispose)
     * 5. Si authentification échoue:
     *    - Afficher le message d'erreur
     *    - Vider le champ mot de passe
     * 6. Si exception:
     *    - Afficher le message d'erreur générale
     */
    private void authenticateUser() {
        // ÉTAPE 1: Récupérer les valeurs saisies
        String login = loginField.getText().trim();                    // Récupérer le login (trim = enlever espaces)
        String password = new String(passwordField.getPassword());     // Récupérer le mot de passe (getPassword retourne char[])

        // ÉTAPE 2: Valider que les champs ne sont pas vides
        if (login.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs");
            return;  // Stop l'exécution
        }

        try {
            // ÉTAPE 3: Appeler le DAO pour vérifier en BD
            // La méthode authenticate retourne:
            // - l'objet Utilisateur si login/password OK
            // - null si login/password incorrect
            Utilisateur user = utilisateurDAO.authenticate(login, password);
            
            // ÉTAPE 4: Vérifier si l'authentification a réussi
            if (user != null) {
                // ✅ AUTHENTIFICATION RÉUSSIE
                // Ouvrir la fenêtre principale et passer l'utilisateur
                new MainFrame(user).setVisible(true);
                
                // Fermer la fenêtre de connexion
                dispose();  // dispose() ferme complètement la fenêtre
            } else {
                // ❌ AUTHENTIFICATION ÉCHOUÉE
                messageLabel.setText("Login ou mot de passe incorrect");
                passwordField.setText("");  // Vider le champ password pour sécurité
            }
        } catch (Exception e) {
            // ❌ EXCEPTION / ERREUR DE CONNEXION
            messageLabel.setText("Erreur de connexion: " + e.getMessage());
            e.printStackTrace();  // Afficher l'erreur complète en console pour débogage
        }
    }
}
