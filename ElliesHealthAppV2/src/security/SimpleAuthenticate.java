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
		String[] input = {"username","password"};
		ResultSet results =  readUsers.readRecord(input, "users");
		//TODO check results contains username/password combo
		try {
			while(results.next()){
				if(results.getString("username").equals(username) && results.getString("password").equals(password)){
					token = true;
					break;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			readUsers.close();
		}
		
	}

	public void logOut() {
		token = false;
	}


}
