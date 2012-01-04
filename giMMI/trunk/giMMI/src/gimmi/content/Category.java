package gimmi.content;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;

import java.sql.SQLException;
import java.util.List;

public class Category extends CorpusContent {
	/** The name of the database table */
	public static final String TABLE_NAME = "category";

	public Category(CorpusDatabase db) throws SQLException,
			CorpusDatabaseException {
		this.database = db;
		this.setTable(db.getTable(Category.TABLE_NAME));
	}

	/**
	 * 
	 * @param translation
	 *            The translation of the category name. This must match the
	 *            corresponding table column name
	 * @param usedOnly
	 *            Get only categories that are actually used by any site entry
	 * @return A string-list with all categories found
	 * @throws CorpusDatabaseException
	 * @throws SQLException
	 */
	@Override
	public List<String> getAllEntries(String translation, boolean usedOnly)
			throws SQLException, CorpusDatabaseException {
		return this.simpleJoin(this.getTable(), new SiteHasCategory(
				this.database).getTable(), "category_id", "category_id",
				translation, usedOnly);
	}
}
