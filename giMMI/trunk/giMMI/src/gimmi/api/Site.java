package gimmi.api;

import gimmi.content.Country;
import gimmi.content.Domain;
import gimmi.content.Language;
import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;
import gimmi.util.ConfigManagerException;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
		Site.DB = gimmi.database.Database.getInstance();
	}

	public Site(String url, String langCode, String countryCode,
			String rootFile, String title) {
		Site.DB = gimmi.database.Database.getInstance();

		setTitle(title);
		setURL(new URL(url));
		setTimestamp(new Timestamp(new Long(
				System.currentTimeMillis() / 1000)));
		setLanguageCode(langCode);
		setCountryCode(countryCode);
		setRootFile(rootFile);
		// write the site to the database
		try {
			write();
		} catch (CorpusDatabaseException e) {
			// seems we missed a property
			e.printStackTrace();
		}
	}

	/**
	 * Set the URL property for this site
	 * 
	 * @param url
	 *            The URL object for this site
	 * @throws SQLException
	 * @throws CorpusDatabaseException
	 */
	public void setURL(URL url) throws SQLException, CorpusDatabaseException {
		this.properties.put("url_path", url.getPath());
		// retrieve the hostname-for the given language-code
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

	/**
	 * Set the timestamp property for this site. The timestamp should reflect
	 * when the site was crawled. If you omit this property the site object will
	 * be committed with a timestamp of the commit-time
	 * 
	 * @param ts
	 *            The timestamp
	 */
	public void setTimestamp(Timestamp ts) {
		this.properties.put("crawl_time", ts);
	}

	/**
	 * Set the language property for this site
	 * 
	 * @param code
	 *            The three-letter language code as specified by ISO639-alpha-3
	 * @throws IllegalArgumentException
	 *             Thrown if the language code does not match the ISO639-alpha-3
	 *             scheme
	 * @throws SQLException
	 * @throws CorpusDatabaseException
	 */
	public void setLanguageCode(String code) throws IllegalArgumentException,
			SQLException, CorpusDatabaseException {
		Language language = new Language(Site.DB);
		Integer lCode = language.getIdByCode(code);
		if (lCode != null) {
			this.properties.put("language_id", lCode);
		}
	}

	/**
	 * Set the country property for this site
	 * 
	 * @param code
	 *            The two-letter language code as specified by ISO3166-1-alpha-2
	 * @throws IllegalArgumentException
	 *             Thrown if the language code does not match the
	 *             ISO3166-1-alpha-2 scheme
	 * @throws CorpusDatabaseException
	 * @throws SQLException
	 */
	public void setCountryCode(String code) throws IllegalArgumentException,
			SQLException, CorpusDatabaseException {
		Country country = new Country(Site.DB);
		Integer cCode = country.getIdByCode(code);
		if (cCode != null) {
			this.properties.put("country_id", cCode);
		}
	}

	/**
	 * Set the root-filename for this site. The root file is the file witch is
	 * opened first to browse a site-copy in the corpus
	 * 
	 * @param fileName
	 */
	public void setRootFile(String fileName) throws IllegalArgumentException {
		String fName = fileName.trim();
		if (fName.length() > 0) {
			this.properties.put("root_file", fName);
		} else {
			throw new IllegalArgumentException(
					"The filename you specified seems to be empty.");
		}
	}

	/**
	 * Set the title for this site
	 * 
	 * @param title
	 *            The title for this site
	 */
	public void setTitle(String title) throws IllegalArgumentException {
		String sName = title.trim();
		if (sName.length() > 0) {
			this.properties.put("title", sName);
		} else {
			throw new IllegalArgumentException(
					"The site-title you specified seems to be empty.");
		}
	}

	/**
	 * Commits the new site object to the database
	 * 
	 * @throws SQLException
	 * @throws CorpusDatabaseException
	 * @throws ConfigManagerException
	 */
	public void write() throws SQLException, CorpusDatabaseException,
			ConfigManagerException {
		// use the underlying content object to write
		gimmi.content.Site site = new gimmi.content.Site(Site.DB);
		site.setProperties(this.properties);
		site.write();
	}
}