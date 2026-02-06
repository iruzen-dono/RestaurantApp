-- Script de création de la base de données Restaurant
-- Base de données pour la gestion d'un restaurant

CREATE DATABASE IF NOT EXISTS restaurant;
USE restaurant;

-- Table Utilisateur
CREATE TABLE utilisateur (
    id INT PRIMARY KEY AUTO_INCREMENT,
    login VARCHAR(50) NOT NULL UNIQUE,
    motDePasse VARCHAR(255) NOT NULL
);

-- Table Categorie
CREATE TABLE categorie (
    id INT PRIMARY KEY AUTO_INCREMENT,
    libelle VARCHAR(100) NOT NULL UNIQUE
);

-- Table Produit
CREATE TABLE produit (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(150) NOT NULL,
    categorie_id INT NOT NULL,
    prix_vente DECIMAL(10, 2) NOT NULL CHECK(prix_vente > 0),
    stock_actuel INT NOT NULL DEFAULT 0 CHECK(stock_actuel >= 0),
    seuil_alerte INT NOT NULL DEFAULT 10,
    FOREIGN KEY (categorie_id) REFERENCES categorie(id) ON DELETE RESTRICT
);

-- Table MouvementStock
CREATE TABLE mouvement_stock (
    id INT PRIMARY KEY AUTO_INCREMENT,
    produit_id INT NOT NULL,
    type ENUM('ENTREE', 'SORTIE') NOT NULL,
    quantite INT NOT NULL CHECK(quantite > 0),
    date_mouvement DATE NOT NULL,
    motif VARCHAR(200),
    FOREIGN KEY (produit_id) REFERENCES produit(id) ON DELETE RESTRICT
);

-- Table Commande
CREATE TABLE commande (
    id INT PRIMARY KEY AUTO_INCREMENT,
    date_commande DATE NOT NULL,
    etat ENUM('EN_COURS', 'VALIDEE', 'ANNULEE') NOT NULL DEFAULT 'EN_COURS',
    total DECIMAL(12, 2) NOT NULL DEFAULT 0 CHECK(total >= 0)
);

-- Table LigneCommande
CREATE TABLE ligne_commande (
    id INT PRIMARY KEY AUTO_INCREMENT,
    commande_id INT NOT NULL,
    produit_id INT NOT NULL,
    quantite INT NOT NULL CHECK(quantite > 0),
    prix_unitaire DECIMAL(10, 2) NOT NULL CHECK(prix_unitaire >= 0),
    montant_ligne DECIMAL(12, 2) NOT NULL,
    FOREIGN KEY (commande_id) REFERENCES commande(id) ON DELETE CASCADE,
    FOREIGN KEY (produit_id) REFERENCES produit(id) ON DELETE RESTRICT
);

-- Index pour améliorer les performances
CREATE INDEX idx_produit_categorie ON produit(categorie_id);
CREATE INDEX idx_mouvement_produit ON mouvement_stock(produit_id);
CREATE INDEX idx_mouvement_date ON mouvement_stock(date_mouvement);
CREATE INDEX idx_commande_date ON commande(date_commande);
CREATE INDEX idx_ligne_commande ON ligne_commande(commande_id);
CREATE INDEX idx_ligne_produit ON ligne_commande(produit_id);

-- Données de test
INSERT INTO utilisateur (login, motDePasse) VALUES ('admin', 'admin123');
INSERT INTO utilisateur (login, motDePasse) VALUES ('user', 'user123');

INSERT INTO categorie (libelle) VALUES ('Boissons');
INSERT INTO categorie (libelle) VALUES ('Plats');
INSERT INTO categorie (libelle) VALUES ('Desserts');
INSERT INTO categorie (libelle) VALUES ('Ingredients');

INSERT INTO produit (nom, categorie_id, prix_vente, stock_actuel, seuil_alerte) VALUES
('Coca Cola 33cl', 1, 2.50, 50, 10),
('Jus d''Orange', 1, 3.00, 30, 8),
('Eau Minérale', 1, 1.50, 80, 20),
('Burger Classique', 2, 8.50, 25, 5),
('Sandwich Poulet', 2, 7.50, 20, 5),
('Frites', 2, 3.50, 40, 10),
('Tiramisu', 3, 5.00, 15, 3),
('Panna Cotta', 3, 4.50, 12, 3);
