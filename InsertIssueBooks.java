package project;

import java.io.*;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;

@WebServlet("/InsertIssueBooks")
public class InsertIssueBooks extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int MAX_BOOK_LIMIT = 6; // Maximum number of books a student can issue

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Connection con = null;
        Connection con1 = null;
        PreparedStatement st = null;
        PreparedStatement checkStmt = null;
        ResultSet rs = null;

        try {
            con = DBConnect.connect();
            con1 = DBConnect1.connect();

            // Check the count of books issued by the student
            String checkSQL = "SELECT COUNT(*) AS bookCount FROM issuebooks WHERE StdRegNumber = ?";
            checkStmt = con.prepareStatement(checkSQL);
            checkStmt.setString(1, request.getParameter("StdRegNumber"));
            rs = checkStmt.executeQuery();

            int issuedBookCount = 0;
            if (rs.next()) {
                issuedBookCount = rs.getInt("bookCount");
            }

            if (issuedBookCount >= MAX_BOOK_LIMIT) {
                // Student has reached the limit
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.println("<html><body><script>alert('You have exceeded the limit of issued books.')</script></body></html>");
            } else {
                // Proceed with book issuance
                st = con.prepareStatement("INSERT INTO issuebooks VALUES(?,?,?,?,?)");
                st.setString(1, request.getParameter("BookId"));
                st.setString(2, request.getParameter("StudentName"));
                st.setString(3, request.getParameter("StdRegNumber"));
                st.setString(4, request.getParameter("IssuedDate"));
                st.setString(5, request.getParameter("IssuedTime"));
                st.executeUpdate();
                st.close();

                // Update statement for booksdata table
                PreparedStatement updateStatement = con1.prepareStatement("UPDATE booksdata SET issuestatus = 'Yes' WHERE AccessionNumber = ?");
                updateStatement.setString(1, request.getParameter("BookId")); // Assuming "BookId" parameter corresponds to "AccessionNumber" in booksdata table
                updateStatement.executeUpdate();
                updateStatement.close();

                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.println("<html><body><script>alert('Book issued successfully')</script></body></html>");
            }
        } catch (Exception e) {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("Error: " + e.getMessage());
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (checkStmt != null) checkStmt.close();
                if (st != null) st.close();
                if (con != null) con.close();
                if (con1 != null) con1.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
