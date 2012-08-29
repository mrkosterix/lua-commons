package org.lua.commons.baseapi.extensions.threadpool;

import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.extensions.LuaExtension;

public interface LuaContextThreadPool extends LuaExtension {

	public LuaThread getContextThread(LuaThread root);

}
