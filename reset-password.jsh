String url = "jdbc:postgresql://localhost:5432/ict-assets";
String user = "postgres";
String pass = "postgres";
try {
    java.sql.Connection conn = java.sql.DriverManager.getConnection(url, user, pass);
    java.sql.Statement stmt = conn.createStatement();
    int n = stmt.executeUpdate("UPDATE user_account SET password='$2a$10$DOWSD1rzpS60MWTQicYKuOFGG9kW3nqOJQx8P4MY6jE6JzmcJcNfW' WHERE check_number=1001");
    System.out.println("rows=" + n);
    conn.close();
} catch (Exception e) {
    e.printStackTrace();
}
