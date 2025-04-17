package org.example.controller.menu;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.Node;
import javafx.scene.control.Button;

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
    // Add others similarly...

    @FXML
    private void initialize() {
        usersButton.setOnAction(e -> loadContent("/User/users.fxml"));
        forumButton.setOnAction(e -> loadContent("/forum/forum.fxml"));
        competitionButton.setOnAction(e -> loadContent("/competition/competition.fxml"));
        projetButton.setOnAction(e -> loadContent("/projet/projet.fxml"));
        webinarButton.setOnAction(e -> loadContent("/webinar/webinar.fxml"));
        coursButton.setOnAction(e -> loadContent("/cours/cours.fxml"));
        // Add handlers for other buttons
    }

    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();
            contentPane.getChildren().setAll(content); // Replaces existing content
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
