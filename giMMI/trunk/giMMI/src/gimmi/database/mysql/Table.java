package gimmi.database.mysql;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseTable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Klasse fuer Tabellen einer MySQL Datenbank
 * 
 * @author kastners
 * 
 */
public class Table implements CorpusDatabaseTable {
	String tableName;
	HashMap<String, Column> columns = new HashMap<String, Column>();
	ArrayList<String> requiredFields = new ArrayList<String>();
	CorpusDatabase database = null;
	Connection connection = null;

	public Table(CorpusDatabase database, String tableName) throws SQLException {
		this.connection = database.getConnection();
		this.database = database;
		this.setTableName(tableName);
		this.createColumns();
	}

	/**
	 * Liest alle Datensaetze aus
	 * 
	 * @throws SQLException
	 */
	public ResultSet fetchAll() throws SQLException {
		String query = "SELECT * FROM "
				+ Table.addBackticks(this.getTableName()) + ";";
		Statement statement = this.connection.createStatement();
		return statement.executeQuery(query);
	}

	/**
	 * Findet Datensaetze zu der angegebenen Bedingung
	 * 
	 * @param condition
	 * @return ResultSet
	 * @throws SQLException
	 */
	public ResultSet find(String condition) throws SQLException {
		String query = "SELECT * FROM "
				+ Table.addBackticks(this.getTableName()) + "" + "WHERE "
				+ condition;
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
	 */
	public ResultSet find(String field, String value) throws SQLException {
		String condition = this.getConditionFromFieldValuePair(field, value);
		return this.find(condition);
	}

	/**
	 * Speichert einen Datensatz in der Tabelle und gibt die erzeugte ID zurueck
	 * 
	 * @param values
	 * @return int
	 * @throws SQLException
	 */
	@Override
	public int save(HashMap<String, String> values) throws SQLException {
		StringBuffer cols = new StringBuffer();
		StringBuffer vals = new StringBuffer();
		int requiredCount = 0;
		Column column;

		Iterator<String> it = values.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			// ueberpruefung, ob der key erlaubt ist, bzw in der tabelle
			// vorhanden
			// ist
			if ((column = this.columns.get(key)) != null) {
				cols.append(Table.addBackticks(column.getColumnName()) + ", ");
				vals.append(this.escape(values.get(key), column) + ", ");
				if (column.isRequired()) {
					requiredCount++;
				}
			} else {
				// nen schicker logger waere toll!
				System.out.println("Das Feld \"" + key
						+ "\" existiert nicht in " + this.getTableName());
			}
		}
		if (requiredCount < this.requiredFields.size()) {
			System.out.println("Required Fields:");
			for (int i = 0; i < this.requiredFields.size(); i++) {
				System.out.println(this.requiredFields.get(i));
			}
			throw new SQLException(
					"Es wurden nicht alle benoetigten Felder angegeben!");
		}
		cols.delete((cols.length() - 2), cols.length());
		vals.delete((vals.length() - 2), vals.length());
		String insert = "INSERT INTO "
				+ Table.addBackticks(this.getTableName()) + "("
				+ cols.toString() + ")" + " VALUES " + "(" + vals.toString()
				+ ");";
		int id = -1;
		Statement st = this.connection.createStatement();
		try {
			st.executeUpdate(insert, Statement.RETURN_GENERATED_KEYS);
		} catch (Exception e) {
			System.out.println("Query: \n" + insert);
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
		return this.getTableName();
	}

	/**
	 * UPDATE von Werten in der Tabelle
	 * 
	 * @param where
	 * @param values
	 * @return int
	 * @throws SQLException
	 */
	public int update(String where, HashMap<String, String> values)
			throws SQLException {
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
				vals.append(Table.addBackticks(column.getColumnName()) + " = "
						+ this.escape(values.get(key), column) + ", ");
			} else {
				// nen schicker logger waere toll!
				System.out.println("Das Feld \"" + key
						+ "\" existiert nicht in " + this.getTableName());
			}
		}
		vals.delete((vals.length() - 2), vals.length());
		String update = "UPDATE " + Table.addBackticks(this.getTableName())
				+ " SET " + vals.toString() + " WHERE " + where + ";";
		Statement st = this.connection.createStatement();
		st.executeUpdate(update);
		int affectedRows = st.getUpdateCount();
		if (affectedRows < 0) {
			System.out.println("Das Update betraf mit '" + where
					+ "' keine Zeilen");
		}
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
	 */
	public int update(String field, String value, HashMap<String, String> values)
			throws SQLException {
		return this.update(this.getConditionFromFieldValuePair(field, value),
				values);
	}

