package org.lua.commons.customapi.container;

import static org.lua.commons.baseapi.BaseLuaThread.newThread;
import static org.lua.commons.baseapi.BaseLuaTools.getGlobalsTable;
import static org.lua.commons.baseapi.types.LuaObjectTools.tolua;
import static org.lua.commons.customapi.sandbox.SandboxLuaThread.safeThread;

import java.util.Set;

import org.lua.commons.baseapi.Lua;
import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.extensions.metatables.LuaMetatablesService;
import org.lua.commons.baseapi.extensions.references.LuaReferencesStorage;
import org.lua.commons.baseapi.extensions.references.WeakLuaReferencesStorage;
import org.lua.commons.baseapi.extensions.threadpool.SimpleLuaContextThreadPool;
import org.lua.commons.baseapi.functions.Functions.Function1;
import org.lua.commons.baseapi.types.LuaObject;
import org.lua.commons.baseapi.types.LuaTable;
import org.lua.commons.customapi.javafunctions.CustomJavaFunction;
import org.lua.commons.customapi.javafunctions.LuaInject;
import org.lua.commons.customapi.javafunctions.handlers.SimpleTypeCastManager;
import org.lua.commons.customapi.javafunctions.handlers.TypeCastManager;
import org.lua.commons.customapi.sandbox.LuaEnvironmentTable;
import org.lua.commons.customapi.sandbox.SafeLua;
import org.lua.commons.customapi.sandbox.SafeLuaStateWrapper;
import org.lua.commons.nativeapi.LuaException;

public class LuaLibraryEntry {

	private final LuaLibrary lib;
	private final Set<String> includes;
	private final Set<String> excludes;

	public LuaLibraryEntry(LuaLibrary lib, Set<String> includes,
			Set<String> excludes) {
		this.lib = lib;
		this.includes = includes;
		this.excludes = excludes;
	}

	public boolean isAllowed(String name) {
		return (includes == null || includes.contains(name))
				&& (excludes == null || !excludes.contains(name));
	}

	public void prepare(Lua lua, LuaTable global, String namespace) {
		LuaThread thread = lua.contextThread();

		LuaTable table = global;
		if (!namespace.isEmpty()) {
			table = LuaTable.newTable(thread);
		}

		thread.state.openLibs();
		LuaTable readEnv = getGlobalsTable(thread);
		LuaEnvironmentTable env = LuaEnvironmentTable.newTable(readEnv, thread);

		Function1<LuaThread, Void> funcobj = new Function1<LuaThread, Void>() {
			public Void invoke(@LuaInject LuaThread thread) {
				lib.prepare(thread);
				return null;
			}
		};

		CustomJavaFunction function = new CustomJavaFunction(funcobj, false,
				false);

		LuaThread newthread = newThread(thread);
		SafeLua safelua = new SafeLua(newthread.state,
				new SimpleLuaContextThreadPool(), SafeLuaStateWrapper.class);
		safelua.addExtension(LuaReferencesStorage.class,
				new WeakLuaReferencesStorage(safelua));
		safelua.addExtension(LuaMetatablesService.class,
				lua.getExtension(LuaMetatablesService.class));
		safelua.addExtension(TypeCastManager.class, new SimpleTypeCastManager());
		safelua.start();
		LuaThread safethread = safeThread(newthread, safelua,
				SafeLuaStateWrapper.class);

		try {
			function.function(safethread, env).call(safethread,
					new LuaObject[] { tolua(thread, funcobj) }, 0);
		} catch (LuaException e) {
			throw new LuaLibraryException("Library " + lib.getClass().getName()
					+ " binding to namespace " + namespace + " failed.", e);
		}

		for (String name : lib.getNames()) {
			if (isAllowed(name)) {
				LuaObject value = env.get(thread, tolua(thread, name));
				table.set(thread, tolua(thread, name), value);
			}
		}

		if (!namespace.isEmpty())
			global.set(thread, tolua(thread, namespace), table);
	}

}
