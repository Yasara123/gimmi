package gimmi.database.mysql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class Row implements Iterator<String[]> {
	ArrayList<String> cols = new ArrayList<String>();
	ArrayList<String> vals = new ArrayList<String>();
	int pointer = 0;

	public Row(String[] columns, String[] values) throws SQLException {
		addCols(columns, values);
	}

	/**
	 * Fuegt eine neue Spalte hinzu
	 * 
	 * @param column
	 *            Spaltenname
	 * @param value
	 *            Spaltenwert
	 */
	public void addCol(String column, String value) {
		int index = getNextIndex();
		cols.add(index, column);
		vals.add(index, value);
	}

	/**
	 * Fuegt mehrere neue Spalte hinzu
	 * 
	 * @param columns
	 * @param values
	 * @throws SQLException
	 */
	public void addCols(String[] columns, String[] values) throws SQLException {
		
		int index = getNextIndex();
		if (columns.length != values.length) {
			throw new SQLException(
					"Die Anzahl der Spalten und die Anzahl der Values muss gleich sein!");
		}
		for (int i = 0; i < columns.length; i++) {
			cols.add(index + i, columns[i]);
			vals.add(index + i, values[i]);
		}
	}

	/**
	 * Gibt den aktuell naechsten Index Wert zurueck
	 * 
	 * @return
	 */
	protected int getNextIndex() {
		return (cols == null) ? 0 : cols.size();
	}

	/**
	 * Gibt die Anzahl von Spalten zurueck
	 * 
	 * @return int
	 */
	public int getNumOfCols() {
		return cols.size();
	}

	@Override
	public boolean hasNext() {

		return pointer < cols.size();
	}

	@Override
	public String[] next() {
		if (cols.get(pointer) != null) {
			String[] row = { cols.get(pointer), vals.get(pointer) };
			pointer++;
			return row;
		}
		else {
			pointer++;
			return null;
		}
	}

	@Override
	public void remove() {
		
	}
}