package gimmi.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Klasse fuer Tabellen einer MySQL Datenbank
 * @author kastners
 *
 */
public class Table {
	String tableName;
	HashMap<String, Column> columns = new HashMap<String, Column>();
	ArrayList<String> requiredFields = new ArrayList<String>();
	MysqlDao dao = MysqlDao.getInstance();
	Connection connection = dao.getConnection();

	public Table(String tableName) throws SQLException {
		setTableName(tableName);
		createColumns();
	}
	
	/**
	 * Liest alle Datensaetze aus
	 * @throws SQLException 
	 */
	public ResultSet fetchAll() throws SQLException {
		String query = "SELECT * FROM " + addBackticks(getTableName()) + ";";
		Statement statement = connection.createStatement();
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
		String query = "SELECT * FROM " + addBackticks(getTableName()) + ""
				+ "WHERE " + condition;
		Statement statement = connection.createStatement();
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
		String condition = getConditionFromFieldValuePair(field, value);
		return find(condition);
	}

	/**
	 * Speichert einen Datensatz in der Tabelle und gibt die erzeugte ID zurueck
	 * 
	 * @param values
	 * @return int
	 * @throws SQLException
	 */
	public int save(HashMap<String, String> values) throws SQLException {
		StringBuffer cols = new StringBuffer();
		StringBuffer vals = new StringBuffer();
		int requiredCount = 0;
		Column column;

		Iterator<String> it = values.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			// ueberpruefung, ob der key erlaubt ist, bzw in der tabelle vorhanden
			// ist
			if ((column = columns.get(key)) != null) {
				cols.append(addBackticks(column.getColumnName()) + ", ");
				vals.append(escape(values.get(key), column) + ", ");
				if (column.isRequired()) {
					requiredCount++;
				}
			} else {
				// nen schicker logger waere toll!
				System.out.println("Das Feld \"" + key
						+ "\" existiert nicht in " + getTableName());
			}
		}
		if (requiredCount < requiredFields.size()) {
			System.out.println("Required Fields:");
			for(int i=0;i<requiredFields.size();i++) {
				System.out.println(requiredFields.get(i));
			}
			throw new SQLException("Es wurden nicht alle benoetigten Felder angegeben!");
		}
		cols.delete((cols.length() - 2), cols.length());
		vals.delete((vals.length() - 2), vals.length());
		String insert = "INSERT INTO " + addBackticks(getTableName()) + "("
				+ cols.toString() + ")" + " VALUES " + "(" + vals.toString()
				+ ");";
		int id = -1;
		Statement st = connection.createStatement();
		try {
			st.executeUpdate(insert, Statement.RETURN_GENERATED_KEYS);
		}
		catch(Exception e) {
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
	
	/**
	 * UPDATE von Werten in der Tabelle
	 * @param where
	 * @param values
	 * @return int
	 * @throws SQLException 
	 */
	public int update(String where, HashMap<String, String> values) throws SQLException {
		//gibt -1 zurück, wenn keine werte für das update angegeben wurden
		if(values.isEmpty())
			return -1;
		
		StringBuffer vals = new StringBuffer();
		Column column;

		Iterator<String> it = values.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			// ueberpruefung, ob der key erlaubt ist, bzw in der tabelle vorhanden
			// ist
			if ((column = columns.get(key)) != null) {
				vals.append(addBackticks(column.getColumnName()) + " = " 
							+ escape(values.get(key), column) + ", ");
			} else {
				// nen schicker logger waere toll!
				System.out.println("Das Feld \"" + key
						+ "\" existiert nicht in " + getTableName());
			}
		}
		vals.delete((vals.length() - 2), vals.length());
		String update = "UPDATE " + addBackticks(getTableName()) + 
						" SET "
						+ vals.toString() + 
						" WHERE " + where + ";";
		Statement st = connection.createStatement();
		st.executeUpdate(update);
		int affectedRows = st.getUpdateCount();
		if (affectedRows < 0) {
			System.out.println("Das Update betraf mit '" + where + "' keine Zeilen");
		}
		return affectedRows;
	}
	
	/**
	 * Update von Werten in der Tabelle
	 * @param field
	 * @param value
	 * @param values
	 * @return int
	 * @throws SQLException
	 */
	public int update(String field, String value, HashMap<String, String> values) throws SQLException {
		return update(getConditionFromFieldValuePair(field, value), values);
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
	 * @param field
	 * @param value
	 * @throws SQLException 
	 */
	protected String getConditionFromFieldValuePair(String field, String value) throws SQLException {
		Column column;
		if ((column = columns.get(field)) == null) {
			throw new SQLException("Feld " + field + " nicht vorhanden!");
		}
		if(value == null) {
			return addBackticks(field) + " IS NULL";
		}
		return addBackticks(field) + " = " + escape(value, column);
	}
	
	/**
	 * Escaped einen String ausgehend vom Spaltentyp
	 * 
	 * @param value
	 * @param column
	 * @return String
	 */
	public String escape(String value, Column column) {
		return escape(value, column.getColumnType());
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
		tableName = name;
		return this;
	}

	/**
	 * Gibt den Tabellennamen zurueck
	 * 
	 * @return String
	 */
	public String getTableName() {
		return tableName;
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
		columns.put(name, new Column(name, type, flag));
		if (flag == true) {
			requiredFields.add(name);
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
		columns.put(name, new Column(name, type, false));
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
		columns.put(name, new Column(name, Column.TEXT_TYPE, false));
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
				&& desc.getString("Default") == null && !desc
				.getString("Extra").equals("auto_increment"));
	}

	/**
	 * Erstellt die Spalten auf Basis eines DESCRIBEs
	 * @throws SQLException 
	 */
	protected void createColumns() throws SQLException {
		ResultSet desc = dao.getResultset("DESCRIBE "
				+ addBackticks(getTableName()));

		while (desc.next()) {
			addColumn(desc.getString("Field"), getTypeByDescription(desc
					.getString("Type")), isRequiredByDescription(desc));
		}

	}
}
