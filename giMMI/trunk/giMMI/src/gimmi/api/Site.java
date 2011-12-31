package gimmi.api;

import gimmi.content.Domain;
import gimmi.content.Language;
import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;
import gimmi.util.ConfigManagerException;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * 
 * @author Jens Bertram <code@jens-bertram.net>
 * 
 */
public class Site {
	private final HashMap<String, Object> properties = new HashMap<String, Object>();
	private static CorpusDatabase DB;

	/**
	 * Constructor
	 * 
	 * @throws ConfigManagerException
	 * @throws SQLException
	 */
	public Site() throws SQLException, ConfigManagerException {
		Site.DB = gimmi.database.Database.getDatabase();
	}

	/**
	 * Constructor
	 * 
	 * @param conf
	 *            Site configuration
	 */
	public Site(HashMap<String, Object> conf) {
		this.properties.putAll(conf);
	}

	public void setURL(URL url) throws SQLException, CorpusDatabaseException {
		this.properties.put("url_path", url.getPath());

		// TODO: host must be resolved to an id
		// retrieve the id for the given language-code
		Domain domain = new Domain(Site.DB);
		ResultSet domainRS = (domain.getTable().find("url", url.getHost()));
		Integer domainId = null;
		if (domainRS.next()) {
			domainId = domainRS.getInt("domain_id");
		} else {
			domain.setProperty("url", url.getHost());
			domainId = domain.write();
		}
		this.properties.put("domain_id", domainId);
	}

	public void setLanguage(String code) throws IllegalArgumentException,
			SQLException, CorpusDatabaseException {
		if (code.length() != 3) {
			throw new IllegalArgumentException(
					"You should only specify 3-letter language-codes as defined by ISO639-alpha-3.");
		}

		// retrieve the id for the given language-code
		ResultSet languageRS = (new Language(Site.DB)).getTable().find(
				"lang_code", code);
		Integer langId = null;
		if (languageRS.next()) {
			langId = languageRS.getInt("language_id");
		} else {
			throw new IllegalArgumentException(
					"The language code you specified could not be found in the database.");
		}
		this.properties.put("language_id", langId);
	}

	public void write() throws SQLException, CorpusDatabaseException,
			ConfigManagerException {
		// use the underlying content object to write
		gimmi.content.Site site = new gimmi.content.Site(Site.DB);
		site.setProperties(this.properties);
		site.write();
	}
}
