package gimmi.content;

import gimmi.database.CorpusDatabaseException;
import gimmi.database.CorpusDatabaseTable;
import gimmi.database.MultilanguageContent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class CorpusContentNamed extends CorpusContent {
	/**
	 * Get the id of a language by it's name. All name fields (of all
	 * translations) will be searched and the first match will be returned. The
	 * search field will default to the name of the table returned by
	 * <code>getTable().getName()</code> with <code>_id</code> as suffix added.
	 * 
	 * @param name
	 *            Name to search for
	 * @return First language which matches name
	 * @throws SQLException
	 */
	public Number getIdByName(String name) throws SQLException {
		return (Number) this.getFieldByName(this.getTable().getName()
				.toLowerCase()
				+ "_id", name);
	}

	/**
	 * Get the id of a language by it's name. All name fields (of all
	 * translations) will be searched and the first match will be returned. Each
	 * translation passed in by name will be part of the matching condition. The
	 * search field will default to the name of the table returned by
	 * <code>getTable().getName()</code> with <code>_id</code> as suffix added.
	 * 
	 * @param name
	 * @param language
	 * @return
	 * @throws SQLException
	 * @throws CorpusDatabaseException
	 * @throws IllegalArgumentException
	 */
	public Number getIdByName(MultilanguageContent name) throws SQLException,
			CorpusDatabaseException, IllegalArgumentException {
		return (Number) this.getFieldByName(this.getTable().getName()
				.toLowerCase()
				+ "_id", name);
	}

	/**
	 * Get a table field based on content of the <code>name_.*</code> field
	 * 
	 * @param field
	 *            Field to get
	 * @param name
	 *            Name to search for in all <code>name_.*</code> fields
	 * @return The first occurrence found
	 * @throws SQLException
	 */
	public Object getFieldByName(String field, String name) throws SQLException {
		ResultSet resultSet = null;
		StringBuffer query = new StringBuffer();
		for (MultilanguageContent.Lang lang : MultilanguageContent.Lang
				.values()) {
			query.append("name_"
					+ lang.toString().toLowerCase()
					+ "="
					+ this.getTable().escape(name.toLowerCase(),
							CorpusDatabaseTable.columnType.TXT) + " OR ");
		}
		resultSet = this.getTable().find(
				query.toString().substring(0,
						query.toString().lastIndexOf(" OR ")));
		if ((resultSet != null) && resultSet.next()) {
			return resultSet.getObject(field);
		}
		return null;
	}

	/**
	 * Get a table field based on content of the <code>name_.*</code> field
	 * 
	 * @param field
	 *            Field to get
	 * @param name
	 *            Name to search for in all <code>name_.*</code> fields
	 * @return The first occurrence found
	 * @throws SQLException
	 */
	public Object getFieldByName(String field, MultilanguageContent name)
			throws SQLException {
		ResultSet resultSet = null;
		StringBuffer query = new StringBuffer();
		for (MultilanguageContent.Lang lang : MultilanguageContent.Lang
				.values()) {
			if (name.getLangString(lang) != null) {
				query.append("name_"
						+ lang.toString().toLowerCase()
						+ "="
						+ this.getTable().escape(name,
								CorpusDatabaseTable.columnType.TXT) + " AND ");
			}
		}
		resultSet = this.getTable().find(
				query.toString().substring(0,
						query.toString().lastIndexOf(" AND ")));
		if ((resultSet != null) && resultSet.next()) {
			return resultSet.getObject(field);
		}
		return null;
	}

	/**
	 * Get the name fields of a row by it's id. Only the first matching the
	 * condition is returned.
	 * 
	 * @param field
	 *            Field that stores the id
	 * @param id
	 *            Id to search for
	 * @return The name in all available languages matching the given id or null
	 *         if nothing was found
	 * @throws SQLException
	 * @throws CorpusDatabaseException
	 */
	public MultilanguageContent getNameById(String field, Number id)
			throws SQLException, CorpusDatabaseException {
		ResultSet resultSet = null;
		List<String> names = new ArrayList<String>(
				MultilanguageContent.Lang.values().length);

		for (MultilanguageContent.Lang lang : MultilanguageContent.Lang
				.values()) {
			names.add("name_" + lang.toString().toLowerCase());
		}
		resultSet = this
				.getTable()
				.fetchAllWithCondition(
						names.toArray(new String[MultilanguageContent.Lang
								.values().length]), field + "=" + id.toString());

		if ((resultSet != null) && resultSet.next()) {
			MultilanguageContent mContent = new MultilanguageContent();
			for (String name : names) {
				mContent.setLangString(mContent.getLangByString(name
						.substring(5)), resultSet.getString(name).trim());
			}
			return mContent;
		}

		return null;
	}

	/**
	 * The search field will default to the name of the table returned by
	 * <code>getTable().getName()</code> with <code>_id</code> as suffix added.
	 * 
	 * @param id
	 *            Id to search for
	 * @return The name in all available languages matching the given id or null
	 *         if nothing was found
	 * @throws SQLException
	 * @throws CorpusDatabaseException
	 */
	public MultilanguageContent getNameById(Number id) throws SQLException,
			CorpusDatabaseException {
		return this.getNameById(
				this.getTable().getName().toLowerCase() + "_id", id);
	}

	/**
	 * 
	 * @param translation
	 *            The translation of the content name. This must match the
	 *            corresponding table column name
	 * @param usedOnly
	 *            Get only content that is actually used by any site entry
	 * @return A string-list with all content found
	 * @throws CorpusDatabaseException
	 * @throws SQLException
	 */
	public abstract List<String> getAllEntries(String translation,
			boolean usedOnly) throws SQLException, CorpusDatabaseException;
}
