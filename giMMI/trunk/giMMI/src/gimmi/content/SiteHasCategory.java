package gimmi.content;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;

import java.sql.SQLException;
import java.util.List;

public class SiteHasCategory extends CorpusContent {
	/** The name of the database table */
	public static final String TABLE_NAME = "site_has_category";

	public SiteHasCategory(CorpusDatabase db) throws SQLException,
			CorpusDatabaseException {
		this.database = db;
		this.setTable(db.getTable(SiteHasCategory.TABLE_NAME));
	}

	@Override
	public List<String> getAllEntries(String translation, boolean usedOnly)
			throws SQLException, CorpusDatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

}
