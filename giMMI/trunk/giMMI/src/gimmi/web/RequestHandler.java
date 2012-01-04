package gimmi.web;

import gimmi.database.CorpusDatabaseException;
import gimmi.util.ConfigManagerException;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletResponse;

public interface RequestHandler {
	/**
	 * Get the response object created by this handler
	 * 
	 * @return
	 * @throws IOException
	 */
	HttpServletResponse getResponse() throws IOException;

	/**
	 * Start parsing the request and doing actions according to the requested
	 * informations
	 * 
	 * @throws ConfigManagerException
	 * @throws CorpusDatabaseException
	 * @throws SQLException
	 */
	void parseRequest() throws SQLException, CorpusDatabaseException,
			ConfigManagerException;
}
