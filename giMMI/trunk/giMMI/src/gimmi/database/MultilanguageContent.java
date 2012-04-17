package gimmi.database;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class MultilanguageContent {
    private final Map<Lang, String> data =
	    new EnumMap<Lang, String>(Lang.class);

    public enum Lang {
	DE, EN
    }

    public void setLangString(Lang language, String string) {
	if (language == null) {
	    throw new IllegalArgumentException("No language specified.");
	}
	this.data.put(language, string);
    }

    public String getLangString(Lang language) {
	return this.data.get(language);
    }

    public Map<Lang, String> getLangStrings() {
	return Collections.unmodifiableMap(this.data);
    }

    public Lang getLangByString(String language) {
	for (Lang lang : Lang.values()) {
	    if (language.toUpperCase().equals(lang.toString().toUpperCase())) {
		return lang;
	    }
	}
	return null;
    }

    @Override
    public String toString() {
	StringBuffer str = new StringBuffer();
	for (MultilanguageContent.Lang lang : MultilanguageContent.Lang
		.values()) {
	    str.append(String.format("%s(%s) ", lang.toString(),
		    this.getLangString(lang)));
	}
	return str.toString();
    }
}
