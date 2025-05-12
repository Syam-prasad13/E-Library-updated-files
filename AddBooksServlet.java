package project;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;

@WebServlet("/AddBooksServlet")
public class AddBooksServlet extends HttpServlet {
    private static final int THREAD_POOL_SIZE = 10; // Adjust as per your requirement

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] accessionNumbersStr = request.getParameter("AccessionNumbers").split(",");
        String titleId = request.getParameter("TitleId");
        String titleName = request.getParameter("TitleName");
        String allowLend = request.getParameter("AllowLend");
        String authorName = request.getParameter("AuthorName");
        String authorId = request.getParameter("AuthorId");
        String supplierName = request.getParameter("SupplierName");
        String billNo = request.getParameter("BillNo");

        // Create thread pool
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try {
            for (String accessionNumberStr : accessionNumbersStr) {
                int accessionNumber = Integer.parseInt(accessionNumberStr);

                // Submit each book insertion task to the thread pool
                executorService.submit(() -> {
                    try {
                        // Generate barcode for the current Accession Number
                        BufferedImage barcodeImage = generateBarcode(accessionNumberStr);

                        // Convert the barcode image to a byte array
                        byte[] barcodeBytes = convertBufferedImageToByteArray(barcodeImage);

                        // Insert book data into the database
                        insertBookData(accessionNumber, titleId, titleName, allowLend, authorName, authorId, supplierName, billNo, barcodeBytes);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            // Send response back to client
            response.setContentType("text/plain");
            response.getWriter().write("Books insertion started successfully.");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Shutdown the thread pool
            executorService.shutdown();
        }
    }

    // Method to generate a barcode for a given text (accession number)
    private BufferedImage generateBarcode(String text) throws WriterException {
        Code128Writer barcodeWriter = new Code128Writer();
        BitMatrix bitMatrix = barcodeWriter.encode(text, BarcodeFormat.CODE_128, 300, 150);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    // Method to convert BufferedImage to byte array
    private byte[] convertBufferedImageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    // Method to insert book data into the database
    private void insertBookData(int accessionNumber, String titleId, String titleName, String allowLend,
                                String authorName, String authorId, String supplierName, String billNo,
                                byte[] barcodeBytes) throws SQLException {
        Connection connection = null;
        PreparedStatement insertStatement = null;

        try {
            // Connect to your database (adjust connection details as needed)
            String url = "jdbc:mysql://localhost:3306/books";
            String user = "root";
            String password = "";

            connection = DriverManager.getConnection(url, user, password);

            // Prepare SQL statement for insertion
            String insertSQL = "INSERT INTO booksdata (AccessionNumber, TitleId, TitleName, AllowLend, AuthorName, AuthorId, SupplierName, BillNo, IssueStatus, Barcode) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'No', ?)";
            insertStatement = connection.prepareStatement(insertSQL);

            // Set parameters for the SQL statement
            insertStatement.setInt(1, accessionNumber);
            insertStatement.setString(2, titleId);
            insertStatement.setString(3, titleName);
            insertStatement.setString(4, allowLend);
            insertStatement.setString(5, authorName);
            insertStatement.setString(6, authorId);
            insertStatement.setString(7, supplierName);
            insertStatement.setString(8, billNo);
            insertStatement.setBytes(9, barcodeBytes);

            // Execute the insertion
            insertStatement.executeUpdate();

        } finally {
            // Close resources
            try {
                if (insertStatement != null) insertStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
