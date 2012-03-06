package gimmi.database.mysql;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;
import gimmi.database.CorpusDatabaseTable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class handling MySQL tables
 * 
 * @author kastners
 * @author Jens Bertram <code@jens-bertram.net>
 * 
 */
public class Table implements CorpusDatabaseTable {
	String tableName;
	HashMap<String, Column> columns = new HashMap<String, Column>();
	ArrayList<String> requiredFields = new ArrayList<String>();
	CorpusDatabase database = null;
	Connection connection = null;
	private static final Pattern PATTERN_COLSIZE = Pattern.compile("([0-9]+)");

	public Table(CorpusDatabase database, String tableName)
			throws SQLException, CorpusDatabaseException {
		this.connection = database.getConnection();
		this.database = database;
		this.setTableName(tableName);
		this.createColumns();
	}

	@Override
	public ResultSet fetchAll() throws SQLException, CorpusDatabaseException {
		return this.fetchAll(new String[] { "*" });
	}

	@Override
	public ResultSet fetchAll(String[] fields) throws SQLException,
			CorpusDatabaseException {
		return this.fetchAllWithCondition(fields, null);
	}

	@Override
	public ResultSet fetchAllWithCondition(String condition)
			throws SQLException, CorpusDatabaseException {
		return this.fetchAllWithCondition(new String[] { "*" }, condition);
	}

	@Override
	public ResultSet fetchAllWithCondition(String[] fields, String condition)
			throws SQLException, CorpusDatabaseException {
		// pre-check
		for (String col : fields) {
			if (this.columns.get(col) == null) {
				throw new CorpusDatabaseException(
						CorpusDatabaseException.Error.COLUMN_NOT_FOUND, col);
			}
		}
		// build field list
		String fieldList = new String();
		for (String field : fields) {
			fieldList = fieldList + Table.addBackticks(field) + ",";
		}
		// query
		String query = "SELECT "
				+ fieldList.substring(0, fieldList.length() - 1) + " FROM "
				+ Table.addBackticks(this.getName());
		if (condition != null) {
			query += " WHERE " + condition;
		}
		query += ";";
		// System.out.println(query);
		Statement statement = this.connection.createStatement();

		return statement.executeQuery(query);
	}

	/**
	 * Search for rows based on the given condition
	 * 
	 * @param condition
	 *            The condition used as WHERE clause in the SQL statement. This
	 *            string will be used as is without any quoting!
	 * @return ResultSet The rows matching the given condition
	 * @throws SQLException
	 */
	@Override
	public ResultSet find(String condition) throws SQLException {
		String query = "SELECT * FROM " + Table.addBackticks(this.getName())
				+ "" + "WHERE " + condition;
		Statement statement = this.connection.createStatement();
		return statement.executeQuery(query);
	}

	/**
	 * Findet Datensaetze zu einem Key Value Paar
	 * 
	 * @param field
	 * @param value
	 * @return ResultSet
	 * @throws SQLException
	 * @throws CorpusDatabaseException
	 */
	@Override
	public ResultSet find(String field, String value) throws SQLException,
			CorpusDatabaseException {
		String condition = this.getConditionFromFieldValuePair(field, value);
		return this.find(condition);
	}

