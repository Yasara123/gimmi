package gimmi.content;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Language extends CorpusContentNamed {
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
	public Number getIdByCode(String code) throws SQLException,
			CorpusDatabaseException, IllegalArgumentException {
		if (code.length() != 3) {
			throw new IllegalArgumentException(
					"You should only specify 3-letter language-codes as defined by ISO639-alpha-3.");
		}

		ResultSet languageRS = this.getTable().find("lang_code", code);
		if (languageRS.next()) {
			return languageRS.getInt("language_id");
		}
		return null;
	}

	public String getCodeById(Number id) throws CorpusDatabaseException,
			SQLException {
		if (id.intValue() < 0) {
			throw new IllegalArgumentException(
					"You should only pass in positive values.");
		}
		ResultSet languageRS = this.getTable().find("language_id",
				id.toString());
		if (languageRS.next()) {
			return languageRS.getString("lang_code");
		}
		return null;
	}

	@Override
	public List<String> getAllEntries(String field, boolean usedOnly)
			throws SQLException, CorpusDatabaseException {
		if (usedOnly) {
			// get only used entries
			return this.simpleJoin(this.getTable(),
					new Site(this.database).getTable(), "language_id",
					"language_id", field);
		} else {
			// get all possible codes
			return this.resultsetToStringList(this.getTable().fetchAll(
					new String[] { field }));
		}
	}
}
