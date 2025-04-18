package org.example.controller.cours;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.models.cours.Cours;
import utils.dataSource;

import java.sql.*;

public class CoursListController {


    @FXML
    private TableView<Cours> coursTable;

    @FXML
    private TableColumn<Cours, String> titleColumn;
    @FXML
    private TableColumn<Cours, String> descColumn;
    @FXML
    private TableColumn<Cours, Double> priceColumn;
    @FXML
    private TableColumn<Cours, String> typeColumn;
    @FXML
    private Button modifyBtn;

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(cell -> cell.getValue().titreProperty());
        descColumn.setCellValueFactory(cell -> cell.getValue().descriptionProperty());
        priceColumn.setCellValueFactory(cell -> cell.getValue().priceProperty().asObject());
        typeColumn.setCellValueFactory(cellData -> {
            boolean isFree = cellData.getValue().isFreeProperty().get();
            return new SimpleStringProperty(isFree ? "Free" : "Paid");
        });

        coursTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            modifyBtn.setDisable(newVal == null);
        });

        modifyBtn.setOnAction(e -> {
            Object selected = coursTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                openEditWindow(selected);
            }
        });

        loadCourses();
    }

    void loadCourses() {
        ObservableList<Cours> coursList = FXCollections.observableArrayList();


        try (Connection conn = dataSource.getInstance().getConnection()) {
            String query = "SELECT * FROM cours";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Cours cours = new Cours();
                cours.setId(rs.getInt("id"));
                cours.setOwnerId(rs.getInt("idowner_id"));
                cours.setTitre(rs.getString("titre"));
                cours.setDescription(rs.getString("description"));
                cours.setPrice(rs.getDouble("price"));
                cours.setImg(rs.getString("img"));
                cours.setIsFree(rs.getBoolean("is_free"));
                cours.setDateCreation(rs.getTimestamp("datecreation").toLocalDateTime());

                coursList.add(cours);
            }

            coursTable.setItems(coursList); // ✅ Set the data to the table

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openEditWindow(Object cours) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cours/edit_cours.fxml"));
            AnchorPane root = loader.load();
            EditCoursController controller = (EditCoursController) loader.getController();

            controller.setCours((Cours) cours); // Pass the selected cours

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier le cours");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
