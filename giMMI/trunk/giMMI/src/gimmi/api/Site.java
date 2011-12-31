package gimmi.api;

import gimmi.database.CorpusDatabaseException;
import gimmi.util.ConfigManagerException;

import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * 
 * @author Jens Bertram <code@jens-bertram.net>
 * 
 */
public class Site {
	private final HashMap<String, Object> properties = new HashMap<String, Object>();

	/**
	 * Constructor
	 */
	public Site() {
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

	public void serURL(URL url) {
		this.properties.put("url_path", url.getPath());
		// TODO: host must be resolved to an id
		this.properties.put("domain_id", null);
	}

	public void setLanguage(String code) throws IllegalArgumentException {
		if (code.length() != 3) {
			throw new IllegalArgumentException(
					"You should only specify 3-letter language-codes as defined by ISO639-alpha-3.");
		}
		// TODO: language-code must be resolved to an id
		this.properties.put("language_id", null);
	}

	public void write() throws SQLException, CorpusDatabaseException,
			ConfigManagerException {
		// use the underlying content object to write
		gimmi.content.Site site = new gimmi.content.Site(
				gimmi.database.Database.getDatabase());
		site.setProperties(this.properties);
		site.write();
	}
}
