package org.example.controller.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.models.user.User;
import utils.dataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AfficherUser {

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, Integer> idColumn;

    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> nameColumn;
    @FXML
    private TableColumn<User, String> rolesColumn;
    //@FXML
    //private TableColumn<User, Integer> loginCountColumn;
    @FXML
    private TableColumn<User, String> numTelColumn;
    //@FXML
    //private TableColumn<User, LocalDateTime> penalizedUntilColumn;
    @FXML
    private TableColumn<User, Void> deleteColumn;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        rolesColumn.setCellValueFactory(new PropertyValueFactory<>("roles"));
        //loginCountColumn.setCellValueFactory(new PropertyValueFactory<>("loginCount"));
        numTelColumn.setCellValueFactory(new PropertyValueFactory<>("numTel"));
        //penalizedUntilColumn.setCellValueFactory(new PropertyValueFactory<>("penalizedUntil"));

        // Load data
        userTable.setItems(getUserList());
        addDeleteButtonToTable();
    }

    private ObservableList<User> getUserList() {
        ObservableList<User> users = FXCollections.observableArrayList();

        try {
            Connection conn = dataSource.getInstance().getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM user");

            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("roles"),
                        rs.getString("name"),
                        rs.getInt("login_count"),
                        rs.getString("image_url"),
                        rs.getString("numtel"),
                        rs.getTimestamp("penalized_until") != null
                                ? rs.getTimestamp("penalized_until").toLocalDateTime()
                                : null
                );
                users.add(user);
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to fetch users: " + e.getMessage());
        }

        return users;
    }

    private void addDeleteButtonToTable() {
        deleteColumn.setCellFactory(param -> new javafx.scene.control.TableCell<>() {
            private final javafx.scene.control.Button deleteButton = new javafx.scene.control.Button("Delete");

            {
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
                deleteButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    deleteUser(user);
                    userTable.getItems().remove(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }
    private void deleteUser(User user) {
        try {
            Connection conn = dataSource.getInstance().getConnection();
            String sql = "DELETE FROM user WHERE email = ?";
            var stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getEmail());
            stmt.executeUpdate();
            System.out.println("🗑️ User deleted: " + user.getEmail());
        } catch (Exception e) {
            System.err.println("❌ Failed to delete user: " + e.getMessage());
        }
    }


}
