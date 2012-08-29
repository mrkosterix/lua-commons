package org.lua.commons.baseapi;

import static org.lua.commons.baseapi.LuaStack.popFunction;
import static org.lua.commons.baseapi.LuaStack.pushJavaFunction;
import static org.lua.commons.baseapi.LuaStack.size;
import static org.lua.commons.baseapi.types.LuaObjectTools.fromlua;
import static org.lua.commons.baseapi.types.LuaObjectTools.tolua;

import org.lua.commons.baseapi.extensions.references.LuaReferencesStorage;
import org.lua.commons.baseapi.extensions.references.WeakLuaReferencesStorage;
import org.lua.commons.baseapi.extensions.threadpool.SimpleLuaContextThreadPool;
import org.lua.commons.baseapi.functions.Functions.Function1;
import org.lua.commons.baseapi.types.LuaFunction;
import org.lua.commons.baseapi.types.LuaNumber;
import org.lua.commons.baseapi.types.LuaObject;
import org.lua.commons.baseapi.types.LuaTable;
import org.lua.commons.impl.nativelua.NativeLuaStateApiFactory;
import org.lua.commons.nativeapi.LuaException;
import org.lua.commons.nativeapi.LuaStateApiProvider;
import org.lua.commons.nativeapi.LuaStateImpl;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestFunction {

	Lua lua;
	LuaThread thread;

	@BeforeMethod
	public void initLua() {
		LuaStateApiProvider.setFactory(new NativeLuaStateApiFactory());
		lua = new Lua(new LuaStateImpl(), new SimpleLuaContextThreadPool());
		lua.addExtension(LuaReferencesStorage.class,
				new WeakLuaReferencesStorage(lua));
		lua.start();

		thread = lua.contextThread();
	}

	@AfterMethod
	public void closeLua() {
		lua.close();
	}

	@Test
	public void testCallLuaFunctionWithEnvironment() throws LuaException {
		Assert.assertEquals(size(thread), 0);
		thread.state.doString("globa = 1\n globb = 8");
		thread.state.loadString("globres = globa + globb");
		LuaFunction function = popFunction(thread);

		LuaTable env = LuaTable.newTable(thread);
		env.set(thread, tolua(thread, "globa"), tolua(thread, 4));
		env.set(thread, tolua(thread, "globb"), tolua(thread, 12));
		function.setEnv(thread, env);

		Assert.assertEquals(size(thread), 0);
		function.call(thread, new LuaObject[0], 0);
		Assert.assertEquals(size(thread), 0);
		Assert.assertEquals(fromlua(env.get(thread, tolua(thread, "globres"))),
				4.0 + 12.0);
	}

	@Test
	public void testCallJavaFunctionWithEnvironment() throws LuaException {
		Assert.assertEquals(size(thread), 0);
		thread.state.doString("globa = \"Goodbye, \"\n globb = \"world!\"");

		LuaTable env = LuaTable.newTable(thread);
		env.set(thread, tolua(thread, "globa"), tolua(thread, "Hello, "));
		env.set(thread, tolua(thread, "globb"), tolua(thread, "world!!!"));

		pushJavaFunction(thread, new Function1<LuaThread, Integer>() {

			public Integer invoke(LuaThread thread) {
				thread.state.getGlobal("globa");
				thread.state.getGlobal("globb");
				thread.state.concat(2);
				thread.state.setGlobal("globres");
				return 0;
			}
		});
		LuaFunction function = popFunction(thread);
		function.setEnv(thread, env);

		Assert.assertEquals(size(thread), 0);
		function.call(thread, new LuaObject[0], 0);
		Assert.assertEquals(size(thread), 0);
		Assert.assertEquals(fromlua(env.get(thread, tolua(thread, "globres"))),
				"Hello, world!!!");
	}

	@Test
	public void testGetUpvaluesForFunctions() {
		Assert.assertEquals(size(thread), 0);
		thread.state
				.doString("function test(a, b)\nreturn function()\nreturn a+b\nend\nend\n");
		thread.state.doString("clos = test(5, 6)");
		thread.state.getGlobal("clos");
		LuaFunction function = popFunction(thread);
		String[] upvalues = function.getUpvalueNames(thread);
		Assert.assertEquals(upvalues.length, 2);
		Assert.assertEquals(upvalues[0], "a");
		Assert.assertEquals(upvalues[1], "b");
	}

	@Test
	public void testSetUpvaluesForFunctions() throws LuaException {
		Assert.assertEquals(size(thread), 0);
		thread.state
				.doString("function test(a, b)\nreturn function()\nreturn a+b\nend\nend\n");
		thread.state.doString("clos = test(5, 6)");
		thread.state.getGlobal("clos");
		LuaFunction function = popFunction(thread);
		function.setUpvalue(thread, 1, LuaNumber.valueOf(thread, 11));
		function.setUpvalue(thread, 2, LuaNumber.valueOf(thread, 24));
		LuaNumber res = (LuaNumber) function.call(thread, new LuaObject[0], 1)[0];
		Assert.assertEquals(res.toInt(), 11 + 24);
	}

}
