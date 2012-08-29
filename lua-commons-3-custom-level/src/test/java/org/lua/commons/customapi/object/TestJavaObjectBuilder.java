package org.lua.commons.customapi.object;

import static org.lua.commons.baseapi.BaseLuaTools.doString;
import static org.lua.commons.baseapi.LuaStack.popInt;
import static org.lua.commons.baseapi.LuaStack.popString;
import static org.lua.commons.baseapi.LuaStack.pushObject;

import org.lua.commons.baseapi.Lua;
import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.extensions.metatables.LuaMetatablesService;
import org.lua.commons.baseapi.extensions.metatables.SimpleLuaMetatablesService;
import org.lua.commons.baseapi.extensions.references.LuaReferencesStorage;
import org.lua.commons.baseapi.extensions.references.WeakLuaReferencesStorage;
import org.lua.commons.baseapi.extensions.threadpool.SimpleLuaContextThreadPool;
import org.lua.commons.baseapi.types.LuaMetatable;
import org.lua.commons.customapi.javafunctions.handlers.SimpleTypeCastManager;
import org.lua.commons.customapi.javafunctions.handlers.TypeCastManager;
import org.lua.commons.impl.nativelua.NativeLuaStateApiFactory;
import org.lua.commons.nativeapi.LuaException;
import org.lua.commons.nativeapi.LuaRuntimeException;
import org.lua.commons.nativeapi.LuaStateApiProvider;
import org.lua.commons.nativeapi.LuaStateImpl;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestJavaObjectBuilder {

	Lua lua;
	LuaThread thread;

	TestObject object;

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

		JavaObjectBuilder builder = new JavaObjectBuilder();
		builder.addField("hello", TestObject.class.getField("field2"), false);
		builder.addField("num", TestObject.class.getDeclaredField("num"), false);
		builder.addField("n", TestObject.class.getDeclaredField("n"), true);
		builder.addField("v", TestObject.class.getDeclaredField("v"), false);

		builder.addMethod("ping", TestObject.class.getMethod("ping"));

		LuaMetatable metatable = builder.build(thread);
		thread.lua.getExtension(LuaMetatablesService.class).register(
				TestObject.class, metatable);

		object = new TestObject();
		pushObject(thread, object);
		thread.state.setGlobal("obj");
	}

	@AfterMethod
	public void closeLua() {
		lua.close();
	}

	private static class TestObject {

		public String field1 = "SECRED";

		public String field2 = "Hello, World";

		private final int num = 42;

		private int n = 17;

		private int v = 94;

		public String ping() {
			return "pong";
		}

	}

	@Test
	public void testJavaObjectBuilder() throws NoSuchMethodException,
			LuaException, SecurityException, NoSuchFieldException {

		doString(lua, "hellores = obj.hello");
		thread.state.getGlobal("hellores");
		Assert.assertEquals(popString(thread), object.field2);

		doString(lua, "obj.hello = \"Hello, from Lua!\"");
		Assert.assertEquals(object.field2, "Hello, from Lua!");

		doString(lua, "numres = obj.num");
		thread.state.getGlobal("numres");
		Assert.assertEquals(popInt(thread), object.num);

		doString(lua, "nres = obj.n");
		thread.state.getGlobal("nres");
		Assert.assertEquals(popInt(thread), object.n);

		doString(lua, "vres = obj.v");
		thread.state.getGlobal("vres");
		Assert.assertEquals(popInt(thread), object.v);

		doString(lua, "obj.v = 11");
		Assert.assertEquals(object.v, 11);

		doString(lua, "pongres = obj:ping()");
		thread.state.getGlobal("pongres");
		Assert.assertEquals(popString(thread), object.ping());
	}

	@Test(expectedExceptions = { LuaRuntimeException.class })
	public void testWriteToNotAdded() throws SecurityException,
			NoSuchFieldException, NoSuchMethodException, LuaException {
		doString(lua, "res = obj.field1");
	}

	@Test(expectedExceptions = { LuaRuntimeException.class })
	public void testWriteToFinalField() throws SecurityException,
			NoSuchFieldException, NoSuchMethodException, LuaException {
		doString(lua, "obj.num = 7");
	}

	@Test(expectedExceptions = { LuaRuntimeException.class })
	public void testWriteToReadonlyField() throws SecurityException,
			NoSuchFieldException, NoSuchMethodException, LuaException {
		doString(lua, "obj.n = 7");
	}

	@Test(expectedExceptions = { LuaRuntimeException.class })
	public void testWriteToMethod() throws SecurityException,
			NoSuchFieldException, NoSuchMethodException, LuaException {
		doString(lua, "obj.pong = 7");
	}

}
