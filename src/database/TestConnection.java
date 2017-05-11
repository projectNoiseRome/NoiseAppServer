package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.Statement;

public class TestConnection {
	

	
	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url="jdbc:mysql://localhost:3306/testpervasive";
			String username="root";
			String password="Pervasive_System_2017";
			Connection conn=DriverManager.getConnection(url,username,password);                              
			DatabaseMetaData md=(DatabaseMetaData) conn.getMetaData();
			System.out.println("Informazioni sul driver:");
			System.out.println("Nome: "+ md.getDriverName() + "; versione: " + md.getDriverVersion());
		    // Do something with the Connection
			//Statement stmt=(Statement) conn.createStatement();
			//String createTableSensors="CREATE TABLE SENSORS"+"(SENSOR_NAME VARCHAR(32) NOT NULL)";
			//stmt.execute(createTableSensors);
			

		   
		} catch (SQLException ex) {
		    // handle any errors
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
	}
	
}
