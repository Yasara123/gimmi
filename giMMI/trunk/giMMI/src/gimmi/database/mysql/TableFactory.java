package gimmi.database.mysql;

import gimmi.database.CorpusDatabaseException;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * Verwaltet die Tabellenobjekte, erzeugt neue Tabellenobjekte oder gibt bereits
 * vorhandene Tabellenobjekte zurueck
 * 
 * @author kastners
 * 
 */
public class TableFactory {
	static HashMap<String, Table> tables = new HashMap<String, Table>();

	static public Table getTable(String table) throws SQLException,
			CorpusDatabaseException {
		if (TableFactory.tables.get(table) == null) {
			System.err
					.println("TableFactory function needs work. NullPointer may follow!");
			TableFactory.tables.put(table, new Table(null, table));
		}
		return TableFactory.tables.get(table);
	}
}
