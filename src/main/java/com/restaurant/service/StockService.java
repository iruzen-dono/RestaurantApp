package com.restaurant.service;

import com.restaurant.model.MouvementStock;
import com.restaurant.model.Product;
import com.restaurant.model.User;
import com.restaurant.repository.MouvementStockRepository;
import com.restaurant.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class StockService {

    @Autowired
    private MouvementStockRepository mouvementStockRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public MouvementStock enregistrerMouvement(Long productId, Integer quantite, String type, String motif, Long userId) {
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
            User user = new User();
            user.setId(userId);
            mouvement.setUser(user);
        }

        return mouvementStockRepository.save(mouvement);
    }

    public Integer getStock(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé : " + productId));
        return product.getStockActuel();
    }

    public List<MouvementStock> getHistorique(Long productId) {
        return mouvementStockRepository.findByProductIdOrderByDateMouvementDesc(productId);
    }
}
