package org.example.controller.quiz;

import org.example.models.quiz.Quiz;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import utils.dataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ModifierQuiz {
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
    private ComboBox<String> statutComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private Quiz quizToModify;
    private Stage dialogStage;

    public void setQuiz(Quiz quiz) {
        this.quizToModify = quiz;

        // Remplir les champs avec les données du quiz
        titreField.setText(quiz.getTitre());
        descriptionArea.setText(quiz.getDescription());

        // Convertir LocalDateTime en LocalDate pour les DatePicker
        dateCreationPicker.setValue(quiz.getDateCreation().toLocalDate());
        dateDebutPicker.setValue(quiz.getDateDebut().toLocalDate());
        dateFinPicker.setValue(quiz.getDateFin().toLocalDate());

        // Configurer le ComboBox de statut
        statutComboBox.getItems().addAll("Actif", "Inactif", "Planifié", "Terminé");
        statutComboBox.setValue(quiz.getStatut());
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    private void initialize() {
        // Configurer les actions des boutons
        saveButton.setOnAction(event -> handleSave());
        cancelButton.setOnAction(event -> dialogStage.close());
    }

    private void handleSave() {
        if (isInputValid()) {
            Connection connection = null;
            PreparedStatement preparedStatement = null;

            try {
                // Préparer la requête SQL
                String query = "UPDATE Quiz SET titre = ?, description = ?, date_creation = ?, datedebut = ?, datefin = ?, statut = ? WHERE id = ?";

                // Obtenir une nouvelle connexion
                connection = dataSource.getInstance().getConnection();

                // Créer et exécuter le statement
                preparedStatement = connection.prepareStatement(query);

                // Remplir les paramètres
                preparedStatement.setString(1, titreField.getText());
                preparedStatement.setString(2, descriptionArea.getText());
                preparedStatement.setTimestamp(3, java.sql.Timestamp.valueOf(LocalDateTime.of(dateCreationPicker.getValue(), LocalDateTime.now().toLocalTime())));
                preparedStatement.setTimestamp(4, java.sql.Timestamp.valueOf(LocalDateTime.of(dateDebutPicker.getValue(), LocalDateTime.now().toLocalTime())));
                preparedStatement.setTimestamp(5, java.sql.Timestamp.valueOf(LocalDateTime.of(dateFinPicker.getValue(), LocalDateTime.now().toLocalTime())));
                preparedStatement.setString(6, statutComboBox.getValue());
                preparedStatement.setInt(7, quizToModify.getId());

                // Exécuter la mise à jour
                int result = preparedStatement.executeUpdate();

                if (result > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Quiz mis à jour avec succès.");
                    dialogStage.close();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la mise à jour du quiz.");
                }

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de base de données",
                        "Impossible de mettre à jour le quiz: " + e.getMessage());
                e.printStackTrace();
            } finally {
                // Fermer les ressources dans l'ordre inverse
                try {
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (titreField.getText() == null || titreField.getText().isEmpty()) {
            errorMessage += "Titre invalide!\n";
        }
        if (dateDebutPicker.getValue() == null) {
            errorMessage += "Date de début invalide!\n";
        }
        if (dateFinPicker.getValue() == null) {
            errorMessage += "Date de fin invalide!\n";
        }
        if (dateDebutPicker.getValue() != null && dateFinPicker.getValue() != null &&
                dateDebutPicker.getValue().isAfter(dateFinPicker.getValue())) {
            errorMessage += "La date de début doit être avant la date de fin!\n";
        }
        if (statutComboBox.getValue() == null) {
            errorMessage += "Statut invalide!\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", errorMessage);
            return false;
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}