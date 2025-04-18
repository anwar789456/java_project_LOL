package org.example.controller.user;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import utils.dataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class signup {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField numTelField;

    @FXML
    private TextField imageUrlField;

    @FXML
    private TextField rolesField;

    @FXML
    private Button registerButton;

    @FXML
    private Button closeButton;


    @FXML
    private void initialize() {
        registerButton.setOnAction(event -> handleSignup());
        closeButton.setOnAction(event -> registerButton.getScene().getWindow().hide());
    }



    private void handleSignup() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String numTel = numTelField.getText();
        String image_url = imageUrlField.getText();
        String roles = rolesField.getText();

        int login_count = 0;
        LocalDateTime penalized_until = LocalDateTime.now(); // Default to current time

        try {
            Connection conn = dataSource.getInstance().getConnection();

            String query = "INSERT INTO user (email, roles, password, name, login_count, image_url, numtel, penalized_until) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, email);
            stmt.setString(2, roles);
            stmt.setString(3, password);
            stmt.setString(4, name);
            stmt.setInt(5, login_count);
            stmt.setString(6, image_url);
            stmt.setString(7, numTel);
            stmt.setTimestamp(8, Timestamp.valueOf(penalized_until));

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("✅ User registered successfully!");
                registerButton.getScene().getWindow().hide(); // Close the signup window
            } else {
                System.out.println("❌ Failed to register user.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
        }
    }
}
