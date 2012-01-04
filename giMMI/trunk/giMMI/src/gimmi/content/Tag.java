package gimmi.content;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;

import java.sql.SQLException;
import java.util.List;

public class Tag extends CorpusContent {
	/** The name of the database table */
	public static final String TABLE_NAME = "tag";

	/**
	 * Constructor
	 * 
	 * @param db
	 *            The database object to work with
	 * @throws SQLException
	 * @throws CorpusDatabaseException
	 */
	public Tag(CorpusDatabase db) throws SQLException, CorpusDatabaseException {
		this.database = db;
		this.setTable(db.getTable(Tag.TABLE_NAME));
	}

	@Override
	public List<String> getAllEntries(String translation, boolean usedOnly)
			throws SQLException, CorpusDatabaseException {
		// TODO Auto-generated method stub
		return null;
	}
}
