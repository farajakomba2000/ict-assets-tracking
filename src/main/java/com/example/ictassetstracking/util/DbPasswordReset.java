package com.example.ictassetstracking.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DbPasswordReset {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:postgresql://localhost:5432/ict-assets";
        String user = "postgres";
        String pass = "postgres";
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {
            int rows = stmt.executeUpdate("UPDATE user_account SET password='$2a$10$DOWSD1rzpS60MWTQicYKuOFGG9kW3nqOJQx8P4MY6jE6JzmcJcNfW' WHERE check_number=1001");
            System.out.println("rows updated=" + rows);
        }
    }
}
