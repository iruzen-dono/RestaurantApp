# 📚 Documentation RestaurantApp - Guide Complet du Code

## 🎯 Vue d'ensemble du projet

**RestaurantApp** est une application de gestion de restaurant en **Java Swing + MySQL**.
Elle permet de gérer les produits, les stocks, les commandes et les statistiques d'un établissement.

---

## 🏗️ Architecture du Projet

### Schéma général:
```
┌─────────────────────────────────────────────────────────────┐
│                   USER INTERFACE (Swing)                     │
│  LoginFrame → MainFrame → 4 Panels (Produits, Stock,etc)   │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                        SERVICES                              │
│  (Couche métier - logique applicative)                       │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                   DAO (Data Access)                          │
│  ProduitDAO, CategorieDAO, CommandeDAO, etc                │
│  Interface: IGenericDAO (CRUD générique)                    │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                      DATABASE (MySQL)                        │
│  Tables: utilisateur, categorie, produit, commande, etc     │
└─────────────────────────────────────────────────────────────┘
```

---

## 📁 Structure des fichiers

### 1. **src/Main.java** - Point d'entrée
- Lance l'application
- Teste la connexion à la BD
- Ouvre LoginFrame

### 2. **src/utils/DatabaseConnection.java** - Gestionnaire de connexion BD
- Gère la connexion à MySQL
- Méthodes: `getConnection()`, `closeConnection()`, `testConnection()`
- **Configuration**: URL, USER, PASSWORD sont définis en haut du fichier

### 3. **src/models/** - Classes entités
```
Utilisateur.java     → id, login, motDePasse
Categorie.java       → id, libelle
Produit.java         → id, nom, categorieId, prix, stock, seuilAlerte
Commande.java        → id, dateCommande, etat, total
LigneCommande.java   → id, commandeId, produitId, quantite, prix
MouvementStock.java  → id, produitId, type (ENTREE/SORTIE), quantite, date
```

### 4. **src/dao/** - Accès aux données
- **IGenericDAO.java** - Interface générique CRUD:
  - `create(T t)` - Ajouter
  - `read(int id)` - Récupérer par ID
  - `readAll()` - Tous les enregistrements
  - `update(T t)` - Modifier
  - `delete(int id)` - Supprimer

- **Implémentations**: ProduitDAO, CategorieDAO, CommandeDAO, etc
  - Chacun implémente IGenericDAO pour son entité
  - Utilise PreparedStatement pour la sécurité (prévention injection SQL)

### 5. **src/ui/frames/** - Fenêtres principales
- **LoginFrame.java** - Écran de connexion (login/password)
- **MainFrame.java** - Fenêtre principale avec 4 onglets

### 6. **src/ui/panels/** - Panneaux fonctionnels
- **ProduitPanel.java** - Gestion des produits et catégories
- **StockPanel.java** - Gestion des mouvements de stock
- **CommandePanel.java** - Gestion des commandes
- **StatistiquesPanel.java** - Rapports et statistiques

### 7. **database/restaurant.sql** - Script de création BD
- Crée la base de données "restaurant"
- Crée les 6 tables avec contraintes
- Insère des données de test

---

## 💾 Flux de Données

### Exemple: Ajouter un produit

1. **UI** (ProduitPanel) 
   - Utilisateur clique "Ajouter Produit"
   - Dialog demande nom, catégorie, prix, stock

2. **DAO** (ProduitDAO)
   ```java
   ProduitDAO dao = new ProduitDAO();
   Produit p = new Produit("Coca 33cl", 1, 2.50, 50, 10);
   dao.create(p);  // INSERT INTO produit...
   ```

3. **BD** (MySQL)
   ```sql
   INSERT INTO produit (nom, categorie_id, prix_vente, stock_actuel, seuil_alerte)
   VALUES ('Coca 33cl', 1, 2.50, 50, 10)
   ```

4. **Retour** → Table mise à jour à l'écran

---

## 🔌 Configuration Base de Données

**Fichier à modifier**: `src/utils/DatabaseConnection.java`

