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

@WebServlet("/UpdateLibrarianServlet")
public class UpdateLibrarianServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        PrintWriter out = response.getWriter();

        try {
            // Establish connection to database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/vvit", "root", "");
            
            // Prepare SQL statement
            String sql = "UPDATE addlibrarain SET firstname=?, lastname=?, password=? WHERE email=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, firstname);
            statement.setString(2, lastname);
            statement.setString(3, password);
            statement.setString(4, email);

            // Execute SQL statement
            int rowsUpdated = statement.executeUpdate();
            
            statement.close();
            connection.close();
            if (rowsUpdated > 0) {
                // Display alert message
                out.println("<script>alert('Data updated successfully!');</script>");
            } else {
                // Display alert message
                out.println("<script>alert('Failed to update data!');</script>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("Error updating data: " + e.getMessage()); // Print error message
        }
    }
}
