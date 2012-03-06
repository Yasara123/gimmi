package gimmi.content;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;
import gimmi.database.CorpusDatabaseTable;
import gimmi.database.MultilanguageContent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * 
 * @author Jens Bertram <code@jens-bertram.net>
 * 
 */
public class Site extends CorpusContent {
	/** The name of the database table */
	public static final String TABLE_NAME = "site";

	public Site(CorpusDatabase db) throws SQLException, CorpusDatabaseException {
		this.database = db;
		this.setTable(db.getTable(Site.TABLE_NAME));
	}

	@Override
	public int write() throws CorpusDatabaseException, SQLException {
		// set crawl-time to now, if not specified
		if (this.rowData.containsKey("crawl_time") == false) {
			this.rowData.put("crawl_time", new Timestamp(Calendar.getInstance()
					.getTime().getTime()));
		}
		return super.write();
	}

	public MultilanguageContent getCategoryNameById(Number id)
			throws SQLException, CorpusDatabaseException {
		CorpusDatabaseTable categoryTable = new Category(this.database)
				.getTable();
		CorpusDatabaseTable siteCategoryTable = new SiteHasCategory(
				this.database).getTable();
		ResultSet categoryRS = this.getTable().joinWithCondition(
				siteCategoryTable, "category_id", categoryTable, "category_id",
				"site_id=" + id + "");
		MultilanguageContent names = new MultilanguageContent();
		if (categoryRS.next()) {
			for (MultilanguageContent.Lang lang : MultilanguageContent.Lang
					.values()) {
				names.setLangString(
						lang,
						categoryRS.getString("name_"
								+ lang.toString().toLowerCase()));
			}
			return names;
		}
		return null;
	}
}
