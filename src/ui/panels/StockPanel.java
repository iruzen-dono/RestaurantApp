package ui.panels;

import dao.MouvementStockDAO;
import dao.ProduitDAO;
import models.MouvementStock;
import models.Produit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel pour la gestion du stock
 */
public class StockPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private MouvementStockDAO mouvementDAO;
    private ProduitDAO produitDAO;
    private JButton addEntryButton, addExitButton;

    public StockPanel() {
        mouvementDAO = new MouvementStockDAO();
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
        
        addEntryButton = new JButton("+ Entrée Stock");
        addEntryButton.setBackground(new Color(46, 204, 113));
        addEntryButton.setForeground(Color.WHITE);
        addEntryButton.setFocusPainted(false);
        addEntryButton.addActionListener(e -> addStockMovement("ENTREE"));
        
        addExitButton = new JButton("- Sortie Stock");
        addExitButton.setBackground(new Color(231, 76, 60));
        addExitButton.setForeground(Color.WHITE);
        addExitButton.setFocusPainted(false);
        addExitButton.addActionListener(e -> addStockMovement("SORTIE"));

        topPanel.add(addEntryButton);
        topPanel.add(addExitButton);
        add(topPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(
            new String[]{"ID", "Produit", "Type", "Quantité", "Date", "Motif"},
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
            List<MouvementStock> mouvements = mouvementDAO.readAll();
            for (MouvementStock m : mouvements) {
                try {
                    Produit p = produitDAO.read(m.getProduitId());
                    tableModel.addRow(new Object[]{
                        m.getId(),
                        p != null ? p.getNom() : "?",
                        m.getType(),
                        m.getQuantite(),
                        m.getDateMouvement(),
                        m.getMotif()
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addStockMovement(String type) {
        JDialog dialog = new JDialog((Frame) null, "Enregistrer " + (type.equals("ENTREE") ? "une Entrée" : "une Sortie") + " de Stock", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(400, 280);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));
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

        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        JTextField dateField = new JTextField(LocalDate.now().toString());
        panel.add(dateField);

        panel.add(new JLabel("Motif:"));
        JTextField motifField = new JTextField();
        panel.add(motifField);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");
        
        saveButton.setBackground(type.equals("ENTREE") ? new Color(46, 204, 113) : new Color(231, 76, 60));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        
        cancelButton.setBackground(new Color(149, 165, 166));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);

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

                MouvementStock mouvement = new MouvementStock();
                mouvement.setProduitId(selectedProduct.getId());
                mouvement.setType(MouvementStock.TypeMouvement.valueOf(type));
                mouvement.setQuantite(quantity);
                mouvement.setDateMouvement(LocalDate.parse(dateField.getText()));
                mouvement.setMotif(motifField.getText());
                
                mouvementDAO.create(mouvement);
                
                // Mettre à jour le stock du produit
                if (type.equals("ENTREE")) {
                    selectedProduct.setStockActuel(selectedProduct.getStockActuel() + quantity);
                } else {
                    if (selectedProduct.getStockActuel() < quantity) {
                        JOptionPane.showMessageDialog(dialog, "Stock insuffisant!", "Erreur", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    selectedProduct.setStockActuel(selectedProduct.getStockActuel() - quantity);
                }
                produitDAO.update(selectedProduct);
                
                refreshTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(StockPanel.this, "Mouvement enregistré avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
