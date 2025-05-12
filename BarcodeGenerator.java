package project;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;

public class BarcodeGenerator {

    public static void main(String[] args) {
        Connection connection = null;
        PreparedStatement updateStatement = null;
        ResultSet resultSet = null;

        try {
            // Register the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database
            String url = "jdbc:mysql://localhost:3306/books";
            String user = "root";
            String password = "";

            connection = DriverManager.getConnection(url, user, password);

            // Retrieve Accession Numbers from 17000 to 19000 from the booksdata table
            String selectSQL = "SELECT AccessionNumber FROM booksdata WHERE AccessionNumber BETWEEN 41000 AND 44000";
            Statement selectStatement = connection.createStatement();
            resultSet = selectStatement.executeQuery(selectSQL);

            // Prepare SQL statement to update Barcode column
            String updateSQL = "UPDATE booksdata SET Barcode = ? WHERE AccessionNumber = ?";
            updateStatement = connection.prepareStatement(updateSQL);

            // Set auto-commit to false for batch processing
            connection.setAutoCommit(false);

            int count = 0;

            while (resultSet.next()) {
                String accessionNumber = resultSet.getString("AccessionNumber");

                // Generate barcode for the current Accession Number
                BufferedImage barcodeImage = generateBarcode(accessionNumber);

                // Convert the barcode image to a byte array
                byte[] barcodeBytes = convertBufferedImageToByteArray(barcodeImage);

                // Update the Barcode column for the current Accession Number
                updateStatement.setBytes(1, barcodeBytes);
                updateStatement.setString(2, accessionNumber);
                updateStatement.addBatch();

                count++;

                if (count % 100 == 0) { // Execute batch update every 100 records
                    updateStatement.executeBatch();
                    connection.commit();
                    System.out.println("Processed " + count + " records.");
                }
            }

            // Execute any remaining batch
            updateStatement.executeBatch();
            connection.commit();

            System.out.println("Barcodes generated and stored successfully for Accession Numbers from 41000 to 44000. Total: " + count);

        } catch (ClassNotFoundException | SQLException | WriterException | IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            // Rollback transaction on error
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (updateStatement != null) updateStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static BufferedImage generateBarcode(String text) throws WriterException {
        Code128Writer barcodeWriter = new Code128Writer();
        BitMatrix bitMatrix = barcodeWriter.encode(text, BarcodeFormat.CODE_128, 300, 150);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    private static byte[] convertBufferedImageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}
