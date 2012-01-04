package gimmi.web;

import gimmi.content.Language;
import gimmi.database.CorpusDatabase;
import gimmi.database.CorpusDatabaseException;
import gimmi.database.Database;
import gimmi.util.ConfigManagerException;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

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

	// just for debugging
	private String tmpMsg = "noAction";

	public Ajax(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType(Ajax.MIME_TYPE);
		this.request = request;
		this.response = response;
	}

	@Override
	public HttpServletResponse getResponse() throws IOException {
		PrintWriter writer = this.response.getWriter();
		JSONObject jObj = new JSONObject();
		jObj.put("DBG", this.tmpMsg);
		writer.write(jObj.toJSONString());
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
		switch (target) {
		case gimmi.content.Language.TABLE_NAME:
			CorpusDatabase db = Database.getDatabase();
			Language language = new Language(db);

			language.getAllLanguages("name_de", true);
			this.tmpMsg = "TABBLL!";
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
