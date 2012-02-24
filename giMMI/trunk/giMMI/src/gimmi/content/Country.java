package gimmi.content;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Country extends CorpusContent {
	/** The name of the database table */
	public static final String TABLE_NAME = "country";

	public Country(CorpusDatabase db) throws SQLException,
			CorpusDatabaseException {
		this.database = db;
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
		return null;
	}

	public Object getCodeById(Number id) throws SQLException,
			CorpusDatabaseException {
		if (id.intValue() < 0) {
			throw new IllegalArgumentException(
					"You should only pass in positive values.");
		}
		ResultSet countryRS = this.getTable().find("country_id", id.toString());
		if (countryRS.next()) {
			return countryRS.getString("country_code");
		}
		return null;
	}

	@Override
	public List<String> getAllEntries(String translation, boolean usedOnly)
			throws SQLException, CorpusDatabaseException {
		if (usedOnly) {
			// get only used entries
			return this.simpleJoin(this.getTable(),
					new Site(this.database).getTable(), "country_id",
					"country_id", translation);
		} else {
			// get all possible codes
			return this.resultsetToStringList(this.getTable().fetchAll(
					new String[] { translation }));
		}
	}
}