	@Override
	public int save(HashMap<String, Object> values) throws SQLException,
			CorpusDatabaseException {
		StringBuffer sqlColString = new StringBuffer();
		StringBuffer sqlValString = new StringBuffer();
		int requiredCols = 0;
		Column column;

		// check & build column > value order
		for (String key : values.keySet()) {
			column = this.columns.get(key);
			if (column != null) {
				sqlColString.append(Table.addBackticks(column.toString())
						+ ", ");
				sqlValString.append(this.escape(values.get(key),
						column.getColumnType())
						+ ", ");

				// collect required rows
				if (column.isRequired()) {
					requiredCols++;
				}
			} else {
				// a row was specified, but not found in the database
				throw new CorpusDatabaseException(
						CorpusDatabaseException.Error.COLUMN_NOT_FOUND, key);
			}
		}

		// test, if all required fields are set
		if (requiredCols < this.requiredFields.size()) {
			String fields = "";
			for (String field : this.requiredFields) {
				fields = fields + field + ", ";
			}
			fields = fields.substring(0, fields.length() - 2);
			throw new CorpusDatabaseException(
					CorpusDatabaseException.Error.FIELDS_MISSING, fields,
					values.keySet().toString());
		}

		sqlColString.delete((sqlColString.length() - 2), sqlColString.length());
		sqlValString.delete((sqlValString.length() - 2), sqlValString.length());
		String insert = "INSERT INTO " + Table.addBackticks(this.getName())
				+ "(" + sqlColString.toString() + ")" + " VALUES " + "("
				+ sqlValString.toString() + ");";
		int id = -1;
		Statement st = this.connection.createStatement();
		try {
			st.executeUpdate(insert, Statement.RETURN_GENERATED_KEYS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ResultSet rs = st.getGeneratedKeys();
		if (rs.next()) {
			id = rs.getInt(1);
		} else {
			// TODO: mehr info einbauen: welche felder wurden nicht angegeben?
			throw new SQLException(
					"Es konnt kein Auto Increment Wert gefunden werden!");
		}
		return id;
	}

	@Override
	public String toString() {
		return this.getName();
	}

	/**
	 * UPDATE von Werten in der Tabelle
	 * 
	 * @param where
	 * @param values
	 * @return int
	 * @throws SQLException
	 * @throws CorpusDatabaseException
	 */
	public int update(String where, HashMap<String, String> values)
			throws SQLException, CorpusDatabaseException {
		// gibt -1 zurück, wenn keine werte für das update angegeben wurden
		if (values.isEmpty()) {
			return -1;
		}

		StringBuffer vals = new StringBuffer();
		Column column;

		Iterator<String> it = values.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			// ueberpruefung, ob der key erlaubt ist, bzw in der tabelle
			// vorhanden
			// ist
			if ((column = this.columns.get(key)) != null) {
				vals.append(Table.addBackticks(column.toString()) + " = "
						+ this.escape(values.get(key), column.getColumnType())
						+ ", ");
			} else {
				throw new CorpusDatabaseException(
						CorpusDatabaseException.Error.COLUMN_NOT_FOUND, key,
						this.getName());
			}
		}
		vals.delete((vals.length() - 2), vals.length());
		String update = "UPDATE " + Table.addBackticks(this.getName())
				+ " SET " + vals.toString() + " WHERE " + where + ";";
		Statement st = this.connection.createStatement();
		st.executeUpdate(update);
		int affectedRows = st.getUpdateCount();
		// if (affectedRows < 0) {
		// System.out.println("Das Update betraf mit '" + where
		// + "' keine Zeilen");
		// }
		return affectedRows;
	}

	/**
	 * Update von Werten in der Tabelle
	 * 
	 * @param field
	 * @param value
	 * @param values
	 * @return int
	 * @throws SQLException
	 * @throws CorpusDatabaseException
	 */
	public int update(String field, String value, HashMap<String, String> values)
			throws SQLException, CorpusDatabaseException {
		return this.update(this.getConditionFromFieldValuePair(field, value),
				values);
	}

	/**
	 * Erzeugt zu einem Feld Wert Paar ein WHERE Statement
	 * 
	 * @param field
	 * @param value
	 * @throws SQLException
	 * @throws CorpusDatabaseException
	 */
	protected String getConditionFromFieldValuePair(String field, String value)
			throws CorpusDatabaseException {
		Column column;
		if ((column = this.columns.get(field)) == null) {
			throw new CorpusDatabaseException(
					CorpusDatabaseException.Error.COLUMN_NOT_FOUND, field);
		}
		if (value == null) {
			return Table.addBackticks(field) + " IS NULL";
		}
		return Table.addBackticks(field) + " = "
				+ this.escape(value, column.getColumnType());
	}

	@Override
	public String escape(Object value, CorpusDatabaseTable.columnType type) {
		if (value == null) {
			return "null";
		}
		switch (type) {
		case DATETIME:
			return "'" + value.toString() + "'";
		case INT:
			if (value.getClass() == Timestamp.class) {
				return ((Timestamp) value).toString();
			}
			return Integer.toString(Integer.parseInt(value.toString()));
		case TXT:
		default:
			return "'"
					+ value.toString().replace("\\", "\\\\")
							.replace("'", "\\'") + "'";
		}
	}

	/**
	 * Setzt den Tabellennamen
	 * 
	 * @param name
	 * @return
	 */
	private Table setTableName(String name) {
		this.tableName = name;
		return this;
	}

	/**
	 * Gibt den Tabellennamen zurueck
	 * 
	 * @return String
	 */
	@Override
	public String getName() {
		return this.tableName;
	}

	/**
	 * Setzt backticks um einen String
	 * 
	 * @param field
	 * @return String
	 */
	static public String addBackticks(String field) {
		return "`" + field + "`";
	}

	/**
	 * Adds a column to the list
	 * 
	 * @param name
	 *            The name of the column
	 * @param type
	 *            The type as defined by {@link CorpusDatabaseTable.columnType}
	 * @param flag
	 *            Denotes, if the column is required
	 * @throws CorpusDatabaseException
	 */
	protected void addColumn(String name, CorpusDatabaseTable.columnType type,
			int size, boolean flag) throws CorpusDatabaseException {
		this.columns.put(name, new Column(name, type, size, flag));
		if (flag == true) {
			this.requiredFields.add(name);
		}
	}

	/**
	 * Tries to guess the type of a column by MySQL DESCRIBE results
	 * 
	 * TODO: add more datatypes
	 * 
	 * @param type
	 *            The type as given by MySQL DESCRIBE
	 * @return The guessed datatype
	 */
	protected CorpusDatabaseTable.columnType getTypeByDescription(String type) {
		if (type.matches("^((tiny|small|medium|big)int|int|integer).*")) {
			return CorpusDatabaseTable.columnType.INT;
		} else if (type.matches("^(date|time|datetime).*")) {
			return CorpusDatabaseTable.columnType.DATETIME;
		} else if (type.matches("^float.*")) {
			return CorpusDatabaseTable.columnType.FLOAT;
		} else if (type.matches("^double.*")) {
			return CorpusDatabaseTable.columnType.DOUBLE;
		}
		return CorpusDatabaseTable.columnType.TXT;
	}

	/**
	 * Roughly guess the size of a column by it's description
	 * 
	 * TODO: is this needed?
	 * 
	 * @param type
	 *            The description string of a table column
	 * @return The size of the column or -1 if the size couldn't be estimated
	 */
	protected int getSizeByDescription(String description) {
		Matcher m = Table.PATTERN_COLSIZE.matcher(description);
		return m.find() ? Integer.parseInt(m.group(1)) : -1;
	}

	/**
	 * Ermittelt anhand eines ResultSets zu einem DESCRIBE, ob ein Feld required
	 * ist oder nicht
	 * 
	 * @param desc
	 * @return
	 * @throws SQLException
	 */
	protected boolean isRequiredByDescription(ResultSet desc)
			throws SQLException {
		return (desc.getString("NULL").equals("NO")
				&& (desc.getString("Default") == null) && !desc.getString(
				"Extra").equals("auto_increment"));
	}

	/**
	 * Erstellt die Spalten auf Basis eines DESCRIBEs
	 * 
	 * @throws SQLException
	 * @throws CorpusDatabaseException
	 */
	protected void createColumns() throws SQLException, CorpusDatabaseException {
		ResultSet desc = this.database.getResultset("DESCRIBE "
				+ Table.addBackticks(this.getName()));

		while (desc.next()) {
			this.addColumn(desc.getString("Field"),
					this.getTypeByDescription(desc.getString("Type")),
					this.getSizeByDescription(desc.getString("Type")),
					this.isRequiredByDescription(desc));
		}

	}

	@Override
	public long countRows() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM "
				+ Table.addBackticks(this.getName()) + ";";
		Statement statement = this.connection.createStatement();
		ResultSet rs = statement.executeQuery(query);
		rs.next();
		return rs.getLong("count");
	}