	/**
	 * Speichert mehrere Datensaetze auf einmal
	 * 
	 * @param values
	 */
	public void save(ArrayList<HashMap<String, String>> values) {
		// TODO: save fuer mehrere datensaetze implementieren
	}

	/**
	 * Erzeugt zu einem Feld Wert Paar ein WHERE Statement
	 * 
	 * @param field
	 * @param value
	 * @throws SQLException
	 */
	protected String getConditionFromFieldValuePair(String field, String value)
			throws SQLException {
		Column column;
		if ((column = this.columns.get(field)) == null) {
			throw new SQLException("Feld " + field + " nicht vorhanden!");
		}
		if (value == null) {
			return Table.addBackticks(field) + " IS NULL";
		}
		return Table.addBackticks(field) + " = " + this.escape(value, column);
	}

	/**
	 * Escaped einen String ausgehend vom Spaltentyp
	 * 
	 * @param value
	 * @param column
	 * @return String
	 */
	public String escape(String value, Column column) {
		return this.escape(value, column.getColumnType());
	}

	/**
	 * Escapet einen String ausgehend vom Typ
	 * 
	 * @param value
	 * @param type
	 * @return String
	 */
	public String escape(String value, int type) {
		if (value == null) {
			return "null";
		}
		if (type == Column.INT_TYPE) {
			// hier koennte man noch alle nicht numerischen zeichen entfernen
			// oder den string parsen..
			return value;
		} else if (type == Column.TEXT_TYPE) {
			return "'" + value.replace("\\", "\\\\").replace("'", "\\'") + "'";
		}
		return value;
	}

	/**
	 * Setzt den Tabellennamen
	 * 
	 * @param name
	 * @return
	 */
	protected Table setTableName(String name) {
		this.tableName = name;
		return this;
	}

	/**
	 * Gibt den Tabellennamen zurueck
	 * 
	 * @return String
	 */
	public String getTableName() {
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
	 * Fuegt eine Spalte hinzu
	 * 
	 * @param name
	 * @param type
	 * @param flag
	 * @return
	 */
	protected Table addColumn(String name, int type, boolean flag) {
		this.columns.put(name, new Column(name, type, flag));
		if (flag == true) {
			this.requiredFields.add(name);
		}
		return this;
	}

	/**
	 * Fuegt eine Spalte hinzu
	 * 
	 * @param name
	 * @param type
	 * @param flag
	 * @return
	 */
	protected Table addColumn(String name, int type) {
		this.columns.put(name, new Column(name, type, false));
		return this;
	}

	/**
	 * Fuegt eine Spalte hinzu
	 * 
	 * @param name
	 * @param type
	 * @param flag
	 * @return
	 */
	protected Table addColumn(String name) {
		this.columns.put(name, new Column(name, Column.TEXT_TYPE, false));
		return this;
	}

	/**
	 * Ermittelt anhand der Daten eines DESCRIBEs den Feldtyp
	 * 
	 * @param type
	 * @return
	 */
	protected int getTypeByDescription(String type) {
		if (type.contains("int")) {
			return Column.INT_TYPE;
		}
		return Column.TEXT_TYPE;
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
	 */
	protected void createColumns() throws SQLException {
		ResultSet desc = this.database.getResultset("DESCRIBE "
				+ Table.addBackticks(this.getTableName()));

		while (desc.next()) {
			this.addColumn(desc.getString("Field"),
					this.getTypeByDescription(desc.getString("Type")),
					this.isRequiredByDescription(desc));
		}

	}

	@Override
	public long countRows() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM "
				+ Table.addBackticks(this.getTableName()) + ";";
		Statement statement = this.connection.createStatement();
		ResultSet rs = statement.executeQuery(query);
		rs.next();
		return rs.getLong("count");
	}
}
