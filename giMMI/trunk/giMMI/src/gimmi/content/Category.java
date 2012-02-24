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

	public Integer getNameById(Integer id) throws SQLException,
			CorpusDatabaseException, IllegalArgumentException {
		ResultSet categoryRS = this.getTable().find("category_id",
				id.toString());
		if (categoryRS.next()) {
			return categoryRS.getInt("category_id");
		}
		throw new CorpusDatabaseException(
				CorpusDatabaseException.Error.VALUE_NOT_FOUND,
				"The language-code you specified could not be found");
	}

	@Override
	public List<String> getAllEntries(String translation, boolean usedOnly)
			throws SQLException, CorpusDatabaseException {
		return this.simpleJoin(this.getTable(), new SiteHasCategory(
				this.database).getTable(), "category_id", "category_id",
				translation);
	}
}
