package project;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/IssueServlet")
public class IssueServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Retrieve the username and password from the session
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        String password = (String) session.getAttribute("password");

        if (username == null || password == null) {
            response.setContentType("text/html");
            response.getWriter().println("<p>Error: Username or password is not available in the session.</p>");
            return;
        }

        // Extract the first 10 characters of the username
        String regNumber = username.length() >= 10 ? username.substring(0, 10) : username;

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        Connection vvitConnection = null;
        Connection booksConnection = null;

        try {
            // Establish database connection to vvit
            String vvitJdbcUrl = "jdbc:mysql://localhost:3306/vvit";
            String dbUser = "root";
            String dbPassword = "";

            Class.forName("com.mysql.cj.jdbc.Driver");
            vvitConnection = DriverManager.getConnection(vvitJdbcUrl, dbUser, dbPassword);

            // Query to get data from issuebooks
            String issueBooksQuery = "SELECT BookId, StdRegNumber, IssuedDate, IssuedTime FROM issuebooks WHERE StdRegNumber = ?";
            PreparedStatement issueBooksStmt = vvitConnection.prepareStatement(issueBooksQuery);
            issueBooksStmt.setString(1, regNumber);

            ResultSet issueBooksResult = issueBooksStmt.executeQuery();

            out.println("<html>");
            out.println("<head>");
            out.println("<style>");
            out.println("body { font-family: 'Arial', sans-serif; background-color: #f4f4f4; }");
            out.println("table { width: 80%; border-collapse: collapse; margin: 20px auto; }");
            out.println("th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }");
            out.println("th { background-color: #343a40; color: white; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<table border='1'>");
            out.println("<tr><th>BookId</th><th>TitleName</th><th>StdRegNumber</th><th>IssuedDate</th><th>IssuedTime</th><th>Fine</th></tr>");

            while (issueBooksResult.next()) {
                String bookId = issueBooksResult.getString("BookId");
                String stdRegNumber = issueBooksResult.getString("StdRegNumber");
                String issuedDate = issueBooksResult.getString("IssuedDate");
                String issuedTime = issueBooksResult.getString("IssuedTime");

                // Establish database connection to books
                String booksJdbcUrl = "jdbc:mysql://localhost:3306/books";
                booksConnection = DriverManager.getConnection(booksJdbcUrl, dbUser, dbPassword);

                // Query to get TitleName from booksdata using BookId
                String booksDataQuery = "SELECT TitleName FROM booksdata WHERE AccessionNumber = ?";
                PreparedStatement booksDataStmt = booksConnection.prepareStatement(booksDataQuery);
                booksDataStmt.setString(1, bookId);

                ResultSet booksDataResult = booksDataStmt.executeQuery();

                String titleName = "";
                if (booksDataResult.next()) {
                    titleName = booksDataResult.getString("TitleName");
                }

                // Close the booksData result set and statement
                booksDataResult.close();
                booksDataStmt.close();
                booksConnection.close();

                // Calculate the fine
                LocalDate issueDate = LocalDate.parse(issuedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDate currentDate = LocalDate.now();
                long daysBetween = ChronoUnit.DAYS.between(issueDate.plusDays(10), currentDate);
                double fine = daysBetween > 0 ? daysBetween * 1.0 : 0.0; // Assuming fine rate is 1.0 per day

                // Print the data in the table
                out.println("<tr>");
                out.println("<td>" + bookId + "</td>");
                out.println("<td>" + titleName + "</td>");
                out.println("<td>" + stdRegNumber + "</td>");
                out.println("<td>" + issuedDate + "</td>");
                out.println("<td>" + issuedTime + "</td>");
                out.println("<td>" + fine + "</td>");
                out.println("</tr>");
            }

            out.println("</table>");
            out.println("</body>");
            out.println("</html>");

            // Close the issueBooks result set and statement
            issueBooksResult.close();
            issueBooksStmt.close();
            vvitConnection.close();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<p>Error: " + e.getMessage() + "</p>");
        } finally {
            if (vvitConnection != null) {
                try {
                    vvitConnection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (booksConnection != null) {
                try {
                    booksConnection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            out.close();
        }
    }
}
