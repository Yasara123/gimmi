package gimmi.web;

import gimmi.content.Category;
import gimmi.content.Country;
import gimmi.content.Language;
import gimmi.database.CorpusDatabaseException;
import gimmi.database.Database;
import gimmi.util.ConfigManagerException;
import gimmi.util.MapTools;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;

public class Ajax implements RequestHandler {
	// JSON mime-type for responses
	private static final String MIME_TYPE = "application/json";
	// communication objects
	private final HttpServletRequest request;
	private final HttpServletResponse response;
	// response
	private String responseJSON;
	// parameters
	private final RequestParameters parameters;

	// TODO: maybe put this in generalized form into an own class
	private class RequestParameters {
		final List<RequestParameter> required = new ArrayList(3);
		final List<RequestParameter> optional = new ArrayList(3);
		Task task;
		Filter filter;
		Target target;

		RequestParameters(final Map<String, String[]> map) {
			this.task = new Task(map);
			this.filter = new Filter(map);
			this.target = new Target(map);
			this.required.add(this.task);
			this.required.add(this.target);
			this.optional.add(this.filter);
		}

		/**
		 * Check a list of RequestParameters if they are satisfied.
		 * 
		 * @param parameters
		 *            A list of <code>RequestParameter</code> objects to
		 *            validate
		 * @return The failing <code>RequestParameter</code> object, if any or
		 *         null if all <code>RequestParameter</code> validated fine
		 */
		RequestParameter validate(List<RequestParameter> parameters) {
			for (RequestParameter p : parameters) {
				if (!p.isSatisfied()) {
					return p;
				}
			}
			return null;
		}

		/** Possible request parameters. */
		class Task extends RequestParameterSingleValue {
			/** This class does only represent the task parameter. */
			Task(final Map<String, String[]> map) {
				super("task", "list", map);
			}

			@Override
			public String getName() {
				return "Task";
			}
		};

		/** Possible filter parameters. */
		class Filter extends RequestParameterSingleValue {
			/** This class does only represent a true or false parameter value. */
			Filter(final Map<String, String[]> map) {
				super("filter", "all", map);
			}

			@Override
			public String getName() {
				return "Filter";
			}
		}

		/** Possible target parameters. */
		class Target extends RequestParameter {
			/** Possible targets. These are the table names. */
			String[] parameterValues = new String[] {
					gimmi.content.Language.TABLE_NAME,
					gimmi.content.Country.TABLE_NAME,
					gimmi.content.Category.TABLE_NAME };

			Target(final Map<String, String[]> map) {
				// this class does only represent the target parameter
				super(new String[] { "target" }, map);
			}

			@Override
			public String getName() {
				return "Target";
			}

			@Override
			public boolean isSatisfied() {
				// is target set?
				if (MapTools.areAllKeysInMap(this.providedParameters,
						this.usedParameters)) {
					// has target a legal value?
					for (String value : this.parameterValues) {
						// one match is enough
						if (this.usedParameters.get(this.providedParameters[0])[0]
								.equals(value)) {
							return true;
						}
					}
				}
				return false;
			}
		}
	}

	/** Constructor for this AJAX type request handler. */
	public Ajax(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType(Ajax.MIME_TYPE);
		// reference objects
		this.request = request;
		this.response = response;
		// parameter handler
		this.parameters = new RequestParameters(this.request.getParameterMap());
	}

	@Override
	public HttpServletResponse getResponse() throws IOException {
		PrintWriter writer = this.response.getWriter();
		if (this.responseJSON != null) {
			writer.write(this.responseJSON);
		} else {
			// TODO: do something useful here!
			writer.write("UH! Got me.. I was left off having nothing to response.");
		}
		return null;
	}

	/**
	 * Check what the user wants us to list.
	 * 
	 * @throws ConfigManagerException
	 * @throws CorpusDatabaseException
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	private void parseListTask() throws SQLException, CorpusDatabaseException,
			ConfigManagerException {
		JSONArray jArray = new JSONArray();
		String target = this.parameters.target.getValue();

		// decide if we should retrieve a full set of data
		boolean getFullData = true;
		if (this.parameters.filter.isSatisfied()) {
			if (this.parameters.filter.getValue().equals("all")) {
				getFullData = false;
			}
		}

		// TODO: full requests need a upper limit for request results
		if (target.equals(gimmi.content.Language.TABLE_NAME)) {
			Language language = new Language(Database.getInstance());
			jArray.addAll(language.getAllEntries("name_de", getFullData));
			this.responseJSON = jArray.toJSONString();
		} else if (target.equals(gimmi.content.Country.TABLE_NAME)) {
			Country country = new Country(Database.getInstance());
			jArray.addAll(country.getAllEntries("name_de", getFullData));
			this.responseJSON = jArray.toJSONString();
		} else if (target.equals(gimmi.content.Category.TABLE_NAME)) {
			Category category = new Category(Database.getInstance());
			jArray.addAll(category.getAllEntries("name_de", getFullData));
			this.responseJSON = jArray.toJSONString();
		}
	}

	/** Entry function. */
	@Override
	public void parseRequest() throws SQLException, CorpusDatabaseException,
			ConfigManagerException {

		RequestParameter invalidObject = this.parameters
				.validate(this.parameters.required);
		if (invalidObject != null) {
			// a required parameter failed
			this.responseJSON = "{\"error\":\"The required parameter '"
					+ invalidObject.getName() + "' is not valid.\"}";
			return;
		}

		// we have only one task, so do it
		this.parseListTask();
	}
}
