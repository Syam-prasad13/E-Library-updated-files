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
import javax.servlet.http.HttpSession;

@WebServlet("/ChangePasswordServlet")
public class ChangePasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("username");
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        PrintWriter out = response.getWriter();
        response.setContentType("text/html");

        if (!newPassword.equals(confirmPassword)) {
            out.println("<script>alert('New password and confirm password do not match.'); window.location.href = 'changepassword.html';</script>");
            return;
        }

        Connection connection = null;

        try {
            // Establish connection to database
            String jdbcUrl = "jdbc:mysql://localhost:3306/vvit";
            String dbUser = "root";
            String dbPassword = "";

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);

            // Check if current password is correct
            String selectSql = "SELECT Password FROM insertregisterdetailsnew WHERE Email = ?";
            PreparedStatement selectStmt = connection.prepareStatement(selectSql);
            selectStmt.setString(1, email);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("Password");
                
                // For debugging purposes
                System.out.println("Stored Password: " + storedPassword);
                System.out.println("Entered Current Password: " + currentPassword);

                if (storedPassword.equals(currentPassword)) {
                    // Update the new password in insertregisterdetailsnew
                    String updateSql1 = "UPDATE insertregisterdetailsnew SET Password = ? WHERE Email = ?";
                    PreparedStatement updateStmt1 = connection.prepareStatement(updateSql1);
                    updateStmt1.setString(1, newPassword);
                    updateStmt1.setString(2, email);

                    int rowsUpdated1 = updateStmt1.executeUpdate();

                    // Update the new password in studentlogin
                    String updateSql2 = "UPDATE studentlogin SET password = ? WHERE username = ?";
                    PreparedStatement updateStmt2 = connection.prepareStatement(updateSql2);
                    updateStmt2.setString(1, newPassword);
                    updateStmt2.setString(2, email);

                    int rowsUpdated2 = updateStmt2.executeUpdate();

                    if (rowsUpdated1 > 0 && rowsUpdated2 > 0) {
                        out.println("<script>alert('Password updated successfully!');</script>");
                    } else {
                        out.println("<script>alert('Error updating password.'); window.location.href = 'changepassword.html';</script>");
                    }

                    updateStmt1.close();
                    updateStmt2.close();
                } else {
                    out.println("<script>alert('Wrong current password.'); window.location.href = 'changepassword.html';</script>");
                }
            } else {
                out.println("<script>alert('User not found.'); window.location.href = 'changepassword.html';</script>");
            }

            rs.close();
            selectStmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<script>alert('Error: " + e.getMessage() + "'); window.location.href = 'changepassword.html';</script>");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            out.close();
        }
    }
}
