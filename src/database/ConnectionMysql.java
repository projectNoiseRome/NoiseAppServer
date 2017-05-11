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
