package gimmi.database;

import java.sql.SQLException;
import java.util.HashMap;

public interface CorpusDatabaseTable {

	/**
	 * Count the number of rows in this table
	 * 
	 * @return The number of rows in this table
	 */
	public long countRows() throws SQLException;

	public int save(HashMap<String, String> values) throws SQLException;
}
