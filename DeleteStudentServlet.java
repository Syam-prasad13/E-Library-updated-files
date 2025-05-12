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

@WebServlet("/DeleteStudentServlet")
public class DeleteStudentServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String name = request.getParameter("name");

        PrintWriter out = response.getWriter();
        response.setContentType("text/html");

        try {
            // Establish connection to database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/vvit", "root", "");

            // Prepare SQL statement
            String sql = "DELETE FROM insertregisterdetailsnew WHERE email=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);

            // Execute SQL statement
            int rowsDeleted = statement.executeUpdate();

            statement.close();
            connection.close();

            if (rowsDeleted > 0) {
                // Send JavaScript alert message to client side and redirect to the details page
                out.println("<script>alert('Student " + name + " deleted successfully!'); window.location.href = 'RetrieveStudentDetails';</script>");
            } else {
                // Send JavaScript alert message to client side
                out.println("<script>alert('No student found with email " + email + "'); window.location.href = 'RetrieveStudentDetails';</script>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Send JavaScript alert message to client side
            out.println("<script>alert('Error deleting student: " + e.getMessage() + "'); window.location.href = 'RetrieveStudentDetails';</script>");
        }
    }
}
