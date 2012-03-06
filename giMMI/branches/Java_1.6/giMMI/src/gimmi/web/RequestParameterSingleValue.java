package gimmi.web;

import gimmi.util.MapTools;

import java.util.Map;

public abstract class RequestParameterSingleValue extends RequestParameter {
	private final String parameterValue;

	protected RequestParameterSingleValue(String parameter, String value,
			final Map<String, String[]> map) {
		super(new String[] { parameter }, map);
		this.parameterValue = value;
	}

	@Override
	public boolean isSatisfied() {
		if (MapTools.areAllKeysInMap(this.providedParameters,
				this.usedParameters)) {
			return this.getValue().equals(this.parameterValue);
		}
		return false;
	}
}