```java
private static final String URL = "jdbc:mysql://localhost:3306/restaurant...";
private static final String USER = "root";      // ← Votre utilisateur MySQL
private static final String PASSWORD = "0000";  // ← Votre mot de passe
```

### Changer les identifiants MySQL:
1. Dans MySQLCommand: `mysql -u VOTRE_USER -p`
2. Modifier USER et PASSWORD dans DatabaseConnection.java
3. Recompiler et relancer

---

## 📥 Export & utilitaires

- **Export CSV**: un menu `Exporter` est disponible dans la barre de menu (Produits, Commandes, Tout). Les exports sont écrits dans le dossier `exports/` du projet.
- **Utilitaires** (dans `src/utils`):
   - `RunExport` : lance `ExportUtils.exportDefaultExports()` et génère `exports/produits.csv` et `exports/commandes.csv` sans ouvrir l'UI.
   - `CheckHistorique` : affiche les 20 dernières lignes de la table `historique` pour debug.
   - `CheckLignes` : affiche les lignes de `ligne_commande` (utile pour diagnostiquer totaux).
   - `FixLignes` : met à jour les lignes existantes où `prix_unitaire` est 0 en copiant `produit.prix_vente` et recalculant `montant_ligne`.

Exécution rapide (Windows):
```powershell
java -cp "bin;lib/*" utils.RunExport
java -cp "bin;lib/*" utils.CheckHistorique
java -cp "bin;lib/*" utils.FixLignes
```

## 🛡️ Historique / Audit

Une table `historique` est maintenant utilisée pour enregistrer les actions importantes (CREATE/UPDATE/DELETE) sur les entités critiques : `produit`, `commande`, `ligne_commande`, `mouvement_stock`, etc. Le format contient : `action`, `table_name`, `record_id`, `user`, `details`, `created_at`.

Points importants:
- La table est créée automatiquement par `AuditUtils.ensureTableExists()` la première fois qu'un log ou une lecture est effectué.
- Les opérations CRUD sur les DAOs principaux appellent maintenant `AuditUtils.log(...)` pour conserver l'historique.
- Par défaut, l'utilisateur enregistré est `system` ; pour enregistrer le login réel, propager l'objet `Utilisateur` courant vers les DAO ou utiliser un `ThreadLocal` (voir `GUIDE_MODIFICATIONS.md` pour une approche).

## 🔧 Correction des totaux des commandes (legacy)

Si vous voyez des montants `0.00` dans les détails d'une commande, cela vient parfois de lignes où `prix_unitaire` n'avait pas été enregistré (valeur 0). Utilisez `FixLignes` pour corriger la base : il copiera `produit.prix_vente` dans `ligne_commande.prix_unitaire` et recalculera `montant_ligne`.


## 🚀 Compilation et Exécution

### Compiler:
```bash
cd RestaurantApp
javac -cp lib/*:src -d bin src/**/*.java
```

### Exécuter:
```bash
java -cp bin:lib/* Main
```

Ou utiliser `run.bat` (Windows) / `run.sh` (Linux)

---

## 🔐 Utilisateurs de test

Dans `database/restaurant.sql`:
```sql
INSERT INTO utilisateur (login, motDePasse) VALUES ('admin', 'admin123');
INSERT INTO utilisateur (login, motDePasse) VALUES ('user', 'user123');
```

- Login: `admin` / Mot de passe: `admin123`
- Login: `user` / Mot de passe: `user123`

---

## 📊 Schéma Base de Données

