package org.lua.commons.customapi.javafunctions.handlers;

import static org.lua.commons.baseapi.types.LuaObjectTools.tolua;
import static org.lua.commons.customapi.javafunctions.handlers.CastUtils.totype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lua.commons.baseapi.Lua;
import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.extensions.references.LuaReferencesStorage;
import org.lua.commons.baseapi.extensions.references.WeakLuaReferencesStorage;
import org.lua.commons.baseapi.extensions.threadpool.SimpleLuaContextThreadPool;
import org.lua.commons.baseapi.types.LuaBoolean;
import org.lua.commons.baseapi.types.LuaNumber;
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

public class TestTypeCastManager {

	Lua lua;
	TypeCastManager castManager;

	@BeforeMethod
	public void initLua() {
		LuaStateApiProvider.setFactory(new NativeLuaStateApiFactory());
		lua = new Lua(new LuaStateImpl(), new SimpleLuaContextThreadPool());
		lua.addExtension(LuaReferencesStorage.class,
				new WeakLuaReferencesStorage(lua));
		lua.start();
		castManager = new SimpleTypeCastManager();
	}

	@AfterMethod
	public void closeLua() {
		lua.close();
	}

	@Test
	public void testBooleanHandling() {
		LuaThread thread = lua.contextThread();

		Assert.assertEquals(castManager.castFrom(thread, tolua(thread, true),
				totype(boolean.class)), Boolean.TRUE);
		Assert.assertEquals(castManager.castFrom(thread, tolua(thread, false),
				totype(boolean.class)), Boolean.FALSE);
		Assert.assertEquals(castManager.castFrom(thread, tolua(thread, true),
				totype(Boolean.class)), Boolean.TRUE);
		Assert.assertEquals(castManager.castFrom(thread, tolua(thread, false),
				totype(Boolean.class)), Boolean.FALSE);

		Assert.assertEquals(((LuaBoolean) castManager.castTo(thread, true))
				.toBoolean(thread), true);
		Assert.assertEquals(((LuaBoolean) castManager.castTo(thread, false))
				.toBoolean(thread), false);
		Assert.assertEquals(((LuaBoolean) castManager.castTo(thread,
				Boolean.TRUE)).toBoolean(thread), true);
		Assert.assertEquals(((LuaBoolean) castManager.castTo(thread,
				Boolean.FALSE)).toBoolean(thread), false);
	}

