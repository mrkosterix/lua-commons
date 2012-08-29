package org.lua.commons.webapi;

import static org.lua.commons.baseapi.BaseLuaTools.doString;
import static org.lua.commons.baseapi.BaseLuaTools.getGlobalsTable;
import static org.lua.commons.baseapi.LuaStack.popFunction;
import static org.lua.commons.baseapi.types.LuaObjectTools.tolua;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.lua.commons.baseapi.Lua;
import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.extensions.references.LuaReferencesStorage;
import org.lua.commons.baseapi.extensions.references.WeakLuaReferencesStorage;
import org.lua.commons.baseapi.extensions.threadpool.SimpleLuaContextThreadPool;
import org.lua.commons.baseapi.types.LuaFunction;
import org.lua.commons.baseapi.types.LuaNumber;
import org.lua.commons.baseapi.types.LuaObject;
import org.lua.commons.baseapi.types.LuaTable;
import org.lua.commons.customapi.javafunctions.handlers.SimpleTypeCastManager;
import org.lua.commons.customapi.javafunctions.handlers.TypeCastManager;
import org.lua.commons.impl.nativelua.NativeLuaStateApiFactory;
import org.lua.commons.nativeapi.LuaException;
import org.lua.commons.nativeapi.LuaStateApiProvider;
import org.lua.commons.nativeapi.LuaStateImpl;
import org.lua.commons.webapi.json.JsonTypeCastManager;
import org.lua.commons.webapi.json.SimpleJsonTypeCastManager;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestFunctionDump {

	Lua lua;
	LuaThread thread;

	@BeforeMethod
	public void initLua() {
		LuaStateApiProvider.setFactory(new NativeLuaStateApiFactory());
		lua = new Lua(new LuaStateImpl(), new SimpleLuaContextThreadPool());
		lua.addExtension(LuaReferencesStorage.class,
				new WeakLuaReferencesStorage(lua));
		lua.addExtension(TypeCastManager.class, new SimpleTypeCastManager());
		lua.addExtension(JsonTypeCastManager.class,
				new SimpleJsonTypeCastManager());
		lua.start();

		thread = lua.contextThread();
		thread.state.openLibs();
	}

	@AfterMethod
	public void closeLua() {
		lua.close();
	}

	@Test
	public void testDumpFunction() throws LuaException {
		doString(lua, "function test(a, b)\nreturn a+b+c\nend");
		thread.state.getGlobal("test");
		LuaFunction function = popFunction(thread);
		FunctionDump dump = FunctionDump.dump(thread, function,
				new LuaObject[] { tolua(thread, 5), tolua(thread, 6) });

		Lua ol = new Lua(new LuaStateImpl(), new SimpleLuaContextThreadPool());
		ol.addExtension(LuaReferencesStorage.class,
				new WeakLuaReferencesStorage(ol));
		ol.addExtension(TypeCastManager.class, new SimpleTypeCastManager());
		ol.addExtension(JsonTypeCastManager.class,
				new SimpleJsonTypeCastManager());
		ol.start();
		LuaThread ot = ol.contextThread();

		LuaTable env = getGlobalsTable(thread);
		env.set(thread, tolua(thread, "c"), tolua(thread, 17));
		LuaFunction of = dump.load(ot);

		LuaNumber result = (LuaNumber) of.call(thread,
				dump.getArguments(thread), 1)[0];
		Assert.assertEquals(result.toInt(), 5 + 6 + 17);
		ol.close();
	}

	@Test
	public void testDumpThroughStream() throws LuaException, IOException {
		doString(lua, "function test(a, b)\nreturn a+b+c\nend");
		thread.state.getGlobal("test");
		LuaFunction function = popFunction(thread);
		FunctionDump dump = FunctionDump.dump(thread, function,
				new LuaObject[] { tolua(thread, 5), tolua(thread, 6) });

		Lua ol = new Lua(new LuaStateImpl(), new SimpleLuaContextThreadPool());
		ol.addExtension(LuaReferencesStorage.class,
				new WeakLuaReferencesStorage(ol));
		ol.addExtension(TypeCastManager.class, new SimpleTypeCastManager());
		ol.addExtension(JsonTypeCastManager.class,
				new SimpleJsonTypeCastManager());
		ol.start();
		LuaThread ot = ol.contextThread();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			dump.toStream(out);
		} finally {
			out.close();
		}
		dump = FunctionDump.fromStream(new ByteArrayInputStream(out
				.toByteArray()));

		LuaTable env = getGlobalsTable(thread);
		env.set(thread, tolua(thread, "c"), tolua(thread, 17));
		LuaFunction of = dump.load(ot);

		LuaNumber result = (LuaNumber) of.call(thread,
				dump.getArguments(thread), 1)[0];
		Assert.assertEquals(result.toInt(), 5 + 6 + 17);
		ol.close();
	}

}
