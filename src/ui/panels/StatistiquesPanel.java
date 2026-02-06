package ui.panels;

import dao.CommandeDAO;
import dao.ProduitDAO;
import models.Commande;
import models.Produit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.AbstractMap;
import java.util.List;

/**
 * Panel pour les statistiques
 */
public class StatistiquesPanel extends JPanel {
    private CommandeDAO commandeDAO;
    private ProduitDAO produitDAO;
    private JLabel totalCommandesLabel, totalVentesLabel, totalProduitLabel, stockAlertLabel;

    public StatistiquesPanel() {
        commandeDAO = new CommandeDAO();
        produitDAO = new ProduitDAO();
        initComponents();
        refreshStats();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Tableau de Bord Statistiques");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Main stats panel
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(2, 2, 20, 20));
        statsPanel.setBorder(new EmptyBorder(40, 20, 40, 20));

        // Card 1: Total Commandes
        var card1 = createStatCard("Total Commandes", "0", new Color(52, 152, 219));
        totalCommandesLabel = card1.getValue();
        statsPanel.add(card1.getKey());

        // Card 2: Total Ventes
        var card2 = createStatCard("Total des Ventes (€)", "0.00", new Color(46, 204, 113));
        totalVentesLabel = card2.getValue();
        statsPanel.add(card2.getKey());

        // Card 3: Total Produits
        var card3 = createStatCard("Total Produits", "0", new Color(155, 89, 182));
        totalProduitLabel = card3.getValue();
        statsPanel.add(card3.getKey());

        // Card 4: Produits en Alerte
        var card4 = createStatCard("Produits en Alerte Stock", "0", new Color(230, 126, 34));
        stockAlertLabel = card4.getValue();
        statsPanel.add(card4.getKey());

        add(statsPanel, BorderLayout.CENTER);

        // Bottom info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(236, 240, 241));
        infoPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        infoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton refreshButton = new JButton("Actualiser");
        refreshButton.setBackground(new Color(41, 128, 185));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> refreshStats());

        infoPanel.add(new JLabel("Données actualisées à "));
        infoPanel.add(new JLabel(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        infoPanel.add(Box.createHorizontalStrut(20));
        infoPanel.add(refreshButton);

        add(infoPanel, BorderLayout.SOUTH);
    }

    private AbstractMap.SimpleEntry<JPanel, JLabel> createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(color, 3));

        // Header panel with color
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(color);
        headerPanel.setPreferredSize(new Dimension(0, 50));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(10, 15, 10, 15));
        headerPanel.add(titleLabel);

        // Value panel
        JPanel valuePanel = new JPanel();
        valuePanel.setBackground(Color.WHITE);
        valuePanel.setLayout(new BorderLayout());
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
        valuePanel.add(valueLabel);

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(valuePanel, BorderLayout.CENTER);

        return new AbstractMap.SimpleEntry<>(card, valueLabel);
    }

    public void refreshStats() {
        try {
            // Total commandes
            List<Commande> commandes = commandeDAO.readAll();
            totalCommandesLabel.setText(String.valueOf(commandes.size()));

            // Total ventes (commandes VALIDEES)
            double totalVentes = 0.0;
            for (Commande c : commandes) {
                if (c.getEtat().toString().equals("VALIDEE")) {
                    totalVentes += c.getTotal();
                }
            }
            totalVentesLabel.setText(String.format("%.2f", totalVentes));

            // Total produits
            List<Produit> produits = produitDAO.readAll();
            totalProduitLabel.setText(String.valueOf(produits.size()));

            // Produits en alerte stock
            int alertCount = 0;
            for (Produit p : produits) {
                if (p.getStockActuel() <= p.getSeuilAlerte()) {
                    alertCount++;
                }
            }
            stockAlertLabel.setText(String.valueOf(alertCount));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
