package org.lua.commons.baseapi;

import static org.lua.commons.baseapi.LuaStack.popFunction;
import static org.lua.commons.baseapi.LuaStack.pushJavaClosure;
import static org.lua.commons.baseapi.LuaStack.pushJavaFunction;
import static org.lua.commons.baseapi.LuaStack.size;

import org.lua.commons.baseapi.extensions.references.LuaReferencesStorage;
import org.lua.commons.baseapi.extensions.references.WeakLuaReferencesStorage;
import org.lua.commons.baseapi.extensions.threadpool.SimpleLuaContextThreadPool;
import org.lua.commons.baseapi.functions.Functions.Function1;
import org.lua.commons.baseapi.types.LuaFunction;
import org.lua.commons.baseapi.types.LuaObject;
import org.lua.commons.baseapi.types.LuaString;
import org.lua.commons.impl.nativelua.NativeLuaStateApiFactory;
import org.lua.commons.nativeapi.LuaException;
import org.lua.commons.nativeapi.LuaStateApiProvider;
import org.lua.commons.nativeapi.LuaStateImpl;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestJavaFunction {

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
	public void testBaseJavaFunction() throws LuaException {
		Assert.assertEquals(size(thread), 0);
		pushJavaFunction(thread, new Function1<LuaThread, Integer>() {
			public Integer invoke(LuaThread thread) {
				thread.state.concat(2);
				return 1;
			}
		});
		LuaFunction function = popFunction(thread);
		Assert.assertEquals(size(thread), 0);
		LuaObject[] results = function.call(thread,
				new LuaObject[] { LuaString.valueOf(thread, "Hello, "),
						LuaString.valueOf(thread, "world!") }, 1);
		Assert.assertEquals(size(thread), 0);
		Assert.assertEquals(results.length, 1);
		Assert.assertEquals(((LuaString) results[0]).toString(thread),
				"Hello, world!");
	}

	@Test
	public void testBaseJavaClosure() throws LuaException {
		Assert.assertEquals(size(thread), 0);
		pushJavaClosure(thread,
				new LuaObject[] { LuaString.valueOf(thread, "Hello, "),
						LuaString.valueOf(thread, "world!") },
				new Function1<LuaThread, Integer>() {
					public Integer invoke(LuaThread thread) {
						thread.state.concat(3);
						return 1;
					}
				});
		LuaFunction function = popFunction(thread);
		Assert.assertEquals(size(thread), 0);
		LuaObject[] results = function.call(thread,
				new LuaObject[] { LuaString.valueOf(thread, "!!") }, 1);
		Assert.assertEquals(size(thread), 0);
		Assert.assertEquals(results.length, 1);
		Assert.assertEquals(((LuaString) results[0]).toString(thread),
				"Hello, world!!!");
	}

}
