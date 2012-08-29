package org.lua.commons.configuration.basic;


public class StringConfigurationApi extends StandartConfigurationApi {

	public String[] getNames() {
		return new String[] { "char", "len", "find", "sub", "match", "gsub",
				"gmatch", "lower", "upper", "reverse", "rep", "format" };
	}

	@Override
	public String getDefault() {
		return "string";
	}

}
