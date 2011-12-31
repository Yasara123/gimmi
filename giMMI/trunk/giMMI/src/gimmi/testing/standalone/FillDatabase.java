package gimmi.testing.standalone;

import gimmi.content.Category;
import gimmi.content.Domain;
import gimmi.content.Site;
import gimmi.content.Tag;
import gimmi.content.TagType;
import gimmi.database.CorpusDatabase;
import gimmi.database.mysql.Database;
import gimmi.util.ConfigManager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class FillDatabase {
	// --- configurable stuff
	// how many new tag-categories should we create?
	private static final int CNF_NEW_TAG_TYPES_AMOUNT = 5;
	// how many new tags should we create?
	private static final int CNF_NEW_TAGS_AMOUNT = 10;
	// how many new categories should we create?
	private static final int CNF_NEW_CATEGORIES_AMOUNT = 10;
	// how many new domains should we create?
	private static final int CNF_NEW_DOMAINS_AMOUNT = 10;
	// how many new sites should we create?
	private static final int CNF_NEW_SITES_AMOUNT = 10;
	// data file
	private static final String DATA_FILE = "src/gimmi/testing/standalone/wordlist_en-de.txt";
	// header lines to skip in data file
	private static final int DATA_FILE_SKIP = 2;

	// internal stuff
	private static Tag TAG = null;
	private static TagType TAGTYPE = null;
	private static Category CATEGORY = null;
	private static Domain DOMAIN = null;
	private static Site SITE = null;
	private static CorpusDatabase DB = null;
	private static List<String> DATA = new ArrayList<String>();
	private static ResultSet rs = null;
	private static final Random random = new Random();

	private static void log(String msg) {
		FillDatabase.log(msg, true);
	}

	private static void log(String msg, boolean nl) {
		if (nl) {
			System.out.println(msg);
		} else {
			System.out.print(msg);
		}
	}

	private static void logHeader(String title) {
		FillDatabase.log("=== " + title + " ===");
	}

	private static String chooseLine() throws IOException {
		int line = (int) (Math.random() * FillDatabase.DATA.size());
		// dumb skip header lines
		line = (line <= FillDatabase.DATA_FILE_SKIP) ? line
				+ FillDatabase.DATA_FILE_SKIP : line;
		return FillDatabase.DATA.get(line);
	}

	private static void errorExit(String message) throws Exception {
		throw new Exception(message);
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// ### internal
		FillDatabase.logHeader("Startup");
		FillDatabase.log("Reading data-file..");
		BufferedReader msgFile = new BufferedReader(new InputStreamReader(
				new FileInputStream(FillDatabase.DATA_FILE), "UTF8"));
		String str = "";
		while ((str = msgFile.readLine()) != null) {
			if (str.trim().length() != 0) {
				FillDatabase.DATA.add(str);
			}
		}
		FillDatabase.log("\tLoaded " + FillDatabase.DATA.size() + " lines");

		// ### config-manager
		FillDatabase.logHeader("Config");
		FillDatabase.log("Initializing config..");
		// access config in non-servlet way
		Properties prop = new Properties();
		prop.load(new FileInputStream("conf/gimmi.properties"));
		ConfigManager.loadProperties(prop);
		ConfigManager.validate();

		// ### connect to DB
		FillDatabase.logHeader("Database");
		FillDatabase.log("Try connecting to database..");
		FillDatabase.DB = new Database(ConfigManager.getByKey("database.name"),
				ConfigManager.getByKey("database.user"),
				ConfigManager.getByKey("database.password"));

		// ### query
		FillDatabase.log("Setting up table objects..");
		FillDatabase.TAGTYPE = new TagType(FillDatabase.DB);
		FillDatabase.TAG = new Tag(FillDatabase.DB);
		FillDatabase.CATEGORY = new Category(FillDatabase.DB);
		FillDatabase.DOMAIN = new Domain(FillDatabase.DB);
		FillDatabase.SITE = new Site(FillDatabase.DB);
		FillDatabase.log("\tOk. Connections established!");

		// ### tag-types
		FillDatabase.logHeader("Tag-Types");
		// tag types
		FillDatabase.log("Testing if we have tag-types..");
		FillDatabase.log("\tFound "
				+ FillDatabase.TAGTYPE.getTable().countRows() + " tag-types");
		FillDatabase.log("Generating " + FillDatabase.CNF_NEW_TAG_TYPES_AMOUNT
				+ " new tag-types..");
		for (int i = 0; i < FillDatabase.CNF_NEW_TAG_TYPES_AMOUNT; i++) {
			String[] names = FillDatabase.chooseLine().split(";");
			FillDatabase.TAGTYPE.setProperty("name_en", names[0]);
			FillDatabase.TAGTYPE.setProperty("name_de", names[1]);
			System.out.println("\tAdding en:" + names[0] + " de:" + names[1]);
			FillDatabase.TAGTYPE.write();
		}
		// query & store id's for later
		FillDatabase.log("Getting ids for "
				+ FillDatabase.TAGTYPE.getTable().countRows() + " tag-types..");
		FillDatabase.rs = FillDatabase.TAGTYPE.getTable().fetchAll(
				new String[] { "tag_type_id" });
		int[] tagTypeIds = new int[(int) FillDatabase.TAGTYPE.getTable()
				.countRows()];
		if (tagTypeIds.length <= 0) {
			FillDatabase.errorExit("No tag-types found in database!");
		}
		int tagTypeIdCount = 0;
		while (FillDatabase.rs.next()) {
			tagTypeIds[tagTypeIdCount++] = FillDatabase.rs
					.getInt("tag_type_id");
		}
		FillDatabase.log("\tOk");

		// ### tags
		FillDatabase.logHeader("Tag");
		FillDatabase.log("Testing if we have tags..");
		FillDatabase.log("\tFound " + FillDatabase.TAG.getTable().countRows()
				+ " tags");
		FillDatabase.log("Generating " + FillDatabase.CNF_NEW_TAGS_AMOUNT
				+ " new tags..");
		for (int i = 0; i < FillDatabase.CNF_NEW_TAGS_AMOUNT; i++) {
			String[] names = FillDatabase.chooseLine().split(";");
			int tagTypeId = FillDatabase.random
					.nextInt((int) FillDatabase.TAGTYPE.getTable().countRows());
			FillDatabase.TAG.setProperty("tag_type_id",
					Integer.toString(tagTypeIds[tagTypeId]));
			FillDatabase.TAG.setProperty("name_en", names[0]);
			FillDatabase.TAG.setProperty("name_de", names[1]);
			System.out.println("\tAdding id:" + tagTypeId + " en:" + names[0]
					+ " de:" + names[1]);
			FillDatabase.TAG.write();
		}

		// ### category
		FillDatabase.logHeader("Category");
		FillDatabase.log("Testing if we have categories..");
		FillDatabase.log("\tFound "
				+ FillDatabase.CATEGORY.getTable().countRows() + " categories");
		FillDatabase.log("Generating " + FillDatabase.CNF_NEW_CATEGORIES_AMOUNT
				+ " new categories..");
		for (int i = 0; i < FillDatabase.CNF_NEW_CATEGORIES_AMOUNT; i++) {
			String[] names = FillDatabase.chooseLine().split(";");
			FillDatabase.CATEGORY.setProperty("name_en", names[0]);
			FillDatabase.CATEGORY.setProperty("name_de", names[1]);
			System.out.println("\tAdding id:" + " en:" + names[0] + " de:"
					+ names[1]);
			FillDatabase.CATEGORY.write();
		}
		// query & store id's for later
		FillDatabase.log("Getting ids for "
				+ FillDatabase.CATEGORY.getTable().countRows()
				+ " categories..");
		FillDatabase.rs = FillDatabase.CATEGORY.getTable().fetchAll(
				new String[] { "category_id" });
		int[] categoryIds = new int[(int) FillDatabase.CATEGORY.getTable()
				.countRows()];
		if (categoryIds.length <= 0) {
			FillDatabase.errorExit("No categories found in database!");
		}
		int categoryIdCount = 0;
		while (FillDatabase.rs.next()) {
			categoryIds[categoryIdCount++] = FillDatabase.rs
					.getInt("category_id");
		}
		FillDatabase.log("\tOk");

		// ### domains
		FillDatabase.logHeader("Domain");
		FillDatabase.log("Testing if we have domains..");
		FillDatabase.log("\tFound "
				+ FillDatabase.DOMAIN.getTable().countRows() + " domains");
		FillDatabase.log("Generating " + FillDatabase.CNF_NEW_DOMAINS_AMOUNT
				+ " new domains..");
		for (int i = 0; i < FillDatabase.CNF_NEW_CATEGORIES_AMOUNT; i++) {
			// take English to avoid special chars
			String domain = (FillDatabase.chooseLine().split(";"))[0];
			domain = domain.replace(" ", "-");
			domain = "http://" + domain + ".example.com/";
			FillDatabase.DOMAIN.setProperty("url", new URL(domain));
			System.out.println("\tAdding domain " + domain);
			FillDatabase.DOMAIN.write();
		}
		// query & store id's for later
		FillDatabase.log("Getting ids for "
				+ FillDatabase.DOMAIN.getTable().countRows() + " domains..");
		FillDatabase.rs = FillDatabase.DOMAIN.getTable().fetchAll(
				new String[] { "domain_id" });
		int[] domainIds = new int[(int) FillDatabase.DOMAIN.getTable()
				.countRows()];
		if (domainIds.length <= 0) {
			FillDatabase.errorExit("No domains found in database!");
		}
		int domainIdCount = 0;
		while (FillDatabase.rs.next()) {
			domainIds[domainIdCount++] = FillDatabase.rs.getInt("domain_id");
		}
		FillDatabase.log("\tOk");

		// ### sites
		FillDatabase.logHeader("Sites");
		FillDatabase.log("Testing if we have sites..");
		FillDatabase.log("\tFound " + FillDatabase.SITE.getTable().countRows()
				+ " sites");
		FillDatabase.log("Generating " + FillDatabase.CNF_NEW_SITES_AMOUNT
				+ " new sites..");
		for (int i = 0; i < FillDatabase.CNF_NEW_SITES_AMOUNT; i++) {
			String title = "Welcome to "
					+ (FillDatabase.chooseLine().split(";"))[0];
			System.out.println("\tAdding site '" + title + "'");
			FillDatabase.SITE.write();
		}
	}
}
