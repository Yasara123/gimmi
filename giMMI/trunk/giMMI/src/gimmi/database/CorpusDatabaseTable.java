package gimmi.database;

import gimmi.database.mysql.Column;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Interface for gimmi table objects
 * 
 * @author Jens Bertram <code@jens-bertram.net>
 * 
 */
public interface CorpusDatabaseTable {
	/** Types of database columns */
	enum columnType {
		INT, TXT, DATETIME, FLOAT, DOUBLE
	}

	public HashMap<String, Column> getColumns();

	/**
	 * Count the number of rows in this table
	 * 
	 * @return The number of rows in this table
	 */
	public long countRows() throws SQLException;

	/**
	 * Saves a table row to the database
	 * 
	 * @param values
	 * @return The row-id of the newly created row
	 * @throws SQLException
	 * @throws CorpusDatabaseException
	 */
	public int save(HashMap<String, Object> values) throws SQLException,
			CorpusDatabaseException;

	/**
	 * Fetch all rows owned by this table
	 * 
	 * @return A ResultSet containing all rows
	 * @throws SQLException
	 * @throws CorpusDatabaseException
	 */
	public ResultSet fetchAll() throws SQLException, CorpusDatabaseException;

	/**
	 * Fetch all rows owned by this table
	 * 
	 * @param fields
	 *            List of field-names to restrict the query to
	 * @return
	 * @throws SQLException
	 * @throws CorpusDatabaseException
	 */
	public ResultSet fetchAll(String[] fields) throws SQLException,
			CorpusDatabaseException;
}
