package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import database.MysqlAccess;
import security.Authenticate;
import security.SimpleAuthenticate;

public class LoginTest {
	static Authenticate auth;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		auth = new Authenticate();
	}
	

	@Test
	public void testAuthenticate() {
		assertNotNull(auth);
	}

	
	@Test
	public void testFormIO(){
		//check that html form passes info to a authentication class
		fail("not yet implemented");
	}
	
	@Test
	public void testCheckInsertOrUpdate(){
		fail("not yet implemented");
	}
	
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
	public void testCreateSelectStatement(){
		MysqlAccess dbtest = new MysqlAccess();
		String[] testData = {"username", "password"};
		assertTrue("unexpected string returned: " + dbtest.createSelectStatement(testData),  
			"select username, password".equals(dbtest.createSelectStatement(testData)));

	}
	
	@Test
	public void testCreateInsertStatement(){
		MysqlAccess dbtest = new MysqlAccess();
		List<String> values = new ArrayList<String>();
		values.add("testUsername");
		values.add("testPassword");
		values.add("?");
		values.add("Y");
		assertTrue("unexpected string returned: " + dbtest.createInsertStatement(values, "users"),  
			"insert into healthapp.users values (default, 'testUsername', 'testPassword', ?, 'Y')".equals(dbtest.createInsertStatement(values, "users")));
	}
	
	@Test
	public void testCreateToken(){
		SimpleAuthenticate test = new SimpleAuthenticate();
		test.authenticateLogin("testUsername", "testPassword");
		assertTrue("token is false", test.getToken());
		SimpleAuthenticate testno = new SimpleAuthenticate();
		testno.authenticateLogin("failUsername", "failPassword");
		assertFalse("token is true", testno.getToken());
	}
	
	@Test
	public void testByPassConstructor(){
		SimpleAuthenticate test = new SimpleAuthenticate("mockValidLogin");
		assertNotNull("Constructor failed", test );
		assertTrue("token set to false", test.getToken());
	}
	@Test
	public void testTokenControlledAccess(){
		SimpleAuthenticate test = new SimpleAuthenticate("mockValidLogin");
		assertTrue("failed to get the requested url", "mockRequestedURL".equals(test.getAccessToUrl("mockRequestedURL")));
		SimpleAuthenticate testnot = new SimpleAuthenticate();
		assertTrue("expected to fail but returned requested url","mockLoginFailedURL".equals(testnot.getAccessToUrl("mockRequestedURL")));
	}
	
	@Test
	public void testLogout(){
		SimpleAuthenticate test = new SimpleAuthenticate("mockValidLogin");
		test.logOut();
		assertFalse(test.getToken());
		assertTrue("expected to fail but returned requested url","mockLoginFailedURL".equals(test.getAccessToUrl("mockRequestedURL")));
		
		
	}
	

}
