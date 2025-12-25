package com.greengrocer.controller;

import com.greengrocer.dao.OrderDAO;
import com.greengrocer.dao.ProductDAO;
import com.greengrocer.model.Order;
import com.greengrocer.model.Product;
import com.greengrocer.util.Session;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.io.IOException;

public class OwnerController {

    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, String> nameCol;
    @FXML
    private TableColumn<Product, Double> priceCol;
    @FXML
    private TableColumn<Product, Double> stockCol;
    @FXML
    private TableColumn<Product, Double> thresholdCol;

    @FXML
    private TableView<Order> orderTable;
    @FXML
    private TableColumn<Order, Integer> orderIdCol;
    @FXML
    private TableColumn<Order, String> customerCol;
    @FXML
    private TableColumn<Order, String> statusCol;
    @FXML
    private TableColumn<Order, Double> totalCostCol;

    @FXML
    private javafx.scene.chart.PieChart salesChart;

    private ProductDAO productDAO;
    private OrderDAO orderDAO;

    public OwnerController() {
        productDAO = new ProductDAO();
        orderDAO = new OrderDAO();
    }

    @FXML
    public void initialize() {
        setupProductTable();
        setupOrderTable();
        refreshData();
        setupChart();
    }

    private void setupChart() {
        // Simple logic: Calculate sales by product type or just mock it for now as
        // complex aggregation in DAO is needed
        // For this demo, let's show status distribution of orders
        ObservableList<javafx.scene.chart.PieChart.Data> pieData = FXCollections.observableArrayList();

        long pending = orderDAO.getAllOrders().stream().filter(o -> "Pending".equals(o.getStatus())).count();
        long delivered = orderDAO.getAllOrders().stream().filter(o -> "Delivered".equals(o.getStatus())).count();
        long selected = orderDAO.getAllOrders().stream().filter(o -> "Selected".equals(o.getStatus())).count();

        pieData.add(new javafx.scene.chart.PieChart.Data("Pending", pending));
        pieData.add(new javafx.scene.chart.PieChart.Data("Delivered", delivered));
        pieData.add(new javafx.scene.chart.PieChart.Data("Selected", selected));

        salesChart.setData(pieData);
    }

    private void setupProductTable() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        priceCol.setOnEditCommit(e -> {
            Product p = e.getRowValue();
            p.setPrice(e.getNewValue());
            productDAO.updateProduct(p);
        });

        stockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        stockCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        stockCol.setOnEditCommit(e -> {
            Product p = e.getRowValue();
            p.setStock(e.getNewValue());
            productDAO.updateStock(p.getId(), e.getNewValue());
        });

        thresholdCol.setCellValueFactory(new PropertyValueFactory<>("threshold"));
        thresholdCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        thresholdCol.setOnEditCommit(e -> {
            Product p = e.getRowValue();
            p.setThreshold(e.getNewValue());
            productDAO.updateProduct(p);
        });

        productTable.setEditable(true);
    }

    private void setupOrderTable() {
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerCol.setCellValueFactory(
                cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getCustomerId()))); // Simplified
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        totalCostCol.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
    }

    @FXML
    public void refreshData() {
        ObservableList<Product> products = FXCollections.observableArrayList(productDAO.getAllProducts());
        productTable.setItems(products);

        ObservableList<Order> orders = FXCollections.observableArrayList(orderDAO.getAllOrders());
        orderTable.setItems(orders);

        setupChart();
    }

    @FXML
    public void handleLogout() {
        Session.logout();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) productTable.getScene().getWindow();
            stage.setTitle("Local Greengrocer Login");
            stage.setScene(new Scene(root, 960, 540));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
