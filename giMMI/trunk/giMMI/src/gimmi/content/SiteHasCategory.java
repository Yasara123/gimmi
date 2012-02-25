package gimmi.content;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;

import java.sql.SQLException;

public class SiteHasCategory extends CorpusContent {
	/** The name of the database table */
	public static final String TABLE_NAME = "site_has_category";

	public SiteHasCategory(CorpusDatabase db) throws SQLException,
			CorpusDatabaseException {
		this.database = db;
		this.setTable(db.getTable(SiteHasCategory.TABLE_NAME));
	}
}
