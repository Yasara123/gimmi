package gimmi.testing.standalone;

import gimmi.ConfigManager;
import gimmi.ConfigManagerException;
import gimmi.content.CorpusContent;
import gimmi.content.Tag;
import gimmi.content.TagType;
import gimmi.database.CorpusDatabase;
import gimmi.database.mysql.Database;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FillDatabase extends CorpusContent {
	// --- configurable stuff
	// how many new tag-categories should we create?
	private static final int CNF_NEW_TAG_TYPES_AMOUNT = 5;
	// how many new tags should we create?
	private static final int CNF_NEW_TAGS_AMOUNT = 10;
	// data file
	private static final String DATA_FILE = "src/gimmi/testing/standalone/wordlist_en-de.txt";
	// header lines to skip in data file
	private static final int DATA_FILE_SKIP = 2;

	// internal stuff
	private static Tag TAG = null;
	private static TagType TAGTYPE = null;
	private static CorpusDatabase DB = null;
	private static ConfigManager CNF = null;
	private static List<String> DATA = new ArrayList<String>();

	private static void log(String msg) {
		System.out.println(msg);
	}

	// private static void err(String msg) {
	// System.err.println(msg);
	// }
	//
	// private static void exit(Exception e) {
	// e.printStackTrace();
	// }

	private static void logHeader(String title) {
		FillDatabase.log("=== " + title + " ===");
	}

	public static String chooseLine() throws IOException {
		int line = (int) (Math.random() * FillDatabase.DATA.size());
		// dumb skip header lines
		line = (line <= FillDatabase.DATA_FILE_SKIP) ? line
				+ FillDatabase.DATA_FILE_SKIP : line;
		return FillDatabase.DATA.get(line);
	}

	/**
	 * @param args
	 * @throws SQLException
	 * @throws ConfigManagerException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws SQLException,
			ConfigManagerException, FileNotFoundException, IOException {
		// internal
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

		// config-manager
		FillDatabase.logHeader("Config");
		FillDatabase.log("Initializing config..");
		// access config in non-servlet way
		Properties prop = new Properties();
		prop.load(new FileInputStream("conf/gimmi.properties"));
		FillDatabase.CNF = new ConfigManager(prop);
		FillDatabase.CNF.validate();

		// connect to DB
		FillDatabase.logHeader("Database");
		FillDatabase.log("Try connecting to database..");
		FillDatabase.DB = new Database(
				FillDatabase.CNF.getByKey("database.name"),
				FillDatabase.CNF.getByKey("database.user"),
				FillDatabase.CNF.getByKey("database.password"));
		// query
		FillDatabase.log("Setting up table objects..");
		FillDatabase.TAG = new Tag(FillDatabase.DB);
		FillDatabase.TAGTYPE = new TagType(FillDatabase.DB);
		FillDatabase.log("\tOk. Connections established!");

		// query for tags
		FillDatabase.logHeader("Tag");
		// categories
		FillDatabase.log("Testing if we have tag-types..");
		FillDatabase.log("\tFound " + FillDatabase.TAGTYPE.count()
				+ " tag-types!");
		FillDatabase.log("Generating " + FillDatabase.CNF_NEW_TAG_TYPES_AMOUNT
				+ " new tag-types..");
		for (int i = 0; i < FillDatabase.CNF_NEW_TAG_TYPES_AMOUNT; i++) {
			String[] names = FillDatabase.chooseLine().split(";");
			FillDatabase.TAGTYPE.add(names[0], names[1]);
		}
		// tags
		FillDatabase.log("Testing if we have tags..");
		FillDatabase.log("\tFound " + FillDatabase.TAG.count() + " tags!");
		FillDatabase.log("Generating new tags..");
	}
}
