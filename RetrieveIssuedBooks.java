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

@WebServlet("/RetrieveIssuedBooks")
public class RetrieveIssuedBooks extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Retrieve the search parameter from the request
        String search = request.getParameter("search");

        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Replace the databaseURL, username, and password with your database connection details
            String databaseURL = "jdbc:mysql://localhost:3306/vvit";
            String username = "root";
            String password = "";

            // Create a connection to the database
            Connection connection = DriverManager.getConnection(databaseURL, username, password);

            // SQL query to retrieve data with optional search filter
            String sql = "SELECT * FROM issuebooks";
            if (search != null && !search.trim().isEmpty()) {
                sql += " WHERE BookId LIKE ? OR StudentName LIKE ? OR StdRegNumber LIKE ? OR IssuedDate LIKE ? OR IssuedTime LIKE ?";
            }
            PreparedStatement statement = connection.prepareStatement(sql);

            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search + "%";
                statement.setString(1, searchPattern);
                statement.setString(2, searchPattern);
                statement.setString(3, searchPattern);
                statement.setString(4, searchPattern);
                statement.setString(5, searchPattern);
            }

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // Display the retrieved data in a styled table
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Issued Books</title>");
            out.println("<style>");
            out.println("body { font-family: 'Arial', sans-serif; background-color: #f4f4f4; }");
            out.println("table { width: 80%; border-collapse: collapse; margin: 20px auto; border: 2px solid #ddd; }");
            out.println("th, td { padding: 12px; text-align: left; border: 1px solid #ddd; }");
            out.println("th { background-color: #343a40; color: white; }");
            out.println("form { text-align: center; margin: 20px; }");
            out.println("input[type='text'] { padding: 10px; width: 300px; }");
            out.println("input[type='submit'] { padding: 10px; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<form method='get' action='RetrieveIssuedBooks'>");
            out.println("<input type='text' name='search' placeholder='Search...' value='" + (search != null ? search : "") + "' />");
            out.println("<input type='submit' value='Search' />");
            out.println("</form>");
            out.println("<table>");
            out.println("<tr><th>BookId</th><th>StudentName</th><th>StdRegNumber</th><th>IssuedDate</th><th>IssuedTime</th></tr>");

            while (resultSet.next()) {
                int bookid = resultSet.getInt("BookId");
                String studentname = resultSet.getString("StudentName");
                String registernum = resultSet.getString("StdRegNumber");
                java.sql.Date date = resultSet.getDate("IssuedDate");
                java.sql.Time time = resultSet.getTime("IssuedTime");

                out.println("<tr>");
                out.println("<td>" + bookid + "</td>");
                out.println("<td>" + studentname + "</td>");
                out.println("<td>" + registernum + "</td>");
                out.println("<td>" + date + "</td>");
                out.println("<td>" + time + "</td>");
                out.println("</tr>");
            }

            out.println("</table>");
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
