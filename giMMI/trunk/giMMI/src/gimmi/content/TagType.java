package gimmi.content;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;
import gimmi.database.MultilanguageContent;

import java.sql.SQLException;
import java.util.List;

public class TagType extends CorpusContent implements CorpusContentNamed {
	public static final String TABLE_NAME = "tagType";

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
		this.database = db;
		this.setTable(db.getTable(TagType.TABLE_NAME));
	}

	@Override
	public Number getIdByName(MultilanguageContent name) throws SQLException,
			CorpusDatabaseException, IllegalArgumentException {
		return (Number) this.getFieldByName("tag_type_id", name);
	}

	@Override
	public Number getIdByName(String name) throws SQLException {
		return (Number) this.getFieldByName("tag_type_id", name);
	}

	@Override
	public List<String> getAllEntries(String translation, boolean usedOnly)
			throws SQLException, CorpusDatabaseException {
		// TODO Auto-generated method stub
		return null;
	}
}
