package org.example.controller.quiz;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import utils.dataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ajoutquiz {
    @FXML
    private TextField titreField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private DatePicker dateCreationPicker;
    @FXML
    private DatePicker dateDebutPicker;
    @FXML
    private DatePicker dateFinPicker;
    @FXML
    private TextField statutField;
    @FXML
    private Button addquizButton;
    @FXML
    private Button returnButton; // Maintenant appelé "Liste" dans l'interface

    @FXML
    private void initialize() {
        addquizButton.setOnAction(event -> handleAddquiz());
        returnButton.setOnAction(event -> showQuizList()); // Renommez la méthode pour plus de clarté
    }

    private void showQuizList() {
        try {
            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) returnButton.getScene().getWindow();
            currentStage.close();

            // Chemin correct du FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/quiz/Afficherquiz.fxml"));            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Quiz");
            stage.show();

        } catch (IOException e) {
            System.err.println("❌ Error loading AfficherQuiz: " + e.getMessage());
            e.printStackTrace(); // Ajout pour voir la trace complète de l'erreur
            showAlert("Erreur", "Impossible d'ouvrir la liste des quiz: " + e.getMessage());
        }
    }

    public void handleAddquiz() {
        String titre = titreField.getText();
        String description = descriptionArea.getText();
        LocalDateTime dateCreation = dateCreationPicker.getValue().atStartOfDay();
        LocalDateTime dateDebut = dateDebutPicker.getValue().atStartOfDay();
        LocalDateTime dateFin = dateFinPicker.getValue().atStartOfDay();
        String statut = statutField.getText();

        try {
            Connection conn = dataSource.getInstance().getConnection();

            String query = "INSERT INTO Quiz (titre, description, date_creation, datedebut, datefin, statut) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, titre);
            stmt.setString(2, description);
            stmt.setTimestamp(3, Timestamp.valueOf(dateCreation));
            stmt.setTimestamp(4, Timestamp.valueOf(dateDebut));
            stmt.setTimestamp(5, Timestamp.valueOf(dateFin));
            stmt.setString(6, statut);

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("✅ Quiz added successfully!");
                showAlert("Succès", "Quiz ajouté avec succès!");
                // Ne pas fermer automatiquement, laisser l'utilisateur décider
            } else {
                System.out.println("❌ Failed to add quiz.");
                showAlert("Erreur", "Échec de l'ajout du quiz.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
            showAlert("Erreur", "Erreur de base de données: " + e.getMessage());
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