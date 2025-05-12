package project;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/DeleteLibrarianServlet")
public class DeleteLibrarianServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String firstname = request.getParameter("firstname");

        PrintWriter out = response.getWriter();

        try {
            // Establish connection to database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/vvit", "root", "");

            // Prepare SQL statement
            String sql = "DELETE FROM addlibrarain WHERE email=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);

            // Execute SQL statement
            int rowsDeleted = statement.executeUpdate();

            statement.close();
            connection.close();

            if (rowsDeleted > 0) {
                // Send JavaScript alert message to client side
                out.println("<script>alert('Librarian deleted successfully!');</script>");
            } else {
                // Send JavaScript alert message to client side
                out.println("<script>alert('No librarian found with email " + email + "');</script>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Send JavaScript alert message to client side
            out.println("<script>alert('Error deleting librarian: " + e.getMessage() + "');</script>");
        }
    }
}
