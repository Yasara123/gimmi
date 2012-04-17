package gimmi.api;

import gimmi.content.Category;
import gimmi.content.CorpusContent;
import gimmi.content.Country;
import gimmi.content.Domain;
import gimmi.content.Language;
import gimmi.content.SiteHasCategory;
import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;
import gimmi.database.MultilanguageContent;
import gimmi.util.ConfigManagerException;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the public API exposed to create a new <code>Site</code> entry.
 */
public class Site {
    /** Store properties set for this Site object. */
    private final transient Map<String, Object> properties =
	    new HashMap<String, Object>();
    /**
     * Hold content that should be written before the actual Site object gets
     * written.
     */
    private final transient List<CorpusContent> preWrite =
	    new ArrayList<CorpusContent>();
    /**
     * Hold content that should be written after the actual Site object has been
     * written.
     */
    private final transient List<CorpusContent> postWrite =
	    new ArrayList<CorpusContent>();
    /** Category-id for this Site. */
    private transient Number siteCategoryId;
    /** Id for this new Site db-entry. This gets set automatically by the db. */
    private transient Number newSiteId = null;
    /** The database object. */
    private static CorpusDatabase database = null; // NOPMD
    /**
     * Allow a fallback value to be set for field settings that couldn't get
     * resolved.
     */
    private static final boolean ALLOW_UNKNOWN_FIELD_CONTENT = true; // NOPMD

    /**
     * Creates a basic site object. All properties must be set by their
     * respective setter functions.
     * 
     * @throws ConfigManagerException
     *             Thrown if database is not accessible
     * @throws SQLException
     *             Thrown if database is not accessible
     */
    public Site() throws SQLException, ConfigManagerException {
	Site.database = gimmi.database.Database.getInstance();
	this.setTimestamp(new Timestamp(Calendar.getInstance().getTime()
		.getTime()));
    }

    /**
     * Constructor creating a new site entry in one step.
     * 
     * @deprecated This function is only TEMPORARY! Please use the
     *             {@link #Site()} constructor and the appropriate setter
     *             functions instead!
     * 
     * @param url
     *            The URL object for this site
     * @param language
     *            The language name in one of the supported translations
     * @param country
     *            The country name in one of the supported translations
     * @param rootFile
     *            Root document for this site snapshot
     * @param category
     *            The sites category
     * @param storage
     *            The relative storage path in the corpus
     * @param subCategory
     *            Subcategory for this Site (first level)
     * @param subSubCategory
     *            Subcategory for this Site (second level)
     * @throws SQLException
     *             Thrown if creating the new Site db-entry has failed
     * @throws ConfigManagerException
     *             Thrown if database is not accessible
     * @throws MalformedURLException
     *             Thrown if the given URL is malformed
     * @throws CorpusDatabaseException
     *             Thrown if database is not accessible
     */
    @Deprecated
    public Site(final String url, final String language, final String country,
	    final String rootFile, final String category,
	    final String subCategory, final String subSubCategory,
	    final String storage) throws SQLException, ConfigManagerException,
	    MalformedURLException, CorpusDatabaseException {
	Site.database = gimmi.database.Database.getInstance();

	// this.setTitle(title);
	this.setURL(new URL(url));
	// default to current adding
	this.setTimestamp(new Timestamp(Calendar.getInstance().getTime()
		.getTime()));
	this.setLanguageCodeByName(language);
	this.setCountryCodeByName(country);
	this.setRootFile(rootFile);
	this.setCategory(category, subCategory, subSubCategory); // NOPMD
	this.setStoragePath(storage);
	// FIXME: This data is currently not supplied by the data list
	this.setTitle("not set");

	// write the site to the database
	try {
	    this.newSiteId = this.write();
	} catch (CorpusDatabaseException e) {
	    // plain die
	    // seems we missed a property
	    e.printStackTrace();
	}
    }

    /**
     * Set the URL property for this site.
     * 
     * @param url
     *            The URL object for this site
     * @throws SQLException
     *             Thrown if Domain table could not be found.
     * @throws CorpusDatabaseException
     *             Thrown if Domain table could not be found.
     */
    public final void setURL(final URL url) throws SQLException,
	    CorpusDatabaseException {
	this.properties.put("url_path", url.getPath());
	// retrieve the hostname-for the given language-code
	final Domain domain = new Domain(Site.database);
	final ResultSet domainRS = domain.getTable().find("url", url.getHost()); // NOPMD
	Integer domainId;
	if (domainRS.next()) {
	    domainId = domainRS.getInt("domain_id");
	} else {
	    domainId = null; // NOPMD
	    domain.setProperty("url", url.getHost());
	    // update on write
	    this.preWrite.add(domain);
	}
	domainRS.close();
	this.properties.put("domain_id", domainId);
    }

