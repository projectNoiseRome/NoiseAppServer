package service;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONArray;
import org.json.JSONObject;

import database.ConnectionManager;
import database.ConnectionMysql;

@Path("/sound")
public class SoundLevel {
	
	private double noiseLevel = 0;
	private static final String SENSOR_TABLE = "sensorslist";
	private static final String ERROR = "Error";
	//SET UP HERE YOUR CONNECTION
    	ConnectionMysql conn = new ConnectionMysql("jdbc:mysql://localhost:3306/","PUT HERE YOUR MYSQL USER NAME","PUT HERE YOUR PASSWORD");
    	
	@Path("/hellosound")
	@GET
	@Produces("text/plain")
	public Response hello(){
		System.out.println("Some one arrives here");
		return Response.ok().entity("Sound path say hello!").build();
	}
	
	
	//Return the sensors table
	@Path("/getSensorList")
	@Produces("text/plain")
	@GET
	public Response getSensorList(){
		//OPEN CONNECTION WITH THE DB
        ConnectionManager cm;
        JSONObject sensorList = new JSONObject();
		try {
			cm = new ConnectionManager(conn);
			 sensorList = cm.sensorList();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    if(sensorList.keySet().contains("Error")){
	    	return Response.status(Status.INTERNAL_SERVER_ERROR).type("text/plain").entity(sensorList.toString()).build();
	    }
		return Response.status(Status.OK).type("text/plain").entity(sensorList.toString()).build();
	}
	
	//Return the sensor's table, to get the values
	@Path("/getSensorValues")
	@Produces("text/plain")
	@GET
	public Response getSensorValues(@QueryParam("sensorName") String sensorName){
		//OPEN CONNECTION WITH THE DB
	    ConnectionManager cm;
	    JSONObject sensorValues = new JSONObject();
	    try {
			cm = new ConnectionManager(conn);
			sensorValues = cm.sensorValues(sensorName);
		}catch(ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    if(sensorValues.keySet().contains("Error")){
	    	return Response.status(Status.INTERNAL_SERVER_ERROR).type("text/plain").entity(sensorValues.toString()).build();
	    	
	    }
		return Response.status(Status.OK).type("text/plain").entity(sensorValues.toString()).build();
	}
	
	//Return the media of noise_level for the day passed as parameter
	@Path("/getAvgValues")
	@Produces("text/plain")
	@GET
	public Response getAvgValues(@QueryParam("sensorName") String sensorName, @QueryParam("day") int dayOfWeek){
		//OPEN CONNECTION WITH THE DB
	    ConnectionManager cm;
	    JSONObject avgValues = new JSONObject();
	    try {
			cm = new ConnectionManager(conn);
			avgValues = cm.avgPerDay(sensorName, dayOfWeek);
			}
	    catch(ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		if(avgValues.keySet().contains("Error")){
		    return Response.status(Status.INTERNAL_SERVER_ERROR).type("text/plain").entity(avgValues.toString()).build();
		    	
		 	}
		return Response.status(Status.OK).type("text/plain").entity(avgValues.toString()).build();
	}
	
	//Return the sensor's table, to get the Stats
	@Path("/getSensorStats")
	@Produces("text/plain")
	@GET
	public Response getSensorStats(@QueryParam("sensorName") String sensorName){
		//OPEN CONNECTION WITH THE DB
	    ConnectionManager cm;
	    JSONObject sensorStats = new JSONObject();
	    try {
			cm = new ConnectionManager(conn);
			sensorStats = cm.sensorStats(sensorName);
		}catch(ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    if(sensorStats.keySet().contains("Error")){
	    	return Response.status(Status.INTERNAL_SERVER_ERROR).type("text/plain").entity(sensorStats.toString()).build();
	    	
	    }
		return Response.status(Status.OK).type("text/plain").entity(sensorStats.toString()).build();
	}
	
	
	//Receive the data, insert it in the right sensor's table and add the data
	@Path("/sendNoiseLevel")
	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	public Response postSoundLevel(String noise) throws ClassNotFoundException, SQLException{
		System.out.println("sendNoiseLevel here");
		System.out.println("Received noise level : " + noise);
		//PARSING JSON DATA HERE
		JSONObject jsonObj = new JSONObject(noise);
        String noiseValue = jsonObj.getString("noiseValue");
        String sensorName = jsonObj.getString("sensorName");
        String latitude = jsonObj.getString("latitude");
        String longitude = jsonObj.getString("longitude");
        double db = Double.parseDouble(noiseValue);
        noiseLevel = Double.parseDouble(noiseValue);
        System.out.println("Parsed noise's value : " + noiseLevel);
        System.out.println("Parsed coordinates : " + latitude + ", " + longitude);
        //ACQUIRING CURRENT DATE
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date dateF = new Date();
        System.out.println("Request's happend on : "+dateFormat.format(dateF));
        
        //NEW VERSION
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
        ConnectionManager cm = new ConnectionManager(conn);
        //START TO USE THE DB
        //CHECK THE PRESENCE OF THE SENSOR IN THE SENSORS TABLE
        String rs = cm.getQueryResultForSensor("SELECT * FROM "+ SENSOR_TABLE +" WHERE SENSOR_NAME = '" + sensorName + "' ", sensorName);
        String result = "";
        //IF EXIST, JUST ADD THE DATA
        if(!rs.equals("")){
        	result = cm.executeQuery("INSERT INTO "+ sensorName + " VALUES ("+ db +", " + day + ", " + month +", " + year +", " + day_of_week +", " + hours +", " + minutes +", " + seconds +")");
        	cm.executeQuery("UPDATE "+SENSOR_TABLE + " SET NOISE_LEVEL = " + db + " WHERE SENSOR_NAME = '" + sensorName +"'");
        	System.out.println("Successfully added the values in "+sensorName+" and updated the sensors table");
        }
        //CREATE THE TABLE FOR THE SENSOR IF NOT EXISTS
        else{
        	cm.executeQuery("CREATE TABLE IF NOT EXISTS " + sensorName + " (NOISE_LEVEL FLOAT NOT NULL, DAY INTEGER NOT NULL, MONTH INTEGER NOT NULL, YEAR INTEGER NOT NULL, DAYWEEK INTEGER NOT NULL, HOUR INTEGER NOT NULL, MINUTE INTEGER NOT NULL, SECONDS INTEGER NOT NULL)");
            cm.executeQuery("INSERT INTO " + SENSOR_TABLE + " VALUES ('" + sensorName +"','"+ latitude +"','"+ longitude +"', "+db+")");
        	cm.executeQuery("INSERT INTO "+ sensorName + " VALUES ("+ db +", " + day + ", " + month +", " + year +", " + day_of_week +", " + hours +", " + minutes +", " + seconds +")");
        	cm.executeQuery("UPDATE "+ SENSOR_TABLE + " SET NOISE_LEVEL = " + db + " WHERE SENSOR_NAME = '" + sensorName  +"'");
        	System.out.println("Successfully created the table "+ sensorName + " and added the values in it. Updated the last sensor's value in the sensors table");
        }
        return Response.status(Status.OK).type("text/plain").entity("Result of the the request : "+result).build();
	}
	
		//Delete the sensor table and the sensor entry int the sensorlist
		@Path("/deleteSensor")
		@Produces(MediaType.APPLICATION_JSON)
		@DELETE
		public Response deleteSensor(@QueryParam("sensorName") String sensorName){
			//OPEN CONNECTION WITH THE DB
	        ConnectionManager cm;
	        JSONObject operationResult = new JSONObject();
			try {
				cm = new ConnectionManager(conn);
				 operationResult = cm.deleteSensor(sensorName);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    if(operationResult.keySet().contains("Error")){
		    	return Response.status(Status.INTERNAL_SERVER_ERROR).type("text/plain").entity(operationResult.toString()).build();
		    }
			return Response.status(Status.OK).type("text/plain").entity(operationResult.toString()).build();
		}

}
