package org.lua.commons.customapi.container;

import static org.lua.commons.baseapi.BaseLuaTools.doString;
import static org.lua.commons.baseapi.BaseLuaTools.getGlobalsTable;
import static org.lua.commons.baseapi.LuaStack.popString;

import org.lua.commons.baseapi.Lua;
import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.extensions.metatables.LuaMetatablesService;
import org.lua.commons.baseapi.extensions.metatables.SimpleLuaMetatablesService;
import org.lua.commons.baseapi.extensions.references.LuaReferencesStorage;
import org.lua.commons.baseapi.extensions.references.WeakLuaReferencesStorage;
import org.lua.commons.baseapi.extensions.threadpool.SimpleLuaContextThreadPool;
import org.lua.commons.baseapi.types.LuaTable;
import org.lua.commons.customapi.javafunctions.handlers.SimpleTypeCastManager;
import org.lua.commons.customapi.javafunctions.handlers.TypeCastManager;
import org.lua.commons.customapi.object.annotations.LuaMember;
import org.lua.commons.customapi.object.annotations.LuaResult;
import org.lua.commons.impl.nativelua.NativeLuaStateApiFactory;
import org.lua.commons.nativeapi.LuaException;
import org.lua.commons.nativeapi.LuaStateApiProvider;
import org.lua.commons.nativeapi.LuaStateImpl;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestLuaContainer {

	Lua lua;
	LuaThread thread;

	@BeforeMethod
	public void initLua() throws SecurityException, NoSuchFieldException,
			NoSuchMethodException {
		LuaStateApiProvider.setFactory(new NativeLuaStateApiFactory());
		lua = new Lua(new LuaStateImpl(), new SimpleLuaContextThreadPool());
		lua.addExtension(LuaReferencesStorage.class,
				new WeakLuaReferencesStorage(lua));
		lua.addExtension(LuaMetatablesService.class,
				new SimpleLuaMetatablesService());
		lua.addExtension(TypeCastManager.class, new SimpleTypeCastManager());
		lua.start();

		thread = lua.contextThread();
		thread.state.openLibs();
	}

	@AfterMethod
	public void closeLua() {
		lua.close();
	}

	@Test
	public void testContainer() throws LuaException {
		LuaContainer container = new LuaContainer();
		container.addClass(TestClass.class);
		container.addObject("TC", new TestClass(" ,olleH"));

		LuaTable table = getGlobalsTable(thread);
		container.prepare(lua, table);

		doString(lua, "res = TC:concat(TC:reverse(TC.HELLO), TC.WORLD)");
		thread.state.getGlobal("res");
		Assert.assertEquals("Hello, world!", popString(thread));
	}

	static class TestSuperClass {

		@LuaMember
		public final String WORLD = "world!";

		@LuaMember(name = "concat")
		public @LuaResult
		String concat(String str1, String str2) {
			return str1 + str2;
		}

	}

	static class TestClass extends TestSuperClass {

		@LuaMember
		public final String HELLO;

		public TestClass(String hello) {
			this.HELLO = hello;
		}

		@LuaMember(name = "reverse")
		public String reverse(String str) {
			StringBuilder builder = new StringBuilder();
			for (int i = str.length() - 1; i >= 0; i--)
				builder.append(str.charAt(i));
			return builder.toString();
		}
	}

}
