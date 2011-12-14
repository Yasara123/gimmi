package gimmi.database;

/**
 * Repraesentiert eine Spalte in einer Datenbank
 * 
 * @author kastners
 * 
 */
public class Column {
	public static int INT_TYPE = 0;
	public static int TEXT_TYPE = 1;

	protected String columnName;
	protected int columnType;
	protected boolean required;
	
	/**
	 * Konstruktor mit allen 3 Parametern
	 * @param name
	 * @param type
	 * @param flag
	 */
	public Column(String name, int type, boolean flag) {
		setColumnName(name);
		setColumnType(type);
		setRequired(flag);
	}
	
	/**
	 * Konstruktor ohne den required Parameter
	 * @param name
	 * @param type
	 */
	public Column(String name, int type) {
		this(name, type, false);
	}
	
	/**
	 * Konstruktor ohne required und ohne type Parameter
	 * @param name
	 */
	public Column(String name) {
		this(name, TEXT_TYPE, false);
	}
	
	public Column setColumnName(String name) {
		columnName = name;
		return this;
	}

	public String getColumnName() {
		return columnName;
	}

	public Column setColumnType(int type) {
		if (type != INT_TYPE && type != TEXT_TYPE) {
			System.out
					.println("Feldtyp kann nicht gesetzt werden! Ungueltiger Typ uebergeben!");
		}
		columnType = type;
		return this;
	}

	public int getColumnType() {
		return columnType;
	}

	public Column setRequired(boolean flag) {
		required = flag;
		return this;
	}

	public boolean isRequired() {
		return required;
	}

}