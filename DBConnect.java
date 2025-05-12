package project;
import java.sql.*;
import java.sql.DriverManager;

public class DBConnect {

	public static Connection connect()throws Exception {
		// TODO Auto-generated method stub
Class.forName("com.mysql.cj.jdbc.Driver");
		
		Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/vvit","root","");
	
		return con;
	}

}