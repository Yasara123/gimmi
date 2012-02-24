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

	/**
	 * Searches for rows matching the given condition
	 * 
	 * @param condition
	 *            An expression for the MySQL WHERE clause
	 * @return
	 * @throws SQLException
	 */
	public ResultSet find(String condition) throws SQLException;

	/**
	 * Searches for rows matching the given field->value condition
	 * 
	 * @param field
	 *            The field to search for
	 * @param value
	 *            The value the given field should have
	 * @return
	 * @throws SQLException
	 * @throws CorpusDatabaseException
	 */
	public ResultSet find(String field, String value) throws SQLException,
			CorpusDatabaseException;

	/**
	 * First table is the current one.
	 * 
	 * @param field1
	 * @param table2
	 * @param field2
	 * @return
	 * @throws SQLException
	 */
	public ResultSet join(String field1, CorpusDatabaseTable table2,
			String field2) throws SQLException;

	public ResultSet join(CorpusDatabaseTable table1, String field1,
			CorpusDatabaseTable table2, String field2) throws SQLException;

	/**
	 * 
	 * @param table2
	 * @param field1
	 * @param field2
	 * @param condition
	 *            The condition used as WHERE clause in the SQL statement. This
	 *            string will be used as is without any quoting!
	 * @return
	 * @throws SQLException
	 */
	public ResultSet joinWithCondition(CorpusDatabaseTable table1,
			String field1, CorpusDatabaseTable table2, String field2,
			String condition) throws SQLException;

	/**
	 * Join with additional WHERE condition. First table is the current one.
	 * 
	 * @param table2
	 * @param field1
	 * @param field2
	 * @param condition
	 *            The condition used as WHERE clause in the SQL statement. This
	 *            string will be used as is without any quoting!
	 * @return
	 * @throws SQLException
	 */
	public ResultSet joinWithCondition(String field1,
			CorpusDatabaseTable table2, String field2, String condition)
			throws SQLException;

	public String getName();
}
