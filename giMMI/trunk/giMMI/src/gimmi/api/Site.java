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
 * 
 * @author Jens Bertram <code@jens-bertram.net>
 * 
 */
public class Site {
	private final Map<String, Object> properties = new HashMap<String, Object>();
	private final List<CorpusContent> preWrite = new ArrayList<CorpusContent>();
	private final List<CorpusContent> postWrite = new ArrayList<CorpusContent>();
	private Number siteCategoryId;
	private Number newSiteId = null;
	private static CorpusDatabase DB;
	/**
	 * Allow a fallback value to be set for field settings that couldn't get
	 * resolved.
	 */
	private static final boolean allowUnknownFieldContent = true;

	/**
	 * Constructor
	 * 
	 * @throws ConfigManagerException
	 * @throws SQLException
	 */
	public Site() throws SQLException, ConfigManagerException {
		Site.DB = gimmi.database.Database.getInstance();
		this.setTimestamp(new Timestamp(Calendar.getInstance().getTime()
				.getTime()));
	}

	/**
	 * Constructor creating a new site entry in one step.
	 * 
	 * @param url
	 *            The URL object for this site
	 * @param language
	 *            The language name in one of the supported translations
	 * @param country
	 *            The country name in one of the supported translations
	 * @param rootFile
	 *            Root document for this site snapshot
	 * @param title
	 *            The title for this site
	 * @param category
	 *            The sites category
	 * @param storage
	 *            The relative storage path in the corpus
	 * @throws SQLException
	 * @throws ConfigManagerException
	 * @throws MalformedURLException
	 * @throws CorpusDatabaseException
	 */
	public Site(String url, String language, String country, String rootFile,
			String title, String category, String subCategory,
			String subSubCategory, String storage) throws SQLException,
			ConfigManagerException, MalformedURLException,
			CorpusDatabaseException {
		Site.DB = gimmi.database.Database.getInstance();

		this.setTitle(title);
		this.setURL(new URL(url));
		// default to current adding
		this.setTimestamp(new Timestamp(Calendar.getInstance().getTime()
				.getTime()));
		this.setLanguageCodeByName(language);
		this.setCountryCodeByName(country);
		this.setRootFile(rootFile);
		this.setCategory(category, subCategory, subSubCategory);
		this.setStoragePath(storage);

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
	 * Set the URL property for this site
	 * 
	 * @param url
	 *            The URL object for this site
	 * @throws SQLException
	 * @throws CorpusDatabaseException
	 */
	public void setURL(URL url) throws SQLException, CorpusDatabaseException {
		this.properties.put("url_path", url.getPath());
		// retrieve the hostname-for the given language-code
		Domain domain = new Domain(Site.DB);
		ResultSet domainRS = (domain.getTable().find("url", url.getHost()));
		Integer domainId = null;
		if (domainRS.next()) {
			domainId = domainRS.getInt("domain_id");
		} else {
			domain.setProperty("url", url.getHost());
			// update on write
			this.preWrite.add(domain);
		}
		this.properties.put("domain_id", domainId);
	}

	/**
	 * Set the timestamp property for this site. The timestamp should reflect
	 * when the site was crawled. If you omit this property the site object will
	 * be committed with a timestamp of the commit-time
	 * 
	 * @param ts
	 *            The timestamp
	 */
	public void setTimestamp(Timestamp ts) {
		this.properties.put("crawl_time", ts);
	}

	/**
	 * Set the language property for this site
	 * 
	 * @param code
	 *            The three-letter language code as specified by ISO639-alpha-3
	 * @throws IllegalArgumentException
	 *             Thrown if the language code does not match the ISO639-alpha-3
	 *             scheme
	 * @throws SQLException
	 * @throws CorpusDatabaseException
	 */
	public void setLanguageCode(String code) throws IllegalArgumentException,
			SQLException, CorpusDatabaseException {
		Language language = new Language(Site.DB);
		Number lCode = language.getIdByCode(code);
		if (lCode != null) {
			this.properties.put("language_id", lCode);
		} else {
			if (Site.allowUnknownFieldContent) {
				this.properties.put("language_id", language.getIdForUnknown());
			} else {
				throw new IllegalArgumentException(
						"The language-code you specified could not be found.");
			}
		}
	}

	/**
	 * Set the language property for this site
	 * 
	 * @param languageName
	 *            Name of the language in one of the possible translations
	 * @throws IllegalArgumentException
	 *             Thrown if the language code does not match the ISO639-alpha-3
	 *             scheme
	 * @throws SQLException
	 * @throws CorpusDatabaseException
	 */
	public void setLanguageCodeByName(String languageName)
			throws IllegalArgumentException, SQLException,
			CorpusDatabaseException {
		Language language = new Language(Site.DB);
		Number lCode = language.getIdByName(languageName);
		if (lCode != null) {
			this.properties.put("language_id", lCode);
		} else {
			if (Site.allowUnknownFieldContent) {
				this.properties.put("language_id", language.getIdForUnknown());
			} else {
				throw new IllegalArgumentException("The language name ("
						+ languageName + ") you specified could not be found.");
			}
		}
	}

	/**
	 * Set the country property for this site
	 * 
	 * @param code
	 *            The two-letter language code as specified by ISO3166-1-alpha-2
	 * @throws IllegalArgumentException
	 *             Thrown if the language code does not match the
	 *             ISO3166-1-alpha-2 scheme
	 * @throws CorpusDatabaseException
	 * @throws SQLException
	 */
	public void setCountryCode(String code) throws IllegalArgumentException,
			SQLException, CorpusDatabaseException {
		Country country = new Country(Site.DB);
		Integer cCode = country.getIdByCode(code);
		if (cCode != null) {
			this.properties.put("country_id", cCode);
		} else {
			if (Site.allowUnknownFieldContent) {
				this.properties.put("country_id", country.getIdForUnknown());
			} else {
				throw new IllegalArgumentException(
						"The country-code you specified could not be found.");
			}
		}
	}

	/**
	 * Set the country property for this site
	 * 
	 * @param countryName
	 *            The country name in one of the supported translations
	 * @throws IllegalArgumentException
	 *             Thrown if the language code does not match the
	 *             ISO3166-1-alpha-2 scheme
	 * @throws CorpusDatabaseException
	 * @throws SQLException
	 */
	public void setCountryCodeByName(String countryName)
			throws IllegalArgumentException, SQLException,
			CorpusDatabaseException {
		Country country = new Country(Site.DB);
		Number cCode = country.getIdByName(countryName);
		if (cCode != null) {
			this.properties.put("country_id", cCode);
		} else {
			if (Site.allowUnknownFieldContent) {
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
	 */
	public void setRootFile(String fileName) throws IllegalArgumentException {
		String fName = fileName.trim();
		if (fName.length() > 0) {
			this.properties.put("root_file", fName);
		} else {
			throw new IllegalArgumentException(
					"The filename you specified seems to be empty.");
		}
	}

	/**
	 * Set the title for this site
	 * 
	 * @param title
	 *            The title for this site
	 */
	public void setTitle(String title) throws IllegalArgumentException {
		String sName = title.trim();
		if (sName.length() > 0) {
			this.properties.put("title", sName);
		} else {
			throw new IllegalArgumentException(
					"The site-title you specified seems to be empty.");
		}
	}

	/**
	 * Set the category id for this site
	 * 
	 * @param category
	 *            The id of the category for this site
	 * @throws CorpusDatabaseException
	 * @throws SQLException
	 */
	public void setCategory(String topCategory, String subCategory,
			String subSubCategory) throws IllegalArgumentException,
			SQLException, CorpusDatabaseException {
		Category category = new Category(Site.DB);
		
		int topCatId;
		int subCatId;
		int subSubCatId;
		
		if(topCategory == null) {
			throw new CorpusDatabaseException("A Top Category has to be supplied for the creation of a new site!");
		}
		Number categoryId = category.getIdByName(topCategory);
		//check whether the top category exists
		if (categoryId != null) {
			topCatId = categoryId.intValue();

			//check whether the sub category exists (if not null)
			//do nothing if subcategory is null
			if(subCategory != null) {
			
				categoryId = category.getCategoryId(subCategory, topCatId);
				//check for the sub sub category if the sub category exists
				if(categoryId != null) {
					subCatId = categoryId.intValue();
					
					//check whether the sub sub category exists (if not null)
					//do nothing if it is null
					if(subSubCategory != null) {
						categoryId = category.getCategoryId(subSubCategory, subCatId);
						//create subcategory if it does not exist yet
						if(categoryId != null) {
							subSubCatId = categoryId.intValue();
						}
						else {
							categoryId = createCategory(subSubCategory, subCatId);
						}
					}
				}
				//create sub category and sub sub category if the sub category does not exist yet
				else {
					categoryId = createCategory(subCategory, topCatId);
					//create sub sub category if it does not exist yet
					if(subSubCategory != null) {
						categoryId = createCategory(subSubCategory, categoryId.intValue());
					}
				}
			}

		//if the top category does not exist, create the complete hierarchy
		} else {
			//create top category
			categoryId = createCategory(topCategory, null);
			//create sub category (if not null)
			if(subCategory != null) {
				categoryId = createCategory(subCategory, categoryId.intValue());
				//create sub sub category (if not null)
				if(subSubCategory != null) {
					categoryId = createCategory(subSubCategory, categoryId.intValue());
				}
			}
		}
		
		this.siteCategoryId = categoryId;
		this.postWrite.add(new SiteHasCategory(Site.DB));
	}
	
	/**
	 * Set the category string for this site
	 * 
	 * @param category
	 *            The category for this site as string
	 * @param language
	 *            Which language should be used when setting the category name.
	 *            Currently only (de,en) are supported
	 * @throws CorpusDatabaseException
	 * @throws SQLException
	 */
	public void setCategory(MultilanguageContent topCategory,
			MultilanguageContent subCategory,
			MultilanguageContent subSubCategory)
			throws IllegalArgumentException, SQLException,
			CorpusDatabaseException {
		Category category = new Category(Site.DB);

		int topCatId;
		int subCatId;
		int subSubCatId;

		if(topCategory == null) {
			throw new CorpusDatabaseException("A Top Category has to be supplied for the creation of a new site!");
		}
		Number categoryId = category.getIdByName(topCategory);
		
		//check whether the top category exists
		if (categoryId != null) {
			topCatId = categoryId.intValue();

			//check whether the sub category exists (if not null)
			//do nothing if subcategory is null
			if(subCategory != null) {
			
				categoryId = category.getCategoryId(subCategory, topCatId);
				//check for the sub sub category if the sub category exists
				if(categoryId != null) {
					subCatId = categoryId.intValue();
					
					//check whether the sub sub category exists (if not null)
					//do nothing if it is null
					if(subSubCategory != null) {
						categoryId = category.getCategoryId(subSubCategory, subCatId);
						//create subcategory if it does not exist yet
						if(categoryId != null) {
							subSubCatId = categoryId.intValue();
						}
						else {
							categoryId = createCategory(subSubCategory, subCatId);
						}
					}
				}
				//create sub category and sub sub category if the sub category does not exist yet
				else {
					categoryId = createCategory(subCategory, categoryId.intValue());
					//create sub sub category if it does not exist yet
					if(subSubCategory != null) {
						categoryId = createCategory(subSubCategory, categoryId.intValue());
					}
				}
			}

		//if the top category does not exist, create the complete hierarchy
		} else {
			//create top category
			categoryId = createCategory(topCategory, null);
			//create sub category (if not null)
			if(subCategory != null) {
				categoryId = createCategory(subCategory, categoryId.intValue());
				//create sub sub category (if not null)
				if(subSubCategory != null) {
					categoryId = createCategory(subSubCategory, categoryId.intValue());
				}
			}
		}
		
		this.siteCategoryId = categoryId;
		this.postWrite.add(new SiteHasCategory(Site.DB));
	}

	/**
	 * Get the database id of a newly created site entry (if any).
	 * 
	 * @return Id of the new entry or null if none was created
	 */
	public Number getNewSiteId() {
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
	public void setStoragePath(String path) {
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		if (!path.endsWith("/")) {
			path = path + "/";
		}
		this.properties.put("storage_path", path);
	}

	/**
	 * Commits the new site object to the database
	 * 
	 * TODO: There is no rollback if write as whole fails. Needs transaction
	 * handling!
	 * 
	 * @throws SQLException
	 * @throws CorpusDatabaseException
	 * @throws ConfigManagerException
	 */
	public Number write() throws SQLException, CorpusDatabaseException,
			ConfigManagerException {
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
		gimmi.content.Site site = new gimmi.content.Site(Site.DB);
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
	 * Create a new category entry. Directly writes a new category which is 
	 * associated with its parent
	 * To create a root node set parent to null
	 * 
	 * @param categoryData
	 * @param parent
	 *            Id of the parent category
	 * @throws CorpusDatabaseException
	 * @throws SQLException
	 */
	public int createCategory(MultilanguageContent categoryData, Integer parent)
			throws CorpusDatabaseException, SQLException {
		Category category = new Category(DB);
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
	 * Quick and dirty workaround
	 * Enables the creation of categories for which only one language is given by 
	 * writing this language for all specified languages
	 * @param categoryName
	 * @param parent
	 * @throws CorpusDatabaseException
	 * @throws SQLException
	 */
	public int createCategory(String categoryName, Integer parent)
			throws CorpusDatabaseException, SQLException {
		MultilanguageContent tc = new MultilanguageContent();
		tc.setLangString(MultilanguageContent.Lang.DE, categoryName);
		tc.setLangString(MultilanguageContent.Lang.EN, categoryName);

		return createCategory(tc, parent);
	}
}