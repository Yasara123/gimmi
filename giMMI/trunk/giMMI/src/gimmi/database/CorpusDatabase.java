package gimmi.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface CorpusDatabase {
	public CorpusDatabaseTable getTable(String name) throws SQLException,
			CorpusDatabaseException;

	public Connection getConnection();

	public ResultSet getResultset(String query);
}
