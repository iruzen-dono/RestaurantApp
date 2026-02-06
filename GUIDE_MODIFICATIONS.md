# 🔧 Guide Pratique - Modification et Ajout de Données

## 📝 Table des matières
1. [Modifier les identifiants MySQL](#1-modifier-les-identifiants-mysql)
2. [Ajouter un nouveau champ à une entité](#2-ajouter-un-nouveau-champ-à-une-entité)
3. [Ajouter une nouvelle table](#3-ajouter-une-nouvelle-table)
4. [Modifier des données existantes](#4-modifier-des-données-existantes-en-code)

---

## 1. Modifier les identifiants MySQL

### 📍 Fichier à modifier
`src/utils/DatabaseConnection.java` (lignes 25-34)

### Comment faire

**Avant** (Configuration par défaut):
```java
private static final String URL = "jdbc:mysql://localhost:3306/restaurant?...";
private static final String USER = "root";
private static final String PASSWORD = "0000";
```

**Après** (Exemple si votre utilisateur MySQL est "admin" avec mot de passe "12345"):
```java
private static final String URL = "jdbc:mysql://localhost:3306/restaurant?...";
private static final String USER = "admin";        // ← Votre utilisateur MySQL
private static final String PASSWORD = "12345";    // ← Votre mot de passe
```

### Étapes
1. Ouvrir `src/utils/DatabaseConnection.java`
2. Trouver la ligne: `private static final String USER = "root";`
3. Remplacer `"root"` par votre utilisateur MySQL
4. Trouver la ligne: `private static final String PASSWORD = "0000";`
5. Remplacer `"0000"` par votre mot de passe MySQL
6. **Recompiler** le projet: `javac -cp lib/*:src -d bin src/**/*.java`
7. **Relancer** l'application

### ⚠️ Important
- Si MySQL n'utilise pas de mot de passe, mettre: `private static final String PASSWORD = "";`
- Si vous changez le port MySQL (ex: 3307 au lieu de 3306):
  ```java
  private static final String URL = "jdbc:mysql://localhost:3307/restaurant?...";
  ```

---

## 2. Ajouter un nouveau champ à une entité

### Exemple: Ajouter un email à Utilisateur

#### ÉTAPE 1: Modifier la table MySQL

Ouvrir `database/restaurant.sql` et modifier:
```sql
-- Avant
CREATE TABLE utilisateur (
    id INT PRIMARY KEY AUTO_INCREMENT,
    login VARCHAR(50) NOT NULL UNIQUE,
    motDePasse VARCHAR(255) NOT NULL
);

-- Après (ajouter email)
CREATE TABLE utilisateur (
    id INT PRIMARY KEY AUTO_INCREMENT,
    login VARCHAR(50) NOT NULL UNIQUE,
    motDePasse VARCHAR(255) NOT NULL,
    email VARCHAR(100)  -- ← NOUVEAU CHAMP
);
```

Exécuter en MySQL:
```sql
ALTER TABLE utilisateur ADD COLUMN email VARCHAR(100);
```

#### ÉTAPE 2: Modifier le modèle

Fichier: `src/models/Utilisateur.java`

```java
public class Utilisateur {
    private int id;
    private String login;
    private String motDePasse;
    private String email;  // ← AJOUTER CE CHAMP

    // Ajouter le getter
    public String getEmail() {
        return email;
    }

    // Ajouter le setter
    public void setEmail(String email) {
        this.email = email;
    }
}
```

#### ÉTAPE 3: Modifier le DAO

Fichier: `src/dao/UtilisateurDAO.java`

Dans la méthode `create()`:
```java
@Override
public void create(Utilisateur utilisateur) throws Exception {
    String sql = "INSERT INTO utilisateur (login, motDePasse, email) VALUES (?, ?, ?)"; // AJOUTER email
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        pstmt.setString(1, utilisateur.getLogin());
        pstmt.setString(2, utilisateur.getMotDePasse());
        pstmt.setString(3, utilisateur.getEmail());  // ← AJOUTER CETTE LIGNE
        // ... reste du code
    }
}
```

Dans la méthode `read()`:
```java
@Override
public Utilisateur read(int id) throws Exception {
    // ...
    if (rs.next()) {
        return new Utilisateur(
            rs.getInt("id"),
            rs.getString("login"),
            rs.getString("motDePasse"),
            rs.getString("email")  // ← AJOUTER CETTE LIGNE
        );
    }
}
```

Dans la méthode `readAll()`:
```java
while (rs.next()) {
    utilisateurs.add(new Utilisateur(
        rs.getInt("id"),
        rs.getString("login"),
        rs.getString("motDePasse"),
        rs.getString("email")  // ← AJOUTER CETTE LIGNE
    ));
}
```

Dans la méthode `update()`:
```java
@Override
public void update(Utilisateur utilisateur) throws Exception {
    String sql = "UPDATE utilisateur SET login = ?, motDePasse = ?, email = ? WHERE id = ?"; // AJOUTER email
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, utilisateur.getLogin());
        pstmt.setString(2, utilisateur.getMotDePasse());
        pstmt.setString(3, utilisateur.getEmail());  // ← AJOUTER CETTE LIGNE
        pstmt.setInt(4, utilisateur.getId());
        // ... reste du code
    }
}
```

#### ÉTAPE 4: Utiliser le nouveau champ

Dans l'UI (exemple LoginFrame):
```java
private void authenticateUser() {
    // ...
    Utilisateur user = utilisateurDAO.authenticate(login, password);
    if (user != null) {
        String email = user.getEmail();  // ← Utiliser le champ email
        System.out.println("Email: " + email);
        // ...
    }
}
```

---

## 3. Ajouter une nouvelle table

### Exemple: Ajouter une table "Fournisseur"

#### ÉTAPE 1: Ajouter la table en BD

Fichier: `database/restaurant.sql`

```sql
-- Nouvelle table
CREATE TABLE fournisseur (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    telephone VARCHAR(20),
    email VARCHAR(100),
    adresse VARCHAR(255)
);

-- Index pour améliorer les recherches
CREATE INDEX idx_fournisseur_nom ON fournisseur(nom);
```

Exécuter en MySQL:
```sql
source database/restaurant.sql;
```

#### ÉTAPE 2: Créer le modèle

Fichier: `src/models/Fournisseur.java`

```java
package models;

public class Fournisseur {
    private int id;
    private String nom;
    private String telephone;
    private String email;
    private String adresse;

    // Constructeurs
    public Fournisseur() {}

    public Fournisseur(String nom, String telephone, String email, String adresse) {
        this.nom = nom;
        this.telephone = telephone;
        this.email = email;
        this.adresse = adresse;
    }

    public Fournisseur(int id, String nom, String telephone, String email, String adresse) {
        this.id = id;
        this.nom = nom;
        this.telephone = telephone;
        this.email = email;
        this.adresse = adresse;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    @Override
    public String toString() {
        return nom;
    }
}
```

#### ÉTAPE 3: Créer le DAO

Fichier: `src/dao/FournisseurDAO.java`

```java
package dao;

import models.Fournisseur;
import utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FournisseurDAO implements IGenericDAO<Fournisseur> {

    @Override
    public void create(Fournisseur fournisseur) throws Exception {
        String sql = "INSERT INTO fournisseur (nom, telephone, email, adresse) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, fournisseur.getNom());
            pstmt.setString(2, fournisseur.getTelephone());
            pstmt.setString(3, fournisseur.getEmail());
            pstmt.setString(4, fournisseur.getAdresse());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    fournisseur.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public Fournisseur read(int id) throws Exception {
        String sql = "SELECT * FROM fournisseur WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Fournisseur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("telephone"),
                        rs.getString("email"),
                        rs.getString("adresse")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<Fournisseur> readAll() throws Exception {
        List<Fournisseur> fournisseurs = new ArrayList<>();
        String sql = "SELECT * FROM fournisseur ORDER BY nom";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                fournisseurs.add(new Fournisseur(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("telephone"),
                    rs.getString("email"),
                    rs.getString("adresse")
                ));
            }
        }
        return fournisseurs;
    }

    @Override
    public void update(Fournisseur fournisseur) throws Exception {
        String sql = "UPDATE fournisseur SET nom = ?, telephone = ?, email = ?, adresse = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fournisseur.getNom());
            pstmt.setString(2, fournisseur.getTelephone());
            pstmt.setString(3, fournisseur.getEmail());
            pstmt.setString(4, fournisseur.getAdresse());
            pstmt.setInt(5, fournisseur.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM fournisseur WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
```

#### ÉTAPE 4: Utiliser le nouveau DAO

```java
// Ajouter un fournisseur
FournisseurDAO dao = new FournisseurDAO();
Fournisseur f = new Fournisseur("Distributeur ABC", "06 12 34 56 78", "contact@abc.com", "Paris");
dao.create(f);

// Récupérer tous les fournisseurs
List<Fournisseur> fournisseurs = dao.readAll();

// Modifier
f.setEmail("newemail@abc.com");
dao.update(f);

// Supprimer
dao.delete(f.getId());
```

---

## 4. Modifier des données existantes en code

### Exemple 1: Changer le prix d'un produit

```java
ProduitDAO dao = new ProduitDAO();

// Récupérer le produit
Produit p = dao.read(1);  // Produit avec id=1

// Modifier le prix
p.setPrixVente(3.50);

// Enregistrer en BD
dao.update(p);

System.out.println("Nouveau prix: " + p.getPrixVente());
```

### Exemple 2: Ajouter du stock

```java
Produit p = dao.read(5);

// Augmenter le stock
dao.increaseStock(5, 20);  // Ajouter 20 unités au produit id=5

// Vérifier
Produit updated = dao.read(5);
System.out.println("Nouveau stock: " + updated.getStockActuel());
```

### Exemple 3: Créer une category, puis un produit

```java
// Créer une catégorie
CategorieDAO catDAO = new CategorieDAO();
Categorie cat = new Categorie("Pizzas");
catDAO.create(cat);
System.out.println("ID attribué: " + cat.getId());

// Créer un produit dans cette catégorie
ProduitDAO prodDAO = new ProduitDAO();
Produit prod = new Produit("Pizza Margherita", cat.getId(), 12.00, 10, 3);
prodDAO.create(prod);
System.out.println("Produit créé avec ID: " + prod.getId());
```

---

## 📊 Récapitulatif: Pattern pour les modifications

```
BD (MySQL)
    ↓
ALTER TABLE ... ADD COLUMN ...
    ↓
Model (Java class)
   + private String newField;
   + public getNewField()
   + public setNewField()
    ↓
DAO (CRUD methods)
   + Ajouter paramètre dans INSERT
   + Récupérer dans SELECT
   + Modifier dans UPDATE
    ↓
UI (Swing frames/panels)
   + Ajouter JTextField/JButton pour saisie
   + Utiliser getter/setter du model
    ↓
✅ Fonctionne!
```

---

## 🔒 Points de sécurité

✅ **TOUJOURS utiliser PreparedStatement:**
```java
// ✅ BON (Protégé contre injection SQL)
String sql = "SELECT * FROM produit WHERE nom = ?";
pstmt.setString(1, nom);

// ❌ MAUVAIS (Injection SQL possible!)
String sql = "SELECT * FROM produit WHERE nom = '" + nom + "'";
```

✅ **Valider les données en Java AVANT d'envoyer à la BD:**
```java
if (prixVente <= 0) {
    throw new IllegalArgumentException("Le prix doit être positif");
}
```

✅ **Jamais stocker les mot de passe en clair** (à améliorer avec hashage):
```java
// ❌ Actuel (non crypté)
private String motDePasse;  // Stocké en clair

// ✅ À faire (hash + salt)
private String motDePasse;  // Utiliser BCrypt ou SHA-256
```

---

## 📞 Test rapide

Pour vérifier que tout fonctionne après une modification:

```java
// Dans Main.java ou un test
ProduitDAO dao = new ProduitDAO();
List<Produit> produits = dao.readAll();
System.out.println("Nombre de produits: " + produits.size());

for (Produit p : produits) {
    System.out.println("- " + p.getNom() + ": " + p.getPrixVente() + "€ (stock: " + p.getStockActuel() + ")");
}
```
