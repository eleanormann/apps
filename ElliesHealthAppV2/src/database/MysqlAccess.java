package database;


import java.sql.Connection;
import java.sql.Timestamp;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

//TODO handle returns better - see what the internet says
public class MysqlAccess {
	private Connection connect = null;
	private Statement statement = null; //does prepared statement inherit statement? can I use both?
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	
	public ResultSet getResultSet(){
		if(resultSet==null){
			System.out.println("no results");
		}
		return resultSet;
	}
	
	
	public void connectToDatabase(String query) throws SQLException{
	    try{ 
		// this will load the MySQL driver, each DB has its own driver
	      Class.forName("com.mysql.jdbc.Driver");
	      // setup the connection with the DB.
	      connect = DriverManager.getConnection("jdbc:mysql://localhost/healthapp?" + "user=guest&password=guest");
	      // statements allow to issue SQL queries to the database
	     statement = connect.createStatement();
	      // resultSet gets the result of the SQL query
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    } catch(ClassNotFoundException e){
	    	e.printStackTrace();
	    }
	}
	
	public void simpleCUD(String query){
		try{
			connectToDatabase(query);
			int out = statement.executeUpdate(query);
			System.out.println(out);
		}catch(NullPointerException e){
			e.printStackTrace();
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			close();
		}
	}
	
	public void simpleRead(String query) throws Exception{
		try{
			 Class.forName("com.mysql.jdbc.Driver");
		      // setup the connection with the DB.
		      connect = DriverManager.getConnection("jdbc:mysql://localhost/healthapp?" + "user=guest&password=guest");
		      // statements allow to issue SQL queries to the database
		     statement = connect.createStatement();
			resultSet = statement.executeQuery(query);

			System.out.println(resultSet.getFetchSize());
		}catch (SQLException e) {
			e.printStackTrace();
		} finally{
			close();
		}
	}


	private void writeMetaData() throws SQLException {
	    // now get some metadata from the database
		  ResultSet  resultSet = statement.executeQuery("select * from healthapp.session");
		  System.out.println("The columns in the table are: ");
		  System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
		  for  (int i = 1; i<= resultSet.getMetaData().getColumnCount(); i++){
			  System.out.println("Column " +i  + " "+ resultSet.getMetaData().getColumnName(i));
		  }
	  }
	  
	  private void writeResultSet(ResultSet resultSet) throws SQLException {
	    // resultSet is initialised before the first data set
		  while (resultSet.next()) {
	      // it is possible to get the columns via name
	      // also possible to get the columns via the column number
	      // which starts at 1
	      // e.g., resultSet.getSTring(2);
	      String user = resultSet.getString("user");
	      String page = resultSet.getString("page");
	      String source = resultSet.getString("source");
	      Date date = resultSet.getDate("timestamp");
	      System.out.println("User: " + user);
	      System.out.println("page: " + page);
	      System.out.println("source: " + source);
	      System.out.println("Date: " + date);
	    }
	  }

	  // you need to close all three to make sure
	  public void close() {
	    close(resultSet);
	    close(statement);
	    close(connect);
	  }
	  
	  public void close(ResultSet c) throws UnsupportedOperationException {
		    try {
		      if (c != null) {
		        c.close();
		      }
		    } catch (Exception e) {
		    // don't throw now as it might leave following closables in undefined state
		    }
	  
	  } 
	  
	  private void close(Statement c) throws UnsupportedOperationException {
		    try {
		      if (c != null) {
		        c.close();
		      }
		    } catch (Exception e) {
		    // don't throw now as it might leave following closables in undefined state
		    }
	  
	  } 
	  private void close(Connection c) throws UnsupportedOperationException {
		    try {
		      if (c != null) {
		        c.close();
		      }
		    } catch (Exception e) {
		    // don't throw now as it might leave following closables in undefined state
		    }
	  
	  }



