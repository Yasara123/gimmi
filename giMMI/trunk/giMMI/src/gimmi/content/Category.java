package gimmi.content;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;
import gimmi.database.MultilanguageContent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
	public ResultSet getAllEntries(boolean usedOnly) throws SQLException,
			CorpusDatabaseException {
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
	public List<String[]> getMap(String translation, Integer[] parents,
			boolean usedOnly) throws SQLException, CorpusDatabaseException {
		ResultSet data;
		StringBuffer path = new StringBuffer();
		List<String[]> results = new ArrayList<String[]>();
		;
		if (parents == null) {
			data = this.getTable().fetchAllWithCondition("path=''");
		} else {
			for (Integer i : parents) {
				path.append(i.toString() + ":");
			}
			data = this.getTable().fetchAllWithCondition(
					new String[] { "category_id", "path", translation },
					"path='" + path.substring(0, path.length() - 1) + "'");
		}
		while (data.next()) {
			results.add(new String[] { data.getString(1), data.getString(2),
					data.getString(3) });
		}
		return results;
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
	public void create(MultilanguageContent categoryData, Integer parent)
			throws CorpusDatabaseException {
		for (MultilanguageContent.Lang lang : MultilanguageContent.Lang
				.values()) {
			System.out.println("name_" + lang.toString());
			this.setProperty("name_" + lang.toString().toLowerCase(),
					categoryData.getLangString(lang));
		}
		if (parent != null) {
			this.setProperty("parent_id", parent.toString());
		}
	}

	public Number getCategoryId(String categoryName) throws SQLException {
		return this.getIdByName(categoryName);
	}

	public Number getCategoryId(String categoryName, Number categoryId)
			throws SQLException {
		return this.getIdByName(categoryName, "parent_id = " + categoryId);
	}

	public Number getCategoryId(MultilanguageContent categoryName,
			Number categoryId) throws SQLException {
		return this.getIdByName(categoryName, "parent_id = " + categoryId);
	}
}
