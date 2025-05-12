package project;
import java.sql.*;

public class SqlDemo1{

	public static void main(String[] args) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/vvit","root","");
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("SELECT * FROM logindetails");
			while(rs.next()) {
				System.out.println(rs.getString(1)+" "+rs.getString(2));
			 
			}
			con.close();
			
			}
		catch(Exception e) {
			System.out.println(e);

		}

	}

}
