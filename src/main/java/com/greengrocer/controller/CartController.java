package com.greengrocer.controller;

import com.greengrocer.dao.OrderDAO;
import com.greengrocer.model.Order;
import com.greengrocer.model.OrderItem;
import com.greengrocer.model.User;
import com.greengrocer.util.Cart;
import com.greengrocer.util.Session;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDateTime;

public class CartController {

    @FXML
    private TableView<OrderItem> cartTable;
    @FXML
    private TableColumn<OrderItem, String> itemCol;
    @FXML
    private TableColumn<OrderItem, String> quantityCol;
    @FXML
    private TableColumn<OrderItem, String> priceCol;
    @FXML
    private TableColumn<OrderItem, String> totalCol;
    @FXML
    private Label totalLabel;
    @FXML
    private DatePicker deliveryDate;
    @FXML
    private TextField deliveryTimeField; // Simple text for time HH:mm

    private OrderDAO orderDAO;
    private Cart cart;

    public CartController() {
        orderDAO = new OrderDAO();
        cart = Cart.getInstance();
    }

    @FXML
    public void initialize() {
        itemCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProductName()));
        quantityCol.setCellValueFactory(
                data -> new SimpleStringProperty(String.format("%.2f kg", data.getValue().getQuantity())));
        priceCol.setCellValueFactory(
                data -> new SimpleStringProperty(String.format("$%.2f", data.getValue().getPricePerKg())));
        totalCol.setCellValueFactory(
                data -> new SimpleStringProperty(String.format("$%.2f", data.getValue().getTotalPrice())));

        cartTable.setItems(cart.getItems());
        updateTotal();
    }

    @FXML
    public void removeItem() {
        OrderItem selected = cartTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            cart.removeItem(selected);
            updateTotal();
        }
    }

    private void updateTotal() {
        double subtotal = cart.getTotal();
        double totalWithTax = subtotal * 1.10; // 10% VAT
        totalLabel.setText(String.format("Total (inc. VAT): $%.2f", totalWithTax));
    }

    @FXML
    public void handleCheckout() {
        if (cart.getItems().isEmpty()) {
            showAlert("Empty Cart", "Cannot checkout an empty cart.");
            return;
        }

        if (deliveryDate.getValue() == null || deliveryTimeField.getText().isEmpty()) {
            showAlert("Delivery Info Missing", "Please select a date and time.");
            return;
        }

        // Validation for date (within 48 hours)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime selectedDateTime = deliveryDate.getValue().atTime(0, 0); // Simplified time parsing
        try {
            String[] parts = deliveryTimeField.getText().split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            selectedDateTime = deliveryDate.getValue().atTime(hour, minute);
        } catch (Exception e) {
            showAlert("Invalid Time", "Format HH:mm required");
            return;
        }

        if (selectedDateTime.isBefore(now)) {
            showAlert("Invalid Date", "Delivery time cannot be in the past.");
            return;
        }
        if (selectedDateTime.isAfter(now.plusHours(48))) {
            showAlert("Invalid Date", "Delivery must be within 48 hours.");
            return;
        }

        // Create Order
        User user = Session.getCurrentUser();
        Order order = new Order();
        order.setCustomerId(user.getId());
        order.setDeliveryTime(selectedDateTime);
        order.setTotalCost(cart.getTotal() * 1.10); // With Tax
        order.setItems(cart.getItems());
        order.setInvoiceData("Invoice for Order by " + user.getUsername() + " Total: " + order.getTotalCost()); // Simplified
                                                                                                                // Invoice
                                                                                                                // text

        if (orderDAO.placeOrder(order)) {
            showAlert("Order Placed", "Your order has been placed successfully!");
            cart.clear();
            ((Stage) totalLabel.getScene().getWindow()).close();
        } else {
            showAlert("Error", "Failed to place order. Please try again.");
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
