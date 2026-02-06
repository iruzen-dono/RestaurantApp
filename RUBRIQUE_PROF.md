# 🎓 Guide pour la présentation au professeur

## 📋 Ce que tu peux expliquer

### 1. Architecture générale

**Montre ce schéma:**
```
                    ┌─────────────────┐
                    │  UTILISATEUR    │
                    │  (Swing UI)     │
                    └────────┬────────┘
                             │
                   ┌─────────▼─────────┐
                   │   IGenericDAO     │
                   │ (Interface DAO)   │
                   └─────────┬─────────┘
                             │
          ┌──────────────────┼──────────────────┐
          │                  │                  │
    ┌─────▼───┐      ┌─────▼──────┐     ┌─────▼───┐
    │Produit  │      │Utilisateur │     │Commande │
    │DAO      │      │DAO         │     │DAO      │
    └─────┬───┘      └─────┬──────┘     └─────┬───┘
          │                │                   │
    ┌─────▼──────────────────┴───────────────────┐
    │     Base de données MySQL                  │
    │  (produit, utilisateur, commande, etc)    │
    └──────────────────────────────────────────┘
```

**Explique:**
- Couches: UI → DAO → BD
- IGenericDAO: Interface générique pour réutilisabilité
- Pattern DAO: Séparation logique données/métier

---

### 2. Pattern DAO et IGenericDAO

**Montre le code IGenericDAO:**
```java
public interface IGenericDAO<T> {
    void create(T t);          // Ajouter
    T read(int id);            // Récupérer par ID
    List<T> readAll();         // Tous les enregistrements
    void update(T t);          // Modifier
    void delete(int id);       // Supprimer
}
```

