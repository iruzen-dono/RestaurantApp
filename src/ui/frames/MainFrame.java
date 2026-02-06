package ui.frames;

import models.Utilisateur;
import ui.panels.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Fenêtre principale avec menu de navigation
 */
public class MainFrame extends JFrame {
    private Utilisateur utilisateurConnecte;
    private JTabbedPane tabbedPane;
    private StatistiquesPanel statistiquesPanel;

    public MainFrame(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
        initComponents();
    }

    private void initComponents() {
        setTitle("RestaurantApp - " + utilisateurConnecte.getLogin());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(41, 128, 185));
        menuBar.setForeground(Color.WHITE);
        
        JMenu fileMenu = new JMenu("Fichier");
        fileMenu.setForeground(Color.WHITE);
        
        JMenuItem disconnectItem = new JMenuItem("Déconnexion");
        disconnectItem.addActionListener(e -> disconnect());
        JMenuItem exitItem = new JMenuItem("Quitter");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(disconnectItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // Main panel with header
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(0, 60));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("GESTION DE RESTAURANT");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel userLabel = new JLabel("Utilisateur: " + utilisateurConnecte.getLogin());
        userLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        userLabel.setForeground(Color.WHITE);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userLabel, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane for different panels
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 12));
        tabbedPane.addTab("Produits", new ProduitPanel());
        tabbedPane.addTab("Stock", new StockPanel());
        tabbedPane.addTab("Commandes", new CommandePanel(this));
        statistiquesPanel = new StatistiquesPanel();
        tabbedPane.addTab("Statistiques", statistiquesPanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void disconnect() {
        int result = JOptionPane.showConfirmDialog(this, 
            "Êtes-vous sûr de vouloir vous déconnecter?", 
            "Confirmation", 
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            dispose();
        }
    }

    public void refreshDashboard() {
        if (statistiquesPanel != null) {
            statistiquesPanel.refreshStats();
        }
    }
}
