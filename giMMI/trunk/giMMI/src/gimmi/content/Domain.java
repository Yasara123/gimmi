package gimmi.content;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

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
			this.rowData.put("added", new Timestamp(Calendar.getInstance()
					.getTime().getTime()));
		}
		return super.write();
	}
}
