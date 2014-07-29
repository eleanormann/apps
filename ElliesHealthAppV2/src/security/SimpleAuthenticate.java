package security;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import database.MysqlAccess;

public class SimpleAuthenticate {
	boolean token = false;
	
	public SimpleAuthenticate(){
	}
	
	public SimpleAuthenticate(String loggedIn) {
		if("mockValidLogin".equals(loggedIn)){
			token = true;
		}
	}

	public boolean getToken() {	
		return token;
	}
	

	public String getAccessToUrl(String url) {
		if(token==true){
			return url;
		}
		return "mockLoginFailedURL";
	}

	public void authenticateLogin(String username, String password) {
		if(token==true){
			//TODO handle request when someone is logged in; e.g. prompt to log out 
			logOut();
		}
		MysqlAccess readUsers = new MysqlAccess();
		Map<String, String> readMap = new HashMap<String, String>();
		readMap.put("username", username);
		readMap.put("password", password);
		String query = readUsers.createSelectStatement(readMap) + " from users "  
				+ readUsers.createWhereStatement(readMap);
		String results =  readUsers.simpleRead(query);
		if(results.contains("username=" + username + " password=" + password)){
			token=true;
		}
		
	}

	//TODO handle request when someone is logged in; e.g. prompt to log out 
	//TODO: handle confirmation - should the user be logged in automatically?
	public void createUser(String username, String password){
		if(token==true){
			logOut();
		}
		MysqlAccess create = new MysqlAccess();
		Map<String, String> userMap = new HashMap<String, String>();
		userMap.put("username", username);
		userMap.put("password", password);
		create.simpleCUD(create.createInsertStatement(userMap, "users"));
	}
	
	public void logOut() {
		token = false;
	}


}
