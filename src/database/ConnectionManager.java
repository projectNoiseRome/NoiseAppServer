package database;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.Statement;

public class ConnectionManager {
	
	private ConnectionMysql connection;
	private Connection conn;
	private static final String DATABASE_NAME = "noiseapp";
	private static final String SENSOR_TABLE = "sensorslist";
	private static final String USER_TABLE = "userlist";
	private static boolean firstRun = true;
	private static boolean firstInitialization = true;
	private static final String SENSOR_LIST_QUERY = "SELECT * FROM "+SENSOR_TABLE;
	
	
	public ConnectionManager(ConnectionMysql connection) throws ClassNotFoundException{
		this.connection = connection;
		if(firstInitialization){
			firstInitialization = false;
			initializeDB();
		}
		
	}
	
	//EACH TIME; WE HAVE TO OPEN AND CLOSE THE CONNECTION TO THE DB
	private void getConnection() throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		if(firstRun){
			System.out.println("First run here");
			firstRun = false;
			conn= (Connection) DriverManager.getConnection(connection.getUrl(), connection.getUsername(), connection.getPassword());
		}
		else{ //DATABASE noiseapp NOW EXISTS, LET'S USE IT!
			System.out.println("Not even more first run");
			conn= (Connection) DriverManager.getConnection(connection.getUrl()+DATABASE_NAME, connection.getUsername(), connection.getPassword());  
		}                            
		DatabaseMetaData md=(DatabaseMetaData) conn.getMetaData();
		System.out.println("Informazioni sul driver:");
		System.out.println("Nome: "+ md.getDriverName() + "; versione: " + md.getDriverVersion());
	}
	
	private void closeConnection(){
		try {
			conn.close();
		} catch (SQLException e) {
			System.out.println("Error while closing the connection to the DMBS");
			e.printStackTrace();
		}
	}
	
	//PRIVATE, FOR THE INITIALIZATION OF THE DB
	private void initializeDB() throws ClassNotFoundException{
		try {
			getConnection();
			String createDatabase = "CREATE DATABASE IF NOT EXISTS "+DATABASE_NAME;
			String useDatabase = "USE " + DATABASE_NAME;
			String sensors = "CREATE TABLE IF NOT EXISTS "+SENSOR_TABLE+"(SENSOR_NAME VARCHAR(32) PRIMARY KEY, LATITUDE VARCHAR(32) NOT NULL, LONGITUDE VARCHAR(32) NOT NULL, NOISE_LEVEL FLOAT NOT NULL)";
			String userRilevations = "CREATE TABLE IF NOT EXISTS "+USER_TABLE+"(USER_NAME VARCHAR(32) PRIMARY KEY, LATITUDE VARCHAR(32) NOT NULL, LONGITUDE VARCHAR(32) NOT NULL, NOISE_LEVEL FLOAT NOT NULL, NOISE_TYPE VARCHAR(32) NOT NULL)";
			Statement stmt = (Statement)conn.createStatement();
			stmt.execute(createDatabase);
			stmt.execute(useDatabase);
			stmt.execute(sensors);
			stmt.execute(userRilevations);
			stmt.close();
			closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//TO EXECUTE A GENERIC OPERATION ON THE DB
	public String executeQuery(String query){
		String result = "Query executed";
		try {
			getConnection();
			Statement stmt = (Statement) conn.createStatement();
			stmt.execute(query);
			stmt.close();
			closeConnection();
			System.out.println("Query executed");
		} catch (SQLException | ClassNotFoundException e) {
			System.out.println("Something went wrong during the query execution");
			e.printStackTrace();
			result = "query failed";
		}
		return result;
	}
	
	//FOR CHECKING IF THERE IS YET THE TABLE FOR A SPECIFIC SENSOR
	public String getQueryResultForSensor(String query, String whereParam){
		String result = "";
		try{
			getConnection();
			Statement stmt = (Statement) conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				String temp = rs.getString("SENSOR_NAME");
				if(temp.toLowerCase().equals(whereParam.toLowerCase())){
					result = rs.getString("SENSOR_NAME");
					System.out.println(rs.getString("SENSOR_NAME"));
					break;
				}
				System.out.println(rs.getString("SENSOR_NAME"));
			}
			stmt.close();
			closeConnection();
		}
		catch (SQLException | ClassNotFoundException e) {
			System.out.println("Something went wrong during the query execution");
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	//RETURN THE SENSOR LIST
	public JSONObject sensorList(){
		JSONArray list = new JSONArray();
		JSONObject sensorList = new JSONObject();
		//ACCESS TO THE DB
		try{
			getConnection();
			Statement stmt = (Statement) conn.createStatement();
			ResultSet rs = stmt.executeQuery(SENSOR_LIST_QUERY);
			while(rs.next()){
				JSONObject temp = new JSONObject();
				String sensorName = rs.getString("SENSOR_NAME");
				String latitude = rs.getString("LATITUDE");
				String longitude = rs.getString("LONGITUDE");
				String noiseLevel = rs.getString("NOISE_LEVEL");
				temp.put("sensorName", sensorName);
				temp.put("latitude", latitude);
				temp.put("longitude", longitude);
				temp.put("noiseLevel", noiseLevel);
				list.put(temp);
			}
			System.out.println(list);
			sensorList.put("sensors", list);
			stmt.close();
			closeConnection();
			
		}
		catch (SQLException | ClassNotFoundException e) {
			System.out.println("Something went wrong during the query execution");
			JSONObject error = new JSONObject();
			error.put("Error", "Something went wrong during the execution of the query");
			list.put(error);
			sensorList.put("Error", list);
			//e.printStackTrace();  //Uncomment to see stack trace for deeper debug
		}
		return sensorList;
	}
	
	//RETURN THE SENSOR'S VALUES
	public JSONObject sensorValues(String sensorName){
		JSONArray sensorValues = new JSONArray();
		JSONObject sensorList = new JSONObject();
		//ACCESS TO THE DB
		try{
			getConnection();
			Statement stmt = (Statement) conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM "+sensorName);
			while(rs.next()){
				/*(NOISE_LEVEL FLOAT NOT NULL, 
					DAY INTEGER NOT NULL, 
					MONTH INTEGER NOT NULL, 
					YEAR INTEGER NOT NULL, 
					DAYWEEK INTEGER NOT NULL, 
					HOUR INTEGER NOT NULL, 
					MINUTE INTEGER NOT NULL, 
					SECONDS INTEGER NOT NULL)
				*/
				JSONObject temp = new JSONObject();
				String noiseLevel = rs.getString("NOISE_LEVEL");
				String day = rs.getString("DAY");
				String month = rs.getString("MONTH");
				String year = rs.getString("YEAR");
				String dayweek = rs.getString("DAYWEEK");
				String hour = rs.getString("HOUR");
				String minute = rs.getString("MINUTE");
				String seconds = rs.getString("SECONDS");
				temp.put("noiseLevel", noiseLevel);
				temp.put("day", day);
				temp.put("month", month);
				temp.put("year", year);
				temp.put("dayweek", dayweek);
				temp.put("hour", hour);
				temp.put("minute", minute);
				temp.put("seconds", seconds);
				sensorValues.put(temp);
				}
			System.out.println(sensorValues);
			sensorList.put(sensorName, sensorValues);
			stmt.close();
			}catch (SQLException | ClassNotFoundException e) {
				System.out.println("Something went wrong during the query execution");
				JSONObject error = new JSONObject();
				error.put("Error", "Sensor not found or technical issue happened");
				sensorValues.put(error);
				sensorList.put("Error", sensorValues);
				//e.printStackTrace();  //Uncomment to see stack trace for deeper debug
			}
			closeConnection();
			return sensorList;
		}
	
	
	//RETURN THE AVG OF NOISE LEVEL FOR THE SELECTED DAY
	public JSONObject avgPerDay(String sensorName, int dayOfWeek){
		JSONArray sensorValues = new JSONArray();
		JSONObject avgValues = new JSONObject();
		//ACCESS TO THE DB
		try{
			getConnection();
			Statement stmt = (Statement) conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT AVG(NOISE_LEVEL) FROM "+sensorName+ " WHERE DAYWEEK = "+ dayOfWeek);
			String noiseAverage = "";
			while(rs.next()){
				if(rs.getString(1)!=null){
					if(noiseAverage.length()>3){
						noiseAverage = rs.getString(1).substring(0, 5);
						System.out.println(noiseAverage);
					}
					noiseAverage = rs.getString(1);
					System.out.println(noiseAverage);
				}
				else{
					noiseAverage = "0";
				}
			}
			avgValues.put("avgNoise", noiseAverage);
			stmt.close();
			}catch (SQLException | ClassNotFoundException e) {
				System.out.println("Something went wrong during the query execution");
				JSONObject error = new JSONObject();
				error.put("Error", "Sensor not found or technical issue happened");
				sensorValues.put(error);
				avgValues.put("Error", sensorValues);
				//e.printStackTrace();  //Uncomment to see stack trace for deeper debug
			}
			closeConnection();
			return avgValues;
		}
	
	
	//RETURN THE SENSOR'S STATS
	public JSONObject sensorStats(String sensorName){
		JSONObject stats = new JSONObject();
		stats.put("sensorName", sensorName);
		//ACCESS TO THE DB
		try{
			getConnection();
			Statement stmt = (Statement) conn.createStatement();
			//AVERAGE
			ResultSet rs = stmt.executeQuery("SELECT AVG(NOISE_LEVEL) AS AVERAGE FROM "+sensorName);
			
			String noiseAverage = "";
			while(rs.next()){
				 noiseAverage = rs.getString(1);
				 System.out.println(noiseAverage);
			}
			stats.put("noiseAverage", noiseAverage);
			//MAX
			rs = stmt.executeQuery("SELECT MAX(NOISE_LEVEL) AS MAX_NOISE FROM "+sensorName);
			String max = "";
			while(rs.next()){
				 max = rs.getString(1);
				 System.out.println(max);
			}
			stats.put("maxNoise", max);
			//MIN
			rs = stmt.executeQuery("SELECT MIN(NOISE_LEVEL) AS MIN_NOISE FROM "+sensorName);
			String min = "";
			while(rs.next()){
				 min = rs.getString(1);
				 System.out.println(min);
			}
			stats.put("minNoise", min);
			
			System.out.println(stats);
			stmt.close();
			}catch (SQLException | ClassNotFoundException e) {
				System.out.println("Something went wrong during the query execution");
				stats.put("Error", "Sensor not found or technical issue happened");
				e.printStackTrace();  //Uncomment to see stack trace for deeper debug
			}
			closeConnection();
			return stats;
		}
	
	//DELETE THE SENSOR TABLE AND THE SENSOR ENTRY IN THE SENSORLIST
	public JSONObject deleteSensor(String sensorName){
		JSONObject operationResult = new JSONObject();
		//ACCESS TO THE DB
		try{
			getConnection();
			Statement stmt = (Statement) conn.createStatement();
			//Veridy if the sensor exist
			ResultSet rs = stmt.executeQuery("SELECT * FROM "+SENSOR_TABLE + " WHERE SENSOR_NAME = '" + sensorName + "'");
			if(rs.first()){
				System.out.println("We get the right sensor");
				stmt.execute("DROP TABLE "+sensorName);
				stmt.execute("DELETE FROM "+SENSOR_TABLE+" WHERE SENSOR_NAME = "+"\'"+sensorName+"\' LIMIT 1");
				stmt.close();
				closeConnection();
				operationResult.put("Success", "The sensor was successufully deleted");
			}
			else{
				operationResult.put("Error", "Sensor not found or technical issue happened");
			}
			}catch (SQLException | ClassNotFoundException e) {
				System.out.println("Something went wrong during the query execution");
				operationResult.put("Error", "Sensor not found or technical issue happened");
				//e.printStackTrace();  //Uncomment to see stack trace for deeper debug
			}
			return operationResult;
		}
	
	
	//POST
	public JSONObject sensorPost(String body){
		JSONObject operationResult = new JSONObject();
		try{
			getConnection();
			JSONObject jsonObj = new JSONObject(body);
			System.out.println("sendNoiseLevel here");
			System.out.println("Received noise level : " + body);
			//PARSING JSON DATA HERE
			String noiseValue = jsonObj.getString("noiseValue");
			String sensorName = jsonObj.getString("sensorName");
			String latitude = jsonObj.getString("latitude");
			String longitude = jsonObj.getString("longitude");
			double db = Double.parseDouble(noiseValue);
			double noiseLevel = Double.parseDouble(noiseValue);
			System.out.println("Parsed noise's value : " + noiseLevel);
			System.out.println("Parsed coordinates : " + latitude + ", " + longitude);
			//ACQUIRING CURRENT DATE
        	Date date = new Date();
			LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			int year  = localDate.getYear();
			int month = localDate.getMonthValue();
			int day   = localDate.getDayOfMonth();
			Calendar c = Calendar.getInstance();
			c.set(year, month-1, day);
			int day_of_week = c.get(Calendar.DAY_OF_WEEK);
			String hours = Integer.toString(c.getTime().getHours());
			String minutes = Integer.toString(c.getTime().getMinutes());
			String seconds = Integer.toString(c.getTime().getSeconds());
        
        
			System.out.println(day+"/"+month+"/"+year+", "+day_of_week + "/" + hours + "-" + minutes +"-" + seconds);
			//ADD THE RESULT TO THE NOISE'S SENSOR TABLE
			//START TO USE THE DB
			//CHECK THE PRESENCE OF THE SENSOR IN THE SENSORS TABLE
			String rs = this.getQueryResultForSensor("SELECT * FROM "+ SENSOR_TABLE +" WHERE SENSOR_NAME = '" + sensorName + "' ", sensorName);
			String result = "";
			//IF EXIST, JUST ADD THE DATA
			if(!rs.equals("")){
				result = this.executeQuery("INSERT INTO "+ sensorName + " VALUES ("+ db +", " + day + ", " + month +", " + year +", " + day_of_week +", " + hours +", " + minutes +", " + seconds +")");
        		this.executeQuery("UPDATE "+SENSOR_TABLE + " SET NOISE_LEVEL = " + db + " WHERE SENSOR_NAME = '" + sensorName +"'");
        		System.out.println("Successfully added the values in "+sensorName+" and updated the sensors table");
			}
			//CREATE THE TABLE FOR THE SENSOR IF NOT EXISTS
			else{
				this.executeQuery("CREATE TABLE IF NOT EXISTS " + sensorName + " (NOISE_LEVEL FLOAT NOT NULL, DAY INTEGER NOT NULL, MONTH INTEGER NOT NULL, YEAR INTEGER NOT NULL, DAYWEEK INTEGER NOT NULL, HOUR INTEGER NOT NULL, MINUTE INTEGER NOT NULL, SECONDS INTEGER NOT NULL)");
				this.executeQuery("INSERT INTO " + SENSOR_TABLE + " VALUES ('" + sensorName +"','"+ latitude +"','"+ longitude +"', "+db+")");
				this.executeQuery("INSERT INTO "+ sensorName + " VALUES ("+ db +", " + day + ", " + month +", " + year +", " + day_of_week +", " + hours +", " + minutes +", " + seconds +")");
				this.executeQuery("UPDATE "+ SENSOR_TABLE + " SET NOISE_LEVEL = " + db + " WHERE SENSOR_NAME = '" + sensorName  +"'");
				System.out.println("Successfully created the table "+ sensorName + " and added the values in it. Updated the last sensor's value in the sensors table");
			}
			closeConnection();
		}catch(Exception E){
			System.out.println("Something went wrong during the query execution");
			operationResult.put("Error", "Sensor not found or technical issue happened");
			
		}
		return operationResult;
	}


}