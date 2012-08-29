package org.lua.commons.configuration.basic;

public class TableConfigurationApi extends StandartConfigurationApi {

	public String[] getNames() {
		return new String[] { "concat", "insert", "pack", "remove", "sort",
				"unpack" };
	}

	@Override
	public String getDefault() {
		return "table";
	}

}
