package gimmi.content;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;

import java.sql.SQLException;

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
}
