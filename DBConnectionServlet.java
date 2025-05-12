package project;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import java.sql.Connection;
import java.sql.DriverManager;

@WebServlet("/DBConnectionServlet")
public class DBConnectionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public void init() throws ServletException {
        // Initialize database connection on servlet startup
        initializeDBConnection();
    }

    private void initializeDBConnection() {
        String jdbcUrl = "jdbc:mysql://localhost:3306/vvit";
        String dbUser = "root";
        String dbPassword = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);

            // Store the database connection in the application scope
            getServletContext().setAttribute("dbConnection", connection);

            System.out.println("Database connection initialized successfully");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error initializing database connection: " + e.getMessage());
        }
    }

    public void destroy() {
        // Close database connection on servlet shutdown
        closeDBConnection();
    }

    private void closeDBConnection() {
        try {
            Connection connection = (Connection) getServletContext().getAttribute("dbConnection");
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error closing database connection: " + e.getMessage());
        }
    }
}
