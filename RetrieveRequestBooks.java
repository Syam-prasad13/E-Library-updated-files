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

@WebServlet("/RetrieveRequestBooks")
public class RetrieveRequestBooks extends HttpServlet {
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
            String sql = "SELECT * FROM requestbooksnew";
            PreparedStatement statement = connection.prepareStatement(sql);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // Display the retrieved data in a styled table
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Issue Books Details</title>");
            out.println("<style>");
            out.println("body { font-family: 'Arial', sans-serif; background-color: #f4f4f4; }");
            out.println("table { width: 80%; border-collapse: collapse; margin: 20px auto; }");
            out.println("th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }");
            out.println("th { background-color: #343a40; color: white; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");

            out.println("<table>");
            out.println("<tr><th>Fullname</th><th>RegisterNumber</th><th>BookId</th><th>BookTitle</th><th>Author</th><th>Book Return Data</th></tr>");

            while (resultSet.next()) {
                String fullname = resultSet.getString("Fullname");
                String registernumber = resultSet.getString("RegisterNumber");
                int bookid = resultSet.getInt("BookId");
                String booktitle = resultSet.getString("BookTitle");
                String author = resultSet.getString("Author");
                java.sql.Date date = resultSet.getDate("Date");
                

                out.println("<tr>");
                out.println("<td>" + fullname + "</td>");
                out.println("<td>" + registernumber + "</td>");
                out.println("<td>" + bookid + "</td>");
                out.println("<td>" + booktitle + "</td>");
                out.println("<td>" + author + "</td>");
                out.println("<td>" + date + "</td>");
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
