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

@WebServlet("/RetrieveLibrarainData")
public class RetrieveLibrarianDetailsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Replace the databaseURL, username, and password with your database connection details
            String databaseURL = "jdbc:mysql://localhost:3306/vvit";
            String username = "root";
            String password = "";

            // Create a connection to the database
            Connection connection = DriverManager.getConnection(databaseURL, username, password);

            // SQL query to retrieve data
            String sql = "SELECT * FROM addlibrarain";
            PreparedStatement statement = connection.prepareStatement(sql);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // Display the retrieved data in a styled table
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Librarian Details</title>");
            out.println("<link rel='stylesheet' href='https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css'>"); // Add Bootstrap CSS
            out.println("<style>");
            out.println("body { font-family: 'Arial', sans-serif; background-color: #f4f4f4; }");
            out.println("table { width: 80%; border-collapse: collapse; margin: 20px auto; }");
            out.println("th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }");
            out.println("th { background-color: #343a40; color: white; }");
            out.println("td:last-child { text-align: center; }"); // Aligning the buttons in the center
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<table class='table table-striped'>");
            out.println("<tr><th>Firstname</th><th>Lastname</th><th>Email address</th><th>Password</th><th>Actions</th></tr>");

            while (resultSet.next()) {
                String firstname = resultSet.getString("Firstname");
                String lastname = resultSet.getString("Lastname");
                String email = resultSet.getString("Email");
                String password1 = resultSet.getString("Password");

                out.println("<tr>");
                out.println("<td>" + firstname + "</td>");
                out.println("<td>" + lastname + "</td>");
                out.println("<td>" + email + "</td>");
                out.println("<td>" + password1 + "</td>");
                // Change the URL to pass the librarian details as query parameters
                out.println("<td><a href='UpdateLibrarain.html?firstname=" + firstname + "&lastname=" + lastname + "&email=" + email + "&password=" + password1 + "' class='btn btn-success'>Edit</a> | <a href='LibrarainDelete.html?email=" + email + "&firstname=" + firstname + "' class='btn btn-danger'>Delete</a>");
                out.println("</tr>");
            }

            out.println("</table>");

            out.println("<script src='https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js'></script>"); // Add jQuery
            out.println("<script src='https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js'></script>"); // Add Popper.js
            out.println("<script src='https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js'></script>"); // Add Bootstrap JS

            out.println("</body>");
            out.println("</html>");

            // Close resources
            resultSet.close();
            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
            out.println("Error:" + e.getMessage());
        }
    }
}
