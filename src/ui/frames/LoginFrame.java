package ui.frames;

import dao.UtilisateurDAO;
import models.Utilisateur;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Écran de connexion
 */
public class LoginFrame extends JFrame {
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton exitButton;
    private JLabel messageLabel;
    private UtilisateurDAO utilisateurDAO;

    public LoginFrame() {
        utilisateurDAO = new UtilisateurDAO();
        initComponents();
    }

    private void initComponents() {
        setTitle("RestaurantApp - Connexion");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Titre
        JLabel titleLabel = new JLabel("Connexion RestaurantApp");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Login
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Login:"), gbc);
        gbc.gridx = 1;
        loginField = new JTextField(15);
        panel.add(loginField, gbc);

        // Password
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Mot de passe:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        // Message
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        messageLabel = new JLabel("");
        messageLabel.setForeground(Color.RED);
        panel.add(messageLabel, gbc);

        // Buttons
        gbc.gridy = 4;
        JPanel buttonPanel = new JPanel();
        loginButton = new JButton("Connexion");
        exitButton = new JButton("Quitter");
        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);
        panel.add(buttonPanel, gbc);

        // Event listeners
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authenticateUser();
            }
        });

        exitButton.addActionListener(e -> System.exit(0));

        // Enter key
        loginField.addActionListener(e -> authenticateUser());
        passwordField.addActionListener(e -> authenticateUser());

        add(panel);
    }

    private void authenticateUser() {
        String login = loginField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (login.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs");
            return;
        }

        try {
            Utilisateur user = utilisateurDAO.authenticate(login, password);
            if (user != null) {
                // Ouverture du menu principal
                new MainFrame(user).setVisible(true);
                dispose();
            } else {
                messageLabel.setText("Login ou mot de passe incorrect");
                passwordField.setText("");
            }
        } catch (Exception e) {
            messageLabel.setText("Erreur de connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
