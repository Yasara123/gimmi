package gimmi.content;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Language extends CorpusContent {
	/** The name of the database table */
	public static final String TABLE_NAME = "language";
	private final CorpusDatabase db;

	public Language(CorpusDatabase db) throws SQLException,
			CorpusDatabaseException {
		this.db = db;
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
				"The language-code you specified");
	}

	/**
	 * 
	 * @param translation
	 *            The translation of the languages. This must match the
	 *            corresponding table column name
	 * @param usedOnly
	 *            Get only language names actually used by any site entry
	 * @return A string-list with all languages found
	 * @throws CorpusDatabaseException
	 * @throws SQLException
	 */
	public List getAllLanguages(String translation, boolean usedOnly)
			throws SQLException, CorpusDatabaseException {
		if (this.getTable().getColumns().keySet().contains(translation) == false) {
			return null;
		}
		if (usedOnly == true) {
			// SELECT gimmi.language.name_de
			// FROM gimmi.language
			// INNER JOIN gimmi.site
			// ON gimmi.site.language_id=gimmi.language.language_id;
			this.getTable().join(new Site(this.db).getTable(), "language_id",
					"language_id");
		} else {

		}
		return new ArrayList();
	}
}
