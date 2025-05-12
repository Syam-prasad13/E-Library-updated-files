package project;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ViewFine")
public class ViewFine extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Database connection details
        String databaseURL = "jdbc:mysql://localhost:3306/vvit";
        String username = "root";
        String password = "";

        try {
            // Explicitly load the JDBC driver (not necessary for JDBC 4.0 and later, but still valid)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            try (Connection connection = DriverManager.getConnection(databaseURL, username, password)) {
                // SQL query to retrieve data with count
                String sql = "SELECT *, DATEDIFF(CURRENT_TIMESTAMP(), ReturnDate) AS DaysOverdue, "
                        + "DATEDIFF(CURRENT_TIMESTAMP(), ReturnDate) * FineRate AS Fine "
                        + "FROM addbookstudents";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    // Execute the query
                    try (ResultSet resultSet = statement.executeQuery()) {
                        // Display the retrieved data in a styled table with count column
                        out.println("<html>");
                        out.println("<head>");
                        out.println("<title>Book Fines</title>");
                        out.println("<style>");
                        out.println("body { font-family: 'Arial', sans-serif; background-color: #f4f4f4; }");
                        out.println("table { width: 80%; border-collapse: collapse; margin: 20px auto; }");
                        out.println("th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }");
                        out.println("th { background-color: #4CAF50; color: white; }");
                        out.println("</style>");
                        out.println("</head>");
                        out.println("<body>");

                        out.println("<h1 style='color: #333; text-align: center;'>Book Fines</h1>");

                        out.println("<table>");
                        out.println("<tr><th>BookId</th><th>BookName</th><th>BookAuthor</th><th>BookVolume</th><th>BookCost</th><th>Days Overdue</th><th>Fine</th></tr>");

                        while (resultSet.next()) {
                            int bookId = resultSet.getInt("BookId");
                            String bookName = resultSet.getString("BookName");
                            String bookAuthor = resultSet.getString("BookAuthor");
                            int bookVolume = resultSet.getInt("BookVolume");
                            double bookCost = resultSet.getDouble("BookCost");
                            int daysOverdue = resultSet.getInt("DaysOverdue");
                            double fine = resultSet.getDouble("Fine");

                            out.println("<tr>");
                            out.println("<td>" + bookId + "</td>");
                            out.println("<td>" + bookName + "</td>");
                            out.println("<td>" + bookAuthor + "</td>");
                            out.println("<td>" + bookVolume + "</td>");
                            out.println("<td>" + bookCost + "</td>");
                            out.println("<td>" + daysOverdue + "</td>");
                            out.println("<td>" + fine + "</td>");
                            out.println("</tr>");
                        }

                        out.println("</table>");

                        out.println("</body>");
                        out.println("</html>");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                out.println("Error:" + e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            out.println("Error: Unable to load JDBC driver");
        }
    }
}
