package gimmi.util;

import java.util.Map;

public class MapTools {
	/**
	 * Check if all keys are contained in a map.
	 * 
	 * @param keys
	 *            Keys as array of strings
	 * @param map
	 *            A map with strings as key
	 * @return True if all keys are set, false otherwise
	 */
	public static boolean areAllKeysInMap(String[] keys, Map<String, ?> map) {
		for (String key : keys) {
			if (map.containsKey(key)) {
				continue;
			} else {
				return false;
			}
		}
		return true;
	}
}
