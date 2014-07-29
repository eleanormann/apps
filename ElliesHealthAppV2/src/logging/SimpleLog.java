package logging;

import java.util.Map;

import database.MysqlAccess;

/** Uses MysqlAccess object to insert simple log info into healthapp.session
 */
public class SimpleLog {


	//TODO: verification of successful insert
	public void logPageAccess(Map<String, String> data, String string) {
		MysqlAccess log = new MysqlAccess();
		String query = log.createInsertStatement(data, "session");
		log.simpleCUD(query);
	}
}
