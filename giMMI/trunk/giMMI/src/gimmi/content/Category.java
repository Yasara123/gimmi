package gimmi.content;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;
import gimmi.database.MultilanguageContent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Category extends CorpusContentNamed {
	/** The name of the database table */
	public static final String TABLE_NAME = "category";

	public Category(CorpusDatabase db) throws SQLException,
			CorpusDatabaseException {
		this.database = db;
		this.setTable(db.getTable(Category.TABLE_NAME));
	}

	@Override
	public List<String> getAllEntries(String translation, boolean usedOnly)
			throws SQLException, CorpusDatabaseException {
		if (usedOnly) {
			// get only used entries
			return this.simpleJoin(this.getTable(), new SiteHasCategory(
					this.database).getTable(), "category_id", "category_id",
					translation);
		} else {
			// get all possible codes
			return this.resultsetToStringList(this.getTable().fetchAll(
					new String[] { translation }));
		}
	}

	@Override
	public ResultSet getAllEntries(boolean usedOnly) throws SQLException, CorpusDatabaseException {
		return this.getTable().join("category_id",
				new SiteHasCategory(this.database).getTable(), "category_id");
	}

	/**
	 * 
	 * @param parents
	 *            List of parent node id's (root to head). If parents is null,
	 *            all root nodes will be returned.
	 * @param usedOnly
	 *            If true only elements currently assigned with a site will be
	 *            returned
	 * @return
	 * @throws CorpusDatabaseException
	 * @throws SQLException
	 */
	public List<Map<Integer, String>> getMap(Integer[] parents, boolean usedOnly)
			throws SQLException, CorpusDatabaseException {
		ResultSet data;
		StringBuffer path = new StringBuffer();
		if (parents == null) {
			data = this.getTable().fetchAllWithCondition("path=''");
		} else {
			for (Integer i : parents) {
				path.append(i.toString() + ":");
			}
			data = this.getTable().fetchAllWithCondition(
					"path='" + path.substring(0, path.length() - 1) + "'");
		}
		return null;
	}

	/**
	 * Create a new category entry. Sets up the data needed to write a new
	 * category entry.
	 * 
	 * TODO: Allow the setting of a parent category
	 * 
	 * @param categoryData
	 * @param parent
	 *            Id of the parent category
	 * @throws CorpusDatabaseException
	 */
	public void create(MultilanguageContent categoryData)
			throws CorpusDatabaseException {
		for (MultilanguageContent.Lang lang : MultilanguageContent.Lang
				.values()) {
			this.setProperty("name_" + lang.toString().toLowerCase(),
					categoryData.getLangString(lang));
		}
	}
}
