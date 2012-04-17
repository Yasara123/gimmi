package gimmi.content;

import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;
import gimmi.database.MultilanguageContent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a category database entry.
 * 
 * @author <a href="mailto:code@jens-bertram.net">Jens Bertram</a>
 * 
 */
public class Category extends CorpusContentNamed {
    /** The name of the database table. */
    public static final String TABLE_NAME = "category";

    /**
     * Default constructor.
     * 
     * @param db
     *            Database to work with
     * @throws CorpusDatabaseException
     *             Thrown on any database error
     * @throws SQLException
     *             Thrown on any SQL error
     */
    public Category(final CorpusDatabase db) throws SQLException,
	    CorpusDatabaseException {
	this.database = db;
	this.setTable(db.getTable(Category.TABLE_NAME));
    }

    /**
     * Gets all entries of this database table.
     * 
     * @param translation
     *            The translation (language) in which to get the categories
     * @param usedOnly
     *            If true only items that are actually assigned to Site entries
     *            will be returned
     * @throws CorpusDatabaseException
     *             Thrown on any database error
     * @throws SQLException
     *             Thrown on any SQL error
     * @return A List with all entries as strings
     */
    @Override
    public final List<String> getAllEntries(final String translation,
	    final boolean usedOnly) throws SQLException,
	    CorpusDatabaseException {
	if (usedOnly) {
	    // get only used entries
	    return this.simpleJoin(this.getTable(), new SiteHasCategory(
		    this.database).getTable(), "category_id", "category_id",
		    translation);
	} else {
	    // get all possible codes
	    return this.resultsetToStringList(this.getTable().fetchAll(
		    new String[] { translation }));
	}
    }

    /**
     * Gets all entries of this database table.
     * 
     * @param usedOnly
     *            If true only items that are actually assigned to Site entries
     *            will be returned
     * @throws CorpusDatabaseException
     *             Thrown on any database error
     * @throws SQLException
     *             Thrown on any SQL error
     * @return A ResultSet with all entries
     */
    @Override
    public final ResultSet getAllEntries(final boolean usedOnly)
	    throws SQLException, CorpusDatabaseException {
	return this.getTable().join("category_id",
		new SiteHasCategory(this.database).getTable(), "category_id");
    }

    /**
     * 
     * @param translation
     *            The translation (language) in which to get the categories
     * @param parents
     *            List of parent node id's (root to head). If parents is null,
     *            all root nodes will be returned.
     * @param usedOnly
     *            If true only elements currently assigned with a site will be
     *            returned
     * @return A list of String arrays with categories contents ordered by root
     *         to head
     * @throws CorpusDatabaseException
     *             Thrown on any database error
     * @throws SQLException
     *             Thrown on any SQL error
     */
    public final List<String[]> getMap(final String translation,
	    final Integer[] parents, final boolean usedOnly)
	    throws SQLException, CorpusDatabaseException {
	ResultSet data;
	StringBuffer path = new StringBuffer();
	List<String[]> results = new ArrayList<String[]>();
	if (parents == null) {
	    data = this.getTable().fetchAllWithCondition("path=''");
	} else {
	    for (Integer i : parents) {
		path.append(i.toString() + ":");
	    }
	    data =
		    this.getTable()
			    .fetchAllWithCondition(
				    new String[] { "category_id", "path",
					    translation },
				    "path='"
					    + path.substring(0,
						    path.length() - 1) + "'");
	}
	while (data.next()) {
	    results.add(new String[] { data.getString(1), data.getString(2),
		    data.getString(3) });
	}
	return results;
    }

    /**
     * Create a new category entry. Sets up the data needed to write a new
     * category entry.
     * 
     * TODO: Allow the setting of a parent category
     * 
     * @param categoryData
     *            The language data for the new category
     * @param parent
     *            Id of the parent category
     * @throws CorpusDatabaseException
     *             Thrown on any database error
     */
    public final void create(final MultilanguageContent categoryData,
	    final Integer parent) throws CorpusDatabaseException {
	for (MultilanguageContent.Lang lang : MultilanguageContent.Lang
		.values()) {
	    System.out.println("name_" + lang.toString());
	    this.setProperty("name_" + lang.toString().toLowerCase(),
		    categoryData.getLangString(lang));
	}
	if (parent != null) {
	    this.setProperty("parent_id", parent.toString());
	}
    }

    /**
     * Get the id of an category by it's name.
     * 
     * @param categoryName
     *            Name as string (case is ignored)
     * @return The category id or null if none found
     * @throws SQLException
     *             Thrown on any SQL error
     */
    public final Number getCategoryId(final String categoryName)
	    throws SQLException {
	return this.getIdByName(categoryName);
    }

    /**
     * Get the id of an category by it's name and by a specific parent defined
     * by it's id.
     * 
     * @param categoryName
     *            Name of the category which id should be resolved
     * @param categoryId
     *            Id of the parent category
     * @return The category id or null if none found
     * @throws SQLException
     *             Thrown on any SQL error
     */
    public final Number getCategoryId(final String categoryName,
	    final Number categoryId) throws SQLException {
	return this.getIdByName(categoryName, "parent_id = " + categoryId);
    }

    /**
     * Get the id of an category by it's name and by a specific parent defined
     * by it's id.
     * 
     * @param categoryName
     *            Name of the category which id should be resolved specified by
     *            a given translation
     * @param categoryId
     *            Id of the parent category
     * @return The category id or null if none found
     * @throws SQLException
     *             Thrown on any SQL error
     */
    public final Number getCategoryId(final MultilanguageContent categoryName,
	    final Number categoryId) throws SQLException {
	return this.getIdByName(categoryName, "parent_id = " + categoryId);
    }
}