```
┌────────────────┐
│  utilisateur   │
├────────────────┤
│ id (PK)        │
│ login (UNIQUE) │
│ motDePasse     │
└────────────────┘

┌────────────────┐
│  categorie     │
├────────────────┤
│ id (PK)        │
│ libelle        │
└────────────────┘
       ↑
       │ 1:N (FK)
       │
┌──────────────┐      ┌─────────────────┐      ┌────────────────┐
│   produit    │ ← 1:N │ mouvement_stock │      │   commande     │
├──────────────┤      └─────────────────┤      │────────────────┤
│ id (PK)      │      │ id (PK)         │      │ id (PK)        │
│ nom          │      │ produit_id (FK) │      │ date_commande  │
│ categorie_id │      │ type (ENTREE/)  │      │ etat           │
│ prix_vente   │      │ quantite        │      │ total          │
│ stock_actual │      │ date_mouvement  │      └────────────────┘
│ seuil_alerte │      └─────────────────┘         ↑
└──────────────┘                                  │ 1:N
       ↑                                          │
       │ 1:N (FK)                                 │
       │                                    ┌─────────────────┐
       └────────────────────────────────────┤ ligne_commande  │
                                            ├─────────────────┤
                                            │ id (PK)         │
                                            │ commande_id (FK)│
                                            │ produit_id (FK) │
                                            │ quantite        │
                                            │ prix_unitaire   │
                                            │ montant_ligne   │
                                            └─────────────────┘
```

---

## 🔑 Points clés du code

### 1. Sécurité (PreparedStatement)
```java
// ✅ BON - Prévient l'injection SQL
String sql = "SELECT * FROM produit WHERE id = ?";
PreparedStatement pstmt = conn.prepareStatement(sql);
pstmt.setInt(1, id);
```

### 2. Pattern DAO (Séparation logique)
```java
// DAO encapsule l'accès BD
ProduitDAO dao = new ProduitDAO();
List<Produit> produits = dao.readAll();  // DAO gère le SQL
```

### 3. Interface générique (Réutilisabilité)
```java
// Chaque DAO implémente ces 5 méthodes CRUD
public interface IGenericDAO<T> {
    void create(T t);
    T read(int id);
    List<T> readAll();
    void update(T t);
    void delete(int id);
}
```

---

## 🐛 Dépannage courant

### Erreur: "Connection Refused"
- MySQL n'est pas en cours d'exécution
- Solution: Démarrer le service MySQL

### Erreur: "Driver not found"
- Le fichier `mysql-connector-java-x.x.x.jar` n'est pas dans `lib/`
- Solution: Télécharger et placer le driver

### Erreur: "Base de données non trouvée"
- La BD 'restaurant' n'a pas été créée
- Solution: Exécuter `database/restaurant.sql`

### Modification des identifiants MySQL
- Éditer `src/utils/DatabaseConnection.java`
- Changer USER et PASSWORD
- Recompiler le projet

---

## 📝 Comment ajouter une nouvelle fonctionnalité

### Exemple: Ajouter un champ "email" à Utilisateur

1. **Modifier la BD** (`database/restaurant.sql`)
```sql
ALTER TABLE utilisateur ADD COLUMN email VARCHAR(100);
```

2. **Modifier le modèle** (`src/models/Utilisateur.java`)
```java
private String email;  // Ajouter le champ
// Ajouter getters/setters
```

3. **Modifier le DAO** (`src/dao/UtilisateurDAO.java`)
```java
// Dans la requête INSERT
pstmt.setString(3, utilisateur.getEmail());

// Dans la requête SELECT
utilisateur.setEmail(rs.getString("email"));
```

4. **Modifier l'UI** (LoginFrame, etc)
```java
// Ajouter un champ de saisie pour email
```

---

## 🎓 Points à expliquer au prof

1. **Pattern DAO** - Vu en cours, bonne séparation des responsabilités
2. **IGenericDAO<T>** - Généricité Java, une interface pour tous les DAO
3. **PreparedStatement** - Sécurité (prévention injection SQL)
4. **Modèle MVC implicite** - Models + DAO/Services + UI (Swing)
5. **Base de données normalisée** - Clés étrangères, contraintes
6. **Connexion singleton** - Une seule connexion réutilisée

---

## 📞 Besoin d'aide?

Pour comprendre un fichier spécifique, regarde les commentaires dans le code:
- Classe: `/** ... */` (JavaDoc)
- Méthodes: `/** Description ... */`
- Blocs complexes: `// Explication...`
