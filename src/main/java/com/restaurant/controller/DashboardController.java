package com.restaurant.controller;

import com.restaurant.model.*;
import com.restaurant.repository.MouvementStockRepository;
import com.restaurant.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired private ProductService productService;
    @Autowired private CategoryService categoryService;
    @Autowired private OrderService orderService;
    @Autowired private StockService stockService;
    @Autowired private ExportService exportService;
    @Autowired private MouvementStockRepository mouvementStockRepository;

    @GetMapping
    public String dashboard(Model model) {
        List<Product> allProducts = productService.findAll();
        List<Product> produitsAlerte = allProducts.stream()
                .filter(p -> p.getStockActuel() <= p.getSeuilAlerte())
                .collect(Collectors.toList());
        List<Order> allOrders = orderService.findAll();
        List<Order> dernieresCommandes = allOrders.size() > 10
                ? allOrders.subList(0, 10) : allOrders;
        double caTotal = allOrders.stream()
                .filter(o -> "VALIDEE".equals(o.getEtat()))
                .mapToDouble(Order::getTotal).sum();

        model.addAttribute("produitsCount", allProducts.size());
        model.addAttribute("commandesCount", allOrders.size());
        model.addAttribute("stockAlerteCount", produitsAlerte.size());
        model.addAttribute("caTotal", caTotal);
        model.addAttribute("dernieresCommandes", dernieresCommandes);
        model.addAttribute("produitsAlerte", produitsAlerte);
        return "dashboard";
    }

    // --- Products ---
    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("produits", productService.findAll());
        return "products/list";
    }

    @GetMapping("/products/new")
    public String productNew(Model model) {
        model.addAttribute("produit", null);
        model.addAttribute("newProduit", new Product());
        model.addAttribute("categories", categoryService.findAll());
        return "products/form";
    }

    @PostMapping("/products/save")
    public String productSave(@RequestParam(required = false) Long id,
                              @RequestParam String nom,
                              @RequestParam(required = false) String description,
                              @RequestParam Double prixVente,
                              @RequestParam Integer stockActuel,
                              @RequestParam(defaultValue = "5") Integer seuilAlerte,
                              @RequestParam Long categoryId,
                              @RequestParam(defaultValue = "true") boolean isActive) {
        Product p;
        if (id != null) {
            p = productService.findById(id);
        } else {
            p = new Product();
        }
        p.setNom(nom);
        p.setDescription(description);
        p.setPrixVente(prixVente);
        p.setStockActuel(stockActuel);
        p.setSeuilAlerte(seuilAlerte);
        p.setIsActive(isActive);
        Category cat = categoryService.findById(categoryId);
        p.setCategory(cat);
        productService.save(p);
        return "redirect:/dashboard/products";
    }

    @GetMapping("/products/edit/{id}")
    public String productEdit(@PathVariable Long id, Model model) {
        model.addAttribute("produit", productService.findById(id));
        model.addAttribute("categories", categoryService.findAll());
        return "products/form";
    }

    @GetMapping("/products/delete/{id}")
    public String productDelete(@PathVariable Long id) {
        productService.delete(id);
        return "redirect:/dashboard/products";
    }

    // --- Categories ---
    @GetMapping("/categories")
    public String categories(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "categories/list";
    }

    @GetMapping("/categories/new")
    public String categoryNew(Model model) {
        model.addAttribute("categorie", null);
        return "categories/form";
    }

    @PostMapping("/categories/save")
    public String categorySave(@RequestParam(required = false) Long id,
                               @RequestParam String libelle,
                               @RequestParam(required = false) String description) {
        Category cat;
        if (id != null) {
            cat = categoryService.findById(id);
        } else {
            cat = new Category();
        }
        cat.setLibelle(libelle);
        cat.setDescription(description);
        categoryService.save(cat);
        return "redirect:/dashboard/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String categoryEdit(@PathVariable Long id, Model model) {
        model.addAttribute("categorie", categoryService.findById(id));
        return "categories/form";
    }

    @GetMapping("/categories/delete/{id}")
    public String categoryDelete(@PathVariable Long id) {
        categoryService.delete(id);
        return "redirect:/dashboard/categories";
    }

    // --- Stock ---
    @GetMapping("/stock")
    public String stock(Model model) {
        model.addAttribute("produits", productService.findAll());
        model.addAttribute("mouvements", mouvementStockRepository.findAll());
        return "stock/index";
    }

    @PostMapping("/stock/entry")
    public String stockEntry(@RequestParam Long productId,
                             @RequestParam Integer quantite,
                             @RequestParam(required = false) String motif) {
        stockService.enregistrerMouvement(productId, quantite, "ENTREE", motif, null);
        return "redirect:/dashboard/stock";
    }

    @PostMapping("/stock/exit")
    public String stockExit(@RequestParam Long productId,
                            @RequestParam Integer quantite,
                            @RequestParam(required = false) String motif) {
        stockService.enregistrerMouvement(productId, quantite, "SORTIE", motif, null);
        return "redirect:/dashboard/stock";
    }

    // --- Orders ---
    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("commandes", orderService.findAll());
        return "orders/list";
    }

    @GetMapping("/orders/{id}")
    public String orderShow(@PathVariable Long id, Model model) {
        model.addAttribute("commande", orderService.findById(id));
        return "orders/detail";
    }

    @PostMapping("/orders/{id}/status")
    public String orderUpdateStatus(@PathVariable Long id, @RequestParam String statut) {
        orderService.updateStatut(id, statut);
        return "redirect:/dashboard/orders/" + id;
    }

    // --- Stats ---
    @GetMapping("/stats")
    public String stats(Model model) {
        List<Product> allProducts = productService.findAll();
        List<Order> allOrders = orderService.findAll();
        model.addAttribute("statsTotalProduits", allProducts.size());
        model.addAttribute("statsTotalCommandes", allOrders.size());
        model.addAttribute("statsStockTotal", allProducts.stream().mapToInt(Product::getStockActuel).sum());
        model.addAttribute("statsCA", allOrders.stream()
                .filter(o -> "VALIDEE".equals(o.getEtat()))
                .mapToDouble(Order::getTotal).sum());
        return "stats/index";
    }

    // --- Export CSV ---
    @GetMapping("/export/products")
    public ResponseEntity<InputStreamResource> exportProducts() {
        List<String[]> rows = exportService.exportProductsCSV();
        String csv = rows.stream().map(r -> String.join(",", r)).collect(Collectors.joining("\n"));
        return csvResponse(csv, "produits.csv");
    }

    @GetMapping("/export/orders")
    public ResponseEntity<InputStreamResource> exportOrders() {
        List<String[]> rows = exportService.exportOrdersCSV();
        String csv = rows.stream().map(r -> String.join(",", r)).collect(Collectors.joining("\n"));
        return csvResponse(csv, "commandes.csv");
    }

    private ResponseEntity<InputStreamResource> csvResponse(String csv, String filename) {
        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + filename);
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv; charset=utf-8"))
                .body(new InputStreamResource(bis));
    }
}
