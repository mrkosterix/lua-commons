package org.lua.commons.baseapi;

import static org.lua.commons.baseapi.LuaStack.popDouble;
import static org.lua.commons.baseapi.LuaStack.popInt;
import static org.lua.commons.baseapi.LuaStack.pushObject;
import static org.lua.commons.baseapi.LuaStack.size;
import static org.lua.commons.baseapi.types.LuaObjectTools.tolua;

import org.lua.commons.baseapi.extensions.metatables.LuaMetatablesService;
import org.lua.commons.baseapi.extensions.metatables.SimpleLuaMetatablesService;
import org.lua.commons.baseapi.extensions.references.LuaReferencesStorage;
import org.lua.commons.baseapi.extensions.references.WeakLuaReferencesStorage;
import org.lua.commons.baseapi.extensions.threadpool.SimpleLuaContextThreadPool;
import org.lua.commons.baseapi.functions.Functions.Function3;
import org.lua.commons.baseapi.types.LuaJavaObject;
import org.lua.commons.baseapi.types.LuaMetatable;
import org.lua.commons.baseapi.types.LuaObject;
import org.lua.commons.impl.nativelua.NativeLuaStateApiFactory;
import org.lua.commons.nativeapi.LuaStateApiProvider;
import org.lua.commons.nativeapi.LuaStateImpl;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestMetatableService {

	Lua lua;
	LuaThread thread;

	@BeforeMethod
	public void initLua() {
		LuaStateApiProvider.setFactory(new NativeLuaStateApiFactory());
		lua = new Lua(new LuaStateImpl(), new SimpleLuaContextThreadPool());
		lua.addExtension(LuaReferencesStorage.class,
				new WeakLuaReferencesStorage(lua));
		lua.addExtension(LuaMetatablesService.class,
				new SimpleLuaMetatablesService());
		lua.start();

		thread = lua.contextThread();
	}

	@AfterMethod
	public void closeLua() {
		lua.close();
	}

	@Test
	public void testMetatableService() {
		Assert.assertEquals(size(thread), 0);
		LuaMetatable metatable = LuaMetatable.newMetatable(thread);
		metatable.setAdd(thread,
				new Function3<LuaThread, LuaObject, LuaObject, LuaObject>() {
					public LuaObject invoke(LuaThread thread, LuaObject o1,
							LuaObject o2) {
						int a = (Integer) ((LuaJavaObject) o1).toObject(thread);
						int b = (Integer) ((LuaJavaObject) o2).toObject(thread);
						return tolua(thread, a + b);
					}
				});
		lua.getExtension(LuaMetatablesService.class).register(Integer.class,
				metatable);

		pushObject(thread, Integer.valueOf(5));
		thread.state.setGlobal("a");
		pushObject(thread, Integer.valueOf(7));
		thread.state.setGlobal("b");

		Assert.assertEquals(size(thread), 0);
		thread.state.doString("res = a + b");
		Assert.assertEquals(size(thread), 0);

		thread.state.getGlobal("res");
		Assert.assertEquals(popInt(thread), 5 + 7);

		Assert.assertEquals(size(thread), 0);
	}

	@Test
	public void testMetatableServiceWithSuperClasses() {
		Assert.assertEquals(size(thread), 0);
		LuaMetatable metatable = LuaMetatable.newMetatable(thread);
		metatable.setAdd(thread,
				new Function3<LuaThread, LuaObject, LuaObject, LuaObject>() {
					public LuaObject invoke(LuaThread thread, LuaObject o1,
							LuaObject o2) {
						Number a = (Number) ((LuaJavaObject) o1)
								.toObject(thread);
						Number b = (Number) ((LuaJavaObject) o2)
								.toObject(thread);
						return tolua(thread, a.doubleValue() + b.doubleValue());
					}
				});
		lua.getExtension(LuaMetatablesService.class).register(Number.class,
				metatable);

		pushObject(thread, Integer.valueOf(5));
		thread.state.setGlobal("a");
		pushObject(thread, Double.valueOf(7.1));
		thread.state.setGlobal("b");

		Assert.assertEquals(size(thread), 0);
		thread.state.doString("res = a + b");
		Assert.assertEquals(size(thread), 0);

		thread.state.getGlobal("res");
		Assert.assertEquals(popDouble(thread), 5 + 7.1);

		Assert.assertEquals(size(thread), 0);
	}

}
