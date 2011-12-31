package gimmi.testing.standalone;

import gimmi.api.Site;
import gimmi.database.CorpusDatabaseException;
import gimmi.util.ConfigManagerException;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;

public class APIClient {

	/**
	 * @param args
	 * @throws ConfigManagerException
	 * @throws CorpusDatabaseException
	 * @throws SQLException
	 * @throws MalformedURLException
	 */
	public static void main(String[] args) throws SQLException,
			CorpusDatabaseException, ConfigManagerException,
			MalformedURLException {
		Site site = new gimmi.api.Site();
		site.setLanguage("ger");
		site.setURL(new URL("http://www.example.com/foo/bar"));
		site.write();
	}
}
