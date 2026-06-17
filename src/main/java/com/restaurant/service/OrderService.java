package com.restaurant.service;

import com.restaurant.model.LigneCommande;
import com.restaurant.model.Order;
import com.restaurant.model.Product;
import com.restaurant.repository.OrderRepository;
import com.restaurant.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockService stockService;

    @Transactional
    public Order creerCommande(Long userId, List<OrderItemRequest> items) {
        Order order = Order.builder()
                .dateCommande(LocalDate.now())
                .etat("EN_COURS")
                .total(0.0)
                .build();

        if (userId != null) {
            com.restaurant.model.User user = new com.restaurant.model.User();
            user.setId(userId);
            order.setUser(user);
        }

        double total = 0.0;

        for (OrderItemRequest item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé : " + item.getProductId()));

            if (product.getStockActuel() < item.getQuantite()) {
                throw new RuntimeException("Stock insuffisant pour : " + product.getNom());
            }

            Double montantLigne = product.getPrixVente() * item.getQuantite();

            LigneCommande ligne = LigneCommande.builder()
                    .quantite(item.getQuantite())
                    .prixUnitaire(product.getPrixVente())
                    .montantLigne(montantLigne)
                    .product(product)
                    .commande(order)
                    .build();

            order.getLigneCommandes().add(ligne);
            total += montantLigne;
        }

        order.setTotal(total);
        Order savedOrder = orderRepository.save(order);

        // Décrémenter le stock
        for (OrderItemRequest item : items) {
            stockService.enregistrerMouvement(
                    item.getProductId(),
                    item.getQuantite(),
                    "SORTIE",
                    "Commande #" + savedOrder.getId(),
                    userId
            );
        }

        return savedOrder;
    }

    public Order findById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public List<Order> findByUser(Long userId) {
        return orderRepository.findByUserIdOrderByDateCommandeDesc(userId);
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Transactional
    public Order updateStatut(Long orderId, String newStatut) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée : " + orderId));
        order.setEtat(newStatut);
        return orderRepository.save(order);
    }

    public Double calculerCA(LocalDate dateDebut, LocalDate dateFin) {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .filter(o -> o.getDateCommande() != null
                        && !o.getDateCommande().isBefore(dateDebut)
                        && !o.getDateCommande().isAfter(dateFin))
                .mapToDouble(Order::getTotal)
                .sum();
    }

    // Inner class for order item requests
    public static class OrderItemRequest {
        private Long productId;
        private Integer quantite;

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Integer getQuantite() {
            return quantite;
        }

        public void setQuantite(Integer quantite) {
            this.quantite = quantite;
        }
    }
}