**Explique:**
- `<T>` = Généricité Java (fonctionne avec n'importe quel type)
- Une interface = Une méthode CRUD pour tous les DAO
- Code réutilisable et cohérent
- ProduitDAO, UtilisateurDAO, CommandeDAO implémentent tous cette interface

---

### 3. Sécurité avec PreparedStatement

**Montre la différence:**

❌ **MAUVAIS (Injection SQL):**
```java
String sql = "SELECT * FROM produit WHERE id = " + id;
```

✅ **BON (PreparedStatement):**
```java
String sql = "SELECT * FROM produit WHERE id = ?";
PreparedStatement pstmt = conn.prepareStatement(sql);
pstmt.setInt(1, id);  // Le ? est remplacé safely
```

**Pourquoi:**
- Les ? sont traités comme des données, pas du code SQL
- Impossible d'injecter du code malveillant
- Exemple de faille: `id = "1 OR 1=1"` afficherait TOUS les produits!

---

### 4. Workflow complet: Créer un produit

**Montre le flux:**

```
1. UI (ProduitPanel)
   ↓
   Utilisateur clique "Ajouter"
   Saisit: nom, catégorie, prix, stock
   
2. DAO (ProduitDAO.create)
   ↓
   Produit p = new Produit("Coca", 1, 2.50, 50, 10);
   dao.create(p);
   
3. Requête SQL
   ↓
   INSERT INTO produit (nom, categorie_id, prix_vente, stock_actuel, seuil_alerte)
   VALUES ('Coca', 1, 2.50, 50, 10)
   
4. BD (MySQL)
   ↓
   Ajoute la ligne (MySQL génère l'ID)
   
5. Retour à l'UI
   ↓
   Table mise à jour
   ✅ Produit visible à l'écran
```

---

### 5. Configuration de la connexion BD

**Explique:**
```java
// DatabaseConnection.java (ligne 25-34)
private static final String URL = "jdbc:mysql://localhost:3306/restaurant?...";
private static final String USER = "root";
private static final String PASSWORD = "0000";
```

**Tu peux dire:**
- ✏️ Ces valeurs sont modifiables selon chaque installation
- `localhost:3306` = serveur MySQL local, port standard
- `restaurant` = nom de la base de données
- Si on veut changer de serveur, on modifie juste ces 3 lignes

---

### 6. Modèles (Entities)

**Montre la structure:**
```java
public class Produit {
    // Attributs = colonnes de la table
    private int id;
    private String nom;
    private int categorieId;
    private double prixVente;
    private int stockActuel;
    private int seuilAlerte;
    
    // Getters/Setters = acceseurs
}
```

**Explique:**
- Simple POJO (Plain Old Java Object)
- Chaque attribut = une colonne en BD
- Getters/Setters = accès sécurisé aux propriétés
- Pas de logique complexe = responsabilité unique

---

### 7. Authentification

**Montre le flux LoginFrame:**
```
1. Utilisateur saisit login + password
2. Click "Connexion"
3. UtilisateurDAO.authenticate(login, password)
4. Requête SQL: SELECT * FROM utilisateur WHERE login=? AND motDePasse=?
5. Si trouvé → Ouvre MainFrame, ferme LoginFrame
6. Si non trouvé → Message d'erreur
```

---

## 🤔 Questions possibles du prof (et réponses)

### Q1: "Pourquoi utiliser une interface DAO?"

**Ta réponse:**
- Cohérence: Tous les DAO ont la même structure (create, read, update, delete)
- Réutilisabilité: `<T>` fonctionne avec n'importe quel type
- Maintenance: Si on change l'implémentation, l'interface reste stable
- Exemple: Si on changerait BD (MySQL → PostgreSQL), le code UI resterait pareil

---

### Q2: "Comment modifier la configuration MySQL?"

**Ta réponse:**
1. Ouvrir `src/utils/DatabaseConnection.java`
2. Changer USER et PASSWORD (lignes 32-33)
3. Recompiler: `javac -cp lib/*:src -d bin src/**/*.java`
4. Relancer l'app
5. Si le serveur change: Modifier l'URL (localhost → autre IP)

---

### Q3: "Pourquoi PreparedStatement et pas String concatenation?"

**Ta réponse:**
- Sécurité contre l'injection SQL
- Exemple d'attaque: Si l'utilisateur saisit `"1 OR 1=1"` comme ID:
  - ❌ SQL concaténé: `WHERE id = 1 OR 1=1` → Retourne TOUS les produits!
  - ✅ PreparedStatement: Traite `1 OR 1=1` comme une donnée, pas du code SQL
- Bonne pratique industrielle

---

### Q4: "Comment ajouter un nouveau champ à une entité?"

**Ta réponse (4 étapes):**
1. Modifier la table MySQL: `ALTER TABLE ... ADD COLUMN ...`
2. Ajouter le champ au modèle Java + getters/setters
3. Modifier le DAO (INSERT, SELECT, UPDATE)
4. Utiliser le nouveau champ dans l'UI

**Exemple dans GUIDE_MODIFICATIONS.md**

---

### Q5: "Comment est structuré le projet?"

**Ta réponse:**
```
src/
├── Main.java              (Point d'entrée)
├── models/                (Entities: Produit, Utilisateur, etc)
├── dao/                   (Accès BD: ProduitDAO, UtilisateurDAO)
├── ui/
│   ├── frames/            (Fenêtres: LoginFrame, MainFrame)
│   └── panels/            (Panneaux: ProduitPanel, StockPanel)
└── utils/                 (Utilitaires: DatabaseConnection)

database/
└── restaurant.sql         (Script de création BD)

lib/
└── mysql-connector-java-*.jar  (Driver MySQL)
```

---

### Q6: "Y a-t-il une logique métier ou juste du CRUD?"

**Ta réponse:**
Actuellement: Principalement CRUD (Create Read Update Delete)

Mais j'ai ajouté quelques méthodes bonus:
- `ProduitDAO.increaseStock()` / `decreaseStock()` → Logique stock
- `ProduitDAO.readAlertProducts()` → Produits en rupture
- `ProduitDAO.readByCategorie()` → Filtrage

À améliorer:
- Service layer pour logique métier
- Validation (prix > 0, stock >= 0)
- Logging pour tracer les opérations

---

### Q7: "Comment gérer les erreurs?"

**Ta réponse:**
Actuellement:
- Try-catch dans les DAO
- Messages d'erreur affichés à l'utilisateur
- printStackTrace() pour logs en console

À améliorer:
- Centralisé Logger pour tous les erreurs
- Gestion des exceptions spécifiques (SQLException, etc)
- Fichier log pour historique

---

### Q8: "Pourquoi try-with-resources?"

**Ta réponse:**
```java
try (Connection conn = DatabaseConnection.getConnection();
     PreparedStatement pstmt = conn.prepareStatement(sql)) {
    // Utiliser conn et pstmt
}  // ← Automatiquement fermés ici
```

Avantages:
- Ferme automatiquement les ressources (connection, statement)
- Pas besoin de `.close()` manuel
- Évite les fuites mémoire
- Code plus lisible

---

### Q9: "Peut-on utiliser une ORM comme Hibernate?"

**Ta réponse:**
Oui, mais:
- **Actuellement:** JDBC pure = plus de contrôle, apprentissage pattern DAO
- **Hibernate:** ORM qui génère le SQL automatiquement
- **Différence:** 
  - JDBC = écrire soi-même le SQL
  - Hibernate = mapper les objets Java directement
- Pour ce TP: JDBC est plus pédagogique pour comprendre la BD

---

### Q10: "Pourquoi l'application n'a pas d'authentification complète?"

**Ta réponse:**
Pour ce TP: Authentification simple (vérifier login/password en BD)

À améliorer:
- Hashage du password (BCrypt, SHA-256) au lieu de stockage en clair
- Rôles et permissions (admin, user, etc)
- Sessions et tokens
- Logs d'authentification

---

## 💡 Conseils pour la présentation

### ✅ À faire
- Montrer le code réel (GitHub/VS Code)
- Exécuter l'app et montrer les fonctionnalités
- Expliquer le workflow complet (UI → DAO → BD)
- Montrer les fichiers de configuration
- Parler du pattern DAO et pourquoi c'est utile
- Mentionner la sécurité (PreparedStatement)

### ❌ À éviter
- Lire juste le code sans contexte
- Parler trop de détails techniques
- Oublier d'expliquer l'architecture globale
- Dire "c'est du code copié" sans comprendre

### 🎯 Résumé à retenir
"RestaurantApp est une application Java with Swing qui gère un restaurant avec:
- **Architecture:** UI → DAO → BD (séparation des responsabilités)
- **Pattern:** IGenericDAO pour CRUD réutilisable
- **Sécurité:** PreparedStatement contre injection SQL
- **Configuration:** Modifiable (USER, PASSWORD MySQL)
- **Extensible:** Facile d'ajouter nouveaux champs ou tables"

---

## 📂 Fichiers de documentation créés

- `DOCUMENTATION.md` - Vue d'ensemble complète
- `GUIDE_MODIFICATIONS.md` - Comment modifier/ajouter des données
- `RUBRIQUE_PROF.md` - **CELUI-CI** - Questions et réponses

Bon courage! 🚀
