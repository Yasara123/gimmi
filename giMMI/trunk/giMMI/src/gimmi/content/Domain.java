package gimmi.content;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;

import java.sql.SQLException;

public class Domain extends CorpusContent {
	/** The name of the database table */
	private static final String TABLE_NAME = "domain";

	public Domain(CorpusDatabase db) throws SQLException,
			CorpusDatabaseException {
		this.setTable(db.getTable(Domain.TABLE_NAME));
	}

	@Override
	public int write() throws CorpusDatabaseException, SQLException {
		// set current time as timestamp, if not explicitly given
		if (this.rowData.containsKey("added") == false) {
			this.rowData.put("added", new Long(
					System.currentTimeMillis() / 1000));
		}
		return super.write();
	}
}
