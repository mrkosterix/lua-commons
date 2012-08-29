package org.lua.commons.configuration.basic;

import org.lua.commons.customapi.container.LuaLibrariesSet;
import org.lua.commons.customapi.container.LuaLibrary;

public class AllInBasicConfigurationApi implements LuaLibrariesSet {

	public String getNamespace() {
		return "";
	}

	public LuaLibrary[] getLibs() {
		return new LuaLibrary[] { new BasicConfigurationApi(),
				new BitwiseConfigurationApi(), new IOConfigurationApi(),
				new MathConfigurationApi(), new OSConfigurationApi(),
				new StringConfigurationApi(), new TableConfigurationApi() };
	}

}
