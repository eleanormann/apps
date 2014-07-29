package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import security.Authenticate;
import security.SimpleAuthenticate;
import database.MysqlAccess;

public class AuthenticationTests {
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
	public void testCreateToken(){
		SimpleAuthenticate test = new SimpleAuthenticate();
		test.createUser("testUsername", "testPassword");
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
