package gimmi.content;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Country extends CorpusContent {
	/** The name of the database table */
	private static final String TABLE_NAME = "country";

	public Country(CorpusDatabase db) throws SQLException,
			CorpusDatabaseException {
		this.setTable(db.getTable(Country.TABLE_NAME));
	}

	/**
	 * Get the id of a country-code string value in the database
	 * 
	 * @param code
	 *            The two-letter language code as specified by ISO3166-1-alpha-2
	 * @return The id of the corresponding database entry
	 * @throws SQLException
	 * @throws CorpusDatabaseException
	 * @throws IllegalArgumentException
	 *             Thrown if the language code does not match the
	 *             ISO3166-1-alpha-2 scheme
	 */
	public Integer getIdByCode(String code) throws SQLException,
			CorpusDatabaseException, IllegalArgumentException {
		if (code.length() != 2) {
			throw new IllegalArgumentException(
					"You should only specify 2-letter language-codes as defined by ISO3166-1-alpha-2.");
		}

		ResultSet languageRS = this.getTable().find("country_code", code);
		if (languageRS.next()) {
			return languageRS.getInt("country_id");
		}
		throw new CorpusDatabaseException(
				CorpusDatabaseException.Error.VALUE_NOT_FOUND,
				"The country-code you specified");
	}
}