    /**
     * Set the timestamp property for this site. The timestamp should reflect
     * when the site was crawled. If you omit this property the site object will
     * be committed with a timestamp of the commit-time
     * 
     * @param timestamp
     *            The timestamp
     */
    public final void setTimestamp(final Timestamp timestamp) {
	this.properties.put("crawl_time", timestamp);
    }

    /**
     * Set the language property for this site.
     * 
     * @param code
     *            The three-letter language code as specified by ISO639-alpha-3
     * @throws CorpusDatabaseException
     *             Thrown on any database error
     * @throws SQLException
     *             Thrown on any SQL error
     */
    public void setLanguageCode(final String code) throws SQLException,
	    CorpusDatabaseException {
	final Language language = new Language(Site.database);
	final Number lCode = language.getIdByCode(code);
	if (lCode != null) {
	    this.properties.put("language_id", lCode);
	} else {
	    if (Site.ALLOW_UNKNOWN_FIELD_CONTENT) {
		this.properties.put("language_id", language.getIdForUnknown());
	    } else {
		throw new IllegalArgumentException(
			"The language-code you specified could not be found.");
	    }
	}
    }

    /**
     * Set the language property for this site.
     * 
     * @deprecated This function is only TEMPORARY! Please use
     *             {@link #setLanguageCode(String)} instead!
     * @param languageName
     *            Name of the language in one of the possible translations
     *            schemes
     * @throws CorpusDatabaseException
     *             Thrown on any database error
     * @throws SQLException
     *             Thrown on any SQL error
     */
    @Deprecated
    public final void setLanguageCodeByName(final String languageName)
	    throws SQLException, CorpusDatabaseException {
	final Language language = new Language(Site.database);
	final Number lCode = language.getIdByName(languageName);
	if (lCode != null) {
	    this.properties.put("language_id", lCode);
	} else {
	    if (Site.ALLOW_UNKNOWN_FIELD_CONTENT) {
		this.properties.put("language_id", language.getIdForUnknown());
	    } else {
		throw new IllegalArgumentException("The language name ("
			+ languageName + ") you specified could not be found.");
	    }
	}
    }

    /**
     * Set the country property for this site.
     * 
     * @param code
     *            The two-letter language code as specified by ISO3166-1-alpha-2
     * @throws CorpusDatabaseException
     *             Thrown on any database error
     * @throws SQLException
     *             Thrown on any SQL error
     */
    public void setCountryCode(final String code) throws SQLException,
	    CorpusDatabaseException {
	final Country country = new Country(Site.database);
	final Integer cCode = country.getIdByCode(code);
	if (cCode != null) {
	    this.properties.put("country_id", cCode);
	} else {
	    if (Site.ALLOW_UNKNOWN_FIELD_CONTENT) {
		this.properties.put("country_id", country.getIdForUnknown());
	    } else {
		throw new IllegalArgumentException(
			"The country-code you specified could not be found.");
	    }
	}
    }

    /**
     * Set the country property for this site.
     * 
     * @deprecated This function is only TEMPORARY! Please use
     *             {@link #setCountryCode(String)} instead!
     * @param countryName
     *            The country name in one of the supported translations
     * @throws CorpusDatabaseException
     *             Thrown on any database error
     * @throws SQLException
     *             Thrown on any SQL error
     */
    @Deprecated
    public final void setCountryCodeByName(final String countryName)
	    throws SQLException, CorpusDatabaseException {
	final Country country = new Country(Site.database);
	final Number cCode = country.getIdByName(countryName);
	if (cCode != null) {
	    this.properties.put("country_id", cCode);
	} else {
	    if (Site.ALLOW_UNKNOWN_FIELD_CONTENT) {
		this.properties.put("country_id", country.getIdForUnknown());
	    } else {
		throw new IllegalArgumentException(
			"The country name you specified (" + countryName
				+ ") could not be found.");
	    }
	}
    }

    /**
     * Set the root-filename for this site. The root file is the file witch is
     * opened first to browse a site-copy in the corpus
     * 
     * @param fileName
     *            Filename should be relative to the storage path.
     */
    public final void setRootFile(final String fileName) {
	final String fName = fileName.trim();
	if (fName.length() > 0) {
	    this.properties.put("root_file", fName);
	} else {
	    throw new IllegalArgumentException(
		    "The filename you specified seems to be empty.");
	}
    }

