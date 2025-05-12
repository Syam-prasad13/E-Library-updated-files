package project;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/LoginCheckServlet")
public class LoginCheckServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Validate login credentials against the database
        validateLogin(request, response);
    }

    private void validateLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Connection connection = (Connection) getServletContext().getAttribute("dbConnection");

        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            String query = "SELECT * FROM logindetails WHERE username=? AND password=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            out.println("<html><body>");

            if (resultSet.next()) {
                // Valid login credentials
                out.println("<h2>Login Successful!</h2>");
                out.println("<p>Welcome, " + username + "!</p>");
            } else {
                // Invalid login credentials
                out.println("<h2>Login Failed</h2>");
                out.println("<p>Invalid username or password. Please try again.</p>");
            }

            out.println("</body></html>");

            resultSet.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error validating login credentials: " + e.getMessage());
        }
    }
}
