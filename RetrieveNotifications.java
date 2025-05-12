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

@WebServlet("/RetrieveNotifications")
public class RetrieveNotifications extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Replace with your database URL, username, and password
            String databaseURL = "jdbc:mysql://localhost:3306/vvit";
            String username = "root";
            String password = "";

            // Create a connection to the database
            Connection connection = DriverManager.getConnection(databaseURL, username, password);

            // SQL query to retrieve data from the notifications table ordered by date descending
            String sql = "SELECT * FROM notifications ORDER BY date DESC";
            PreparedStatement statement = connection.prepareStatement(sql);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // Display the retrieved data in a card layout
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Notifications</title>");
            out.println("<link rel='stylesheet' href='https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css'>");
            out.println("<script src='https://code.jquery.com/jquery-3.5.1.min.js'></script>");
            out.println("<script src='https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.3/dist/umd/popper.min.js'></script>");
            out.println("<script src='https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js'></script>");
            out.println("<style>");
            out.println("body { font-family: 'Arial', sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }");
            out.println(".container { margin-top: 80px; }"); // Adjust margin-top to account for the fixed navbar
            out.println(".card { margin-bottom: 20px; transition: transform 0.3s ease, box-shadow 0.3s ease; }");
            out.println(".card:hover { transform: scale(1.02); box-shadow: 0px 4px 20px rgba(0, 0, 0, 0.2); }");
            out.println(".card-header { background-color: #343a40; color: white; position: relative; }");
            out.println(".card-header .badge { position: absolute; top: 10px; right: 10px; background-color: #ff6f61; color: white; }");
            out.println(".card-body { background-color: white; }");
            out.println(".card-footer { background-color: #f8f9fa; }");
            out.println(".notification-date { font-size: 0.9em; color: #666; }");
            out.println(".fade-in { animation: fadeIn 1s ease-in; }");
            out.println("@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }");
            out.println(".navbar { padding-top: 20px; padding-bottom: 20px; position: fixed; width: 100%; top: 0; left: 0; z-index: 1000; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            // Add the navbar
            out.println("<nav class='navbar navbar-expand-lg navbar-dark bg-dark'>");
            out.println("<button class='navbar-toggler' type='button' data-toggle='collapse' data-target='#navbarNavAltMarkup' aria-controls='navbarNavAltMarkup' aria-expanded='false' aria-label='Toggle navigation'>");
            out.println("<span class='navbar-toggler-icon'></span>");
            out.println("</button>");
            out.println("<div class='collapse navbar-collapse' id='navbarNavAltMarkup'>");
            out.println("<div class='navbar-nav coloring'>");
            out.println("<li class='nav-item'><a class='nav-link' href='E-Library2.html' style='color:white; font-size:17px'>Home</a></li>");
            out.println("<li class='nav-item'><a class='nav-link' href='E-Library3.html' style='color:white'>Admin</a></li>");
            out.println("<li class='nav-item'><a class='nav-link' href='Librarainlogin.html' style='color:white'>Librarian</a></li>");
            out.println("<li class='nav-item'><a class='nav-link' href='studentlogin.html' style='color:white'>Student</a></li>");
            out.println("<li class='nav-item'><a class='nav-link' href='previousyearquestions.html' style='color:white;'>Previous Question Papers</a></li>");
            out.println("<li class='nav-item'><a class='nav-link' href='openaccessresources.html' style='color:white'>Open Access Resources</a></li>");
            out.println("<li class='nav-item'><a class='nav-link' href='RetrieveNotifications' style='color:white'>Notifications</a></li>");
            out.println("</div>");
            out.println("</div>");
            out.println("</nav>");
            // Add the container for notifications
            out.println("<div class='container'>");
            out.println("<h1 class='text-center'>Notifications</h1><br><br><br>");

            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String content = resultSet.getString("content");
                java.sql.Date date = resultSet.getDate("date");

                // Determine if the notification is new (e.g., posted today)
                boolean isNew = date.toLocalDate().equals(java.time.LocalDate.now());

                out.println("<div class='card fade-in'>");
                out.println("<div class='card-header'>");
                out.println("<h5>" + title + "</h5>");
                if (isNew) {
                    out.println("<span class='badge badge-primary'>New</span>");
                }
                out.println("</div>");
                out.println("<div class='card-body'>");
                out.println("<p>" + content + "</p>");
                out.println("</div>");
                out.println("<div class='card-footer text-muted notification-date'>");
                out.println("Date: " + date);
                out.println("</div>");
                out.println("</div>");
            }

            out.println("</div>");
            out.println("</body>");
            out.println("</html>");

            // Close resources
            resultSet.close();
            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<html><body><h3>Error: " + e.getMessage() + "</h3></body></html>");
        }
    }
}
