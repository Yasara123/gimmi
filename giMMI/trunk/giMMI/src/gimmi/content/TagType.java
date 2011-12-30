package gimmi.content;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseTable;

import java.sql.SQLException;
import java.util.HashMap;

public class TagType extends CorpusContent {
	private static final String TABLE_NAME = "tagType";
	protected CorpusDatabaseTable table = null;

	/**
	 * Constructor
	 * 
	 * @param db
	 *            The database object to work with
	 * @throws SQLException
	 */
	public TagType(CorpusDatabase db) throws SQLException {
		this.setTable(db.getTable(TagType.TABLE_NAME));
	}

	/**
	 * Add a new tag to the database.
	 * 
	 * @param name_en
	 *            Tag name in English
	 * @param name_de
	 *            Tag name in German
	 * @throws SQLException
	 */
	public void add(String name_en, String name_de) throws SQLException {
		HashMap<String, String> data = new HashMap<String, String>(2);
		data.put("name_en", name_en);
		if (name_de != null) {
			data.put("name_de", name_de);
		}
		this.getTable().save(data);
	}

	/**
	 * Add a new tag to the database.
	 * 
	 * @param name_en
	 *            Tag name in English
	 * @throws SQLException
	 */
	public void add(String name_en) throws SQLException {
		this.add(name_en, null);
	}
}
