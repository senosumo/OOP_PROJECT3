package com.greengrocer.controller;

import com.greengrocer.dao.OrderDAO;
import com.greengrocer.model.Order;
import com.greengrocer.util.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class CarrierController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private TableView<Order> availableTable;
    @FXML
    private TableColumn<Order, Integer> avIdCol;
    @FXML
    private TableColumn<Order, String> avAddressCol; // Ideally should join with User table for address
    @FXML
    private TableColumn<Order, String> avDateCol;

    @FXML
    private TableView<Order> myTable;
    @FXML
    private TableColumn<Order, Integer> myIdCol;
    @FXML
    private TableColumn<Order, String> myStatusCol;
    @FXML
    private TableColumn<Order, String> myDateCol;

    private OrderDAO orderDAO;

    public CarrierController() {
        orderDAO = new OrderDAO();
    }

    @FXML
    public void initialize() {
        if (Session.getCurrentUser() != null) {
            welcomeLabel.setText("Carrier: " + Session.getCurrentUser().getUsername());
        }

        avIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        avDateCol.setCellValueFactory(new PropertyValueFactory<>("deliveryTime"));

        myIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        myStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        myDateCol.setCellValueFactory(new PropertyValueFactory<>("deliveryTime")); // or deliveryTime

        refreshData();
    }

    @FXML
    public void refreshData() {
        ObservableList<Order> available = FXCollections.observableArrayList(orderDAO.getAvailableOrders());
        availableTable.setItems(available);

        ObservableList<Order> myOrders = FXCollections
                .observableArrayList(orderDAO.getOrdersByCarrier(Session.getCurrentUser().getId()));
        myTable.setItems(myOrders);
    }

    @FXML
    public void handleTakeOrder() {
        Order selected = availableTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (orderDAO.assignCarrier(selected.getId(), Session.getCurrentUser().getId())) {
                showAlert("Success", "Order assigned to you.");
                refreshData();
            } else {
                showAlert("Error", "Could not assign order. It may have been taken.");
                refreshData();
            }
        }
    }

    @FXML
    public void handleCompleteOrder() {
        Order selected = myTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if ("Delivered".equals(selected.getStatus())) {
                showAlert("Info", "Order is already delivered.");
                return;
            }
            if (orderDAO.updateOrderStatus(selected.getId(), "Delivered")) {
                showAlert("Success", "Order marked as Delivered!");
                refreshData();
            } else {
                showAlert("Error", "Failed to update status.");
            }
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
