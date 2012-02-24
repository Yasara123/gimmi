package gimmi.testing.api;

import gimmi.api.Site;
import gimmi.content.Language;
import gimmi.database.CorpusDatabase;
import gimmi.testing.Testing;

import java.util.List;

/**
 * Static test for the Site api
 * 
 * @author Jens Bertram <code@jens-bertram.net>
 * 
 */
public class SiteTest extends Testing {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Testing.so("Initialize", Format.HEADER);
		Testing.so("Opening database", Format.STEP);
		CorpusDatabase db = null;
		try {
			db = gimmi.database.Database.getInstance();
		} catch (Exception e) {
			Testing.err(e);
		}
		Testing.so("Getting languages", Format.STEP);
		try {
			List<String> languages = new Language(db).getAllEntries("name_de",
					false);
			Testing.so(String.format("Languages: %d", languages.size()),
					Format.STEPSUB);
		} catch (Exception e) {
			Testing.err(e);
		}

		// this should pass without errors
		Testing.so("Legal site creation", Format.HEADER);
		Testing.so("Creating a new Site object", Format.STEP);
		Site site = null;
		try {
			site = new Site();
		} catch (Exception e) {
			Testing.err(e);
		}

		Testing.so("Setting language", Format.STEP);
		// Language.site.setLanguageCode();
	}
}
