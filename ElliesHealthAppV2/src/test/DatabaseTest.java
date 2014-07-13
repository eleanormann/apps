package test;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import database.MysqlAccess;

public class DatabaseTest {

	@Test
	public void testInsertOrUpdate(){
		Map<String, String> upsertMap = new HashMap<String, String>();
		upsertMap.put("username", "newUser");
		upsertMap.put("password", "newPassword");
		MysqlAccess test = new MysqlAccess();
		assertTrue(test.upsert(upsertMap, "users").equals("insert"));
		assertTrue(test.upsert(upsertMap, "users").equals("update"));
	}
	
}
