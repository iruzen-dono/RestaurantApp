package ui.panels;

import dao.HistoriqueDAO;
import models.AuditEntry;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HistoriquePanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    public HistoriquePanel() {
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new Object[]{"ID","Action","Table","Record ID","User","Détails","Date"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refresh = new JButton("Actualiser");
        refresh.addActionListener(e -> loadData());
        top.add(refresh);
        add(top, BorderLayout.NORTH);

        loadData();
    }

    private void loadData() {
        try {
            HistoriqueDAO dao = new HistoriqueDAO();
            List<AuditEntry> entries = dao.readAll();
            model.setRowCount(0);
            for (AuditEntry a : entries) {
                model.addRow(new Object[]{a.getId(), a.getAction(), a.getTableName(), a.getRecordId(), a.getUser(), a.getDetails(), a.getCreatedAt()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur chargement historique: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
