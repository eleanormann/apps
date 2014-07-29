package database;


import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Map;


//TODO handle returns better - see what the internet says
public class MysqlAccess {
	private Connection connect = null;
	private Statement statement = null; //does prepared statement inherit statement? can I use both?
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	
	/**Connects to the healthapp mysql database using the java.sql methods.
	 * This simple connect does not make use of the PreparedStatement class that provides greater
	 * flexibility in creating the query. 
	 * NB: Connection and Statement MUST BE CLOSED via another method
	 * TODO: generify
	 * TODO: enable preparedStatement
	 * TODO: explore java.sql methods for other useful stuff
	 */
	public void connectToDatabase(){
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
	
	/**A simple create/update/delete method that executes a valid sql query
	 * Currently does not handle failed inserts due to existing record.
	 * TODO: decide whether to automatically update existing records when insert fails 
	 * 	or whether to create a separate simpleUpsert method
	 * @param query
	 */
	public void simpleCUD(String query){
		try{
			connectToDatabase();
			statement.executeUpdate(query);
		}catch(NullPointerException e){
			e.printStackTrace();
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			close();
		}
	}
	
	/**A simple read method that executes a valid sql query and parses the results into a single string
	 *  formated key=value for each row, with new line between rows
	 * TODO: decide whether to refactor to produce a csv file
	 * @param query
	 * @return
	 */
	public String simpleRead(String query){
		String results = "";
		try{
			connectToDatabase();
			resultSet = statement.executeQuery(query);
			results = writeResultSet(resultSet, query);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			close();
		}
		return results;
	}

	/** This method creates a string of the ResultSet, using table metadata to identify the data type to get. 
	 * This impl is far from ideal, as the if list potentially could get rather long.  
	 * It was most convenient to parse the resultSet into a string because the open connection was tricky to handle.
	 */
	private String writeResultSet(ResultSet resultSet, String query) throws SecurityException, NoSuchMethodException, Exception {
		  String results = "";
		  ResultSetMetaData tableInfo =  resultSet.getMetaData();
		  // resultSet is initialised before the first data set
		  while (resultSet.next()) {
		      // column number start at 1
			  for (int i = 1; i<= resultSet.getMetaData().getColumnCount(); i++){
				  results += tableInfo.getColumnName(i)  + "=";
				  int colType = tableInfo.getColumnType(i);
				  if(colType==4){ 
					  results += resultSet.getInt(i) + " "; 
				  } else if(colType==1 || colType==12) { 
					  results += resultSet.getString(i) + " "; 
				  } else if(colType==91) { 
					  results += resultSet.getDate(i) + " ";
				  }
			  }
			  results += "\n";
		  }
		  
		  return results;
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

	  /**Three methods to create valid queries and to call the execute methods
	   * @return true if successful and false if not
	   */
	  public boolean updateRecord(Map<String, String> updateMap, String table){
			//TODO create statement then get record id then update
			return false;
		}
		
		public boolean insertRecord(Map<String, String> insertMap, String table){
			return false;
		}
		
		public boolean deleteRecord(Map<String, String> deleteMap, String table){
			return false;
		}
		
	public String readRecord(Map<String,String> selectMap, Map<String, String> whereMap, String table){
		String query = createSelectStatement(selectMap) + " from " + table;
		if(null!=whereMap){
			query += " " + createWhereStatement(whereMap);
		}
		return query;
	}

	//TODO ensure that the values are in the correct order
	public String createInsertStatement(Map<String, String> data, String table) {
		String insertStatement = "insert into healthapp." + table + " values (";
		try{
			connectToDatabase();
			resultSet = statement.executeQuery("select * from " + table);
			ResultSetMetaData tableInfo = resultSet.getMetaData();
			for(int i = 1; i<=tableInfo.getColumnCount(); i++){
				String value = data.get(tableInfo.getColumnName(i));
				if(null==value){
					insertStatement += "default, ";
				}else if(value.equals("?")){
					insertStatement += value + ", ";
				}else{
					insertStatement += "'" + value + "', ";
				}	
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			close();
		}
		insertStatement = insertStatement.substring(0, insertStatement.length()-2) + ")";
		return insertStatement;
	}

	private String createUpdateStatement(Map<String, String> upsertMap,	String table) {
		String updateStatement =  "update " + table + " set ";
		for(String key : upsertMap.keySet()){
			if(upsertMap.get(key).equals("?")){
				updateStatement += key + "=" + upsertMap.get(key) + ", ";
			}else{
				updateStatement += key + "='" + upsertMap.get(key) + "', ";
			}	
		}
		return updateStatement.substring(0, updateStatement.lastIndexOf(',')) + " where id=" + upsertMap.get("id");
	} 
	
	public String createSelectStatement(Map<String, String> selectMap) {
		String query = "select ";
		for (String key : selectMap.keySet()){
			query += key + ", ";
		}
		query = query.substring(0, query.length()-2);
		return query;
	}
	
	public String createWhereStatement(Map<String,String> lookupMap){
		String whereStatement = " where ";
		for (String key : lookupMap.keySet()){
			if(!"default".equals(lookupMap.get(key)) && !"?".equals(lookupMap.get(key))){
				whereStatement += key + "='" + lookupMap.get(key) + "' and ";
			}
		}
		return whereStatement.substring(0, whereStatement.length()-5);
	}

	


//insert will fail if the order of columns is not correct TODO fix that in createInsertStatement
	public String upsert(Map<String, String> upsertMap, String table) {
		String upsert = null;
		String upsertStatement = null;
		try {
			String lookupId = simpleRead("select id from " + table + createWhereStatement(upsertMap));
			if(!lookupId.isEmpty()){
				 upsertStatement = createUpdateStatement(upsertMap, table);
				 //TODO replace with log entry
				 upsert = "update";
			} else{
				upsertStatement = createInsertStatement(upsertMap, table);
				//TODO replace with log entry
				upsert = "insert";
			}
			connectToDatabase();
		    preparedStatement = connect.prepareStatement(upsertStatement);
		    Date currentTime = new Date();
		  	preparedStatement.setTimestamp(1, new Timestamp(currentTime.getTime()));
		    int rows = preparedStatement.executeUpdate();
		    System.out.println(rows);
		    return upsert;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			close();
		}
		return upsert;
	}



	
}
