package gimmi.testing.api;

import gimmi.api.Site;
import gimmi.content.Category;
import gimmi.content.Country;
import gimmi.content.Language;
import gimmi.content.SiteHasCategory;
import gimmi.database.CorpusDatabase;
import gimmi.database.MultilanguageContent;
import gimmi.testing.Testing;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Static test for the Site api
 * 
 * @author Jens Bertram <code@jens-bertram.net>
 * 
 */
public class SiteTest extends Testing {
	private static CorpusDatabase db = null;
	private static Site site = null;
	// store settings for final comparison
	private static final Map<String, Object> properties = new HashMap<String, Object>();

	private static void testTimestamp() {
		Timestamp ts = new Timestamp(Calendar.getInstance().getTime().getTime());
		Testing.so("Setting timestamp: " + ts.toString(), Format.STEP);
		try {
			SiteTest.site.setTimestamp(ts);
			SiteTest.properties.put("timestamp", ts.toString());
		} catch (Exception e) {
			Testing.err(e);
		}
	}

	private static void testTitle() {
		String title = Testing.randomString(35);
		Testing.so("Setting title: " + title, Format.STEP);
		try {
			SiteTest.site.setTitle(title);
			SiteTest.properties.put("title", title);
		} catch (Exception e) {
			Testing.err(e);
		}
	}

	private static void testRootFile() {
		String file = Testing.randomString(15) + ".html";
		Testing.so("Setting root-file: " + file, Format.STEP);
		try {
			SiteTest.site.setRootFile(file);
			SiteTest.properties.put("rootfile", file);
		} catch (Exception e) {
			Testing.err(e);
		}
	}

	private static void testCategory() {
		MultilanguageContent mCategory = new MultilanguageContent();
		for (MultilanguageContent.Lang lang : MultilanguageContent.Lang
				.values()) {
			mCategory.setLangString(lang, Testing.randomString(15));
		}
		Testing.so("Setting category: " + mCategory, Format.STEP);
		try {
			SiteTest.site.setCategory(mCategory);
			SiteTest.properties.put("category", mCategory.toString());
		} catch (Exception e) {
			Testing.err(e);
		}
	}

	private static void testURL() {
		try {
			String URL = "http://www." + Testing.randomString(10)
					+ ".com/testpage/";
			Testing.so("Setting URL: " + URL, Format.STEP);
			SiteTest.site.setURL(new URL(URL));
			SiteTest.properties.put("url", URL);
		} catch (Exception e) {
			Testing.err(e);
		}
	}

	private static void testCountryCode() {
		Testing.so("Getting country-codes..", Format.STEP);

		// get test data
		List<String> ccodesAll = null;
		List<String> ccodesUsed = null;
		try {
			ccodesAll = new Country(SiteTest.db).getAllEntries("country_code",
					false);
			ccodesUsed = new Country(SiteTest.db).getAllEntries("country_code",
					true);
			Testing.so(String.format("%d of %d currently used",
					ccodesUsed.size(), ccodesAll.size()), Format.STEPSUB);
		} catch (Exception e) {
			Testing.err(e);
		}

		// test assignment
		Testing.so("Assigning country-codes", Format.STEP);
		Testing.so("This test only assigning the value. Site won't be saved.",
				Format.STEPINFO);
		for (int i = 0; i < ccodesAll.size(); i++) {
			Testing.so("Try assigning country-code '" + ccodesAll.get(i) + "'",
					Format.STEPSUB);
			try {
				SiteTest.site.setCountryCode(ccodesAll.get(i));
			} catch (Exception e) {
				Testing.err(e);
			}
		}

		// test setting
		try {
			String cCode = ccodesAll.get(Testing.randomInt(ccodesAll.size()));
			Testing.so("Setting country-code for this site to (random): '"
					+ cCode + "'", Format.STEP);
			SiteTest.site.setCountryCode(cCode);
			SiteTest.properties.put("countrycode", cCode);
		} catch (Exception e) {
			Testing.err(e);
		}
	}