    /**
     * Set the title for this site.
     * 
     * @param title
     *            The title for this site
     */
    public final void setTitle(final String title) {
	final String sName = title.trim();
	if (sName.length() > 0) {
	    this.properties.put("title", sName);
	} else {
	    throw new IllegalArgumentException(
		    "The site-title you specified seems to be empty.");
	}
    }

    /**
     * Set the category id for this site.
     * 
     * @deprecated This function is only TEMPORARY! Please use
     *             {@link #setCategory(MultilanguageContent, MultilanguageContent, MultilanguageContent)}
     *             instead!
     * 
     * @param topCategory
     *            Category as string (root level)
     * @param subCategory
     *            Subcategory as string (first level)
     * @param subSubCategory
     *            Subcategory as string (second level)
     * @throws CorpusDatabaseException
     *             Thrown if category could not be created or retrieved
     * @throws SQLException
     *             Thrown on any db-error
     */
    @Deprecated
    public final void setCategory(final String topCategory,
	    final String subCategory, final String subSubCategory)
	    throws SQLException, CorpusDatabaseException {
	final Category category = new Category(Site.database);

	int topCatId;
	int subCatId;

	if (topCategory == null) {
	    throw new IllegalArgumentException(
		    "A Top Category has to be supplied for the creation of a new site!");
	}
	Number categoryId = category.getIdByName(topCategory);
	// check whether the top category exists
	if (categoryId != null) {
	    topCatId = categoryId.intValue();

	    // check whether the sub category exists (if not null)
	    // do nothing if subcategory is null
	    if (subCategory != null) {

		categoryId = category.getCategoryId(subCategory, topCatId);
		// check for the sub sub category if the sub category exists
		if (categoryId != null) {
		    subCatId = categoryId.intValue();

		    // check whether the sub sub category exists (if not null)
		    // do nothing if it is null
		    if (subSubCategory != null) {
			categoryId =
				category.getCategoryId(subSubCategory, subCatId);
			// create subcategory if it does not exist yet
			if (categoryId == null) {
			    categoryId =
				    this.createCategory(subSubCategory,
					    subCatId);
			}
		    }
		}
		// create sub category and sub sub category if the sub category
		// does not exist yet
		else {
		    categoryId = this.createCategory(subCategory, topCatId);
		    // create sub sub category if it does not exist yet
		    if (subSubCategory != null) {
			categoryId =
				this.createCategory(subSubCategory,
					categoryId.intValue());
		    }
		}
	    }

	    // if the top category does not exist, create the complete hierarchy
	} else {
	    // create top category
	    categoryId = this.createCategory(topCategory, null);
	    // create sub category (if not null)
	    if (subCategory != null) {
		categoryId =
			this.createCategory(subCategory, categoryId.intValue());
		// create sub sub category (if not null)
		if (subSubCategory != null) {
		    categoryId =
			    this.createCategory(subSubCategory,
				    categoryId.intValue());
		}
	    }
	}

	this.siteCategoryId = categoryId;
	this.postWrite.add(new SiteHasCategory(Site.database));
    }

    /**
     * Sets the category (hierarchy) for this site.
     * 
     * @param topCategory
     *            Category as string (root level)
     * @param subCategory
     *            Subcategory as string (first level)
     * @param subSubCategory
     *            Subcategory as string (second level)
     * @throws CorpusDatabaseException
     *             Thrown on any database error
     * @throws SQLException
     *             Thrown on any SQL error
     */
    public final void setCategory(final MultilanguageContent topCategory,
	    final MultilanguageContent subCategory,
	    final MultilanguageContent subSubCategory) throws SQLException,
	    CorpusDatabaseException {
	final Category category = new Category(Site.database);

	int topCatId;
	int subCatId;

	if (topCategory == null) {
	    throw new CorpusDatabaseException(
		    "A Top Category has to be supplied for the creation of a new site!");
	}
	Number categoryId = category.getIdByName(topCategory);

	// check whether the top category exists
	if (categoryId != null) {
	    topCatId = categoryId.intValue();

	    // check whether the sub category exists (if not null)
	    // do nothing if subcategory is null
	    if (subCategory != null) {
		categoryId = category.getCategoryId(subCategory, topCatId);

		// check for the sub sub category if the sub category exists
		if (categoryId != null) {
		    subCatId = categoryId.intValue();

		    // check whether the sub sub category exists (if not null)
		    // do nothing if it is null
		    if (subSubCategory != null) {
			categoryId =
				category.getCategoryId(subSubCategory, subCatId);

			// create subcategory if it does not exist yet
			if (categoryId == null) {
			    categoryId =
				    this.createCategory(subSubCategory,
					    subCatId);
			}
		    }
		}
		// create sub category and sub sub category if the sub category
		// does not exist yet
		else {
		    categoryId = this.createCategory(subCategory, null);
		    // create sub sub category if it does not exist yet
		    if (subSubCategory != null) {
			categoryId =
				this.createCategory(subSubCategory,
					categoryId.intValue());
		    }
		}
	    }

	    // if the top category does not exist, create the complete hierarchy
	} else {
	    // create top category
	    categoryId = this.createCategory(topCategory, null);
	    // create sub category (if not null)
	    if (subCategory != null) {
		categoryId =
			this.createCategory(subCategory, categoryId.intValue());
		// create sub sub category (if not null)
		if (subSubCategory != null) {
		    categoryId =
			    this.createCategory(subSubCategory,
				    categoryId.intValue());
		}
	    }
	}

	this.siteCategoryId = categoryId;
	this.postWrite.add(new SiteHasCategory(Site.database));
    }

