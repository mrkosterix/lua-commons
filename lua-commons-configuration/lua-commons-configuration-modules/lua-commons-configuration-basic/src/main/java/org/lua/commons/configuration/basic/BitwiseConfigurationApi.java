package org.lua.commons.configuration.basic;

public class BitwiseConfigurationApi extends StandartConfigurationApi {

	public String[] getNames() {
		return new String[] { "arshift", "band", "bnot", "bor", "btest",
				"bxor", "extract", "replace", "lrotate", "lshift", "rrotate",
				"rshift" };
	}

	@Override
	public String getDefault() {
		return "bit32";
	}

}
