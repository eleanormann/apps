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


public class MysqlAccess {
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	public void readDataBaseEntries(String table) throws Exception {
		try {
	      // this will load the MySQL driver, each DB has its own driver
	      Class.forName("com.mysql.jdbc.Driver");
	      // setup the connection with the DB.
	      connect = DriverManager.getConnection("jdbc:mysql://localhost/healthapp?" + "user=guest&password=guest");

	      // statements allow to issue SQL queries to the database
	      statement = connect.createStatement();
	      // resultSet gets the result of the SQL query
	      resultSet = statement.executeQuery("select * from healthapp." + table);
	      writeResultSet(resultSet);

	     
	  
	    } catch (Exception e) {
	      throw e;
	    } finally {
	      close();
	    }

	  }
	
	
	
	public void insertLog(String[] data) throws SQLException{
		try {
		      // this will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
		    // setup the connection with the DB.
		    connect = DriverManager.getConnection("jdbc:mysql://localhost/healthapp?" + "user=guest&password=guest");
		    Date currentTime = new Date();
		    // preparedStatements can use variables and are more efficient
		    preparedStatement = connect.prepareStatement("insert into healthapp.session values (default, ?, ?, ?, ?)");
		  
		    // user, timestamp, page, source;
		    // parameters start with 1
		    preparedStatement.setString(1, data[0]);
		    preparedStatement.setTimestamp(2, new Timestamp(currentTime.getTime())); //sort out date so its the actual time accessed site not the time now
		    preparedStatement.setString(3, data[1]);
		    preparedStatement.setString(4, data[2]);
		    preparedStatement.executeUpdate();
		} catch (Exception e) {
		      e.printStackTrace();;
		} finally {
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




	public String createSelectStatement(String[] variables) {
		String query = "select ";
		for (String variable : variables){
			query += variable + ", ";
		}
		query = query.substring(0, query.length()-2);
		System.out.println(query);
		return query;
	}



	public String upsert(Map<String, String> upsertMap, String string) {
		//TODO check whether record exists
		return "not yet implemented";
	} 
}