    /**
     * Get the database id of a newly created site entry (if any).
     * 
     * @return Id of the new entry or null if none was created
     */
    public final Number getNewSiteId() {
	return this.newSiteId;
    }

    /**
     * Set the relative path portion (relative to the corpus store-root) for the
     * current site. Path must be noted in POSIX (forward slashes!) style.
     * 
     * TODO: check path for invalid chars
     * 
     * @param path
     *            The relative path portion. Leading and trailing slashes will
     *            be added if missing. Path is unchecked!
     */
    public final void setStoragePath(final String path) {
	String storagePath = path;
	if (path.charAt(0) != '/') {
	    storagePath = "/" + path;
	}
	if (!path.endsWith("/")) {
	    storagePath = path + "/";
	}
	this.properties.put("storage_path", storagePath);
    }

    /**
     * Commits the new site object to the database.
     * 
     * TODO: There is no rollback if write as whole fails. Needs transaction
     * handling!
     * 
     * @throws CorpusDatabaseException
     *             Thrown on any database error
     * @throws SQLException
     *             Thrown on any SQL error
     * @throws ConfigManagerException
     * @return Returns the database id of the created site entry
     */
    public final Number write() throws SQLException, CorpusDatabaseException {
	// write content that should be written beforehand
	for (CorpusContent content : this.preWrite) {
	    if (content.getClass().equals(Domain.class)) {
		this.properties.put("domain_id", content.write());
	    } else if (content.getClass().equals(Category.class)) {
		this.siteCategoryId = content.write();
	    } else {
		content.write();
	    }
	}
	// use the underlying content object to write
	final gimmi.content.Site site = new gimmi.content.Site(Site.database);
	site.setProperties(this.properties);
	this.newSiteId = site.write();
	// write content that should be written afterwards
	for (CorpusContent content : this.postWrite) {
	    if (content.getClass().equals(SiteHasCategory.class)) {
		content.setProperty("site_id", this.newSiteId);
		content.setProperty("category_id", this.siteCategoryId);
		content.write();
	    } else {
		content.write();
	    }
	}
	return this.newSiteId;
    }

    /**
     * Creates a new category entry.
     * 
     * @param categoryData
     *            Titles of this category
     * @param parent
     *            The id of the parent category or null if there isn't one
     * @return The database id of the new category
     * @throws CorpusDatabaseException
     *             Thrown on any database error
     * @throws SQLException
     *             Thrown on any SQL error
     */
    public final int createCategory(final MultilanguageContent categoryData,
	    final Integer parent) throws CorpusDatabaseException, SQLException {
	final Category category = new Category(Site.database);
	for (MultilanguageContent.Lang lang : MultilanguageContent.Lang
		.values()) {
	    category.setProperty("name_" + lang.toString().toLowerCase(),
		    categoryData.getLangString(lang));
	}
	if (parent != null) {
	    category.setProperty("parent_id", parent.toString());
	}
	return category.write();
    }

    /**
     * Quick and dirty workaround: Enables the creation of categories for which
     * only one language is given by writing this language for all specified
     * languages.
     * 
     * @param categoryName
     *            Name for the category, which gets set for all languages
     * @param parent
     *            The parent category, if any
     * @throws CorpusDatabaseException
     *             Thrown on any database error
     * @throws SQLException
     *             Thrown on any SQL error
     * @return The id of the new category
     */
    public final int createCategory(final String categoryName,
	    final Integer parent) throws CorpusDatabaseException, SQLException {
	final MultilanguageContent catName = new MultilanguageContent();
	catName.setLangString(MultilanguageContent.Lang.DE, categoryName);
	catName.setLangString(MultilanguageContent.Lang.EN, categoryName);

	return this.createCategory(catName, parent);
    }
}