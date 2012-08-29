package org.lua.commons.webapi.json;

import static org.lua.commons.baseapi.types.LuaObjectTools.tolua;

import org.lua.commons.baseapi.Lua;
import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.extensions.metatables.LuaMetatablesService;
import org.lua.commons.baseapi.extensions.metatables.SimpleLuaMetatablesService;
import org.lua.commons.baseapi.extensions.references.LuaReferencesStorage;
import org.lua.commons.baseapi.extensions.references.WeakLuaReferencesStorage;
import org.lua.commons.baseapi.extensions.threadpool.SimpleLuaContextThreadPool;
import org.lua.commons.baseapi.types.LuaBoolean;
import org.lua.commons.baseapi.types.LuaNumber;
import org.lua.commons.baseapi.types.LuaString;
import org.lua.commons.baseapi.types.LuaTable;
import org.lua.commons.customapi.javafunctions.handlers.SimpleTypeCastManager;
import org.lua.commons.customapi.javafunctions.handlers.TypeCastManager;
import org.lua.commons.impl.nativelua.NativeLuaStateApiFactory;
import org.lua.commons.nativeapi.LuaStateApiProvider;
import org.lua.commons.nativeapi.LuaStateImpl;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestSimpleJsonTypeCastManager {

	private JsonTypeCastManager castManager;

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
		castManager = new SimpleJsonTypeCastManager();
	}

	@AfterMethod
	public void closeLua() {
		lua.close();
	}

	@Test
	public void testSimpleToJson() {
		Assert.assertEquals(
				castManager.toJson(thread, LuaBoolean.valueOf(thread, true)),
				"true");
		Assert.assertEquals(
				castManager.toJson(thread, LuaBoolean.valueOf(thread, false)),
				"false");
		Assert.assertEquals(
				castManager.toJson(thread, LuaNumber.valueOf(thread, 1234.77)),
				"1234.77");
		Assert.assertEquals(
				castManager.toJson(thread,
						LuaString.valueOf(thread, "Hello, world!")),
				"\"Hello, world!\"");
	}

	@Test
	public void testTableToJson() {
		LuaTable table = LuaTable.newTable(thread);
		table.set(thread, tolua(thread, "key1"), tolua(thread, 5));
		table.set(thread, tolua(thread, "key2"), tolua(thread, 9));
		table.set(thread, tolua(thread, 3), tolua(thread, 14));
		table.set(thread, tolua(thread, "key4"), tolua(thread, true));
		table.set(thread, tolua(thread, "key5"), null);
		table.set(thread, tolua(thread, "key6"), tolua(thread, "asdf"));
		Assert.assertEquals(castManager.toJson(thread, table),
				"{\"key6\":\"asdf\",\"key2\":9,\"key1\":5,3:14,\"key4\":true}");
	}

	@Test
	public void testToJson() {
		LuaTable table = LuaTable.newTable(thread);
		table.set(thread, tolua(thread, "key1"), tolua(thread, 5));
		LuaTable value2 = LuaTable.newTable(thread);
		value2.set(thread, tolua(thread, "k1"), tolua(thread, "v1"));
		value2.set(thread, tolua(thread, "k2"), tolua(thread, "v2"));
		table.set(thread, tolua(thread, "key2"), value2);
		Assert.assertEquals(castManager.toJson(thread, table),
				"{\"key1\":5,\"key2\":{\"k1\":\"v1\",\"k2\":\"v2\"}}");
	}

	@Test
	public void testSimpleToLua() {
		Assert.assertEquals(
				((LuaBoolean) castManager.toLua(thread, "true")).toBoolean(),
				true);
		Assert.assertEquals(
				((LuaBoolean) castManager.toLua(thread, "false")).toBoolean(),
				false);
		Assert.assertEquals(
				((LuaNumber) castManager.toLua(thread, "1234.777")).toDouble(),
				1234.777);
		Assert.assertEquals(((LuaString) castManager.toLua(thread,
				"\"Hello, world!\"")).toString(), "Hello, world!");
	}

	@Test
	public void testTableToLua() {
		LuaTable table = (LuaTable) castManager.toLua(thread,
				"{'key1': 'value1', 'key2': 7.7}");
		Assert.assertEquals(((LuaString) table.get(thread,
				tolua(thread, "key1"))).toString(), "value1");
		Assert.assertEquals(((LuaNumber) table.get(thread,
				tolua(thread, "key2"))).toDouble(), 7.7);
	}

	@Test
	public void testToLua() {
		LuaTable table = (LuaTable) castManager.toLua(thread,
				"{'key1': 'value1', 'key2': {'k1':'v1',  'k2': 'v2'}}");
		Assert.assertEquals(((LuaString) table.get(thread,
				tolua(thread, "key1"))).toString(), "value1");
		LuaTable v2 = (LuaTable) table.get(thread, tolua(thread, "key2"));
		Assert.assertEquals(
				((LuaString) v2.get(thread, tolua(thread, "k1"))).toString(),
				"v1");
		Assert.assertEquals(
				((LuaString) v2.get(thread, tolua(thread, "k2"))).toString(),
				"v2");
	}

	@Test
	public void testToJsonToLua() {
		LuaTable table = LuaTable.newTable(thread);
		table.set(thread, tolua(thread, "key1"), tolua(thread, "value1"));
		LuaTable value2 = LuaTable.newTable(thread);
		value2.set(thread, tolua(thread, "k1"), tolua(thread, "v1"));
		value2.set(thread, tolua(thread, "k2"), tolua(thread, "v2"));
		table.set(thread, tolua(thread, "key2"), value2);
		LuaTable result = (LuaTable) castManager.toLua(thread,
				castManager.toJson(thread, table));
		Assert.assertEquals(((LuaString) table.get(thread,
				tolua(thread, "key1"))).toString(), ((LuaString) result.get(
				thread, tolua(thread, "key1"))).toString());
		Assert.assertEquals(((LuaString) ((LuaTable) table.get(thread,
				tolua(thread, "key2"))).get(thread, tolua(thread, "k1")))
				.toString(), ((LuaString) ((LuaTable) result.get(thread,
				tolua(thread, "key2"))).get(thread, tolua(thread, "k1")))
				.toString());
		Assert.assertEquals(((LuaString) ((LuaTable) table.get(thread,
				tolua(thread, "key2"))).get(thread, tolua(thread, "k2")))
				.toString(), ((LuaString) ((LuaTable) result.get(thread,
				tolua(thread, "key2"))).get(thread, tolua(thread, "k2")))
				.toString());
	}
}
