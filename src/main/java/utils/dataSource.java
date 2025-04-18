package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dataSource {
    private final String url = "jdbc:mysql://localhost:3232/pidev3";
    private final String username = "root";
    private final String pwd = "";

    private static dataSource instance;

    private dataSource() {
        try {
            // Just testing the connection once to validate the credentials.
            DriverManager.getConnection(url, username, pwd).close();
            System.out.println("✅ Connexion à la base de données réussie !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion à la base de données !");
            throw new RuntimeException(e);
        }
    }

    public static dataSource getInstance() {
        if (instance == null) {
            instance = new dataSource();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, pwd); // ✅ Fresh connection each time
    }
}
