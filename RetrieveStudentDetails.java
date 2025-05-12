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

@WebServlet("/RetrieveStudentDetails")
public class RetrieveStudentDetails extends HttpServlet {
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
            String sql = "SELECT * FROM insertregisterdetailsnew";
            PreparedStatement statement = connection.prepareStatement(sql);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // Display the retrieved data in a styled table
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Student Details</title>");
            out.println("<link rel='stylesheet' href='https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css'>"); // Bootstrap CSS link
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
            out.println("<tr><th>Name</th><th>Email</th><th>Password</th><th>PhoneNumber</th><th>Gender</th><th>Language</th><th>Actions</th></tr>");

            while (resultSet.next()) {
                String name = resultSet.getString("Name");
                String email = resultSet.getString("Email");
                String password1 = resultSet.getString("Password");
                long phonenumber = resultSet.getLong("PhoneNumber");
                String gender = resultSet.getString("Gender");
                String language = resultSet.getString("Language");

                out.println("<tr>");
                out.println("<td>" + name + "</td>");
                out.println("<td>" + email + "</td>");
                out.println("<td>" + password1 + "</td>");
                out.println("<td>" + phonenumber + "</td>");
                out.println("<td>" + gender + "</td>");
                out.println("<td>" + language + "</td>");
                // Change the URL to pass the student details as query parameters
                out.println("<td><a href='UpdateStudentDetails.html?name=" + name + "&email=" + email + "&password=" + password1 + "&phonenumber=" + phonenumber + "&gender=" + gender + "&language=" + language + "' class='btn btn-success'>Edit</a> | <a href='StudentDelete.html?name=" + name + "&email=" + email + "' class='btn btn-danger'>Delete</a>");
                out.println("</td>");
                out.println("</tr>");
            }

            out.println("</table>");

            out.println("<script src='https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js'></script>"); // jQuery
            out.println("<script src='https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js'></script>"); // Popper.js
            out.println("<script src='https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js'></script>"); // Bootstrap JS

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
