package gimmi.content;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;

import java.sql.SQLException;
import java.util.List;

public class Domain extends CorpusContent {
	/** The name of the database table */
	public static final String TABLE_NAME = "domain";

	public Domain(CorpusDatabase db) throws SQLException,
			CorpusDatabaseException {
		this.database = db;
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

	@Override
	public List<String> getAllEntries(String translation, boolean usedOnly)
			throws SQLException, CorpusDatabaseException {
		return this.simpleJoin(this.getTable(),
				new Site(this.database).getTable(), "domain_id", "domain_id",
				translation);
	}
}
