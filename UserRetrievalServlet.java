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

@WebServlet("/UserRetrievalServlet")
public class UserRetrievalServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Retrieve user information from the database
        retrieveUser(request, response);
    }

    private void retrieveUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Connection connection = (Connection) getServletContext().getAttribute("dbConnection");

        try {
            String username = request.getParameter("username");

            String query = "SELECT * FROM logindetails WHERE username=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            out.println("<html><body>");
            out.println("<h2>User Information</h2>");

            while (resultSet.next()) {
                out.println("<p>Username: " + resultSet.getString("username") + "</p>");
                out.println("<p>Password: " + resultSet.getString("password") + "</p>");
            }

            out.println("</body></html>");

            resultSet.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error retrieving user information: " + e.getMessage());
        }
    }
}
