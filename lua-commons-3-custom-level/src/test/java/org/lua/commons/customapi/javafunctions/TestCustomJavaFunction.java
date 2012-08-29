package org.lua.commons.customapi.javafunctions;

import static org.lua.commons.baseapi.types.LuaObjectTools.tolua;
import static org.lua.commons.customapi.CustomLuaTools.castFrom;
import static org.lua.commons.customapi.CustomLuaTools.castTo;
import static org.lua.commons.customapi.javafunctions.handlers.CastUtils.totype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lua.commons.baseapi.Lua;
import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.extensions.references.LuaReferencesStorage;
import org.lua.commons.baseapi.extensions.references.WeakLuaReferencesStorage;
import org.lua.commons.baseapi.extensions.threadpool.SimpleLuaContextThreadPool;
import org.lua.commons.baseapi.types.LuaFunction;
import org.lua.commons.baseapi.types.LuaNumber;
import org.lua.commons.baseapi.types.LuaObject;
import org.lua.commons.baseapi.types.LuaString;
import org.lua.commons.customapi.javafunctions.handlers.SimpleTypeCastManager;
import org.lua.commons.customapi.javafunctions.handlers.TypeCastManager;
import org.lua.commons.impl.nativelua.NativeLuaStateApiFactory;
import org.lua.commons.nativeapi.LuaException;
import org.lua.commons.nativeapi.LuaStateApiProvider;
import org.lua.commons.nativeapi.LuaStateImpl;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestCustomJavaFunction {

	Lua lua;
	LuaThread thread;

	@BeforeMethod
	public void initLua() {
		LuaStateApiProvider.setFactory(new NativeLuaStateApiFactory());
		lua = new Lua(new LuaStateImpl(), new SimpleLuaContextThreadPool());
		lua.addExtension(LuaReferencesStorage.class,
				new WeakLuaReferencesStorage(lua));
		lua.addExtension(TypeCastManager.class, new SimpleTypeCastManager());
		lua.start();

		thread = lua.contextThread();
	}

	@AfterMethod
	public void closeLua() {
		lua.close();
	}

	@Test
	public void testCustomJavaFunction() throws NoSuchMethodException,
			LuaException {
		LuaFunction function = new CustomJavaFunction(getClass().getMethod(
				"test", String.class, String.class), false, false)
				.function(thread);

		LuaObject[] objects = function.call(
				thread,
				new LuaObject[] { tolua(thread, "Hello, "),
						tolua(thread, "world!") }, 1);
		Assert.assertEquals(objects.length, 1);
		Assert.assertEquals(((LuaString) objects[0]).toString(thread),
				"Hello, world!");
	}

	public static String test(String str1, String str2) {
		return str1 + str2;
	}

	@Test
	public void testCustomJavaFunctionWithCollections()
			throws NoSuchMethodException, LuaException {
		LuaFunction function = new CustomJavaFunction(getClass().getMethod(
				"test", List.class, Map.class), false, false).function(thread);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("key1", "value1");
		map.put("key2", "value2");
		map.put("key3", "value3");
		map.put("key4", "value4");
		map.put("key5", "value5");
		ArrayList<String> keys = new ArrayList<String>();
		keys.add("key1");
		keys.add("key3");
		keys.add("key2");
		keys.add("key5");
		LuaObject[] objects = function.call(thread,
				new LuaObject[] { castTo(thread, keys), castTo(thread, map) },
				1);
		Assert.assertEquals(objects.length, 1);
		String[] values = (String[]) castFrom(thread, objects[0],
				totype(String[].class));
		Assert.assertEquals(values.length, 4);
		Assert.assertEquals(values[0], "value1");
		Assert.assertEquals(values[1], "value3");
		Assert.assertEquals(values[2], "value2");
		Assert.assertEquals(values[3], "value5");
	}

	public static String[] test(List<String> keys, Map<String, String> map) {
		ArrayList<String> results = new ArrayList<String>();
		for (String key : keys)
			results.add(map.get(key));
		return results.toArray(new String[results.size()]);
	}

	@Test
	public void testNotStaticCustomJavaFunction() throws NoSuchMethodException,
			LuaException {
		LuaFunction function = new CustomJavaFunction(getClass().getMethod(
				"test", String.class), false, false).function(thread);
		LuaObject[] objects = function.call(thread,
				new LuaObject[] { tolua(thread, this), tolua(thread, "Lua!") },
				1);
		Assert.assertEquals(objects.length, 1);
		Assert.assertEquals(((LuaString) objects[0]).toString(thread),
				"Hello, Lua!");
	}

	private final String hello = "Hello";

	public String test(String name) {
		return hello + ", " + name;
	}

	@Test
	public void testCustomJavaFunctionWithVarRes()
			throws NoSuchMethodException, LuaException {
		LuaFunction function = new CustomJavaFunction(getClass().getMethod(
				"test", int.class, int.class), false, true).function(thread);
		LuaObject[] objects = function.call(thread,
				new LuaObject[] { tolua(thread, 5), tolua(thread, 7) }, 2);
		Assert.assertEquals(objects.length, 2);
		Assert.assertEquals(((LuaNumber) objects[0]).toInt(thread), 5 - 7);
		Assert.assertEquals(((LuaNumber) objects[1]).toInt(thread), 7 - 5);
	}

	public static int[] test(int a, int b) {
		return new int[] { a - b, b - a };
	}

	@Test
	public void testCustomJavaFunctionWithVarArgs()
			throws NoSuchMethodException, LuaException {
		LuaFunction function = new CustomJavaFunction(getClass().getMethod(
				"test", int[].class), false, false).function(thread);
		LuaObject[] objects = function.call(
				thread,
				new LuaObject[] { tolua(thread, 5), tolua(thread, 7),
						tolua(thread, 11) }, 1);
		Assert.assertEquals(objects.length, 1);
		Assert.assertEquals(((LuaNumber) objects[0]).toInt(thread), 23);
	}

	public static int test(int... a) {
		int result = 0;
		for (int i = 0; i < a.length; i++)
			result += a[i];
		return result;
	}

	@Test
	public void testCustomJavaFunctionWithVarArgsAndVarRes()
			throws NoSuchMethodException, LuaException {
		LuaFunction function = new CustomJavaFunction(getClass().getMethod(
				"test", List[].class), false, true).function(thread);
		ArrayList<Integer> list1 = new ArrayList<Integer>();
		list1.add(1);
		list1.add(2);
		list1.add(3);
		list1.add(4);
		ArrayList<Integer> list2 = new ArrayList<Integer>();
		list2.add(2);
		list2.add(3);
		list2.add(4);
		list2.add(5);
		ArrayList<Integer> list3 = new ArrayList<Integer>();
		list3.add(3);
		list3.add(4);
		list3.add(5);
		list3.add(6);
		ArrayList<Integer> list4 = new ArrayList<Integer>();
		list4.add(4);
		list4.add(5);
		list4.add(6);
		list4.add(7);
		LuaObject[] objects = function.call(thread,
				new LuaObject[] { castTo(thread, list1), castTo(thread, list2),
						castTo(thread, list3), castTo(thread, list4) }, 4);
		Assert.assertEquals(objects.length, 4);
		List<Integer> list1o = (List<Integer>) castFrom(thread, objects[0],
				totype(List.class, Integer.class));
		Assert.assertEquals(list1o.size(), 4);
		Assert.assertEquals(list1o.get(0).intValue(), 1);
		Assert.assertEquals(list1o.get(1).intValue(), 2);
		Assert.assertEquals(list1o.get(2).intValue(), 3);
		Assert.assertEquals(list1o.get(3).intValue(), 4);
		List<Integer> list2o = (List<Integer>) castFrom(thread, objects[1],
				totype(List.class, Integer.class));
		Assert.assertEquals(list2o.size(), 4);
		Assert.assertEquals(list2o.get(0).intValue(), 6);
		Assert.assertEquals(list2o.get(1).intValue(), 9);
		Assert.assertEquals(list2o.get(2).intValue(), 12);
		Assert.assertEquals(list2o.get(3).intValue(), 15);
		List<Integer> list3o = (List<Integer>) castFrom(thread, objects[2],
				totype(List.class, Integer.class));
		Assert.assertEquals(list3o.size(), 4);
		Assert.assertEquals(list3o.get(0).intValue(), 15);
		Assert.assertEquals(list3o.get(1).intValue(), 20);
		Assert.assertEquals(list3o.get(2).intValue(), 25);
		Assert.assertEquals(list3o.get(3).intValue(), 30);
		List<Integer> list4o = (List<Integer>) castFrom(thread, objects[3],
				totype(List.class, Integer.class));
		Assert.assertEquals(list4o.size(), 4);
		Assert.assertEquals(list4o.get(0).intValue(), 28);
		Assert.assertEquals(list4o.get(1).intValue(), 35);
		Assert.assertEquals(list4o.get(2).intValue(), 42);
		Assert.assertEquals(list4o.get(3).intValue(), 49);
	}

	public static List<Integer>[] test(List<Integer>... lists) {
		ArrayList<Integer>[] results = new ArrayList[lists.length];
		for (int i = 0; i < lists.length; i++) {
			results[i] = new ArrayList<Integer>();
			for (int val : lists[i])
				results[i].add(val * lists[i].get(i));
		}
		return results;
	}

}
