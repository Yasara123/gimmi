package gimmi.web;

import gimmi.util.MapTools;

import java.util.Map;

public abstract class RequestParameterBoolean extends RequestParameter {
	protected String[] providedParameters;

	protected RequestParameterBoolean(String parameter,
			final Map<String, String[]> map) {
		super(new String[] { parameter }, map);
		this.providedParameters = new String[] { parameter };
	}

	@Override
	public boolean isSatisfied() {
		if (MapTools.areAllKeysInMap(this.providedParameters,
				this.usedParameters)) {
			if (this.getValue().equals("true")
					|| this.getValue().equals("false")) {
				return true;
			}
		}
		return false;
	}
}
