package gimmi.content;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;
import gimmi.database.MultilanguageContent;

import java.sql.ResultSet;
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

	public boolean hasId(Number id) {
		try {
			if (this.getTable().find("category_id", id.toString()).first()) {
				return true;
			}
		} catch (SQLException | CorpusDatabaseException e) {
			return false;
		}
		return false;
	}

	/**
	 * FIXME: parameters are _unchecked_ passed to database
	 * 
	 * @param name
	 * @param language
	 * @return
	 * @throws SQLException
	 * @throws CorpusDatabaseException
	 * @throws IllegalArgumentException
	 */
	public Number getIdByName(MultilanguageContent name) throws SQLException,
			CorpusDatabaseException, IllegalArgumentException {
		ResultSet categoryRS = null;
		for (MultilanguageContent.Lang lang : MultilanguageContent.Lang
				.values()) {
			if (name.getLangString(lang) != null) {
				categoryRS = this.getTable().find(
						"name_" + lang.toString().toLowerCase() + "='"
								+ name.getLangString(lang) + "'");
			}
		}
		if ((categoryRS != null) && categoryRS.next()) {
			return categoryRS.getInt("category_id");
		}
		return null;
	}

	@Override
	public List<String> getAllEntries(String translation, boolean usedOnly)
			throws SQLException, CorpusDatabaseException {
		return this.simpleJoin(this.getTable(), new SiteHasCategory(
				this.database).getTable(), "category_id", "category_id",
				translation);
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
