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
import javax.servlet.http.HttpSession;

@WebServlet("/ProfileServlet")
public class ProfileServlet extends HttpServlet {
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

        // Retrieve the email and password from the session
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("username"); // Assuming username is the email
        String password = (String) session.getAttribute("password");

        if (email == null || password == null) {
            response.setContentType("text/html");
            response.getWriter().println("<p>Error: Email or password is not available in the session.</p>");
            return;
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        Connection connection = null;

        try {
            // Establish database connection
            String jdbcUrl = "jdbc:mysql://localhost:3306/vvit";
            String dbUser = "root";
            String dbPassword = "";

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);

            // Query to get user details based on email and password
            String query = "SELECT Name, Email, PhoneNumber, Gender, Language FROM insertregisterdetailsnew WHERE Email = ? AND Password = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String fullName = rs.getString("Name");
                String phoneNumber = rs.getString("PhoneNumber");
                String gender = rs.getString("Gender");
                String language = rs.getString("Language");

                // Display user details in a styled profile format
                out.println("<!DOCTYPE html>");
                out.println("<html lang=\"en\">");
                out.println("<head>");
                out.println("<meta charset=\"UTF-8\">");
                out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
                out.println("<title>Profile</title>");
                out.println("<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css\">");
                out.println("<style>");
                out.println("body { background-color: #f2f2f2; }");
                out.println(".profile-container { max-width: 600px; margin: 50px auto; padding: 30px; background-color: #fff; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }");
                out.println(".profile-picture { width: 150px; height: 150px; border-radius: 50%; margin: 0 auto 20px; display: block; }");
                out.println("h1 { text-align: center; margin-bottom: 20px; }");
                out.println("</style>");
                out.println("</head>");
                out.println("<body>");
                out.println("<div class=\"container\">");
                out.println("<div class=\"profile-container\">");
                out.println("<img src=\"https://res.cloudinary.com/dcwrp6n69/image/upload/v1718412286/307ce493-b254-4b2d-8ba4-d12c080d6651_h45bnd.jpg\" class=\"profile-picture\" alt=\"Profile Picture\">");
                out.println("<h1 class=\"text-center\">Profile Details</h1>");
                out.println("<p><strong>Full Name:</strong> " + fullName + "</p>");
                out.println("<p><strong>Email:</strong> " + email + "</p>");
                out.println("<p><strong>Phone Number:</strong> " + phoneNumber + "</p>");
                out.println("<p><strong>Gender:</strong> " + gender + "</p>");
                out.println("<p><strong>Language:</strong> " + language + "</p>");
                
                // Logout button
                out.println("<form action='E-Library2.html' method='POST'>");
                out.println("<button type='submit' class='btn btn-danger'>Logout</button>");
                out.println("</form>");
                
                out.println("</div>");
                out.println("</div>");
                out.println("</body>");
                out.println("</html>");
            } else {
                out.println("<p>Error: Invalid email or password.</p>");
            }

            rs.close();
            stmt.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<p>Error: " + e.getMessage() + "</p>");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            out.close();
        }
    }
}
