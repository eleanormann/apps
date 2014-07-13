package security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

public class Authenticate {
	
	public static void  main(String[] args) {
		LoginContext lc = null;
		try {
			lc = new LoginContext("Security", new MyCallbackHandler());
		} catch (LoginException e) {
			// TODO save stack trace to logging file under ERROR tag
			e.printStackTrace();
		} catch (SecurityException e){
			// TODO save stack trace to logging file under ERROR tag
			e.printStackTrace();
		}
		int i;
		
        for (i = 0; i < 3; i++) {
            try {
                lc.login();
                break;
            } catch (LoginException le) {
                //TODO log ERROR auth failed
            	//TODO write to interface:
            	System.err.println("Authentication failed:");
                System.err.println("  " + le.getMessage());
            }
        }
        // did they fail three times?
        if (i == 3) {
        	//TODO INFO auth failed
        	//TODO write to interface
            System.out.println("No more login attempts allowed. Contact your system administrator");
            System.exit(-1);
        }

        //TODO add INFO auth succeeded to logs
        System.out.println("Authentication succeeded!");		
	}

}

class MyCallbackHandler implements CallbackHandler{

	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		System.out.println("hello");
		for(Callback callback : callbacks){
			if(callback instanceof NameCallback){
				
				NameCallback nc = (NameCallback)callback;
				nc.setName("testUser");
			
			}else if(callback instanceof PasswordCallback){
				 
				PasswordCallback pc = (PasswordCallback)callback;
				
				pc.setPassword(readPassword(System.in));
			
			}else {
		        throw new UnsupportedCallbackException (callback, "Unrecognized Callback");
			}    
		}
	}
	
	public char[] readPassword(InputStream in){
		//implement a password reader
		char[] password = {'t','e','s','t','p','a','s','s','w','o','r','d'};
		
		return password;
	}
	
}
