package project;

import java.io.*;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;

@WebServlet("/ReturnIssueBook")
public class ReturnIssueBook extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String bookId = request.getParameter("BookId");
        try {
            Connection con = DBConnect.connect();
            Connection con1 = DBConnect1.connect();

            // Check if the book exists in the issuebooks table
            PreparedStatement checkStatement = con.prepareStatement("SELECT * FROM issuebooks WHERE BookId = ?");
            checkStatement.setString(1, bookId);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                // Book exists, proceed with deletion
                PreparedStatement deleteStatement = con.prepareStatement("DELETE FROM issuebooks WHERE BookId = ?");
                deleteStatement.setString(1, bookId);
                int deletedRows = deleteStatement.executeUpdate();
                deleteStatement.close();

                if (deletedRows == 1) {
                    // Update issuestatus in booksdata table
                    PreparedStatement updateStatement = con1.prepareStatement("UPDATE booksdata SET issuestatus = 'No' WHERE AccessionNumber = ?");
                    updateStatement.setString(1, bookId);
                    updateStatement.executeUpdate();
                    updateStatement.close();

                    response.setContentType("text/html");
                    PrintWriter out = response.getWriter();
                    out.println("<html><body><script>alert('Successfully Book Returned')</script></body></html>");
                } else {
                    throw new SQLException("Failed to delete book from issuebooks table");
                }
            } else {
                throw new SQLException("Book with ID " + bookId + " not found in issuebooks table");
            }

            con.close();
            con1.close();
        } catch (SQLException e) {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("Error: " + e.getMessage());
            e.printStackTrace(out);
        } catch (Exception e) {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("Unexpected error: " + e.getMessage());
            e.printStackTrace(out);
        }
    }
}
