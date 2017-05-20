/*
 *	This project was realized for the Pervasive System class at La Sapienza - Università di Roma
 *	It is released with the Apache License 2.0
 *	Developed by Federico Boarelli, Alessio Tirabasso and Marco Nigro
 *	Rome, May 2017 
 *
 */

package database;

public class ConnectionMysql {
	
	private String username;
	private String password;
	private String url;
	
	public ConnectionMysql(String url, String username, String password){
		this.username = username;
		this.password = password;
		this.url = url;
	}
	
	public String getUsername(){
		return username;
	}
	
	public String getPassword(){
		return password;
	}
	
	public String getUrl(){
		return url;
	}
	
}
