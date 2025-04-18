package org.example.controller.menu;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class menu {

    @FXML
    private Pane contentPane;

    @FXML
    private Button usersButton;
    @FXML
    private Button forumButton;
    @FXML
    private Button competitionButton;
    @FXML
    private Button projetButton;
    @FXML
    private Button webinarButton;
    @FXML
    private Button coursButton;

    @FXML
    private Button logoutButton;

    @FXML
    private void initialize() {

        // Logout button logic
        logoutButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/Login.fxml")); // Replace with your actual login FXML
                Parent root = loader.load();

                Stage stage = (Stage) logoutButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Menu buttons
        usersButton.setOnAction(e -> loadContent("/user/users.fxml"));
        forumButton.setOnAction(e -> loadContent("/forum/forum.fxml"));
        competitionButton.setOnAction(e -> loadContent("/competition/competition.fxml"));
        projetButton.setOnAction(e -> loadContent("/projet/projet.fxml"));
        webinarButton.setOnAction(e -> loadContent("/webinar/webinar.fxml"));
        coursButton.setOnAction(e -> loadContent("/cours/cours.fxml"));
    }

    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();
            contentPane.getChildren().setAll(content); // Replace existing content
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
