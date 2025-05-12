package project;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@WebServlet("/ActualBooks")
public class ActualBooks extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Retrieve the search parameter from the request
        String search = request.getParameter("search");

        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Replace the databaseURL, username, and password with your database connection details
            String databaseURL = "jdbc:mysql://localhost:3306/books";
            String username = "root";
            String password = "";

            // Create a connection to the database
            Connection connection = DriverManager.getConnection(databaseURL, username, password);

            // SQL query to retrieve data from library_books table with optional search filter
            String sql = "SELECT AccessionNumber, TitleId, TitleName, AllowLend, AuthorName, AuthorId, SupplierName, BillNo, Barcode, COUNT(TitleId) AS BookCount FROM library_books WHERE issuestatus='No'";
            if (search != null && !search.trim().isEmpty()) {
                sql += " AND (AccessionNumber LIKE ? OR TitleId LIKE ? OR TitleName LIKE ? OR AuthorName LIKE ? OR SupplierName LIKE ? OR BillNo LIKE ?)";
            }
            sql += " GROUP BY TitleId";
            PreparedStatement statement = connection.prepareStatement(sql);

            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search + "%";
                statement.setString(1, searchPattern);
                statement.setString(2, searchPattern);
                statement.setString(3, searchPattern);
                statement.setString(4, searchPattern);
                statement.setString(5, searchPattern);
                statement.setString(6, searchPattern);
            }

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // Display the retrieved data in a styled table with count column
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Book Details</title>");
            out.println("<style>");
            out.println("body { font-family: 'Arial', sans-serif; background-color: #f4f4f4; }");
            out.println("table { width: 80%; border-collapse: collapse; margin: 20px auto; }");
            out.println("th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }");
            out.println("th { background-color: #343a40; color: white; }");
            out.println("img { width: 150px; height: 50px; }"); // Ensure the barcode fits well in the table
            out.println("form { text-align: center; margin: 20px; }");
            out.println("input[type='text'] { padding: 10px; width: 300px; }");
            out.println("input[type='submit'] { padding: 10px; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<form method='get' action='ActualBooks'>");
            out.println("<input type='text' name='search' placeholder='Search...' value='" + (search != null ? search : "") + "' />");
            out.println("<input type='submit' value='Search' />");
            out.println("</form>");
            out.println("<table border=2.0>");
            out.println("<tr><th>Accession Number</th><th>Title ID</th><th>Title Name</th><th>Allow Lend</th><th>Author Name</th><th>Author ID</th><th>Supplier Name</th><th>Bill Number</th><th>Book Count</th><th>Barcode</th></tr>");

            while (resultSet.next()) {
                String accessionNumber = resultSet.getString("AccessionNumber");
                String titleId = resultSet.getString("TitleId");
                String titleName = resultSet.getString("TitleName");
                String allowLend = resultSet.getString("AllowLend");
                String authorName = resultSet.getString("AuthorName");
                String authorId = resultSet.getString("AuthorId");
                String supplierName = resultSet.getString("SupplierName");
                String billNo = resultSet.getString("BillNo");
                int bookCount = resultSet.getInt("BookCount");
                byte[] barcodeBytes = resultSet.getBytes("Barcode");
                String base64Barcode = "";

                if (barcodeBytes != null && barcodeBytes.length > 0) {
                    BufferedImage image = ImageIO.read(new ByteArrayInputStream(barcodeBytes));
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(image, "png", baos);
                    base64Barcode = Base64.getEncoder().encodeToString(baos.toByteArray());
                }

                out.println("<tr>");
                out.println("<td>" + accessionNumber + "</td>");
                out.println("<td>" + titleId + "</td>");
                out.println("<td>" + titleName + "</td>");
                out.println("<td>" + allowLend + "</td>");
                out.println("<td>" + authorName + "</td>");
                out.println("<td>" + authorId + "</td>");
                out.println("<td>" + supplierName + "</td>");
                out.println("<td>" + billNo + "</td>");
                out.println("<td>" + bookCount + "</td>");
                if (!base64Barcode.isEmpty()) {
                    out.println("<td><img src='data:image/png;base64," + base64Barcode + "' alt='Barcode'/></td>");
                } else {
                    out.println("<td>No Barcode</td>");
                }
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
