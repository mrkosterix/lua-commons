package org.lua.commons.configuration;

import static org.lua.commons.baseapi.BaseLuaTools.loadFile;
import static org.lua.commons.baseapi.LuaStack.popFunction;

import java.io.File;

import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.types.LuaObject;
import org.lua.commons.customapi.container.LuaContainer;
import org.lua.commons.nativeapi.LuaException;

public class FileLuaConfiguration extends LuaConfiguration {

	public FileLuaConfiguration(LuaContainer container,
			File source) {
		super(container);

		try {
			LuaThread thread = lua.contextThread();

			loadFile(thread, source);
			table.push(thread);
			thread.state.setUpValue(-2, 1);
			popFunction(thread).call(thread, new LuaObject[0], 0);
		} catch (LuaException e) {
			throw new LuaConfigurationException(
					"Couldn't initialize file lua configuration.", e);
		}

	}

}