	@Test
	public void testNumberHandling() {
		LuaThread thread = lua.contextThread();

		// byte
		Assert.assertEquals(castManager.castFrom(thread, tolua(thread, 5),
				totype(byte.class)), (byte) 5);
		Assert.assertEquals(castManager.castFrom(thread, tolua(thread, 111),
				totype(byte.class)), (byte) 111);
		Assert.assertEquals(castManager.castFrom(thread, tolua(thread, 5),
				totype(Byte.class)), (byte) 5);
		Assert.assertEquals(castManager.castFrom(thread, tolua(thread, 111),
				totype(Byte.class)), (byte) 111);

		Assert.assertEquals(((LuaNumber) castManager.castTo(thread, (byte) 5))
				.toByte(thread), 5);
		Assert.assertEquals(
				((LuaNumber) castManager.castTo(thread, (byte) 111))
						.toByte(thread), 111);

		// short
		Assert.assertEquals(castManager.castFrom(thread, tolua(thread, 55),
				totype(short.class)), (short) 55);
		Assert.assertEquals(castManager.castFrom(thread, tolua(thread, 333),
				totype(short.class)), (short) 333);
		Assert.assertEquals(castManager.castFrom(thread, tolua(thread, 55),
				totype(Short.class)), (short) 55);
		Assert.assertEquals(castManager.castFrom(thread, tolua(thread, 333),
				totype(Short.class)), (short) 333);

		Assert.assertEquals(
				((LuaNumber) castManager.castTo(thread, (short) 55))
						.toShort(thread), 55);
		Assert.assertEquals(((LuaNumber) castManager
				.castTo(thread, (short) 333)).toShort(thread), 333);

		// int
		Assert.assertEquals(castManager.castFrom(thread, tolua(thread, 1111),
				totype(int.class)), 1111);
		Assert.assertEquals(castManager.castFrom(thread,
				tolua(thread, 9991999), totype(int.class)), 9991999);
		Assert.assertEquals(castManager.castFrom(thread, tolua(thread, 1111),
				totype(Integer.class)), 1111);
		Assert.assertEquals(castManager.castFrom(thread,
				tolua(thread, 9991999), totype(Integer.class)), 9991999);

		Assert.assertEquals(
				((LuaNumber) castManager.castTo(thread, 1111)).toInt(thread),
				1111);
		Assert.assertEquals(
				((LuaNumber) castManager.castTo(thread, 9991999)).toInt(thread),
				9991999);

		// long
		Assert.assertEquals(castManager.castFrom(thread,
				tolua(thread, 9991999L), totype(long.class)), 9991999L);
		Assert.assertEquals(castManager.castFrom(thread,
				tolua(thread, 5551555155515551555L), totype(long.class)),
				5551555155515551555L);
		Assert.assertEquals(castManager.castFrom(thread,
				tolua(thread, 9991999L), totype(Long.class)), 9991999L);
		Assert.assertEquals(castManager.castFrom(thread,
				tolua(thread, 5551555155515551555L), totype(Long.class)),
				5551555155515551555L);

		Assert.assertEquals(((LuaNumber) castManager.castTo(thread, 9991999L))
				.toLong(thread), 9991999);
		Assert.assertEquals(((LuaNumber) castManager.castTo(thread,
				5551555155515551555L)).toLong(thread), 5551555155515551555L);

		// float
		Assert.assertEquals(castManager.castFrom(thread,
				tolua(thread, (float) 555.1), totype(float.class)),
				(float) 555.1);
		Assert.assertEquals(castManager.castFrom(thread,
				tolua(thread, (float) 9991999.999), totype(float.class)),
				(float) 9991999.999);
		Assert.assertEquals(castManager.castFrom(thread,
				tolua(thread, (float) 555.1), totype(Float.class)),
				(float) 555.1);
		Assert.assertEquals(castManager.castFrom(thread,
				tolua(thread, (float) 9991999.999), totype(Float.class)),
				(float) 9991999.999);

		Assert.assertEquals(
				((LuaNumber) castManager.castTo(thread, 555.1)).toFloat(thread),
				(float) 555.1);
		Assert.assertEquals(((LuaNumber) castManager
				.castTo(thread, 9991999.999)).toFloat(thread),
				(float) 9991999.999);

		// double
		Assert.assertEquals(castManager.castFrom(thread, tolua(thread, 555.1),
				totype(double.class)), 555.1);
		Assert.assertEquals(castManager.castFrom(thread,
				tolua(thread, 9991999.999), totype(double.class)), 9991999.999);
		Assert.assertEquals(castManager.castFrom(thread, tolua(thread, 555.1),
				totype(Double.class)), 555.1);
		Assert.assertEquals(castManager.castFrom(thread,
				tolua(thread, 9991999.999), totype(Double.class)), 9991999.999);

		Assert.assertEquals(((LuaNumber) castManager.castTo(thread, 555.1))
				.toDouble(thread), 555.1);
		Assert.assertEquals(((LuaNumber) castManager
				.castTo(thread, 9991999.999)).toDouble(thread), 9991999.999);
	}

	@Test
	public void testStringHandling() {
		LuaThread thread = lua.contextThread();

		Assert.assertEquals(castManager.castFrom(thread,
				tolua(thread, "string1"), totype(String.class)), "string1");
		Assert.assertEquals(castManager.castFrom(thread,
				tolua(thread, "string2"), totype(String.class)), "string2");

		Assert.assertEquals(((LuaString) castManager.castTo(thread, "string1"))
				.toString(thread), "string1");
		Assert.assertEquals(((LuaString) castManager.castTo(thread, "string2"))
				.toString(thread), "string2");
	}

