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
	public void write() throws CorpusDatabaseException, SQLException {
		super.write();
	}
}
