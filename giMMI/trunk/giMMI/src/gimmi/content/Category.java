package gimmi.content;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;

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

	public boolean hasId(Integer id) {
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
	public Integer getIdByName(String name) throws SQLException,
			CorpusDatabaseException, IllegalArgumentException {
		ResultSet categoryRS = this.getTable().find(
				"name_en='" + name + "' OR name_de='" + name + "'");
		if (categoryRS.next()) {
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
}
