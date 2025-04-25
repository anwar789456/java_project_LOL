package org.example.controller.quiz;

import org.example.models.quiz.QuestionQuiz;
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

public class AfficherQuestionQuiz {
    @FXML
    private TableView<QuestionQuiz> questionTableView;
    @FXML
    private TableColumn<QuestionQuiz, Integer> idColumn;
    @FXML
    private TableColumn<QuestionQuiz, Integer> idQuizColumn;
    @FXML
    private TableColumn<QuestionQuiz, String> questionColumn;
    @FXML
    private TableColumn<QuestionQuiz, String> option1Column;
    @FXML
    private TableColumn<QuestionQuiz, String> option2Column;
    @FXML
    private TableColumn<QuestionQuiz, String> option3Column;
    @FXML
    private TableColumn<QuestionQuiz, String> option4Column;
    @FXML
    private TableColumn<QuestionQuiz, Integer> bonneReponseColumn;
    @FXML
    private TableColumn<QuestionQuiz, String> typeQuestionColumn;
    @FXML
    private Button returnButton;
    @FXML
    private Button addButton;
    @FXML
    private Button modifyButton;
    @FXML
    private Button deleteButton;

    private ObservableList<QuestionQuiz> questionList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idQuizColumn.setCellValueFactory(new PropertyValueFactory<>("idQuiz"));
        questionColumn.setCellValueFactory(new PropertyValueFactory<>("question"));
        option1Column.setCellValueFactory(new PropertyValueFactory<>("option1"));
        option2Column.setCellValueFactory(new PropertyValueFactory<>("option2"));
        option3Column.setCellValueFactory(new PropertyValueFactory<>("option3"));
        option4Column.setCellValueFactory(new PropertyValueFactory<>("option4"));
        bonneReponseColumn.setCellValueFactory(new PropertyValueFactory<>("bonneReponse"));
        typeQuestionColumn.setCellValueFactory(new PropertyValueFactory<>("typeQuestion"));

        loadQuestionData();

        addButton.setOnAction(event -> handleAdd());
        modifyButton.setOnAction(event -> handleModify());
        deleteButton.setOnAction(event -> handleDelete());
        returnButton.setOnAction(event -> handleReturn());
    }

    @FXML
    private void handleReturn() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Menu/menu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) returnButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner au menu: " + e.getMessage());
        }
    }

    private void loadQuestionData() {
        questionList.clear();

        try (Connection conn = dataSource.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM questionquiz")) {

            while (rs.next()) {
                QuestionQuiz question = new QuestionQuiz(
                    rs.getInt("id"),
                    rs.getInt("id_quiz"),
                    rs.getString("question"),
                    rs.getString("option_1"),
                    rs.getString("option_2"),
                    rs.getString("option_3"),
                    rs.getString("option_4"),
                    rs.getInt("bonne_reponse"),
                    rs.getString("explication"),
                    rs.getTimestamp("date_creation").toLocalDateTime(),
                    rs.getString("type_question")
                );
                questionList.add(question);
            }

            questionTableView.setItems(questionList);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de base de données",
                    "Impossible de charger les questions: " + e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/quiz/ajouterquestion.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter une question");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadQuestionData();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre d'ajout: " + e.getMessage());
        }
    }

    @FXML
    private void handleModify() {
        QuestionQuiz selectedQuestion = questionTableView.getSelectionModel().getSelectedItem();

        if (selectedQuestion == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection",
                    "Veuillez sélectionner une question à modifier.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/quiz/modifierquestion.fxml"));
            Parent root = loader.load();

            ModifierQuestionQuiz controller = loader.getController();
            controller.setQuestionData(
                selectedQuestion.getId(),
                selectedQuestion.getIdQuiz(),
                selectedQuestion.getQuestion(),
                selectedQuestion.getOption1(),
                selectedQuestion.getOption2(),
                selectedQuestion.getOption3(),
                selectedQuestion.getOption4(),
                selectedQuestion.getBonneReponse(),
                selectedQuestion.getExplication(),
                selectedQuestion.getDateCreation(),
                selectedQuestion.getTypeQuestion()
            );

            Stage stage = new Stage();
            stage.setTitle("Modifier la question");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadQuestionData();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'ouvrir la fenêtre de modification: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        QuestionQuiz selectedQuestion = questionTableView.getSelectionModel().getSelectedItem();

        if (selectedQuestion == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection",
                    "Veuillez sélectionner une question à supprimer.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation de suppression");
        confirmDialog.setHeaderText("Suppression de la question");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer cette question ?");

        Optional<ButtonType> result = confirmDialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection conn = dataSource.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM questionquiz WHERE id = ?")) {

                stmt.setInt(1, selectedQuestion.getId());
                int rowsDeleted = stmt.executeUpdate();

                if (rowsDeleted > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Succès",
                            "Question supprimée avec succès.");
                    loadQuestionData();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur",
                            "Impossible de supprimer la question.");
                }

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de base de données",
                        "Impossible de supprimer la question: " + e.getMessage());
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