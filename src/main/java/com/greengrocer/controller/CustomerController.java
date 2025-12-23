package com.greengrocer.controller;

import com.greengrocer.dao.ProductDAO;
import com.greengrocer.model.Product;
import com.greengrocer.util.Cart;
import com.greengrocer.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CustomerController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private GridPane fruitsGrid;
    @FXML
    private GridPane vegetablesGrid;
    @FXML
    private TextField searchField;
    @FXML
    private TitledPane fruitsPane;
    @FXML
    private TitledPane vegetablesPane;

    private ProductDAO productDAO;

    public CustomerController() {
        productDAO = new ProductDAO();
    }

    @FXML
    public void initialize() {
        if (Session.getCurrentUser() != null) {
            welcomeLabel.setText("Welcome, " + Session.getCurrentUser().getUsername());
        }
        loadProducts("");
    }

    @FXML
    public void handleSearch() {
        loadProducts(searchField.getText());
    }

    private void loadProducts(String keyword) {
        List<Product> allProducts = keyword.isEmpty() ? productDAO.getAllProducts()
                : productDAO.searchProducts(keyword);

        List<Product> fruits = allProducts.stream().filter(p -> "fruit".equalsIgnoreCase(p.getType()))
                .collect(Collectors.toList());
        List<Product> vegetables = allProducts.stream().filter(p -> "vegetable".equalsIgnoreCase(p.getType()))
                .collect(Collectors.toList());

        populateGrid(fruitsGrid, fruits);
        populateGrid(vegetablesGrid, vegetables);

        fruitsPane.setText("Fruits (" + fruits.size() + ")");
        vegetablesPane.setText("Vegetables (" + vegetables.size() + ")");
    }

    private void populateGrid(GridPane grid, List<Product> products) {
        grid.getChildren().clear();
        int col = 0;
        int row = 0;

        for (Product product : products) {
            VBox card = createProductCard(product);
            grid.add(card, col, row);
            col++;
            if (col == 4) { // 4 items per row
                col = 0;
                row++;
            }
        }
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(10));
        card.setStyle(
                "-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 0);");
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(200);

        ImageView imageView = new ImageView();
        if (product.getImage() != null) {
            imageView.setImage(product.getImage());
        }
        imageView.setFitHeight(100);
        imageView.setFitWidth(150);
        imageView.setPreserveRatio(true);

        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Label priceLabel = new Label(String.format("$%.2f / kg", product.getCurrentPrice()));
        if (product.getCurrentPrice() > product.getPrice()) {
            priceLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;"); // Red price if doubled
            priceLabel.setText(priceLabel.getText() + " (High Demand!)");
        } else {
            priceLabel.setStyle("-fx-text-fill: #2ecc71;");
        }

        Label stockLabel = new Label("Stock: " + product.getStock() + " kg");

        TextField amountField = new TextField();
        amountField.setPromptText("Kg");
        amountField.setMaxWidth(80);

        Button addButton = new Button("Add to Cart");
        addButton.getStyleClass().add("button");
        addButton.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    showAlert("Invalid Amount", "Please enter a positive amount.");
                    return;
                }
                if (amount > product.getStock()) {
                    showAlert("Stock Warning", "Not enough stock available. Max: " + product.getStock());
                    return;
                }
                Cart.getInstance().addItem(product, amount);
                showAlert("Success", "Added " + amount + " kg of " + product.getName() + " to cart.");
                amountField.clear();
            } catch (NumberFormatException ex) {
                showAlert("Invalid Input", "Please enter a valid number for amount.");
            }
        });

        card.getChildren().addAll(imageView, nameLabel, priceLabel, stockLabel, amountField, addButton);
        return card;
    }

    @FXML
    public void openCart() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/cart.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Shopping Cart");
            stage.setScene(new Scene(root, 600, 500));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogout() {
        Session.logout();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setTitle("Local Greengrocer Login");
            stage.setScene(new Scene(root, 960, 540));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
