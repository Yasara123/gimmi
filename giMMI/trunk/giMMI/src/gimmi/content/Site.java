package gimmi.content;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;

import java.sql.SQLException;

/**
 * 
 * @author Jens Bertram <code@jens-bertram.net>
 * 
 */
public class Site extends CorpusContent {
	/** The name of the database table */
	private static final String TABLE_NAME = "sites";

	// // size of title db-field
	// private static final int MAX_TITLE_LENGTH = 256;
	// // size of language-code db-field
	// private static final int MAX_LANGUAGECODE_LENGTH = 3;
	// // size of country-code db-field
	// private static final int MAX_COUNTRYCODE_LENGTH = 2;
	//
	// private final String title = null;
	// private final URL url = null;
	// private final String languageCode = null;
	// private final String countryCode = null;
	// private final Date crawlTime = null;

	public Site(CorpusDatabase db) throws SQLException, CorpusDatabaseException {
		this.setTable(db.getTable(Site.TABLE_NAME));
	}

	// /**
	// * General constructor. The crawl-time will be set to the time when this
	// * function is called.
	// *
	// * @see #Site(URL, Date, String, String, String, String)
	// */
	// public Site(URL url, String title, String languageCode, String
	// countryCode,
	// String rootFile) throws Exception {
	// this.checkConstrainedParameters(title, languageCode, countryCode);
	// this.url = url;
	// this.title = title;
	// this.languageCode = languageCode;
	// this.countryCode = countryCode;
	// this.crawlTime = new Date();
	// }
	//
	// /**
	// * Default constructor
	// *
	// * @param url
	// * The URL of the site to add
	// * @param title
	// * The title of the site to add (length restricted to
	// * {@value #MAX_TITLE_LENGTH} chars)
	// * @param crawlTime
	// * A timestamp describing when this site was originally crawled
	// * @param languageCode
	// * The {@value #MAX_LANGUAGECODE_LENGTH}-chars language-code
	// * describing the content of the site to add
	// * @param countryCode
	// * The {@value #MAX_COUNTRYCODE_LENGTH}-chars country-code
	// * describing the content of the site to add
	// * @param rootFile
	// * The root file-name of this site callable by a webbrowser
	// * @throws Exception
	// */
	// public Site(URL url, Date crawlTime, String title, String languageCode,
	// String countryCode, String rootFile) throws Exception {
	// this.checkConstrainedParameters(title, languageCode, countryCode);
	// this.checkConstrainedParameters(title, languageCode, countryCode);
	// this.url = url;
	// this.title = title;
	// this.languageCode = languageCode;
	// this.countryCode = countryCode;
	// this.crawlTime = crawlTime;
	// }

	// /**
	// * Simply check, if the given parameters will fit our database layout
	// *
	// * @throws Exception
	// * Thrown if any of the given parameters did not fit
	// */
	// private void checkConstrainedParameters(String title, String
	// languageCode,
	// String countryCode) throws Exception {
	// String msgExceeded =
	// "The given %s exceeded the maximum of %d chars (you gave %d).";
	//
	// // pre-check for null
	// if ((title == null) || (languageCode == null) || (countryCode == null)) {
	// throw new Exception(
	// "One of your parameters 'title, languageCode, countryCode' was null.");
	// }
	// // title
	// if (title.length() > Site.MAX_TITLE_LENGTH) {
	// throw new Exception(String.format(msgExceeded, "Site title",
	// Site.MAX_TITLE_LENGTH, title.length()));
	// }
	// // languageCode
	// if (languageCode.length() > Site.MAX_LANGUAGECODE_LENGTH) {
	// throw new Exception(String.format(msgExceeded, "Language-Code",
	// Site.MAX_LANGUAGECODE_LENGTH, languageCode.length()));
	// }
	// // countryCode
	// if (countryCode.length() > Site.MAX_COUNTRYCODE_LENGTH) {
	// throw new Exception(String.format(msgExceeded, "Country-Code",
	// Site.MAX_COUNTRYCODE_LENGTH, countryCode.length()));
	// }
	// }
}
