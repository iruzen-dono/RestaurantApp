package com.restaurant.service;

import com.restaurant.model.MouvementStock;
import com.restaurant.model.Product;
import com.restaurant.repository.MouvementStockRepository;
import com.restaurant.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MouvementStockRepository mouvementStockRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    @Transactional
    public void updateStock(Long productId, Integer quantite, String type, String motif, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé : " + productId));

        if ("ENTREE".equals(type)) {
            product.setStockActuel(product.getStockActuel() + quantite);
        } else if ("SORTIE".equals(type)) {
            if (product.getStockActuel() < quantite) {
                throw new RuntimeException("Stock insuffisant pour le produit : " + product.getNom());
            }
            product.setStockActuel(product.getStockActuel() - quantite);
        } else {
            throw new RuntimeException("Type de mouvement invalide : " + type);
        }

        productRepository.save(product);

        MouvementStock mouvement = MouvementStock.builder()
                .type(type)
                .quantite(quantite)
                .motif(motif)
                .dateMouvement(LocalDate.now())
                .product(product)
                .build();
        if (userId != null) {
            mouvement.setUser(new com.restaurant.model.User());
            mouvement.getUser().setId(userId);
        }
        mouvementStockRepository.save(mouvement);
    }
}
