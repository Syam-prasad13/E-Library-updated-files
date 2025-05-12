package project;
import java.sql.*;
import java.sql.DriverManager;

public class DBConnect2 {

	public static Connection connect()throws Exception {
		// TODO Auto-generated method stub
Class.forName("com.mysql.cj.jdbc.Driver");
		
		Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/books","root","");
	
		return con;
	}

}