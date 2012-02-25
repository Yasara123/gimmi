package gimmi.content;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;
import gimmi.database.CorpusDatabaseTable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * General abstract class for corpus content tables
 * 
 * @author Jens Bertram <code@jens-bertram.net>
 * 
 */
public abstract class CorpusContent {
	protected CorpusDatabase database;
	protected CorpusDatabaseTable table;
	protected HashMap<String, Object> rowData;

	/**
	 * Set the database table associated with this CorpusContent
	 * 
	 * @param table
	 *            The database table
	 * @return The database table given as parameter
	 */
	protected void setTable(CorpusDatabaseTable table) {
		this.table = table;
		this.rowData = new HashMap<String, Object>(this.getTable().getColumns()
				.size());
	}

	/**
	 * Get the database table associated with this CorpusContent
	 * 
	 * @return The database table associated with this CorpusContent
	 */
	public CorpusDatabaseTable getTable() {
		return this.table;
	}

	/**
	 * Get the database associated with this CorpusContent
	 * 
	 * @return The database associated with this CorpusContent
	 */
	public CorpusDatabase getDatabase() {
		return this.database;
	}

	/**
	 * Check if a field exists in the current table
	 * 
	 * @param name
	 *            The field name to check for
	 * @throws CorpusDatabaseException
	 *             Thrown if field is not valid
	 */
	protected void checkField(String name) throws CorpusDatabaseException {
		if (this.getTable().getColumns().containsKey(name) != true) {
			throw new CorpusDatabaseException(
					CorpusDatabaseException.Error.COLUMN_NOT_FOUND,
					this.getTable() + "." + name);
		}
	}

	/**
	 * Set object field data for writing to the database. Implementing objects
	 * must overwrite this function to implement a storing of this data.
	 * 
	 * @param name
	 *            The name of the property to set
	 * @param data
	 *            The content of the property
	 * @throws CorpusDatabaseException
	 *             Thrown if the property does not exist in the database
	 */
	public void setProperty(String name, Object data)
			throws CorpusDatabaseException {
		// check if field is valid
		this.checkField(name);
		this.rowData.put(name, data);
	}

	/**
	 * Set object field data for writing to the database.
	 * 
	 * @param data
	 *            A Hashmap with property key-value pairs
	 * @throws CorpusDatabaseException
	 *             Thrown if a property does not exist in the database
	 */
	public void setProperties(Map<String, Object> data)
			throws CorpusDatabaseException {
		// check if fields are valid
		for (String name : data.keySet()) {
			this.checkField(name);
		}
		this.rowData.putAll(data);
	}

	public int write() throws CorpusDatabaseException, SQLException {
		return this.getTable().save(this.rowData);
	}

	protected List<String> simpleJoin(CorpusDatabaseTable tLeft,
			CorpusDatabaseTable tRight, String joinLeft, String joinRight,
			String translation) throws SQLException, CorpusDatabaseException {
		if (this.getTable().getColumns().keySet().contains(translation) == false) {
			return null;
		}

		ArrayList<String> results = new ArrayList<String>();
		ResultSet rs = tLeft.join(joinLeft, tRight, joinRight);
		while (rs.next()) {
			results.add(rs.getString(translation));
		}
		return results;
	}

	protected List<String> resultsetToStringList(ResultSet rs)
			throws SQLException {
		ArrayList<String> results = new ArrayList<String>();
		while (rs.next()) {
			results.add(rs.getString(1));
		}
		return results;
	}

	/**
	 * Check if the id field contains the given id. The search field will
	 * default to the name of the table returned by
	 * <code>getTable().getName()</code> with <code>_id</code> as suffix added.
	 * 
	 * @param id
	 * @return
	 */
	protected boolean hasId(Number id) {
		try {
			if (this.getTable()
					.find(this.getTable().getName().toLowerCase() + "_id",
							id.toString()).first()) {
				return true;
			}
		} catch (SQLException | CorpusDatabaseException e) {
			return false;
		}
		return false;
	}

	/**
	 * Get the id for an entry by the content of a specific field. The search
	 * field will default to the name of the table returned by
	 * <code>getTable().getName()</code> with <code>_id</code> as suffix added.
	 * 
	 * @param field
	 *            The field to find the content in
	 * @param content
	 *            Content to search for
	 * @return Id of the row where the content was found, or null if nothing was
	 *         found
	 * @throws SQLException
	 * @throws CorpusDatabaseException
	 */
	protected Number getIdByField(String field, String content)
			throws SQLException, CorpusDatabaseException {
		return this.getIdByField(this.getTable().getName().toLowerCase()
				+ "_id", field, content);
	}

	/**
	 * Get the id for an entry by the content of a specific field.
	 * 
	 * @param idField
	 *            Field that contains the row id to return
	 * @param field
	 *            The field to find the content in
	 * @param content
	 *            Content to search for
	 * @return Id of the row where the content was found, or <code>null</code>
	 *         if nothing was found
	 * @throws SQLException
	 * @throws CorpusDatabaseException
	 */
	protected Number getIdByField(String idField, String field, String content)
			throws SQLException, CorpusDatabaseException {
		ResultSet languageRS = this.getTable().find(field, content);
		if (languageRS.next()) {
			return languageRS.getInt(idField);
		}
		return null;
	}
}
