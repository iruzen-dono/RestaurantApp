package ui.panels;

import dao.CommandeDAO;
import dao.LigneCommandeDAO;
import dao.ProduitDAO;
import models.Commande;
import models.LigneCommande;
import models.Produit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel pour la gestion des commandes
 */
public class CommandePanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private CommandeDAO commandeDAO;
    private LigneCommandeDAO ligneDAO;
    private ProduitDAO produitDAO;
    private JButton newButton, validateButton, cancelButton, addLineButton, deleteButton;
    private JButton detailsButton;
    private ui.frames.MainFrame mainFrame;

    public CommandePanel() {
        this(null);
    }

    public CommandePanel(ui.frames.MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        commandeDAO = new CommandeDAO();
        ligneDAO = new LigneCommandeDAO();
        produitDAO = new ProduitDAO();
        initComponents();
        refreshTable();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top panel with buttons
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBackground(new Color(230, 230, 230));
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        newButton = new JButton("+ Nouvelle Commande");
        newButton.setBackground(new Color(46, 204, 113));
        newButton.setForeground(Color.WHITE);
        newButton.setFocusPainted(false);
        newButton.addActionListener(e -> newCommand());
        
        addLineButton = new JButton("+ Ajouter Article");
        addLineButton.setBackground(new Color(52, 152, 219));
        addLineButton.setForeground(Color.WHITE);
        addLineButton.setFocusPainted(false);
        addLineButton.addActionListener(e -> addLineCommand());
        
        validateButton = new JButton("✓ Valider");
        validateButton.setBackground(new Color(26, 188, 156));
        validateButton.setForeground(Color.WHITE);
        validateButton.setFocusPainted(false);
        validateButton.addActionListener(e -> validateCommand());
        
        cancelButton = new JButton("✕ Annuler");
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> cancelCommand());
        
        deleteButton = new JButton("Supprimer");
        deleteButton.setBackground(new Color(192, 57, 43));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> deleteCommand());

        detailsButton = new JButton("Détails");
        detailsButton.setBackground(new Color(155, 89, 182));
        detailsButton.setForeground(Color.WHITE);
        detailsButton.setFocusPainted(false);
        detailsButton.addActionListener(e -> showDetails());

        topPanel.add(newButton);
        topPanel.add(addLineButton);
        topPanel.add(validateButton);
        topPanel.add(cancelButton);
        topPanel.add(deleteButton);
        topPanel.add(detailsButton);
        add(topPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(
            new String[]{"ID", "Date", "État", "Total (€)"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 11));
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(41, 128, 185));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void refreshTable() {
        try {
            tableModel.setRowCount(0);
            List<Commande> commandes = commandeDAO.readAll();
            for (Commande c : commandes) {
                tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getDateCommande(),
                    c.getEtat(),
                    String.format("%.2f", c.getTotal())
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void newCommand() {
        try {
            Commande commande = new Commande();
            commande.setDateCommande(LocalDate.now());
            commande.setEtat(Commande.EtatCommande.EN_COURS);
            commande.setTotal(0.0);
            
            commandeDAO.create(commande);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Nouvelle commande créée!", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addLineCommand() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une commande", "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int commandeId = (int) tableModel.getValueAt(selectedRow, 0);
        
        JDialog dialog = new JDialog((Frame) null, "Ajouter un Article", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Produit:"));
        JComboBox<Produit> productBox = new JComboBox<>();
        try {
            for (Produit p : produitDAO.readAll()) {
                productBox.addItem(p);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(dialog, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        panel.add(productBox);

        panel.add(new JLabel("Quantité:"));
        JTextField quantityField = new JTextField();
        panel.add(quantityField);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Ajouter");
        JButton cancelBtn = new JButton("Annuler");
        
        saveButton.setBackground(new Color(52, 152, 219));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        
        cancelBtn.setBackground(new Color(149, 165, 166));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);

        saveButton.addActionListener(e -> {
            try {
                if (quantityField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Veuillez entrer une quantité", "Erreur", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Produit selectedProduct = (Produit) productBox.getSelectedItem();
                int quantity = Integer.parseInt(quantityField.getText());
                
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(dialog, "La quantité doit être positive", "Erreur", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                LigneCommande ligne = new LigneCommande();
                ligne.setCommandeId(commandeId);
                ligne.setProduitId(selectedProduct.getId());
                ligne.setQuantite(quantity);
                // enregistrer le prix unitaire au moment de l'ajout
                ligne.setPrixUnitaire(selectedProduct.getPrixVente());
                
                ligneDAO.create(ligne);
                
                // Recalculer le total de la commande
                Commande commande = commandeDAO.read(commandeId);
                double newTotal = commande.getTotal() + (selectedProduct.getPrixVente() * quantity);
                commande.setTotal(newTotal);
                commandeDAO.update(commande);
                
                refreshTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(CommandePanel.this, "Article ajouté!", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelBtn);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void validateCommand() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une commande", "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String etat = tableModel.getValueAt(selectedRow, 2).toString();
        
        if (!etat.equals("EN_COURS")) {
            JOptionPane.showMessageDialog(this, "Seules les commandes en cours peuvent être validées", "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Commande commande = commandeDAO.read(id);
            commande.setEtat(Commande.EtatCommande.VALIDEE);
            commandeDAO.update(commande);
            refreshTable();
            if (mainFrame != null) {
                mainFrame.refreshDashboard();
            }
            JOptionPane.showMessageDialog(this, "Commande validée!", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelCommand() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une commande", "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String etat = tableModel.getValueAt(selectedRow, 2).toString();
        
        if (etat.equals("ANNULEE")) {
            JOptionPane.showMessageDialog(this, "Cette commande est déjà annulée", "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir annuler cette commande?", "Confirmation", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Commande commande = commandeDAO.read(id);
                commande.setEtat(Commande.EtatCommande.ANNULEE);
                commandeDAO.update(commande);
                refreshTable();
                if (mainFrame != null) {
                    mainFrame.refreshDashboard();
                }
                JOptionPane.showMessageDialog(this, "Commande annulée!", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteCommand() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez selectionner une commande", "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String etat = tableModel.getValueAt(selectedRow, 2).toString();
        
        if (etat.equals("EN_COURS")) {
            JOptionPane.showMessageDialog(this, "Impossible de supprimer une commande en cours. Veuillez d'abord l'annuler.", "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Etes-vous sur de vouloir supprimer cette commande? Cette action est irreversible.", "Confirmation", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                List<LigneCommande> lignes = ligneDAO.readAll();
                for (LigneCommande ligne : lignes) {
                    if (ligne.getCommandeId() == id) {
                        ligneDAO.delete(ligne.getId());
                    }
                }
                
                Commande commande = commandeDAO.read(id);
                commandeDAO.delete(commande.getId());
                refreshTable();
                if (mainFrame != null) {
                    mainFrame.refreshDashboard();
                }
                JOptionPane.showMessageDialog(this, "Commande supprimee!", "Succes", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showDetails() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une commande", "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int commandeId = (int) tableModel.getValueAt(selectedRow, 0);

        try {
            java.util.List<LigneCommande> lignes = ligneDAO.readByCommande(commandeId);

            JDialog dialog = new JDialog((Frame) null, "Détails commande #" + commandeId, true);
            dialog.setSize(600, 400);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());

            DefaultTableModel model = new DefaultTableModel(new String[]{"Produit","Quantité","Prix unitaire (€)","Montant (€)"}, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            JTable detailsTable = new JTable(model);

            double total = 0.0;
            for (LigneCommande l : lignes) {
                Produit p = produitDAO.read(l.getProduitId());
                String name = p != null ? p.getNom() : "#" + l.getProduitId();
                double montant = l.getMontantLigne();
                model.addRow(new Object[]{name, l.getQuantite(), String.format("%.2f", l.getPrixUnitaire()), String.format("%.2f", montant)});
                total += montant;
            }

            dialog.add(new JScrollPane(detailsTable), BorderLayout.CENTER);

            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottom.add(new JLabel("Total: " + String.format("%.2f €", total)));
            JButton close = new JButton("Fermer");
            close.addActionListener(e -> dialog.dispose());
            bottom.add(close);
            dialog.add(bottom, BorderLayout.SOUTH);

            dialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur chargement details: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
