package service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.json.*;

@Path("/operation")
public class Temperature {
	
	private static double lastTemperature = 0;
	
	@Path("/helloworld")
	@GET
	public Response helloWorld(){
		return Response.status(Status.OK).type("text/plain").entity("Hello Azure!").build();
	}
	
	@Path("/recTemperature")
	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	public Response newTemperature(String temperature){
		System.out.println("Coming here");
		System.out.println("Received temperature : " + temperature);
		JSONObject jsonObj = new JSONObject(temperature);
        String temperatureValue = jsonObj.getString("degreesValue");
        double degrees = Double.parseDouble(temperatureValue);
        lastTemperature = Double.parseDouble(temperatureValue);
        System.out.println("Parsed temperature's value : " + lastTemperature);
		return Response.status(Status.OK).type("text/plain").entity("Temperature received from the server: " + degrees).build();
	}
	
	@Path("/sendTemperature")
	@Produces("text/plain")
	@GET
	public Response sendTemperature(){
		System.out.println("Sending temperature : " + lastTemperature);
		return Response.status(Status.OK).type("text/plain").entity("Temperature calculated : "+Double.toString(lastTemperature)).build();
	}

}
