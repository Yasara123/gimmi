package gimmi.content;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;

import java.sql.SQLException;

public class TagType extends CorpusContent {
	private static final String TABLE_NAME = "tagType";

	/**
	 * Constructor
	 * 
	 * @param db
	 *            The database object to work with
	 * @throws SQLException
	 * @throws CorpusDatabaseException
	 */
	public TagType(CorpusDatabase db) throws SQLException,
			CorpusDatabaseException {
		this.setTable(db.getTable(TagType.TABLE_NAME));
	}
}
