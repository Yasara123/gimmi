package gimmi.web;

import gimmi.content.Category;
import gimmi.content.Country;
import gimmi.content.Language;
import gimmi.database.CorpusDatabaseException;
import gimmi.database.Database;
import gimmi.util.ConfigManagerException;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;

interface Command {
	void run();
}

public class Ajax implements RequestHandler {
	private static final String MIME_TYPE = "application/json";
	// request parameters
	private static final String RQP_TASK = "task";
	// tasks
	private static final String TSK_LIST = "list";
	// targets
	private static final String TGT_GENERAL = "target";

	private final HttpServletRequest request;
	private final HttpServletResponse response;

	private String responseJSON;

	public Ajax(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType(Ajax.MIME_TYPE);
		this.request = request;
		this.response = response;
	}

	@Override
	public HttpServletResponse getResponse() throws IOException {
		PrintWriter writer = this.response.getWriter();
		if (this.responseJSON != null) {
			writer.write(this.responseJSON);
		} else {
			// TODO: do something useful here!
			writer.write("UH! Got me..");
		}
		return null;
	}

	/**
	 * Check what the user wants us to list
	 * 
	 * @throws ConfigManagerException
	 * @throws CorpusDatabaseException
	 * @throws SQLException
	 */
	private void parseListTask() throws SQLException, CorpusDatabaseException,
			ConfigManagerException {
		String target = this.request.getParameter(Ajax.TGT_GENERAL);
		if (target == null) {
			return;
		}

		JSONArray jArray = new JSONArray();
		switch (target) {
		case gimmi.content.Language.TABLE_NAME:
			Language language = new Language(Database.getInstance());
			jArray.addAll(language.getAllEntries("name_de", true));
			this.responseJSON = jArray.toJSONString();
			break;
		case gimmi.content.Country.TABLE_NAME:
			Country country = new Country(Database.getInstance());
			jArray.addAll(country.getAllEntries("name_de", true));
			this.responseJSON = jArray.toJSONString();
			break;
		case gimmi.content.Category.TABLE_NAME:
			Category category = new Category(Database.getInstance());
			jArray.addAll(category.getAllEntries("name_de", true));
			this.responseJSON = jArray.toJSONString();
			break;
		}
	}

	/**
	 * Tries to run an ajax-task
	 * 
	 * @param task
	 *            The task we should perform
	 * @throws ConfigManagerException
	 * @throws CorpusDatabaseException
	 * @throws SQLException
	 */
	private void parseTask(String task) throws SQLException,
			CorpusDatabaseException, ConfigManagerException {
		if (task == null) {
			return;
		}
		switch (task) {
		case TSK_LIST:
			this.parseListTask();
			break;
		}
	}

	@Override
	public void parseRequest() throws SQLException, CorpusDatabaseException,
			ConfigManagerException {
		this.parseTask(this.request.getParameter(Ajax.RQP_TASK));
	}
}
