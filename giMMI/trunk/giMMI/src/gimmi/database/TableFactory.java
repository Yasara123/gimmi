package gimmi.database;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * Verwaltet die Tabellenobjekte, erzeugt neue Tabellenobjekte oder gibt bereits 
 * vorhandene Tabellenobjekte zurueck
 * @author kastners
 *
 */
public class TableFactory {
	static HashMap<String, Table> tables = new HashMap<String, Table>();
	
	static public Table getTable(String table) throws SQLException {
		if(tables.get(table) == null) {
			tables.put(table, new Table(table));
		}
		return tables.get(table);
	}
}
