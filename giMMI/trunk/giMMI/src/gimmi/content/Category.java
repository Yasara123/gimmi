package gimmi.content;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;

import java.sql.SQLException;

public class Category extends CorpusContent {
	/** The name of the database table */
	private static final String TABLE_NAME = "category";

	public Category(CorpusDatabase db) throws SQLException,
			CorpusDatabaseException {
		this.setTable(db.getTable(Category.TABLE_NAME));
	}
}
