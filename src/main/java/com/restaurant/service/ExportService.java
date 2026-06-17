package com.restaurant.service;

import com.restaurant.model.Order;
import com.restaurant.model.Product;
import com.restaurant.repository.OrderRepository;
import com.restaurant.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExportService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    public List<String[]> exportProductsCSV() {
        List<Product> products = productRepository.findAll();
        List<String[]> lines = new ArrayList<>();

        // Header
        lines.add(new String[]{"ID", "Nom", "Description", "Prix Vente", "Stock Actuel", "Seuil Alerte", "Catégorie", "Actif"});

        for (Product p : products) {
            lines.add(new String[]{
                    String.valueOf(p.getId()),
                    p.getNom(),
                    p.getDescription() != null ? p.getDescription() : "",
                    String.valueOf(p.getPrixVente()),
                    String.valueOf(p.getStockActuel()),
                    String.valueOf(p.getSeuilAlerte()),
                    p.getCategory() != null ? p.getCategory().getLibelle() : "",
                    p.getIsActive() ? "Oui" : "Non"
            });
        }

        return lines;
    }

    public List<String[]> exportOrdersCSV() {
        List<Order> orders = orderRepository.findAll();
        List<String[]> lines = new ArrayList<>();

        // Header
        lines.add(new String[]{"ID", "Date", "État", "Total", "Utilisateur"});

        for (Order o : orders) {
            lines.add(new String[]{
                    String.valueOf(o.getId()),
                    o.getDateCommande() != null ? o.getDateCommande().toString() : "",
                    o.getEtat(),
                    String.valueOf(o.getTotal()),
                    o.getUser() != null ? o.getUser().getLogin() : ""
            });
        }

        return lines;
    }
}
