package org.example.controller.quiz;

import org.example.models.quiz.Quiz;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import utils.dataSource;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class AfficherQuiz {
    @FXML
    private TableView<Quiz> quizTableView;
    @FXML
    private TableColumn<Quiz, Integer> idColumn;
    @FXML
    private TableColumn<Quiz, String> titreColumn;
    @FXML
    private TableColumn<Quiz, String> descriptionColumn;
    @FXML
    private TableColumn<Quiz, LocalDateTime> dateCreationColumn;
    @FXML
    private TableColumn<Quiz, LocalDateTime> dateDebutColumn;
    @FXML
    private TableColumn<Quiz, LocalDateTime> dateFinColumn;
    @FXML
    private TableColumn<Quiz, String> statutColumn;
    @FXML
    private Button refreshButton;
    @FXML
    private Button addButton;
    @FXML
    private Button modifyButton;
    @FXML
    private Button deleteButton;

    private ObservableList<Quiz> quizList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateCreationColumn.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));
        dateDebutColumn.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        dateFinColumn.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        loadQuizData();

        refreshButton.setOnAction(event -> loadQuizData());

        addButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/quiz/quiz.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Ajouter un quiz");
                stage.setScene(new Scene(root));
                stage.showAndWait();
                loadQuizData();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre d'ajout: " + e.getMessage());
            }
        });

        modifyButton.setOnAction(event -> modifySelectedQuiz());
        deleteButton.setOnAction(event -> deleteSelectedQuiz());
        
    }


    private void loadQuizData() {
        quizList.clear();

        try (Connection conn = dataSource.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Quiz")) {

            while (rs.next()) {
                Quiz quiz = new Quiz(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getTimestamp("date_creation").toLocalDateTime(),
                        rs.getTimestamp("datedebut").toLocalDateTime(),
                        rs.getTimestamp("datefin").toLocalDateTime(),
                        rs.getString("statut")
                );
                quizList.add(quiz);
            }

            quizTableView.setItems(quizList);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de base de données",
                    "Impossible de charger les quiz: " + e.getMessage());
        }
    }

    private void modifySelectedQuiz() {
        Quiz selectedQuiz = quizTableView.getSelectionModel().getSelectedItem();

        if (selectedQuiz == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection",
                    "Veuillez sélectionner un quiz à modifier.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/quiz/modifierquiz.fxml"));
            Parent root = loader.load();

            ModifierQuiz controller = loader.getController();
            controller.setQuiz(selectedQuiz);

            Stage stage = new Stage();
            controller.setDialogStage(stage);
            stage.setTitle("Modifier le quiz");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadQuizData();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'ouvrir la fenêtre de modification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteSelectedQuiz() {
        Quiz selectedQuiz = quizTableView.getSelectionModel().getSelectedItem();

        if (selectedQuiz == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection",
                    "Veuillez sélectionner un quiz à supprimer.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation de suppression");
        confirmDialog.setHeaderText("Suppression du quiz");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer le quiz \"" +
                selectedQuiz.getTitre() + "\" ?");

        Optional<ButtonType> result = confirmDialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection conn = dataSource.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM Quiz WHERE id = ?")) {

                stmt.setInt(1, selectedQuiz.getId());
                int rowsDeleted = stmt.executeUpdate();

                if (rowsDeleted > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Succès",
                            "Quiz supprimé avec succès.");
                    loadQuizData();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur",
                            "Impossible de supprimer le quiz.");
                }

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de base de données",
                        "Impossible de supprimer le quiz: " + e.getMessage());
            }
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