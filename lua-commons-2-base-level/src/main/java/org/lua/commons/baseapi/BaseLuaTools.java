package org.lua.commons.baseapi;

import java.io.File;

import org.lua.commons.baseapi.types.LuaTable;
import org.lua.commons.nativeapi.LuaException;

public class BaseLuaTools {

	public static void openLibs(Lua lua) {
		openLibs(lua.contextThread());
	}

	public static void openLibs(LuaThread thread) {
		thread.state.openLibs();
	}

	public static void doFile(Lua lua, File file) throws LuaException {
		doFile(lua.contextThread(), file);
	}

	public static void doFile(LuaThread thread, File file) throws LuaException {
		String path = file.getAbsolutePath();
		if (path == null)
			throw new LuaException(
					"Couldn't execute file, absolute path is null.");
		int result = thread.state.doFile(path);
		if (result != 0) {
			String message = thread.state.toString(-1);
			throw new LuaException("File ($path) execution failed with status "
					+ result + " and message " + message);
		}
	}

	public static void doString(Lua lua, String script) throws LuaException {
		doString(lua.contextThread(), script);
	}

	public static void doString(LuaThread thread, String script)
			throws LuaException {
		int result = thread.state.doString(script);
		if (result != 0) {
			String message = thread.state.toString(-1);
			throw new LuaException("Script execution failed with status "
					+ result + " and message " + message);
		}
	}

	public static void loadFile(Lua lua, File file) throws LuaException {
		loadFile(lua.contextThread(), file);
	}

	public static void loadFile(LuaThread thread, File file)
			throws LuaException {
		String path = file.getAbsolutePath();
		if (path == null)
			throw new LuaException("Couldn't load file, absolute path is null.");
		int result = thread.state.loadFile(path);
		if (result != 0) {
			String message = thread.state.toString(-1);
			throw new LuaException("File ($path) execution failed with status "
					+ result + " and message " + message);
		}
	}

	public static void loadString(Lua lua, String script) throws LuaException {
		loadString(lua.contextThread(), script);
	}

	public static void loadString(LuaThread thread, String script)
			throws LuaException {
		int result = thread.state.loadString(script);
		if (result != 0) {
			String message = thread.state.toString(-1);
			throw new LuaException("Script loading failed with status "
					+ result + " and message " + message);
		}
	}

	public static LuaTable getGlobalsTable(LuaThread thread) {
		thread.state.checkStack(1);
		thread.state.rawGetI(thread.state.getLUA_REGISTRYINDEX(),
				thread.state.getLUA_RIDX_GLOBALS());
		return LuaStack.popTable(thread);
	}

	public static void setGlobalsTable(LuaThread thread, LuaTable env) {
		thread.state.checkStack(2);
		env.push(thread);
		thread.state.rawSetI(thread.state.getLUA_REGISTRYINDEX(),
				thread.state.getLUA_RIDX_GLOBALS());
	}

}