	@Test
	public void testPrimitiveArrayHandling() {
		LuaThread thread = lua.contextThread();

		LuaTable byteInTable = LuaTable.newTable(thread);
		byteInTable.set(thread, tolua(thread, 1), tolua(thread, (byte) 1));
		byteInTable.set(thread, tolua(thread, 2), tolua(thread, (byte) 2));
		byteInTable.set(thread, tolua(thread, 3), tolua(thread, (byte) 3));
		byteInTable.set(thread, tolua(thread, 4), tolua(thread, (byte) 4));
		byteInTable.set(thread, tolua(thread, 5), tolua(thread, (byte) 5));
		byte[] byteFromArray = (byte[]) castManager.castFrom(thread,
				byteInTable, totype(byte[].class));
		Assert.assertEquals(byteFromArray.length, 5);
		Assert.assertEquals(byteFromArray[0], (byte) 1);
		Assert.assertEquals(byteFromArray[1], (byte) 2);
		Assert.assertEquals(byteFromArray[2], (byte) 3);
		Assert.assertEquals(byteFromArray[3], (byte) 4);
		Assert.assertEquals(byteFromArray[4], (byte) 5);

		byte[] byteToArray = { (byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5 };
		LuaTable byteToTable = (LuaTable) castManager.castTo(thread,
				byteToArray);
		List<LuaTableEntry> entries = byteToTable.getEntries(thread);
		Assert.assertEquals(entries.size(), 5);
		Assert.assertEquals(
				((LuaNumber) entries.get(0).getValue()).toByte(thread),
				(byte) 1);
		Assert.assertEquals(
				((LuaNumber) entries.get(1).getValue()).toByte(thread),
				(byte) 2);
		Assert.assertEquals(
				((LuaNumber) entries.get(2).getValue()).toByte(thread),
				(byte) 3);
		Assert.assertEquals(
				((LuaNumber) entries.get(3).getValue()).toByte(thread),
				(byte) 4);
		Assert.assertEquals(
				((LuaNumber) entries.get(4).getValue()).toByte(thread),
				(byte) 5);
	}

	@Test
	public void testObjectArrayHandling() {
		LuaThread thread = lua.contextThread();

		LuaTable stringInTable = LuaTable.newTable(thread);
		stringInTable.set(thread, tolua(thread, 1), tolua(thread, "string1"));
		stringInTable.set(thread, tolua(thread, 2), tolua(thread, "string2"));
		stringInTable.set(thread, tolua(thread, 3), tolua(thread, "string3"));
		stringInTable.set(thread, tolua(thread, 4), tolua(thread, "string4"));
		stringInTable.set(thread, tolua(thread, 5), tolua(thread, "string5"));
		String[] stringFromArray = (String[]) castManager.castFrom(thread,
				stringInTable, totype(String[].class));
		Assert.assertEquals(stringFromArray.length, 5);
		Assert.assertEquals(stringFromArray[0], "string1");
		Assert.assertEquals(stringFromArray[1], "string2");
		Assert.assertEquals(stringFromArray[2], "string3");
		Assert.assertEquals(stringFromArray[3], "string4");
		Assert.assertEquals(stringFromArray[4], "string5");

		String[] stringToArray = { "string1", "string2", "string3", "string4",
				"string5" };
		LuaTable stringToTable = (LuaTable) castManager.castTo(thread,
				stringToArray);
		List<LuaTableEntry> entries = stringToTable.getEntries(thread);
		Assert.assertEquals(entries.size(), 5);
		Assert.assertEquals(
				((LuaString) entries.get(0).getValue()).toString(thread),
				"string1");
		Assert.assertEquals(
				((LuaString) entries.get(1).getValue()).toString(thread),
				"string2");
		Assert.assertEquals(
				((LuaString) entries.get(2).getValue()).toString(thread),
				"string3");
		Assert.assertEquals(
				((LuaString) entries.get(3).getValue()).toString(thread),
				"string4");
		Assert.assertEquals(
				((LuaString) entries.get(4).getValue()).toString(thread),
				"string5");
	}

	@Test
	public void testArrayArrayHandling() {
		LuaThread thread = lua.contextThread();

		LuaTable arrayInTable = LuaTable.newTable(thread);

		LuaTable value1 = LuaTable.newTable(thread);
		value1.set(thread, tolua(thread, 1), tolua(thread, 1));
		arrayInTable.set(thread, tolua(thread, 1), value1);

		LuaTable value2 = LuaTable.newTable(thread);
		value2.set(thread, tolua(thread, 1), tolua(thread, 2));
		arrayInTable.set(thread, tolua(thread, 2), value2);

		LuaTable value3 = LuaTable.newTable(thread);
		value3.set(thread, tolua(thread, 1), tolua(thread, 3));
		arrayInTable.set(thread, tolua(thread, 3), value3);

		LuaTable value4 = LuaTable.newTable(thread);
		value4.set(thread, tolua(thread, 1), tolua(thread, 4));
		arrayInTable.set(thread, tolua(thread, 4), value4);

		LuaTable value5 = LuaTable.newTable(thread);
		value5.set(thread, tolua(thread, 1), tolua(thread, 5));
		arrayInTable.set(thread, tolua(thread, 5), value5);

		int[][] arrayFromArray = (int[][]) castManager.castFrom(thread,
				arrayInTable, totype(int[][].class));
		Assert.assertEquals(arrayFromArray.length, 5);

		Assert.assertEquals(arrayFromArray[0].length, 1);
		Assert.assertEquals(arrayFromArray[0][0], 1);

		Assert.assertEquals(arrayFromArray[1].length, 1);
		Assert.assertEquals(arrayFromArray[1][0], 2);

		Assert.assertEquals(arrayFromArray[2].length, 1);
		Assert.assertEquals(arrayFromArray[2][0], 3);

		Assert.assertEquals(arrayFromArray[3].length, 1);
		Assert.assertEquals(arrayFromArray[3][0], 4);

		Assert.assertEquals(arrayFromArray[4].length, 1);
		Assert.assertEquals(arrayFromArray[4][0], 5);

		int[][] arrayToArray = { { 1 }, { 2 }, { 3 }, { 4 }, { 5 } };
		LuaTable arrayToTable = (LuaTable) castManager.castTo(thread,
				arrayToArray);
		List<LuaTableEntry> entries = arrayToTable.getEntries(thread);
		Assert.assertEquals(entries.size(), 5);
		Assert.assertEquals(((LuaNumber) ((LuaTable) entries.get(0).getValue())
				.get(thread, tolua(thread, 1))).toInt(thread), 1);
		Assert.assertEquals(((LuaNumber) ((LuaTable) entries.get(1).getValue())
				.get(thread, tolua(thread, 1))).toInt(thread), 2);
		Assert.assertEquals(((LuaNumber) ((LuaTable) entries.get(2).getValue())
				.get(thread, tolua(thread, 1))).toInt(thread), 3);
		Assert.assertEquals(((LuaNumber) ((LuaTable) entries.get(3).getValue())
				.get(thread, tolua(thread, 1))).toInt(thread), 4);
		Assert.assertEquals(((LuaNumber) ((LuaTable) entries.get(4).getValue())
				.get(thread, tolua(thread, 1))).toInt(thread), 5);
	}

	@Test
	public void testObjectCollectionHandling() {
		LuaThread thread = lua.contextThread();

		LuaTable stringInTable = LuaTable.newTable(thread);
		stringInTable.set(thread, tolua(thread, 1), tolua(thread, "string1"));
		stringInTable.set(thread, tolua(thread, 2), tolua(thread, "string2"));
		stringInTable.set(thread, tolua(thread, 3), tolua(thread, "string3"));
		stringInTable.set(thread, tolua(thread, 4), tolua(thread, "string4"));
		stringInTable.set(thread, tolua(thread, 5), tolua(thread, "string5"));
		List<String> stringFromList = (List<String>) castManager.castFrom(
				thread, stringInTable, totype(List.class, String.class));
		Assert.assertEquals(stringFromList.size(), 5);
		Assert.assertEquals(stringFromList.get(0), "string1");
		Assert.assertEquals(stringFromList.get(1), "string2");
		Assert.assertEquals(stringFromList.get(2), "string3");
		Assert.assertEquals(stringFromList.get(3), "string4");
		Assert.assertEquals(stringFromList.get(4), "string5");

		ArrayList<String> stringToList = new ArrayList<String>();
		stringToList.add("string1");
		stringToList.add("string2");
		stringToList.add("string3");
		stringToList.add("string4");
		stringToList.add("string5");
		LuaTable stringToTable = (LuaTable) castManager.castTo(thread,
				stringToList);
		List<LuaTableEntry> entries = stringToTable.getEntries(thread);
		Assert.assertEquals(entries.size(), 5);
		Assert.assertEquals(
				((LuaString) entries.get(0).getValue()).toString(thread),
				"string1");
		Assert.assertEquals(
				((LuaString) entries.get(1).getValue()).toString(thread),
				"string2");
		Assert.assertEquals(
				((LuaString) entries.get(2).getValue()).toString(thread),
				"string3");
		Assert.assertEquals(
				((LuaString) entries.get(3).getValue()).toString(thread),
				"string4");
		Assert.assertEquals(
				((LuaString) entries.get(4).getValue()).toString(thread),
				"string5");
	}

	@Test
	public void testCollectionCollectionHandling() {
		LuaThread thread = lua.contextThread();

		LuaTable collectionInTable = LuaTable.newTable(thread);

		LuaTable value1 = LuaTable.newTable(thread);
		value1.set(thread, tolua(thread, 1), tolua(thread, "string1"));
		collectionInTable.set(thread, tolua(thread, 1), value1);

		LuaTable value2 = LuaTable.newTable(thread);
		value2.set(thread, tolua(thread, 1), tolua(thread, "string2"));
		collectionInTable.set(thread, tolua(thread, 2), value2);

		LuaTable value3 = LuaTable.newTable(thread);
		value3.set(thread, tolua(thread, 1), tolua(thread, "string3"));
		collectionInTable.set(thread, tolua(thread, 3), value3);

		LuaTable value4 = LuaTable.newTable(thread);
		value4.set(thread, tolua(thread, 1), tolua(thread, "string4"));
		collectionInTable.set(thread, tolua(thread, 4), value4);

		LuaTable value5 = LuaTable.newTable(thread);
		value5.set(thread, tolua(thread, 1), tolua(thread, "string5"));
		collectionInTable.set(thread, tolua(thread, 5), value5);

		List<List<String>> collectionFromList = (List<List<String>>) castManager
				.castFrom(thread, collectionInTable,
						totype(List.class, totype(List.class, String.class)));
		Assert.assertEquals(collectionFromList.size(), 5);
		Assert.assertEquals(collectionFromList.get(0).size(), 1);
		Assert.assertEquals(collectionFromList.get(0).get(0), "string1");
		Assert.assertEquals(collectionFromList.get(1).size(), 1);
		Assert.assertEquals(collectionFromList.get(1).get(0), "string2");
		Assert.assertEquals(collectionFromList.get(2).size(), 1);
		Assert.assertEquals(collectionFromList.get(2).get(0), "string3");
		Assert.assertEquals(collectionFromList.get(3).size(), 1);
		Assert.assertEquals(collectionFromList.get(3).get(0), "string4");
		Assert.assertEquals(collectionFromList.get(4).size(), 1);
		Assert.assertEquals(collectionFromList.get(4).get(0), "string5");

		ArrayList<ArrayList<String>> collectionToList = new ArrayList<ArrayList<String>>();

		ArrayList<String> list1 = new ArrayList<String>();
		list1.add("string1");
		collectionToList.add(list1);

		ArrayList<String> list2 = new ArrayList<String>();
		list2.add("string2");
		collectionToList.add(list2);

		ArrayList<String> list3 = new ArrayList<String>();
		list3.add("string3");
		collectionToList.add(list3);

		ArrayList<String> list4 = new ArrayList<String>();
		list4.add("string4");
		collectionToList.add(list4);

		ArrayList<String> list5 = new ArrayList<String>();
		list5.add("string5");
		collectionToList.add(list5);

		LuaTable collectionToTable = (LuaTable) castManager.castTo(thread,
				collectionToList);
		List<LuaTableEntry> entries = collectionToTable.getEntries(thread);
		Assert.assertEquals(entries.size(), 5);
		Assert.assertEquals(((LuaString) ((LuaTable) entries.get(0).getValue())
				.get(thread, tolua(thread, 1))).toString(thread), "string1");
		Assert.assertEquals(((LuaString) ((LuaTable) entries.get(1).getValue())
				.get(thread, tolua(thread, 1))).toString(thread), "string2");
		Assert.assertEquals(((LuaString) ((LuaTable) entries.get(2).getValue())
				.get(thread, tolua(thread, 1))).toString(thread), "string3");
		Assert.assertEquals(((LuaString) ((LuaTable) entries.get(3).getValue())
				.get(thread, tolua(thread, 1))).toString(thread), "string4");
		Assert.assertEquals(((LuaString) ((LuaTable) entries.get(4).getValue())
				.get(thread, tolua(thread, 1))).toString(thread), "string5");
	}

	@Test
	public void testObjectMapHandling() {
		LuaThread thread = lua.contextThread();

		LuaTable stringInTable = LuaTable.newTable(thread);
		stringInTable.set(thread, tolua(thread, "key1"),
				tolua(thread, "value1"));
		stringInTable.set(thread, tolua(thread, "key2"),
				tolua(thread, "value2"));
		stringInTable.set(thread, tolua(thread, "key3"),
				tolua(thread, "value3"));
		stringInTable.set(thread, tolua(thread, "key4"),
				tolua(thread, "value4"));
		stringInTable.set(thread, tolua(thread, "key5"),
				tolua(thread, "value5"));
		Map<String, String> stringFromMap = (Map<String, String>) castManager
				.castFrom(thread, stringInTable,
						totype(Map.class, String.class, String.class));
		Assert.assertEquals(stringFromMap.size(), 5);
		Assert.assertEquals(stringFromMap.get("key1"), "value1");
		Assert.assertEquals(stringFromMap.get("key2"), "value2");
		Assert.assertEquals(stringFromMap.get("key3"), "value3");
		Assert.assertEquals(stringFromMap.get("key4"), "value4");
		Assert.assertEquals(stringFromMap.get("key5"), "value5");

		Map<String, String> stringToMap = new HashMap<String, String>();
		stringToMap.put("key1", "value1");
		stringToMap.put("key2", "value2");
		stringToMap.put("key3", "value3");
		stringToMap.put("key4", "value4");
		stringToMap.put("key5", "value5");
		LuaTable stringToTable = (LuaTable) castManager.castTo(thread,
				stringToMap);
		List<LuaTableEntry> entries = stringToTable.getEntries(thread);
		Assert.assertEquals(entries.size(), 5);
		HashMap<String, String> map = new HashMap<String, String>();
		for (LuaTableEntry entry : entries)
			map.put(((LuaString) entry.getKey()).toString(thread),
					((LuaString) entry.getValue()).toString(thread));
		Assert.assertEquals(map.get("key1"), "value1");
		Assert.assertEquals(map.get("key2"), "value2");
		Assert.assertEquals(map.get("key3"), "value3");
		Assert.assertEquals(map.get("key4"), "value4");
		Assert.assertEquals(map.get("key5"), "value5");
	}

	@Test
	public void testMapMapHandling() {
		LuaThread thread = lua.contextThread();

		LuaTable mapInTable = LuaTable.newTable(thread);

		LuaTable value1 = LuaTable.newTable(thread);
		value1.set(thread, tolua(thread, "key1"), tolua(thread, "value1"));
		mapInTable.set(thread, tolua(thread, "gkey1"), value1);

		LuaTable value2 = LuaTable.newTable(thread);
		value2.set(thread, tolua(thread, "key2"), tolua(thread, "value2"));
		mapInTable.set(thread, tolua(thread, "gkey2"), value2);

		LuaTable value3 = LuaTable.newTable(thread);
		value3.set(thread, tolua(thread, "key3"), tolua(thread, "value3"));
		mapInTable.set(thread, tolua(thread, "gkey3"), value3);

		LuaTable value4 = LuaTable.newTable(thread);
		value4.set(thread, tolua(thread, "key4"), tolua(thread, "value4"));
		mapInTable.set(thread, tolua(thread, "gkey4"), value4);

		LuaTable value5 = LuaTable.newTable(thread);
		value5.set(thread, tolua(thread, "key5"), tolua(thread, "value5"));
		mapInTable.set(thread, tolua(thread, "gkey5"), value5);

		Map<String, Map<String, String>> stringFromMap = (Map<String, Map<String, String>>) castManager
				.castFrom(
						thread,
						mapInTable,
						totype(Map.class, String.class,
								totype(Map.class, String.class, String.class)));
		Assert.assertEquals(stringFromMap.size(), 5);
		Assert.assertEquals(stringFromMap.get("gkey1").get("key1"), "value1");
		Assert.assertEquals(stringFromMap.get("gkey2").get("key2"), "value2");
		Assert.assertEquals(stringFromMap.get("gkey3").get("key3"), "value3");
		Assert.assertEquals(stringFromMap.get("gkey4").get("key4"), "value4");
		Assert.assertEquals(stringFromMap.get("gkey5").get("key5"), "value5");

		Map<String, Map<String, String>> mapToMap = new HashMap<String, Map<String, String>>();
		mapToMap.put("gkey1", new HashMap<String, String>());
		mapToMap.get("gkey1").put("key1", "value1");

		mapToMap.put("gkey2", new HashMap<String, String>());
		mapToMap.get("gkey2").put("key2", "value2");

		mapToMap.put("gkey3", new HashMap<String, String>());
		mapToMap.get("gkey3").put("key3", "value3");

		mapToMap.put("gkey4", new HashMap<String, String>());
		mapToMap.get("gkey4").put("key4", "value4");

		mapToMap.put("gkey5", new HashMap<String, String>());
		mapToMap.get("gkey5").put("key5", "value5");

		LuaTable mapToTable = (LuaTable) castManager.castTo(thread, mapToMap);
		List<LuaTableEntry> entries = mapToTable.getEntries(thread);
		Assert.assertEquals(entries.size(), 5);
		HashMap<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
		for (LuaTableEntry entry : entries) {
			String gkey = ((LuaString) entry.getKey()).toString(thread);
			String key = ((LuaString) ((LuaTable) entry.getValue()).getKeys(
					thread).get(0)).toString(thread);
			String value = ((LuaString) ((LuaTable) entry.getValue()).get(
					thread, tolua(thread, key))).toString(thread);
			map.put(gkey, new HashMap<String, String>());
			map.get(gkey).put(key, value);
		}
		Assert.assertEquals(map.get("gkey1").get("key1"), "value1");
		Assert.assertEquals(map.get("gkey2").get("key2"), "value2");
		Assert.assertEquals(map.get("gkey3").get("key3"), "value3");
		Assert.assertEquals(map.get("gkey4").get("key4"), "value4");
		Assert.assertEquals(map.get("gkey5").get("key5"), "value5");
	}

	@Test
	public void testNopMapHandling() {
		LuaThread thread = lua.contextThread();

		LuaTable nopInTable = LuaTable.newTable(thread);
		nopInTable.set(thread, tolua(thread, "key1"), tolua(thread, true));
		nopInTable.set(thread, tolua(thread, "key2"), tolua(thread, true));
		nopInTable.set(thread, tolua(thread, "key3"), tolua(thread, true));
		nopInTable.set(thread, tolua(thread, "key4"), tolua(thread, true));
		nopInTable.set(thread, tolua(thread, "key5"), tolua(thread, true));
		Set<String> nopFromSet = ((Map<String, LuaObject>) castManager
				.castFrom(thread, nopInTable,
						totype(Map.class, String.class, LuaObject.class)))
				.keySet();
		Assert.assertEquals(nopFromSet.size(), 5);
		Assert.assertTrue(nopFromSet.contains("key1"));
		Assert.assertTrue(nopFromSet.contains("key2"));
		Assert.assertTrue(nopFromSet.contains("key3"));
		Assert.assertTrue(nopFromSet.contains("key4"));
		Assert.assertTrue(nopFromSet.contains("key5"));

		Map<String, LuaObject> nopToMap = new HashMap<String, LuaObject>();
		nopToMap.put("key1", tolua(thread, true));
		nopToMap.put("key2", tolua(thread, true));
		nopToMap.put("key3", tolua(thread, true));
		nopToMap.put("key4", tolua(thread, true));
		nopToMap.put("key5", tolua(thread, true));
		LuaTable stringToTable = (LuaTable) castManager
				.castTo(thread, nopToMap);
		List<LuaTableEntry> entries = stringToTable.getEntries(thread);
		Assert.assertEquals(entries.size(), 5);
		HashSet<String> set = new HashSet<String>();
		for (LuaTableEntry entry : entries)
			set.add(((LuaString) entry.getKey()).toString(thread));
		Assert.assertTrue(set.contains("key1"));
		Assert.assertTrue(set.contains("key2"));
		Assert.assertTrue(set.contains("key3"));
		Assert.assertTrue(set.contains("key4"));
		Assert.assertTrue(set.contains("key5"));
	}

}
