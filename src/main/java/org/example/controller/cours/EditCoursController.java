package org.example.controller.cours;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.models.cours.Cours;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditCoursController {

    @FXML
    private TextField nomCours;
    @FXML
    private TextField descriptionCours;
    @FXML
    private ChoiceBox<String> choiceTypeCours;
    @FXML
    private TextField priceField;
    @FXML
    private Button uploadImageButton;
    @FXML
    private ImageView imagePreview;
    @FXML
    private Button submitButton;

    private Cours cours;

    @FXML
    private void initialize() {
        // Populate the ChoiceBox with "Free" and "Paid" options
        choiceTypeCours.getItems().addAll("Free", "Paid");
        choiceTypeCours.setValue("Free"); // Default value

        // Set initial visibility of price field
        togglePriceFieldVisibility();

        // Listener for changes in the ChoiceBox
        choiceTypeCours.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            togglePriceFieldVisibility();
        });

        // Upload image button action
        uploadImageButton.setOnAction(e -> openFileChooser());

        // Save button action
        submitButton.setOnAction(e -> saveCourse());
    }
    private void togglePriceFieldVisibility() {
        if ("Paid".equals(choiceTypeCours.getValue())) {
            // If "Paid" is selected, show the price field
            priceField.setVisible(true);
        } else {
            // If "Free" is selected, hide the price field and set price to 0
            priceField.setVisible(false);
            priceField.setText("0");
        }
    }
    public void setCours(Cours cours) {
        this.cours = cours;

        // Initialize fields with course data
        nomCours.setText(cours.getTitre());
        descriptionCours.setText(cours.getDescription());
        choiceTypeCours.setValue(cours.isFreeProperty().get() ? "Free" : "Paid");
        priceField.setText(String.valueOf(cours.getPrice()));

        // Optionally set the image preview if there is an image path
        if (cours.getImg() != null && !cours.getImg().isEmpty()) {
            imagePreview.setImage(new javafx.scene.image.Image("file:" + cours.getImg()));
        }
    }

    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            // Define the directory where the images will be stored
            String imagesFolder = "saved_images";  // This is the folder where images will be saved

            // Get the file name (to check if it exists already)
            String fileName = file.getName();
            File savedImageFile = new File(imagesFolder, fileName);

            // If the file doesn't exist in the saved_images folder, copy it there
            if (!savedImageFile.exists()) {
                try {
                    // Create the folder if it doesn't exist
                    File dir = new File(imagesFolder);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }

                    // Copy the selected file to the saved_images folder
                    Files.copy(file.toPath(), savedImageFile.toPath());
                    System.out.println("Image saved: " + savedImageFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }

            // Set the image path in the Cours object
            cours.setImg(savedImageFile.getAbsolutePath());

            // Display the selected image in the preview
            imagePreview.setImage(new javafx.scene.image.Image("file:" + savedImageFile.getAbsolutePath()));
        }
    }


    private void saveCourse() {
        try (Connection conn = utils.dataSource.getInstance().getConnection()) {
            String query = "UPDATE cours SET titre = ?, description = ?, price = ?, is_free = ?, img = ? WHERE id = ?";

            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, nomCours.getText());
            stmt.setString(2, descriptionCours.getText());
            stmt.setDouble(3, Double.parseDouble(priceField.getText()));
            stmt.setBoolean(4, "Free".equals(choiceTypeCours.getValue()));
            stmt.setString(5, cours.getImg()); // Update image path
            stmt.setInt(6, cours.getId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Course updated successfully!");
            } else {
                System.out.println("Failed to update course.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Close the window after saving
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }

}
