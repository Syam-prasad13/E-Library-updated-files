package project;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/LibrarainServlet")
public class LibrarainServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (validateUser(username, password)) {
            request.getRequestDispatcher("Librarainweb.html").forward(request, response);
        } else {
        	response.sendRedirect("loginfailedlibrarian.html");
        }
    }

    private boolean validateUser(String username, String password) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/vvit";
        String dbUser = "root";
        String dbPassword = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);

            System.out.println("Connected to the database");

            System.out.println("Received username: " + username);

            String query = "SELECT * FROM librarainlogin WHERE username=? AND password=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            System.out.println("Executing query: " + preparedStatement.toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println("User found in the database");
                resultSet.close();
                preparedStatement.close();
                connection.close();
                return true;
            }

            System.out.println("No matching user found in the database");

            resultSet.close();
            preparedStatement.close();
            connection.close();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error during user validation: " + e.getMessage());
            return false;
        }
    }
}
