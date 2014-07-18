package test;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import database.MysqlAccess;

public class DatabaseTest {
	
	@Test
	public void testInsertLoginDetails(){
		MysqlAccess dbtest = new MysqlAccess();
		List<String> values = new ArrayList<String>();
		values.add("testUsername");
		values.add("testPassword");
		values.add("?");
		values.add("Y");
		assertTrue("insert failed", dbtest.insertRecord(values, "users"));
	}
	
	@Test
	public void testInsertOrUpdate(){
		Map<String, String> upsertMap = new HashMap<String, String>();
		upsertMap.put("username", "'newUser'");
		upsertMap.put("password", "'newPassword'");
		upsertMap.put("updated_date", "?");
		upsertMap.put("active", "'Y'");
		MysqlAccess test = new MysqlAccess();
		String actual = test.upsert(upsertMap, "users");
		String actual2 = test.upsert(upsertMap, "users");
		
		assertTrue("insert didn't happen as expected, method returned " + actual, actual.equals("insert"));
		assertTrue("update didn't happen as expected, method returned " + actual2, actual2.equals("update"));
	}
	
	
	@Test
	public void testCreateWhereStatement(){
		Map<String, String> whereStatementEntries = new HashMap<String, String>();
		whereStatementEntries.put("username", "'testUsername'");
		whereStatementEntries.put("password", "'testPassword'");
		whereStatementEntries.put("updated_date", "?");
		whereStatementEntries.put("active", "'Y'");
		MysqlAccess test = new MysqlAccess();
		String whereStatement = test.createWhereStatement(whereStatementEntries);
		assertTrue("got this statement: " + whereStatement, whereStatement.contains("username='testUsername'")
				&& whereStatement.contains("password='testPassword'") && whereStatement.contains("active='Y'") 
				&& whereStatement.startsWith(" where ") && whereStatement.endsWith("'"));
	}
	
	@Test
	public void testSimpleLookup(){
		Map<String, String> lookupMap = new HashMap<String, String>();
		lookupMap.put("id", "27");
		lookupMap.put("username", "'newUsername'");
		lookupMap.put("password", "'newPassword'");
		lookupMap.put("updated_date", "?");
		lookupMap.put("active", "'Y'");
		MysqlAccess test = new MysqlAccess();
		List<String> values = new ArrayList<String>(); 
		values.addAll(lookupMap.values());
		test.insertRecord(values, "users");
		assertTrue("27".equals(test.simpleLookup(lookupMap, "users")));
		
	}
	
	@Test
	public void testSimpleCrud() throws Exception{
		MysqlAccess test = new MysqlAccess();
		test.simpleCUD("insert into healthapp.session values ('33', 'testUser', '2014-03-30', 'test', 'user')");
	}
	
	@Test
	public void testSimpleRead() throws Exception{
		MysqlAccess test = new MysqlAccess();
		test.simpleRead("select * from healthapp.session");
	}
	
	@Test
	public void testGetResultSet(){
		Map<String, String> lookupMap = new HashMap<String, String>();
		lookupMap.put("id", "14");
		lookupMap.put("username", "'testUsername'");
		lookupMap.put("password", "'testPassword'");
		lookupMap.put("updated_date", "?");
		lookupMap.put("active", "'Y'");
		MysqlAccess test = new MysqlAccess();
		List<String> values = new ArrayList<String>(); 
		values.addAll(lookupMap.values());
		test.simpleLookup(lookupMap, "users");
		assertNotNull(test.getResultSet());
	}
}