	@Override
	public HashMap<String, Column> getColumns() {
		return this.columns;
	}

	@Override
	public ResultSet join(String field1, CorpusDatabaseTable table2,
			String field2) throws SQLException {
		String query = String.format(
				"SELECT * FROM %s INNER JOIN %s ON %s",
				Table.addBackticks(this.getName()),//
				Table.addBackticks(table2.getName()),//
				Table.addBackticks(this.getName()) + "."
						+ Table.addBackticks(field1) + "="
						+ Table.addBackticks(table2.getName()) + "."
						+ Table.addBackticks(field2));
		Statement statement = this.connection.createStatement();
		return statement.executeQuery(query);
	}

	@Override
	public ResultSet joinWithCondition(String field1,
			CorpusDatabaseTable table2, String field2, String condition)
			throws SQLException {
		return this.joinWithCondition(this, field1, table2, field2, condition);
	}

	@Override
	public ResultSet join(CorpusDatabaseTable table1, String field1,
			CorpusDatabaseTable table2, String field2) throws SQLException {
		return this.joinWithCondition(table1, field1, table2, field2, null);
	}

	@Override
	public ResultSet joinWithCondition(CorpusDatabaseTable table1,
			String field1, CorpusDatabaseTable table2, String field2,
			String condition) throws SQLException {
		String queryBase = "SELECT * FROM %s INNER JOIN %s ON %s";
		if (condition != null) {
			queryBase += " WHERE " + condition;
		}
		String query = String.format(
				queryBase,
				Table.addBackticks(table1.getName()),//
				Table.addBackticks(table2.getName()),//
				Table.addBackticks(table1.getName()) + "."
						+ Table.addBackticks(field1) + "="
						+ Table.addBackticks(table2.getName()) + "."
						+ Table.addBackticks(field2));
		Statement statement = this.connection.createStatement();
		return statement.executeQuery(query);
	}
}
