package gimmi.content;

import gimmi.database.CorpusDatabaseException;
import gimmi.database.MultilanguageContent;

import java.sql.SQLException;

public interface CorpusContentNamed {
	/**
	 * Get the id of a language by it's name. All name fields (of all
	 * translations) will be searched and the first match will be returned.
	 * 
	 * @param name
	 *            Name to search for
	 * @return First language which matches name
	 * @throws SQLException
	 */
	Number getIdByName(String name) throws SQLException;

	/**
	 * Get the id of a language by it's name. All name fields (of all
	 * translations) will be searched and the first match will be returned. Each
	 * translation passed in by name will be part of the matching condition.
	 * 
	 * @param name
	 * @param language
	 * @return
	 * @throws SQLException
	 * @throws CorpusDatabaseException
	 * @throws IllegalArgumentException
	 */
	Number getIdByName(MultilanguageContent name) throws SQLException,
			CorpusDatabaseException, IllegalArgumentException;
}
