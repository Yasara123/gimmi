package gimmi.testing.standalone;

import gimmi.api.Site;
import gimmi.database.CorpusDatabaseException;
import gimmi.util.ConfigManagerException;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;

public class APIClient {

    /**
     * @param args
     * @throws ConfigManagerException
     * @throws CorpusDatabaseException
     * @throws SQLException
     * @throws IllegalArgumentException
     * @throws MalformedURLException
     */
    public static void main(String[] args) throws SQLException,
	    ConfigManagerException, IllegalArgumentException,
	    MalformedURLException, CorpusDatabaseException {
	Site site;

	// add a Site by single properties
	site = new gimmi.api.Site();
	site.setTitle("A new Site - build in single steps");
	site.setURL(new URL("http://www.example.com/foo/bar"));
	site.setTimestamp(new Timestamp(new Long(
		System.currentTimeMillis() / 1000)));
	site.setLanguageCode("ger");
	site.setCountryCode("de");
	site.setRootFile("index.html");
	// write the site to the database
	try {
	    site.write();
	} catch (CorpusDatabaseException e) {
	    // seems we missed a property
	    e.printStackTrace();
	}
    }
}
