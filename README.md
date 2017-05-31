## NoiseApp <br />
An accademic project developed in for the Pervasive System class a Univerity of Rome - La Sapienza, Master Degree in Computer Engineering. The goal of the project is to have a noise map of the city, in order to achieve better booking for tourist based on their preferences: for example, young tourist may like to be near a zone with high nightlife activities(so with a higher noise of level) and viceversa a family maybe want to avoid such places. <br />

## Overview <br />

This repository is dedicated to the Server component of our project. <br />
We are developing it using Tomcat as servlet container and mySql as DMBS, this will allow us to deploy it directly on Microsoft Azure cloud platform, in order to make it worldwide available. This is server is built over the REST paradigm, using the Jersey library and the JAX-RS framework. <br />
We deploy this Server on Eclipse, using Tomcat(port 8080) as servlet container. The website is accessible at http://yourhost.com/NoiseAppServer <br />
For the REST rpc, the base path is NoiseAppServer/service/sound <br />

## Function <br />
We have two main class here:
- SoundLevel
- ConnectionManager <br />

In the SoundLevel class(which we explain in the next section) we have all the REST logic. <br />
In the ConnectionManager class, we have all the JDBC logic that interact with the database. <br />

## SoundLevel Class:

### Set your mySql credentials:
You will find this line in class SoundLevel and you need to put here your mySql credentials
> //Set up your connection here <br />
> ConnectionMysql conn = new ConnectionMysql("jdbc:mysql://localhost:3306/","USERNAME","PASSWORD"); <br />

### HelloSound:
This function help to check is the Server is online and working <br />

> @Path("/hellosound") <br />
>	@GET  <br />
>	@Produces("text/plain") <br />

### GetSensorList:
This function return a json containing the sensors list stored in the db <br />
This is used to populate the map with the static sensor <br />

> @Path("/getSensorList") <br />
>	@GET  <br />
>	@Produces("MediaType.APPLICATION_JSON") <br />

### GetUserDataList:
This function return the list with the user rilevations <br />
This is used to populate the map in the client side <br />

> @Path("/getUserDataList") <br />
>	@Produces(MediaType.APPLICATION_JSON) <br />
>	@GET <br />

### GetSensorValues:
This function return all the data collected by a sensor: <br />
The sensorName is passed as a query parameter <br />
This is used to choose how to draw the graphical component in the client <br />

> @Path("/getSensorValues") <br />
>	@Produces(MediaType.APPLICATION_JSON) <br />
>	@GET <br />

### GetAvgValues:
This function calculate some stats on the specified sensor <br />
The sensorName is passed as a parameter and we have to specify a day too <br />
The result is a json with all the rilevations taken by the sensor in the choosen day <br />
This is used on the client side to draw the chart pie

>	@Path("/getAvgValues") <br />
>	@Produces(MediaType.APPLICATION_JSON) <br />
>	@GET <br />

### GetSensorStats:
This function calculate some logic(Average of noise, Max, Min and last rilevaion) <br />
This data are printed when the client click on a static sensor in the map <br />

>	@Path("/getSensorStats") <br />
>	@Produces(MediaType.APPLICATION_JSON) <br />
>	@GET <br />

### SendNoiseLevel:
This function is used by the sensor to post their own data <br />
The parameter "String noise" contain a json with all the value of the sensor (sensorName, latitude, longitude, noiseLevel)<br />
It return the body in case of success <br />

>	@Path("/sendNoiseLevel") <br />
>	@Consumes(MediaType.APPLICATION_JSON) <br />
>	@POST <br />

Note: the sensorName is NOT unique, so create any sensorName different from other one, otherwise tey will overwrite their data

### UserNoiseLevel:
This function is used by the user to post their own data <br />
The parameter "String noise" contain a json with all the value of the sensor (userName, latitude, longitude, noiseLevel, noiseType) <br />
It return the body in case of success <br />

>	@Path("/userNoiseLevel") <br />
>	@Consumes(MediaType.APPLICATION_JSON) <br />
>	@POST <br />

### DeleteSensor:
This function is used to delete one sensor from the sensorlist table <br />
Useful in the deployment stage, in order to avoid the schema drop each time <br />

>	@Path("/deleteSensor") <br />
>	@Produces(MediaType.APPLICATION_JSON) <br />
>	@DELETE <br />


## Links <br />
Project presentation : https://github.com/projectNoiseRome/projectNoiseRome <br />
Slides initial concept: https://www.slideshare.net/MarcoNigro6/noise-app<br />
MVP: https://www.slideshare.net/MarcoNigro6/noise-app-mvp<br />
Final Presentation: https://www.slideshare.net/MarcoNigro6/noiseapp-final-presentation<br/>
Android rep: https://github.com/projectNoiseRome/NoiseApp<br />


## Developed by:<br />
Marco Nigro       : https://www.linkedin.com/in/marco-nigro-283024140/<br />
Alessio Tirabasso : https://www.linkedin.com/in/alessio-tirabasso-44a023140/<br />
Federico Boarelli : https://www.linkedin.com/in/federico-boarelli-a4885311b/<br />
