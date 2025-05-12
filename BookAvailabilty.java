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

@WebServlet("/BookAvailabilty")
public class BookAvailabilty extends HttpServlet {
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

            // SQL query to retrieve data with count
            String sql = "SELECT *, COUNT(BookName) AS BookCount FROM addbook GROUP BY BookName";
            PreparedStatement statement = connection.prepareStatement(sql);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // Display the retrieved data in a styled table with count column
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Book Availability</title>");
            out.println("<style>");
            out.println("body { font-family: 'Arial', sans-serif; background-color: #f4f4f4; }");
            out.println("table { width: 80%; border-collapse: collapse; margin: 20px auto; }");
            out.println("th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }");
            out.println("th { background-color: #4CAF50; color: white; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");

            out.println("<h1 style='color: #333; text-align: center;'>Book Availability</h1>");

            out.println("<table>");
            out.println("<tr><th>BookId</th><th>BookName</th><th>BookAuthor</th><th>BookVolume</th><th>BookCost</th><th>BookCount</th></tr>");

            while (resultSet.next()) {
                int bookId = resultSet.getInt("BookId");
                String bookName = resultSet.getString("BookName");
                String bookAuthor = resultSet.getString("BookAuthor");
                int bookVolume = resultSet.getInt("BookVolume");
                double bookCost = resultSet.getDouble("BookCost");
                int bookCount = resultSet.getInt("BookCount");

                out.println("<tr>");
                out.println("<td>" + bookId + "</td>");
                out.println("<td>" + bookName + "</td>");
                out.println("<td>" + bookAuthor + "</td>");
                out.println("<td>" + bookVolume + "</td>");
                out.println("<td>" + bookCost + "</td>");
                out.println("<td>" + bookCount + "</td>");
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
