package gimmi.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Represents one or more request URL parameters which are part of a
 * <code>GET</code> request URL string.
 */
abstract class RequestParameter {
	/**
	 * Parameters this class is providing. This must be overwritten by any
	 * subclass. All parameters are expected to be in lower-case only.
	 */
	protected String[] providedParameters;
	/**
	 * Parameters this class provides and which provide a value. These pairs are
	 * extracted from the URL-parameterMap passed in by
	 * {@link #setParameterMap(Map)}.
	 */
	protected Map<String, String[]> usedParameters;

	protected RequestParameter(String[] classProvidedParameters,
			Map<String, String[]> parameterMap) {
		this.providedParameters = classProvidedParameters;
		this.setParameterMap(parameterMap);
	}

	public abstract String getName();

	@Override
	public String toString() {
		return this.getName();
	}

	/**
	 * Set the values for this request-parameter.
	 * 
	 * @param parameterMap
	 *            Map with parameter->value mapping as provided by
	 *            {@link HttpServletRequest#getParameterMap()}.
	 */
	protected void setParameterMap(Map<String, String[]> parameterMap) {
		this.usedParameters = new HashMap<String, String[]>(
				this.providedParameters.length);
		// store only parameters we provide
		for (String parameter : this.providedParameters) {
			if (parameterMap.containsKey(parameter.toLowerCase())) {
				this.usedParameters.put(parameter, parameterMap.get(parameter));
			}
		}
	}

	/**
	 * Check if the provided parameters are complete and provide useful values.
	 * Complete does not necessary mean that all possible parameters are set,
	 * but that the parameters that are set do provide enough information.
	 * 
	 * @return True if values are ok, false otherwise
	 */
	public abstract boolean isSatisfied();

	/** Get the value for this request-parameter. */
	public String getValue() {
		try {
			return this.usedParameters.get(this.providedParameters[0])[0];
		} catch (NullPointerException e) {
			return null;
		}
	}

	/** Get the values (if there are many) for this request-parameter. */
	public String[] getValues() {
		return new String[] { this.getValue() };
	}
}
