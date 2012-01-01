package gimmi.content;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;

import java.sql.SQLException;

/**
 * 
 * @author Jens Bertram <code@jens-bertram.net>
 * 
 */
public class Site extends CorpusContent {
    /** The name of the database table */
    private static final String TABLE_NAME = "sites";

    public Site(CorpusDatabase db) throws SQLException, CorpusDatabaseException {
	this.setTable(db.getTable(Site.TABLE_NAME));
    }

    @Override
    public int write() throws CorpusDatabaseException, SQLException {
	// set crawl-time to now, if not specified
	if (this.rowData.containsKey("crawl_time") == false) {
	    this.rowData.put("crawl_time", new Long(
		    System.currentTimeMillis() / 1000));
	}
	// TODO: generate a real storage path
	this.rowData.put("storage_path", "/gimmi-storage/");
	return super.write();
    }
}
