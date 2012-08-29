package org.lua.commons.baseapi;

import static org.lua.commons.baseapi.LuaStack.popReference;
import static org.lua.commons.baseapi.LuaStack.pushDouble;
import static org.lua.commons.baseapi.LuaStack.pushInt;
import static org.lua.commons.baseapi.LuaStack.pushLong;
import static org.lua.commons.baseapi.LuaStack.pushString;
import static org.lua.commons.baseapi.LuaStack.size;
import static org.lua.commons.baseapi.types.LuaObjectTools.tolua;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lua.commons.baseapi.extensions.references.LuaReferencesStorage;
import org.lua.commons.baseapi.extensions.references.WeakLuaReferencesStorage;
import org.lua.commons.baseapi.extensions.threadpool.SimpleLuaContextThreadPool;
import org.lua.commons.baseapi.types.LuaObject;
import org.lua.commons.baseapi.types.LuaString;
import org.lua.commons.baseapi.types.LuaTable;
import org.lua.commons.baseapi.types.LuaTable.LuaTableEntry;
import org.lua.commons.impl.nativelua.NativeLuaStateApiFactory;
import org.lua.commons.nativeapi.LuaStateApiProvider;
import org.lua.commons.nativeapi.LuaStateImpl;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestLuaTable {

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
	public void testGetIfKeyNotExists() {
		thread.state.newTable();
		LuaObject object = popReference(thread);
		Assert.assertTrue(object instanceof LuaTable);
		LuaTable table = (LuaTable) object;
		int before = size(thread);
		Assert.assertNull(table.get(thread, tolua(thread, "key")));
		Assert.assertEquals(size(thread), before);
	}

	@Test
	public void testGetIfKeyExists() {
		thread.state.newTable();
		thread.state.pushString("value");
		thread.state.setField(-2, "key");
		LuaObject object = popReference(thread);
		Assert.assertTrue(object instanceof LuaTable);
		LuaTable table = (LuaTable) object;
		int before = size(thread);
		Assert.assertEquals(((LuaString) table
				.get(thread, tolua(thread, "key"))).toString(thread), "value");
		Assert.assertEquals(size(thread), before);
	}

	@Test
	public void testGetIfKeyIsInt() {
		thread.state.newTable();
		pushInt(thread, 5);
		thread.state.pushString("value");
		thread.state.setTable(-3);
		LuaObject object = popReference(thread);
		Assert.assertTrue(object instanceof LuaTable);
		LuaTable table = (LuaTable) object;
		int before = size(thread);
		Assert.assertEquals(((LuaString) table.get(thread, tolua(thread, 5)))
				.toString(thread), "value");
		Assert.assertEquals(size(thread), before);
	}

	@Test
	public void testGetIfKeyIsLong() {
		thread.state.newTable();
		pushLong(thread, 1234567890123456L);
		thread.state.pushString("value");
		thread.state.setTable(-3);
		LuaObject object = popReference(thread);
		Assert.assertTrue(object instanceof LuaTable);
		LuaTable table = (LuaTable) object;
		int before = size(thread);
		Assert.assertEquals(((LuaString) table.get(thread,
				tolua(thread, 1234567890123456L))).toString(thread), "value");
		Assert.assertEquals(size(thread), before);
	}

	@Test
	public void testGetIfKeyIsDouble() {
		thread.state.newTable();
		pushDouble(thread, 55.3);
		thread.state.pushString("value");
		thread.state.setTable(-3);
		LuaObject object = popReference(thread);
		Assert.assertTrue(object instanceof LuaTable);
		LuaTable table = (LuaTable) object;
		int before = size(thread);
		Assert.assertEquals(
				((LuaString) table.get(thread, tolua(thread, 55.3)))
						.toString(thread), "value");
		Assert.assertEquals(size(thread), before);
	}

	@Test
	public void testSetIfKeyDoesntExist() {
		thread.state.newTable();
		LuaObject object = popReference(thread);
		Assert.assertTrue(object instanceof LuaTable);
		LuaTable table = (LuaTable) object;
		Assert.assertNull(table.get(thread, tolua(thread, "key")));
		int before = size(thread);
		table.set(thread, tolua(thread, "key"), tolua(thread, "value"));
		Assert.assertEquals(size(thread), before);
		Assert.assertEquals(((LuaString) table
				.get(thread, tolua(thread, "key"))).toString(thread), "value");
	}

	@Test
	public void testSetIfKeyExist() {
		thread.state.newTable();
		pushString(thread, "key");
		pushString(thread, "value1");
		thread.state.setTable(-3);
		LuaObject object = popReference(thread);
		Assert.assertTrue(object instanceof LuaTable);
		LuaTable table = (LuaTable) object;
		Assert.assertEquals(((LuaString) table
				.get(thread, tolua(thread, "key"))).toString(thread), "value1");
		int before = size(thread);
		table.set(thread, tolua(thread, "key"), tolua(thread, "value2"));
		Assert.assertEquals(size(thread), before);
		Assert.assertEquals(((LuaString) table
				.get(thread, tolua(thread, "key"))).toString(thread), "value2");
	}

	@Test
	public void testSetIfKeyIsInt() {
		thread.state.newTable();
		LuaObject object = popReference(thread);
		Assert.assertTrue(object instanceof LuaTable);
		LuaTable table = (LuaTable) object;
		int before = size(thread);
		table.set(thread, tolua(thread, 5), tolua(thread, "value"));
		Assert.assertEquals(size(thread), before);
		Assert.assertEquals(((LuaString) table.get(thread, tolua(thread, 5)))
				.toString(thread), "value");
	}

	@Test
	public void testSetIfKeyIsLong() {
		thread.state.newTable();
		LuaObject object = popReference(thread);
		Assert.assertTrue(object instanceof LuaTable);
		LuaTable table = (LuaTable) object;
		int before = size(thread);
		table.set(thread, tolua(thread, 1234567890123456L),
				tolua(thread, "value"));
		Assert.assertEquals(size(thread), before);
		Assert.assertEquals(((LuaString) table.get(thread,
				tolua(thread, 1234567890123456L))).toString(thread), "value");
	}

	@Test
	public void testSetIfKeyIsDouble() {
		thread.state.newTable();
		LuaObject object = popReference(thread);
		Assert.assertTrue(object instanceof LuaTable);
		LuaTable table = (LuaTable) object;
		int before = size(thread);
		table.set(thread, tolua(thread, 55.3), tolua(thread, "value"));
		Assert.assertEquals(size(thread), before);
		Assert.assertEquals(
				((LuaString) table.get(thread, tolua(thread, 55.3)))
						.toString(thread), "value");
	}

	@Test
	public void testSetIfKeyIsBoolean() {
		thread.state.newTable();
		LuaObject object = popReference(thread);
		Assert.assertTrue(object instanceof LuaTable);
		LuaTable table = (LuaTable) object;
		int before = size(thread);
		table.set(thread, tolua(thread, true), tolua(thread, "value"));
		Assert.assertEquals(size(thread), before);
		Assert.assertEquals(
				((LuaString) table.get(thread, tolua(thread, true)))
						.toString(thread), "value");
	}

	@Test
	public void testSetIfValueIsNull() {
		thread.state.newTable();
		LuaObject object = popReference(thread);
		Assert.assertTrue(object instanceof LuaTable);
		LuaTable table = (LuaTable) object;
		table.set(thread, tolua(thread, "key"), tolua(thread, "value"));
		Assert.assertEquals(((LuaString) table
				.get(thread, tolua(thread, "key"))).toString(thread), "value");
		int before = size(thread);
		table.set(thread, tolua(thread, "key"), null);
		Assert.assertEquals(size(thread), before);
		Assert.assertNull(table.get(thread, tolua(thread, "key")));
	}

	@Test
	public void testContainsKeyIfNotExists() {
		thread.state.newTable();
		LuaObject object = popReference(thread);
		Assert.assertTrue(object instanceof LuaTable);
		LuaTable table = (LuaTable) object;
		int before = size(thread);
		Assert.assertFalse(table.containsKey(thread, tolua(thread, "key")));
		Assert.assertEquals(size(thread), before);
	}

	@Test
	public void testContainsKeyIfExists() {
		thread.state.newTable();
		LuaObject object = popReference(thread);
		Assert.assertTrue(object instanceof LuaTable);
		LuaTable table = (LuaTable) object;
		table.set(thread, tolua(thread, "key"), tolua(thread, "value"));
		int before = size(thread);
		Assert.assertTrue(table.containsKey(thread, tolua(thread, "key")));
		Assert.assertEquals(size(thread), before);
	}

	@Test
	public void testContainsKeyIfKeyIsInt() {
		thread.state.newTable();
		LuaObject object = popReference(thread);
		Assert.assertTrue(object instanceof LuaTable);
		LuaTable table = (LuaTable) object;
		table.set(thread, tolua(thread, 5), tolua(thread, "value"));
		int before = size(thread);
		Assert.assertTrue(table.containsKey(thread, tolua(thread, 5)));
		Assert.assertEquals(size(thread), before);
	}

	@Test
	public void testContainsKeyIfKeyIsLong() {
		thread.state.newTable();
		LuaObject object = popReference(thread);
		Assert.assertTrue(object instanceof LuaTable);
		LuaTable table = (LuaTable) object;
		table.set(thread, tolua(thread, 1234567890123456L),
				tolua(thread, "value"));
		int before = size(thread);
		Assert.assertTrue(table.containsKey(thread,
				tolua(thread, 1234567890123456L)));
		Assert.assertEquals(size(thread), before);
	}

	@Test
	public void testContainsKeyIfKeyIsDouble() {
		thread.state.newTable();
		LuaObject object = popReference(thread);
		Assert.assertTrue(object instanceof LuaTable);
		LuaTable table = (LuaTable) object;
		table.set(thread, tolua(thread, 55.3), tolua(thread, "value"));
		int before = size(thread);
		Assert.assertTrue(table.containsKey(thread, tolua(thread, 55.3)));
		Assert.assertEquals(size(thread), before);
	}

	@Test
	public void testContainsKeyIfKeyIsBoolean() {
		thread.state.newTable();
		LuaObject object = popReference(thread);
		Assert.assertTrue(object instanceof LuaTable);
		LuaTable table = (LuaTable) object;
		table.set(thread, tolua(thread, true), tolua(thread, "value"));
		int before = size(thread);
		Assert.assertTrue(table.containsKey(thread, tolua(thread, true)));
		Assert.assertEquals(size(thread), before);
	}

	@Test
	public void testGetKeys() {
		thread.state.newTable();
		LuaObject object = popReference(thread);
		Assert.assertTrue(object instanceof LuaTable);
		LuaTable table = (LuaTable) object;
		table.set(thread, tolua(thread, "key1"), tolua(thread, "value1"));
		table.set(thread, tolua(thread, "key2"), tolua(thread, "value2"));
		table.set(thread, tolua(thread, "key3"), tolua(thread, "value3"));
		table.set(thread, tolua(thread, "key4"), tolua(thread, "value4"));
		int before = size(thread);
		List<LuaObject> keys = table.getKeys(thread);
		Assert.assertEquals(size(thread), before);
		Assert.assertEquals(keys.size(), 4);
		Set<String> keysSet = new HashSet<String>();
		for (LuaObject key : keys)
			keysSet.add(((LuaString) key).toString(thread));
		Assert.assertTrue(keysSet.contains("key1"));
		Assert.assertTrue(keysSet.contains("key2"));
		Assert.assertTrue(keysSet.contains("key3"));
		Assert.assertTrue(keysSet.contains("key4"));
	}

	@Test
	public void testGetEntries() {
		thread.state.newTable();
		LuaObject object = popReference(thread);
		Assert.assertTrue(object instanceof LuaTable);
		LuaTable table = (LuaTable) object;
		table.set(thread, tolua(thread, "key1"), tolua(thread, "value1"));
		table.set(thread, tolua(thread, "key2"), tolua(thread, "value2"));
		table.set(thread, tolua(thread, "key3"), tolua(thread, "value3"));
		table.set(thread, tolua(thread, "key4"), tolua(thread, "value4"));
		int before = size(thread);
		List<LuaTableEntry> entries = table.getEntries(thread);
		Assert.assertEquals(size(thread), before);
		Assert.assertEquals(entries.size(), 4);
		Map<String, String> map = new HashMap<String, String>();
		for (LuaTableEntry entry : entries)
			map.put(((LuaString) (entry.getKey())).toString(thread),
					((LuaString) (entry.getValue())).toString(thread));
		Assert.assertTrue(map.containsKey("key1"));
		Assert.assertEquals(map.get("key1"), "value1");
		Assert.assertTrue(map.containsKey("key2"));
		Assert.assertEquals(map.get("key2"), "value2");
		Assert.assertTrue(map.containsKey("key3"));
		Assert.assertEquals(map.get("key3"), "value3");
		Assert.assertTrue(map.containsKey("key4"));
		Assert.assertEquals(map.get("key4"), "value4");
	}
}
