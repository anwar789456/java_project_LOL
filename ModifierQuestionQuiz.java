package org.example.controller.quiz;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import utils.dataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ModifierQuestionQuiz {
    @FXML
    private TextField idQuizField;
    @FXML
    private TextArea questionArea;
    @FXML
    private TextField option1Field;
    @FXML
    private TextField option2Field;
    @FXML
    private TextField option3Field;
    @FXML
    private TextField option4Field;
    @FXML
    private TextField reponseCorrecteField;
    @FXML
    private TextArea explicationArea;
    @FXML
    private TextField typeQuestionField;
    @FXML
    private Button updateButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button closeButton;

    private int questionId;

    @FXML
    private void initialize() {
        // Aucune initialisation nécessaire pour le moment
    }

    public void setQuestionData(int id, int quizId, String question, String option1, String option2, 
                              String option3, String option4, int bonneReponse, String explication,
                              LocalDateTime dateCreation, String typeQuestion) {
        this.questionId = id;
        idQuizField.setText(String.valueOf(quizId));
        questionArea.setText(question);
        option1Field.setText(option1);
        option2Field.setText(option2);
        option3Field.setText(option3);
        option4Field.setText(option4);
        reponseCorrecteField.setText(String.valueOf(bonneReponse));
        explicationArea.setText(explication);
        typeQuestionField.setText(typeQuestion);
    }

    @FXML
    private void handleUpdate() {
        if (isInputValid()) {
            try {
                Connection conn = dataSource.getInstance().getConnection();
                String query = "UPDATE questionquiz SET question = ?, option_1 = ?, option_2 = ?, " +
                        "option_3 = ?, option_4 = ?, bonne_reponse = ?, explication = ?, type_question = ? " +
                        "WHERE id = ?";

                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, questionArea.getText());
                stmt.setString(2, option1Field.getText());
                stmt.setString(3, option2Field.getText());
                stmt.setString(4, option3Field.getText());
                stmt.setString(5, option4Field.getText());
                stmt.setInt(6, Integer.parseInt(reponseCorrecteField.getText()));
                stmt.setString(7, explicationArea.getText());
                stmt.setString(8, typeQuestionField.getText());
                stmt.setInt(9, questionId);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Question mise à jour avec succès");
                    Stage stage = (Stage) updateButton.getScene().getWindow();
                    stage.close();
                }

                stmt.close();
                conn.close();

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de mettre à jour la question: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (questionArea.getText() == null || questionArea.getText().isEmpty()) {
            errorMessage += "Question invalide!\n";
        }

        if (option1Field.getText() == null || option1Field.getText().isEmpty()) {
            errorMessage += "Option 1 invalide!\n";
        }

        if (option2Field.getText() == null || option2Field.getText().isEmpty()) {
            errorMessage += "Option 2 invalide!\n";
        }

        if (option3Field.getText() == null || option3Field.getText().isEmpty()) {
            errorMessage += "Option 3 invalide!\n";
        }

        if (option4Field.getText() == null || option4Field.getText().isEmpty()) {
            errorMessage += "Option 4 invalide!\n";
        }

        try {
            int reponse = Integer.parseInt(reponseCorrecteField.getText());
            if (reponse < 1 || reponse > 4) {
                errorMessage += "La réponse correcte doit être entre 1 et 4!\n";
            }
        } catch (NumberFormatException e) {
            errorMessage += "Réponse correcte invalide!\n";
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