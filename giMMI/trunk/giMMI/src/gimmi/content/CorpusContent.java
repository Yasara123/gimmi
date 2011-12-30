package gimmi.content;

import gimmi.database.CorpusDatabaseTable;

import java.sql.SQLException;

/**
 * 
 * @author Jens Bertram <code@jens-bertram.net>
 * 
 */
public abstract class CorpusContent {
	protected CorpusDatabaseTable table;

	protected CorpusDatabaseTable setTable(CorpusDatabaseTable table) {
		this.table = table;
		return table;
	}

	protected CorpusDatabaseTable getTable() {
		return this.table;
	}

	/**
	 * Get the overall number of rows currently available in the database for
	 * this CorpusContent type
	 * 
	 * @return The number of tags as long value
	 * @throws SQLException
	 */
	public long count() throws SQLException {
		return this.getTable().countRows();
	}
}
