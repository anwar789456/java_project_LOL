package org.example.controller.cours;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import utils.dataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
public class cours {

    @FXML
    private ChoiceBox<String> chocieTypeCours;
    @FXML
    private Button showCoursListButton;
    @FXML
    private Label priceLabel;

    @FXML
    private TextField priceField,nomCours, descriptionCours;
    @FXML
    private Button uploadImageButton;

    @FXML
    private ImageView imagePreview;
    @FXML
    private Button submitButton;
    private File selectedImageFile;

    @FXML
    public void initialize() {
        // Add choices to the ChoiceBox
        showCoursListButton.setOnAction(e -> openCoursListWindow());

        uploadImageButton.setOnAction(e -> handleImageUpload());
        submitButton.setOnAction(e -> handleSubmit());
        chocieTypeCours.getItems().addAll("Gratuit", "Payant");
        chocieTypeCours.setValue("Gratuit");
        togglePriceField(false);
        // Optional: Handle value changes
        chocieTypeCours.setOnAction(e -> {
            String selected = chocieTypeCours.getValue();
            System.out.println("Selected: " + selected);
        });
        chocieTypeCours.setOnAction(e -> {
            boolean isPaid = "Payant".equals(chocieTypeCours.getValue());
            togglePriceField(isPaid);
        });
    }

    private void togglePriceField(boolean show) {
        priceLabel.setVisible(show);
        priceLabel.setManaged(show);
        priceField.setVisible(show);
        priceField.setManaged(show);
    }
    private void openCoursListWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cours/cours_list.fxml"));
            AnchorPane listRoot = loader.load();

            // ✅ Get controller and call loadCourses()
            CoursListController controller = loader.getController();
             controller.loadCourses();

            Stage listStage = new Stage();
            listStage.setScene(new Scene(listRoot));
            listStage.setTitle("Liste des Cours");
            listStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(uploadImageButton.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;

            try (FileInputStream input = new FileInputStream(file)) {
                imagePreview.setImage(new Image(input));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void handleSubmit() {
        String titre = nomCours.getText();
        String description = descriptionCours.getText();
        boolean isFree = chocieTypeCours.getValue().equals("Gratuit");
        double price = isFree ? 0.0 : Double.parseDouble(priceField.getText());
        String imagePath = "";
        if (selectedImageFile != null) {
            imagePath = saveImageLocally(selectedImageFile);
        }
        int idOwner = 10;
        try {
            Connection conn = dataSource.getInstance().getConnection();
            String query = "INSERT INTO cours (idowner_id, titre, price, img, description, is_free, datecreation) VALUES (?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, idOwner);
            stmt.setString(2, titre);
            stmt.setDouble(3, price);
            stmt.setString(4, imagePath);
            stmt.setString(5, description);
            stmt.setBoolean(6, isFree);
            stmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));

            stmt.executeUpdate();
            if (selectedImageFile != null) {
                imagePath = saveImageLocally(selectedImageFile);
            }
            System.out.println("✅ Cours inséré avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors de l'insertion dans la base de données.");
        }

        // 📝 Later: Add your logic here to handle form data, DB insertion, etc.
        System.out.println("✅ Formulaire soumis.");
    }

    private String saveImageLocally(File sourceFile) {
        try {
            File destFolder = new File("saved_images");
            if (!destFolder.exists()) destFolder.mkdirs();

            File destFile = new File(destFolder, sourceFile.getName());

            try (FileInputStream in = new FileInputStream(sourceFile);
                 FileOutputStream out = new FileOutputStream(destFile)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }

                return destFile.getAbsolutePath(); // Store full or relative path in DB

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
