package gimmi.content;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Language extends CorpusContent {
	/** The name of the database table */
	public static final String TABLE_NAME = "language";

	public Language(CorpusDatabase db) throws SQLException,
			CorpusDatabaseException {
		this.database = db;
		this.setTable(db.getTable(Language.TABLE_NAME));
	}

	/**
	 * Get the id of a language-code string value in the database
	 * 
	 * @param code
	 * @return
	 * @throws SQLException
	 * @throws CorpusDatabaseException
	 * @throws IllegalArgumentException
	 */
	public Integer getIdByCode(String code) throws SQLException,
			CorpusDatabaseException, IllegalArgumentException {
		if (code.length() != 3) {
			throw new IllegalArgumentException(
					"You should only specify 3-letter language-codes as defined by ISO639-alpha-3.");
		}

		ResultSet languageRS = this.getTable().find("lang_code", code);
		if (languageRS.next()) {
			return languageRS.getInt("language_id");
		}
		throw new CorpusDatabaseException(
				CorpusDatabaseException.Error.VALUE_NOT_FOUND,
				"The language-code you specified could not be found");
	}

	@Override
	public List<String> getAllEntries(String translation, boolean usedOnly)
			throws SQLException, CorpusDatabaseException {
		return this.simpleJoin(this.getTable(),
				new Site(this.database).getTable(), "language_id",
				"language_id", translation, usedOnly);
	}
}
