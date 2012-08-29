package org.lua.commons.configuration.basic;

public class OSConfigurationApi extends StandartConfigurationApi {

	public String[] getNames() {
		return new String[] { "clock", "date", "difftime", "execute", "getenv",
				"remove", "rename", "setlocale", "time", "tmpname" };
	}

	@Override
	public String getDefault() {
		return "os";
	}

}
