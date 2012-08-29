package org.lua.commons.configuration.basic;

public class IOConfigurationApi extends StandartConfigurationApi {

	public String[] getNames() {
		return new String[] { "close", "flush", "input", "lines", "open",
				"output", "popen", "read", "tmpfile", "type", "write" };
	}

	@Override
	public String getDefault() {
		return "io";
	}

}