	private static void testLanguage() {
		Testing.so("Getting languages..", Format.STEP);

		// get test data
		List<String> languagesAll = null;
		List<String> languagesUsed = null;
		try {
			languagesAll = new Language(SiteTest.db).getAllEntries("lang_code",
					false);
			languagesUsed = new Language(SiteTest.db).getAllEntries(
					"lang_code", true);
			Testing.so(
					String.format("%d of %d currently used",
							languagesUsed.size(), languagesAll.size()),
					Format.STEPSUB);
		} catch (Exception e) {
			Testing.err(e);
		}

		// test assignment
		Testing.so("Assigning languages", Format.STEP);
		Testing.so("This test only assigning the value. Site won't be saved.",
				Format.STEPINFO);
		for (int i = 0; i < languagesAll.size(); i++) {
			Testing.so("Try assigning language '" + languagesAll.get(i) + "'",
					Format.STEPSUB);
			try {
				SiteTest.site.setLanguageCode(languagesAll.get(i));
			} catch (Exception e) {
				Testing.err(e);
			}
		}

		// test setting
		try {
			String langCode = languagesAll.get(Testing.randomInt(languagesAll
					.size()));
			Testing.so("Setting language for this site to (random): '"
					+ langCode + "'", Format.STEP);
			SiteTest.site.setLanguageCode(langCode);
			SiteTest.properties.put("languagecode", langCode);
		} catch (Exception e) {
			Testing.err(e);
		}
	}

	private static final boolean checkTest(Number siteId) {
		final Map<String, Object> dbData = new HashMap<String, Object>();
		System.out.println("Site id is " + siteId);

		try {
			gimmi.content.Site newSite = new gimmi.content.Site(SiteTest.db);
			ResultSet newSiteRS = newSite.getTable().find("site_id",
					siteId.toString());
			if (newSiteRS.next()) {
				// simple types
				dbData.put("timestamp", newSiteRS.getString("crawl_time"));
				dbData.put("time", newSiteRS.getString("crawl_time"));
				dbData.put("url", newSiteRS.getString("url_path"));
				dbData.put("rootfile", newSiteRS.getString("root_file"));
				dbData.put("title", newSiteRS.getString("title"));
				// referenced types
				Language language = new Language(SiteTest.db);
				dbData.put("languagecode",
						language.getCodeById(newSiteRS.getInt("language_id")));
				Country country = new Country(SiteTest.db);
				dbData.put("countrycode",
						country.getCodeById(newSiteRS.getInt("country_id")));
				// relation types
				SiteHasCategory siteCategory = new SiteHasCategory(SiteTest.db);
				ResultSet siteCategoryRS = siteCategory.getTable()
						.joinWithCondition(
								"category_id",
								new Category(SiteTest.db).getTable(),
								"category_id",
								siteCategory.getTable().getName()
										+ ".site_id='" + siteId + "'");
				if (siteCategoryRS.next()) {
					dbData.put("category", newSite.getCategoryNameById(siteId)
							.toString());
				}
			}
		} catch (Exception e) {
			Testing.err(e);
		}

		for (String setting : SiteTest.properties.keySet()) {
			String test = null;
			if (setting == "url") {
				try {
					test = (new URL(SiteTest.properties.get(setting).toString())
							.getPath()).equals(dbData.get(setting)) ? "ok"
							: "fail";
				} catch (MalformedURLException e) {
					Testing.err(e);
				}
			} else if (setting == "timestamp") {
				// cut the hundreds - they aren't stored in the db
				String ts1 = SiteTest.properties.get(setting).toString();
				ts1 = ts1.substring(0, ts1.lastIndexOf("."));
				String ts2 = SiteTest.properties.get(setting).toString();
				ts2 = ts2.substring(0, ts2.lastIndexOf("."));
				test = ts1.equals(ts2) ? "ok" : "fail";
			} else {
				test = SiteTest.properties.get(setting).equals(
						dbData.get(setting)) ? "ok" : "fail";
			}
			System.out.printf("%-15s: set['%-45s'] get['%-45s'] = %s\n",
					setting, SiteTest.properties.get(setting),
					dbData.get(setting), test);
		}
		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Number newSiteId = -1;

		Testing.so("Initialize", Format.HEADER);
		Testing.so("These tests should run without any errors.",
				Format.HEADERSUB);
		Testing.so("Opening database", Format.STEP);

		try {
			SiteTest.db = gimmi.database.Database.getInstance();
		} catch (Exception e) {
			Testing.err(e);
		}

		// test 1 - easy
		Testing.so("Legal site creation (easy)", Format.HEADER);
		Testing.so("These tests should run without any errors.",
				Format.HEADERSUB);
		Testing.so("Creating a new Site object", Format.STEP);
		try {
			SiteTest.site = new Site();
		} catch (Exception e) {
			Testing.err(e);
		}
		// run tests
		SiteTest.testLanguage();
		SiteTest.testCountryCode();
		SiteTest.testURL();
		SiteTest.testCategory();
		SiteTest.testRootFile();
		SiteTest.testTitle();
		SiteTest.testTimestamp();
		Testing.so("Writing data..", Format.STEPFINAL);
		try {
			newSiteId = SiteTest.site.write();
		} catch (Exception e) {
			Testing.err(e);
		}
		Testing.so("Legal site creation (easy): final check", Format.HEADER);
		SiteTest.checkTest(newSiteId);
	}
}
