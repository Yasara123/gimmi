package gimmi.database;

import gimmi.util.ConfigManager;
import gimmi.util.ConfigManagerException;

import java.sql.SQLException;

public class Database {
	static CorpusDatabase database = null;

	private static CorpusDatabase initDatabase() throws SQLException,
			ConfigManagerException {
		if (ConfigManager.isInitialized() == false) {
			ConfigManager.tryLoadFallbackConfig();
		}
		Database.database = new gimmi.database.mysql.Database(
				ConfigManager.getByKey("database.name"),
				ConfigManager.getByKey("database.user"),
				ConfigManager.getByKey("database.password"));
		return Database.database;
	}

	static public CorpusDatabase getDatabase() throws SQLException,
			ConfigManagerException {
		return (Database.database == null) ? Database.initDatabase()
				: Database.database;
	}
}