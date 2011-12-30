package gimmi.content;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;

import java.sql.SQLException;

public class Tag extends CorpusContent {
	/** The name of the database table */
	private static final String TABLE_NAME = "tag";

	/**
	 * Constructor
	 * 
	 * @param db
	 *            The database object to work with
	 * @throws SQLException
	 * @throws CorpusDatabaseException
	 */
	public Tag(CorpusDatabase db) throws SQLException, CorpusDatabaseException {
		this.setTable(db.getTable(Tag.TABLE_NAME));
	}

	// /**
	// * Add a new tag to the database.
	// *
	// * @param tagType
	// * The id of the tagType this tag belongs to
	// * @param name_en
	// * Tag name in English
	// * @param name_de
	// * Tag name in German
	// * @throws SQLException
	// * @throws CorpusDatabaseException
	// */
	// public void add(long tagTypeId, String name_en, String name_de)
	// throws SQLException, CorpusDatabaseException {
	// HashMap<String, String> data = new HashMap<String, String>(2);
	// data.put("tag_type_id", Long.toString(tagTypeId));
	// if ((name_de != null) && (name_de.trim().length() > 0)) {
	// data.put("name_de", name_de);
	// }
	// if ((name_en != null) && (name_en.trim().length() > 0)) {
	// data.put("name_en", name_en);
	// }
	// this.getTable().save(data);
	// }
}
