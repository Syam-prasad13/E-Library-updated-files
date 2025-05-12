package project;

import java.io.*;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;

@WebServlet("/InsertLibrarainDetails")
public class InsertLibrarainDetails extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Connection con = null;
        PreparedStatement st = null;
        PreparedStatement stLogin = null;

        try {
            con = DBConnect.connect();
            con.setAutoCommit(false); // Start transaction

            // Insert into addlibrarain table
            String insertLibrarianQuery = "INSERT INTO addlibrarain (Firstname, Lastname, Email, Password) VALUES (?, ?, ?, ?)";
            st = con.prepareStatement(insertLibrarianQuery);
            st.setString(1, request.getParameter("Firstname"));
            st.setString(2, request.getParameter("Lastname"));
            st.setString(3, request.getParameter("Email"));
            st.setString(4, request.getParameter("Password"));
            st.executeUpdate();

            // Insert into librarainlogin table
            String insertLoginQuery = "INSERT INTO librarainlogin (username, password) VALUES (?, ?)";
            stLogin = con.prepareStatement(insertLoginQuery);
            stLogin.setString(1, request.getParameter("Email"));
            stLogin.setString(2, request.getParameter("Password"));
            stLogin.executeUpdate();

            con.commit(); // Commit transaction

            // Set response content type
            response.setContentType("text/html");

            // Display the styled alert message using JavaScript
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<script type='text/javascript'>");
            out.println("alert('Record Successfully Inserted!');");
            out.println("</script>");
            out.println("</body></html>");
        } catch (Exception e) {
            if (con != null) {
                try {
                    con.rollback(); // Rollback transaction on error
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            }
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("Error: " + e);
        } finally {
            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (stLogin != null) {
                try {
                    stLogin.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
