package com.restaurant.config;

import com.restaurant.model.Category;
import com.restaurant.model.Product;
import com.restaurant.model.User;
import com.restaurant.repository.CategoryRepository;
import com.restaurant.repository.ProductRepository;
import com.restaurant.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Données déjà initialisées, skip.");
            return;
        }

        log.info("Initialisation des données...");

        // Créer utilisateurs
        User admin = User.builder()
                .login("admin")
                .motDePasse(passwordEncoder.encode("admin123"))
                .nom("Administrateur")
                .role("ADMIN")
                .isActive(true)
                .build();
        userRepository.save(admin);

        User user = User.builder()
                .login("user")
                .motDePasse(passwordEncoder.encode("admin123"))
                .nom("Utilisateur")
                .role("USER")
                .isActive(true)
                .build();
        userRepository.save(user);

        // Créer catégories
        Category boissons = Category.builder().libelle("Boissons").description("Boissons fraîches et chaudes").build();
        Category snacks = Category.builder().libelle("Snacks").description("Petites collations salées").build();
        Category plats = Category.builder().libelle("Plats").description("Plats principaux").build();
        Category desserts = Category.builder().libelle("Desserts").description("Desserts et pâtisseries").build();

        categoryRepository.save(boissons);
        categoryRepository.save(snacks);
        categoryRepository.save(plats);
        categoryRepository.save(desserts);

        // Créer produits de test
        productRepository.save(Product.builder().nom("Coca-Cola").description("Canette 33cl").prixVente(3.50).stockActuel(100).seuilAlerte(10).category(boissons).build());
        productRepository.save(Product.builder().nom("Eau Minérale").description("Bouteille 50cl").prixVente(2.00).stockActuel(150).seuilAlerte(20).category(boissons).build());
        productRepository.save(Product.builder().nom("Jus d'Orange").description("Jus pressé 25cl").prixVente(4.50).stockActuel(80).seuilAlerte(10).category(boissons).build());
        productRepository.save(Product.builder().nom("Café Expresso").description("Café serré").prixVente(2.50).stockActuel(200).seuilAlerte(30).category(boissons).build());

        productRepository.save(Product.builder().nom("Chips Nature").description("Sachet 150g").prixVente(3.00).stockActuel(60).seuilAlerte(10).category(snacks).build());
        productRepository.save(Product.builder().nom("Sandwich Jambon-Beurre").description("Pain tradition, jambon, beurre").prixVente(5.50).stockActuel(30).seuilAlerte(5).category(snacks).build());

        productRepository.save(Product.builder().nom("Pizza Margherita").description("Tomate, mozzarella, basilic").prixVente(12.00).stockActuel(20).seuilAlerte(5).category(plats).build());
        productRepository.save(Product.builder().nom("Burger Classic").description("Steak, salade, tomate, frites").prixVente(14.50).stockActuel(25).seuilAlerte(5).category(plats).build());
        productRepository.save(Product.builder().nom("Salade César").description("Poulet, laitue, parmesan, croûtons").prixVente(11.00).stockActuel(15).seuilAlerte(5).category(plats).build());

        productRepository.save(Product.builder().nom("Tiramisu").description("Dessert italien au café").prixVente(6.50).stockActuel(30).seuilAlerte(5).category(desserts).build());

        log.info("Données initialisées avec succès !");
    }
}
