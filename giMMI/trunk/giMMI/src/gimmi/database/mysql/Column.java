package gimmi.database.mysql;

import gimmi.database.CorpusDatabaseException;
import gimmi.database.CorpusDatabaseTable;

/**
 * Repraesentiert eine Spalte in einer Datenbank
 * 
 * @author kastners
 * 
 */
public class Column {
	protected String columnName;
	protected CorpusDatabaseTable.columnType columnType;
	protected boolean required;

	/** size of this column */
	private int size = -1;

	public Column(String name, CorpusDatabaseTable.columnType type, int size,
			boolean flag) throws CorpusDatabaseException {
		this.setColumnName(name);
		this.setColumnType(type);
		this.setRequired(flag);
		this.setSize(size);
	}

	public Column(String name, CorpusDatabaseTable.columnType type, boolean flag)
			throws CorpusDatabaseException {
		this.setColumnName(name);
		this.setColumnType(type);
		this.setRequired(flag);
	}

	public Column setColumnName(String name) {
		this.columnName = name;
		return this;
	}

	@Override
	public String toString() {
		return this.columnName;
	}

	public void setColumnType(CorpusDatabaseTable.columnType type)
			throws CorpusDatabaseException {
		this.columnType = type;
	}

	public CorpusDatabaseTable.columnType getColumnType() {
		return this.columnType;
	}

	public Column setRequired(boolean flag) {
		this.required = flag;
		return this;
	}

	public boolean isRequired() {
		return this.required;
	}

	private void setSize(int size) {
		this.size = size;
	}

	/**
	 * Get the size of this column
	 * 
	 * @return The size as Integer value or null if no size could be determined
	 */
	public Integer getSize() {
		if (this.size == -1) {
			return null;
		}
		return Integer.valueOf(this.size);
	}
}