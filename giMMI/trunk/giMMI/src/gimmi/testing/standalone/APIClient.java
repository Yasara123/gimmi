package gimmi.testing.standalone;

import gimmi.api.Site;
import gimmi.database.CorpusDatabaseException;
import gimmi.util.ConfigManagerException;

import java.sql.SQLException;

public class APIClient {

	/**
	 * @param args
	 * @throws ConfigManagerException
	 * @throws CorpusDatabaseException
	 * @throws SQLException
	 */
	public static void main(String[] args) throws SQLException,
			CorpusDatabaseException, ConfigManagerException {
		Site site = new gimmi.api.Site();
		site.write();
	}

}
