# RestaurantApp - Application de Gestion de Restaurant

## Vue d'ensemble
RestaurantApp est une application de bureau développée en **Java SE** avec une interface graphique en **Java Swing**. Elle permet de gérer les stocks, les commandes, et les statistiques d'un restaurant, snack ou fast-food.

## Fonctionnalités principales
- Gestion des produits et catégories
- Gestion du stock (entrées/sorties)
- Gestion des commandes clients
- Statistiques et rapports
- Authentification utilisateur

## Prérequis

### 1. Java SE (version 8 ou supérieure)

### 2. MySQL Server
- Télécharger depuis: https://www.mysql.com/downloads/
- Installer et démarrer le service MySQL
- Créer un compte utilisateur (si nécessaire)

### 3. MySQL Connector/J (Driver JDBC)

## Installation et Configuration

### Étape 1: Créer la base de données
```bash
# Ouvrir MySQL
mysql -u root -p

# Exécuter le script SQL
mysql -u root -p < database/restaurant.sql
```

Ou directement dans MySQL:
```sql
source database/restaurant.sql;
```

### Étape 2: Configurer la connexion à la BD
Éditer le fichier `src/utils/DatabaseConnection.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/restaurant";
private static final String USER = "root";  // Votre utilisateur MySQL
private static final String PASSWORD = "";  // Votre mot de passe MySQL
```

### Étape 3: Compiler le projet
```bash
cd RestaurantApp
javac -cp lib/*:src -d bin src/**/*.java
```

### Étape 4: Exécuter l'application
```bash
java -cp bin:lib/* Main
```

## Structure du projet
```
RestaurantApp/
├── src/
│   ├── models/           # Classes modèles (entités)
│   │   ├── Categorie.java
│   │   ├── Produit.java
│   │   ├── Commande.java
│   │   ├── LigneCommande.java
│   │   ├── MouvementStock.java
│   │   └── Utilisateur.java
│   ├── dao/              # Data Access Objects
│   │   ├── IGenericDAO.java
│   │   ├── CategorieDAO.java
│   │   ├── ProduitDAO.java
│   │   ├── CommandeDAO.java
│   │   ├── LigneCommandeDAO.java
│   │   ├── MouvementStockDAO.java
│   │   └── UtilisateurDAO.java
│   ├── ui/
│   │   ├── frames/       # Fenêtres principales
│   │   │   ├── LoginFrame.java
│   │   │   └── MainFrame.java
│   │   └── panels/       # Panels fonctionnels
│   │       ├── ProduitPanel.java
│   │       ├── StockPanel.java
│   │       ├── CommandePanel.java
│   │       └── StatistiquesPanel.java
│   ├── utils/            # Classes utilitaires
│   │   └── DatabaseConnection.java
│   └── Main.java         # Point d'entrée
├── lib/                  # Dépendances externes
│   └── mysql-connector-java-x.x.x.jar
├── database/
│   └── restaurant.sql    # Script de création BD
└── README.md             # Ce fichier
```

## Identifiants par défaut

### Utilisateurs de test:
- **Login**: admin | **Mot de passe**: admin123
- **Login**: user | **Mot de passe**: user123

### Données de test:
Des catégories et produits sont créés automatiquement lors de l'exécution du script SQL.

## Architecture

### Modèle MVC
- **Model**: Classes dans `models/` + DAOs dans `dao/`
- **View**: Frames et Panels dans `ui/`
- **Controller**: Logique dans les Panels et Frames

### Design Patterns utilisés
- **DAO Pattern**: Pour l'abstraction de la persistance
- **Singleton-like**: DatabaseConnection pour gérer les connexions
- **MVC**: Séparation des responsabilités

## Utilisation de l'application

### 1. Connexion
Entrer vos identifiants (login/mot de passe) et cliquer sur "Connexion"

### 2. Menu principal
Une fois connecté, vous accédez à 4 onglets:
- Produits: Gérer les produits et catégories
- Stock: Enregistrer entrées/sorties de stock
- Commandes: Créer et gérer les commandes
- Statistiques: Consulter les rapports

### 3. Gestion des produits
- Ajouter un produit
- Modifier les informations
- Supprimer un produit
- Filtrer par catégorie

### 4. Gestion du stock
- Enregistrer une entrée de stock
- Enregistrer une sortie de stock
- Consulter l'historique

### 5. Gestion des commandes
- Créer une nouvelle commande
- Ajouter des produits
- Valider la commande
- Annuler si nécessaire

## Règles de gestion implémentées

### Produits
- Le libellé de catégorie doit être unique
- Le prix de vente doit être strictement positif
- Le stock ne peut pas être négatif

### Stock
- Quantité de mouvement > 0
- Interdire une sortie si la quantité dépasse le stock

### Commandes
- Au moins une ligne pour valider
- Calcul automatique du total

## Dépannage

### "Driver MySQL non trouvé"
→ Assurez-vous que mysql-connector-java-x.x.x.jar est dans lib/

"Erreur de connexion à la base de données"
→ Vérifiez que:
  - MySQL est en cours d'exécution
  - Les identifiants dans DatabaseConnection.java sont corrects
  - La base de données 'restaurant' existe

L'application démarre lentement
→ C'est normal à la première exécution (chargement des drivers et compilation à chaud)

## Améliorations futures
- Implémentation complète des dialogs d'ajout/modification
- Validation côté application plus avancée
- Gestion complète des produits et catégories
- Gestion complète du stock
- Gestion complète des commandes
- Tableau de bord statistiques avec 4 KPI
- Graphiques pour les statistiques (courbes, camemberts)
- Export en PDF des rapports
- Gestion multi-utilisateurs avancée (rôles, permissions)
- Sauvegarde/restauration fichiers de configuration
- Import/Export de données en CSV/Excel
- Historique complet des modifications

## Licence
Travaux pratiques - Université

## Support
Pour toute question ou bug, veuillez consulter votre instructeur.