	public boolean insertRecord(List<String> data, String table) {
		String insertStatement = createInsertStatement(data, table);
		try{
			Class.forName("com.mysql.jdbc.Driver");
		    // setup the connection with the DB.
		    connect = DriverManager.getConnection("jdbc:mysql://localhost/healthapp?" + "user=guest&password=guest");
		    // preparedStatements can use variables and are more efficient
		    preparedStatement = connect.prepareStatement(insertStatement);
		    Date currentTime = new Date();
		  	preparedStatement.setTimestamp(1, new Timestamp(currentTime.getTime()));
		    preparedStatement.executeUpdate();
		    return true;
		
		}catch(ClassNotFoundException e){
			System.out.println("couldnt find db class");
			e.printStackTrace();
			
		} catch (SQLException e) {
			System.out.println("sql failed");
			e.printStackTrace();
		}
		return false;
	}


	
	public boolean updateRecord(){
		//TODO get record id and then do the above
		return false;
	}

	public String createInsertStatement(List<String> data, String table) {
		String insertStatement = "insert into healthapp." + table + " values (default, ";
		for(String value : data){
			if(value.equals("?")){
				insertStatement += value + ", ";
			}else{
				insertStatement += "'" + value + "', ";
			}	
		}
		insertStatement = insertStatement.substring(0, insertStatement.length()-2) + ")";
		return insertStatement;
	}



	public ResultSet readRecord(String[] columns, String table) {
		String query = createSelectStatement(columns);
		
		try {
		// this will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
		    connect = DriverManager.getConnection("jdbc:mysql://localhost/healthapp?" + "user=guest&password=guest");
		    statement = connect.createStatement();
		    ResultSet results =  statement.executeQuery(query + " from " + table);
		    return results;
		    
	    } catch (Exception e) {
		   System.out.println("database error");
		   close();
		} 
		return null;
	}

	public String createWhereStatement(Map<String,String> lookupMap){
		String whereStatement = " where ";
		for (String key : lookupMap.keySet()){
			if(!"default".equals(lookupMap.get(key)) && !"?".equals(lookupMap.get(key))){
				whereStatement += key + "=" + lookupMap.get(key) + " and ";
			}
		}
		return whereStatement.substring(0, whereStatement.length()-5);
	}
	
	public ResultSet simpleLookup(Map<String, String> lookupMap, String table){
		String query = "select id from " + table + createWhereStatement(lookupMap);
		try {
			// this will load the MySQL driver, each DB has its own driver
				Class.forName("com.mysql.jdbc.Driver");
			    connect = DriverManager.getConnection("jdbc:mysql://localhost/healthapp?" + "user=guest&password=guest");
			    statement = connect.createStatement();
			    ResultSet results =  statement.executeQuery(query);
			    return results;
			    
		    } catch (Exception e) {
			   e.printStackTrace();
		    	System.out.println("database error");
			   close();
			} 
		return null;
	}



	public String createSelectStatement(String[] variables) {
		String query = "select ";
		for (String variable : variables){
			query += variable + ", ";
		}
		query = query.substring(0, query.length()-5);
		System.out.println(query);
		return query;
	}



	public String upsert(Map<String, String> upsertMap, String table) {
		String upsert = null;
		String upsertStatement = null;
		try {
			ResultSet lookupId = simpleLookup(upsertMap, table);
			if(resultSet.last()){
				 upsertStatement = createUpdateStatement(upsertMap, table);
				 //TODO replace with log entry
				 upsert = "update";
			} else{
				List<String> columns = new ArrayList<String>();
				columns.addAll(upsertMap.keySet());
				upsertStatement = createInsertStatement(columns, table);
				//TODO replace with log entry
				upsert = "insert";
			}
			
		    preparedStatement = connect.prepareStatement(upsertStatement);
		    Date currentTime = new Date();
		  	preparedStatement.setTimestamp(1, new Timestamp(currentTime.getTime()));
		    int rows = preparedStatement.executeUpdate();
		    System.out.println(rows);
		    return upsert;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return upsert;
	}



	private String createUpdateStatement(Map<String, String> upsertMap,	String table) {
		String updateStatement =  "update " + table + " set ";
		for(String key : upsertMap.keySet()){
			updateStatement += key + "=" + upsertMap.get(key) + ", ";
		}
		return updateStatement.substring(0, updateStatement.lastIndexOf(',')) + " where id=" + upsertMap.get("id");
	} 
}
