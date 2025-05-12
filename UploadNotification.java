package project;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/uploadnotifications")
public class UploadNotification extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Assuming DBConnect is a utility class to get database connection
            Connection con = DBConnect.connect();

            String title = request.getParameter("title");
            String content = request.getParameter("content");
            String date = request.getParameter("date");

            PreparedStatement st = con.prepareStatement("INSERT INTO notifications (title, content, date) VALUES (?, ?, ?)");
            st.setString(1, title);
            st.setString(2, content);
            st.setDate(3, java.sql.Date.valueOf(date));
            st.executeUpdate();
            st.close();
            con.close();

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body><script>alert('Notification Uploaded Successfully');</script></body></html>");
        } catch (Exception e) {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body><script>alert('Error: " + e.getMessage() + "');</script></body></html>");
        }
    }
}
