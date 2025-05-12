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

@WebServlet("/UpdateStudentServlet")
public class UpdateStudentServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phonenumber = request.getParameter("phonenumber");
        String gender = request.getParameter("gender");
        String language = request.getParameter("language");

        PrintWriter out = response.getWriter();

        try {
            // Establish connection to database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/vvit", "root", "");
            
            // Prepare SQL statement
            String sql = "UPDATE insertregisterdetailsnew SET name=?, password=?, phonenumber=?, gender=?, language=? WHERE email=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, password);
            statement.setString(3, phonenumber);
            statement.setString(4, gender);
            statement.setString(5, language);
            statement.setString(6, email);

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
