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
	ConnectionMysql conn = new ConnectionMysql("jdbc:mysql://localhost:3306/","PUT YOUR USERNAME HERE","PUT YOUR PASSWORD HERE");
		
	@Path("/hellosound")
	@GET
	@Produces("text/plain")
	public Response hello(){
		System.out.println("Some one arrives here");
		return Response.ok().entity("Sound path say hello!").build();
	}
	
	
	//Return the sensors table
	@Path("/getSensorList")
	@Produces(MediaType.APPLICATION_JSON)
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
	    	return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON).entity(sensorList.toString()).build();
	    }
		return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(sensorList.toString()).build();
	}
	
	@Path("/getUserDataList")
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public Response getUserDataList(){
		//OPEN CONNECTION WITH THE DB
        ConnectionManager cm;
        JSONObject sensorList = new JSONObject();
		try {
			cm = new ConnectionManager(conn);
			 sensorList = cm.userDataList();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    if(sensorList.keySet().contains("Error")){
	    	return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON).entity(sensorList.toString()).build();
	    }
		return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(sensorList.toString()).build();
	}
	
	//Return the sensor's table, to get the values
	@Path("/getSensorValues")
	@Produces(MediaType.APPLICATION_JSON)
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
	    	return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON).entity(sensorValues.toString()).build();
	    	
	    }
		return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(sensorValues.toString()).build();
	}
	
	//Return the media of noise_level for the day passed as parameter
	@Path("/getAvgValues")
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public Response getAvgDayValues(@QueryParam("sensorName") String sensorName, @QueryParam("day") int dayOfWeek){
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
		    return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON).entity(avgValues.toString()).build();
		    	
		 	}
		return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(avgValues.toString()).build();
	}
	
	//Return the sensor's table, to get the Stats(when from client the user click on a pin)
	@Path("/getSensorStats")
	@Produces(MediaType.APPLICATION_JSON)
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
	    	return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON).entity(sensorStats.toString()).build();
	    	
	    }
		return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(sensorStats.toString()).build();
	}
	
	
	//Receive the data, insert it in the right sensor's table and add the data
	@Path("/sendNoiseLevel")
	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	public Response postSoundLevel(String noise) throws ClassNotFoundException, SQLException{
		//OPENING CONNECTION WITH THE DB
		ConnectionManager cm;
	    JSONObject sensorPost = new JSONObject();
	    try {
			cm = new ConnectionManager(conn);
			sensorPost = cm.sensorPost(noise);
		}catch(ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    if(sensorPost.keySet().contains("Error")){
	    	return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON).entity(sensorPost.toString()).build();
	    	
	    }
        return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity("Result of the the request : "+ sensorPost.toString()).build();
	}
	
	//Receive the data, insert it in the right sensor's table and add the data
	@Path("/userNoiseLevel")
	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	public Response userData(String noise) throws ClassNotFoundException, SQLException{
		//OPENING CONNECTION WITH THE DB
		ConnectionManager cm;
	    JSONObject sensorPost = new JSONObject();
	    try {
			cm = new ConnectionManager(conn);
			sensorPost = cm.userPost(noise);
		}catch(ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    if(sensorPost.keySet().contains("Error")){
	    	return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON).entity(sensorPost.toString()).build();
	    	
	    }
	       return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity("Result of the the request : "+ sensorPost.toString()).build();
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
	    	return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON).entity(operationResult.toString()).build();
	    }
		return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(operationResult.toString()).build();
		}

}
