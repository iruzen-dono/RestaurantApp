package ui.panels;

import dao.CategorieDAO;
import dao.ProduitDAO;
import models.Categorie;
import models.Produit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel pour la gestion des produits
 */
public class ProduitPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private ProduitDAO produitDAO;
    private CategorieDAO categorieDAO;
    private JButton addButton, editButton, deleteButton, addCategoryButton, deleteCategoryButton;
    private JComboBox<Categorie> categoryFilter;

    public ProduitPanel() {
        produitDAO = new ProduitDAO();
        categorieDAO = new CategorieDAO();
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

        JLabel filterLabel = new JLabel("Filtrer par catégorie:");
        filterLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        categoryFilter = new JComboBox<>();
        categoryFilter.setPreferredSize(new Dimension(200, 30));
        categoryFilter.addActionListener(e -> filterByCategory());

        addButton = new JButton("+ Ajouter Produit");
        addButton.setBackground(new Color(46, 204, 113));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> addProduct());

        editButton = new JButton("✎ Modifier");
        editButton.setBackground(new Color(52, 152, 219));
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);
        editButton.addActionListener(e -> editProduct());

        deleteButton = new JButton("✕ Supprimer");
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> deleteProduct());

        addCategoryButton = new JButton("+ Catégorie");
        addCategoryButton.setBackground(new Color(155, 89, 182));
        addCategoryButton.setForeground(Color.WHITE);
        addCategoryButton.setFocusPainted(false);
        addCategoryButton.addActionListener(e -> addCategory());

        deleteCategoryButton = new JButton("✕ Supprimer Cat.");
        deleteCategoryButton.setBackground(new Color(192, 57, 43));
        deleteCategoryButton.setForeground(Color.WHITE);
        deleteCategoryButton.setFocusPainted(false);
        deleteCategoryButton.addActionListener(e -> deleteCategory());

        topPanel.add(filterLabel);
        topPanel.add(categoryFilter);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(addButton);
        topPanel.add(editButton);
        topPanel.add(deleteButton);
        topPanel.add(addCategoryButton);
        topPanel.add(deleteCategoryButton);
        
        add(topPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(
            new String[]{"ID", "Nom", "Catégorie", "Prix (€)", "Stock", "Seuil Alerte"}, 
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

        // Load categories
        loadCategories();
    }

    private void loadCategories() {
        try {
            List<Categorie> categories = categorieDAO.readAll();
            categoryFilter.removeAllItems();
            categoryFilter.addItem(new Categorie(0, "Tous les produits"));
            for (Categorie cat : categories) {
                categoryFilter.addItem(cat);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTable() {
        try {
            tableModel.setRowCount(0);
            List<Produit> produits = produitDAO.readAll();
            for (Produit p : produits) {
                Categorie cat = categorieDAO.read(p.getCategorieId());
                tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getNom(),
                    cat != null ? cat.getLibelle() : "?",
                    String.format("%.2f", p.getPrixVente()),
                    p.getStockActuel(),
                    p.getSeuilAlerte()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterByCategory() {
        try {
            tableModel.setRowCount(0);
            Categorie selected = (Categorie) categoryFilter.getSelectedItem();
            if (selected != null && selected.getId() == 0) {
                refreshTable();
            } else if (selected != null) {
                List<Produit> produits = produitDAO.readByCategorie(selected.getId());
                for (Produit p : produits) {
                    tableModel.addRow(new Object[]{
                        p.getId(),
                        p.getNom(),
                        selected.getLibelle(),
                        String.format("%.2f", p.getPrixVente()),
                        p.getStockActuel(),
                        p.getSeuilAlerte()
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addProduct() {
        JDialog dialog = new JDialog((Frame) null, "Ajouter un Produit", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Nom:"));
        JTextField nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Catégorie:"));
        JComboBox<Categorie> categoryBox = new JComboBox<>();
        try {
            for (Categorie cat : categorieDAO.readAll()) {
                categoryBox.addItem(cat);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(dialog, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        panel.add(categoryBox);

        panel.add(new JLabel("Prix de vente (€):"));
        JTextField priceField = new JTextField();
        panel.add(priceField);

        panel.add(new JLabel("Stock initial:"));
        JTextField stockField = new JTextField();
        panel.add(stockField);

        panel.add(new JLabel("Seuil d'alerte:"));
        JTextField thresholdField = new JTextField();
        panel.add(thresholdField);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");
        
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        
        cancelButton.setBackground(new Color(149, 165, 166));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);

        saveButton.addActionListener(e -> {
            try {
                if (nameField.getText().isEmpty() || priceField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Veuillez remplir tous les champs", "Erreur", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                Produit produit = new Produit();
                produit.setNom(nameField.getText());
                produit.setCategorieId(((Categorie) categoryBox.getSelectedItem()).getId());
                produit.setPrixVente(Double.parseDouble(priceField.getText()));
                produit.setStockActuel(Integer.parseInt(stockField.getText().isEmpty() ? "0" : stockField.getText()));
                produit.setSeuilAlerte(Integer.parseInt(thresholdField.getText().isEmpty() ? "10" : thresholdField.getText()));
                
                produitDAO.create(produit);
                refreshTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(ProduitPanel.this, "Produit ajouté avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
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

    private void editProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit", "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        
        try {
            Produit produit = produitDAO.read(id);
            if (produit == null) return;

            JDialog dialog = new JDialog((Frame) null, "Modifier le Produit", true);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(this);

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(5, 2, 10, 10));
            panel.setBorder(new EmptyBorder(20, 20, 20, 20));

            panel.add(new JLabel("Nom:"));
            JTextField nameField = new JTextField(produit.getNom());
            panel.add(nameField);

            panel.add(new JLabel("Catégorie:"));
            JComboBox<Categorie> categoryBox = new JComboBox<>();
            for (Categorie cat : categorieDAO.readAll()) {
                categoryBox.addItem(cat);
                if (cat.getId() == produit.getCategorieId()) {
                    categoryBox.setSelectedItem(cat);
                }
            }
            panel.add(categoryBox);

            panel.add(new JLabel("Prix de vente (€):"));
            JTextField priceField = new JTextField(String.valueOf(produit.getPrixVente()));
            panel.add(priceField);

            panel.add(new JLabel("Stock:"));
            JTextField stockField = new JTextField(String.valueOf(produit.getStockActuel()));
            panel.add(stockField);

            panel.add(new JLabel("Seuil d'alerte:"));
            JTextField thresholdField = new JTextField(String.valueOf(produit.getSeuilAlerte()));
            panel.add(thresholdField);

            JPanel buttonPanel = new JPanel();
            JButton saveButton = new JButton("Enregistrer");
            JButton cancelButton = new JButton("Annuler");
            
            saveButton.setBackground(new Color(52, 152, 219));
            saveButton.setForeground(Color.WHITE);
            saveButton.setFocusPainted(false);
            
            cancelButton.setBackground(new Color(149, 165, 166));
            cancelButton.setForeground(Color.WHITE);
            cancelButton.setFocusPainted(false);

            saveButton.addActionListener(e -> {
                try {
                    produit.setNom(nameField.getText());
                    produit.setCategorieId(((Categorie) categoryBox.getSelectedItem()).getId());
                    produit.setPrixVente(Double.parseDouble(priceField.getText()));
                    produit.setStockActuel(Integer.parseInt(stockField.getText()));
                    produit.setSeuilAlerte(Integer.parseInt(thresholdField.getText()));
                    
                    produitDAO.update(produit);
                    refreshTable();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(ProduitPanel.this, "Produit modifié avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit", "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Êtes-vous sûr de vouloir supprimer '" + name + "'?", 
            "Confirmation", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                produitDAO.delete(id);
                refreshTable();
                JOptionPane.showMessageDialog(this, "Produit supprimé avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addCategory() {
        JDialog dialog = new JDialog((Frame) null, "Ajouter une Catégorie", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(350, 150);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Nom de la catégorie:"));
        JTextField nameField = new JTextField();
        panel.add(nameField);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");
        
        saveButton.setBackground(new Color(155, 89, 182));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        
        cancelButton.setBackground(new Color(149, 165, 166));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);

        saveButton.addActionListener(e -> {
            try {
                if (nameField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Veuillez entrer un nom", "Erreur", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                Categorie categorie = new Categorie();
                categorie.setLibelle(nameField.getText());
                
                categorieDAO.create(categorie);
                loadCategories();
                dialog.dispose();
                JOptionPane.showMessageDialog(ProduitPanel.this, "Catégorie ajoutée avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
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

    private void deleteCategory() {
        Categorie selected = (Categorie) categoryFilter.getSelectedItem();
        
        if (selected == null || selected.getId() == 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une catégorie à supprimer", "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Êtes-vous sûr de vouloir supprimer la catégorie '" + selected.getLibelle() + "'?", 
            "Confirmation", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Vérifier si la catégorie a des produits
                List<Produit> produits = produitDAO.readByCategorie(selected.getId());
                if (!produits.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "Impossible de supprimer! Cette catégorie contient " + produits.size() + " produit(s).\nVeuillez d'abord supprimer ou déplacer les produits.", 
                        "Erreur", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                categorieDAO.delete(selected.getId());
                loadCategories();
                refreshTable();
                JOptionPane.showMessageDialog(this, "Catégorie supprimée avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
