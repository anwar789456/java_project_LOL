package org.example.controller.competition;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import utils.dataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;


public class ajoutcomp {
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
    private Button addCompetitionButton;

    @FXML
    private void initialize() {
        addCompetitionButton.setOnAction(event -> handleAddCompetition());
    }

    public void handleAddCompetition() {
        String titre = titreField.getText();
        String description = descriptionArea.getText();
        LocalDateTime dateCreation = dateCreationPicker.getValue().atStartOfDay();
        LocalDateTime dateDebut = dateDebutPicker.getValue().atStartOfDay();
        LocalDateTime dateFin = dateFinPicker.getValue().atStartOfDay();
        String statut = statutField.getText();

        try {
            Connection conn = dataSource.getInstance().getConnection();

            String query = "INSERT INTO competition (titre, description, date_creation, datedebut, datefin, statut) " +
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
                System.out.println("✅ Competition added successfully!");
                addCompetitionButton.getScene().getWindow().hide(); // Close window if needed
            } else {
                System.out.println("❌ Failed to add competition.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
        }
    }
}

