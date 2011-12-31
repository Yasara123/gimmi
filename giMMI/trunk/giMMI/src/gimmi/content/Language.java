package gimmi.content;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Language extends CorpusContent {
	/** The name of the database table */
	private static final String TABLE_NAME = "language";

	public Language(CorpusDatabase db) throws SQLException,
			CorpusDatabaseException {
		this.setTable(db.getTable(Language.TABLE_NAME));
	}

	public Integer getIdByLangCode(String code) throws SQLException {
		if (code.length() != 3) {
			return null;
		}

		ResultSet rs = this.getTable().find("lang_code", code);

		return null;
	}
}
