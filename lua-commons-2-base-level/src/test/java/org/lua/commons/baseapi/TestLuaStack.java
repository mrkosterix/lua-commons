package org.lua.commons.baseapi;

import static org.lua.commons.baseapi.LuaStack.getBoolean;
import static org.lua.commons.baseapi.LuaStack.getByte;
import static org.lua.commons.baseapi.LuaStack.getInt;
import static org.lua.commons.baseapi.LuaStack.getLong;
import static org.lua.commons.baseapi.LuaStack.getShort;
import static org.lua.commons.baseapi.LuaStack.getString;
import static org.lua.commons.baseapi.LuaStack.isBoolean;
import static org.lua.commons.baseapi.LuaStack.isByte;
import static org.lua.commons.baseapi.LuaStack.isInt;
import static org.lua.commons.baseapi.LuaStack.isLong;
import static org.lua.commons.baseapi.LuaStack.isShort;
import static org.lua.commons.baseapi.LuaStack.isString;
import static org.lua.commons.baseapi.LuaStack.popReference;
import static org.lua.commons.baseapi.LuaStack.pushBoolean;
import static org.lua.commons.baseapi.LuaStack.pushByte;
import static org.lua.commons.baseapi.LuaStack.pushDouble;
import static org.lua.commons.baseapi.LuaStack.pushInt;
import static org.lua.commons.baseapi.LuaStack.pushLong;
import static org.lua.commons.baseapi.LuaStack.pushNil;
import static org.lua.commons.baseapi.LuaStack.pushShort;
import static org.lua.commons.baseapi.LuaStack.pushString;
import static org.lua.commons.baseapi.LuaStack.size;

import org.lua.commons.baseapi.extensions.references.LuaReferencesStorage;
import org.lua.commons.baseapi.extensions.references.WeakLuaReferencesStorage;
import org.lua.commons.baseapi.extensions.threadpool.SimpleLuaContextThreadPool;
import org.lua.commons.baseapi.types.LuaBoolean;
import org.lua.commons.baseapi.types.LuaFunction;
import org.lua.commons.baseapi.types.LuaNumber;
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

public class TestLuaStack {

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
	public void testBoolean() {
		Assert.assertEquals(size(thread), 0);
		pushBoolean(thread, true);
		Assert.assertEquals(size(thread), 1);
		Assert.assertTrue(isBoolean(thread, -1));
		Assert.assertTrue(isBoolean(thread, -1));
		Assert.assertEquals(getBoolean(thread, -1), true);
	}

	@Test
	public void testByte() {
		Assert.assertEquals(size(thread), 0);
		pushByte(thread, (byte) 114);
		Assert.assertEquals(size(thread), 1);
		Assert.assertTrue(thread.state.isInteger(-1, Byte.MIN_VALUE,
				Byte.MAX_VALUE));
		Assert.assertTrue(isByte(thread, -1));
		Assert.assertEquals(getByte(thread, -1), 114);

		thread.state.pushInteger(Byte.MAX_VALUE + 1);
		Assert.assertFalse(isByte(thread, -1));
	}

	@Test
	public void testShort() {
		Assert.assertEquals(size(thread), 0);
		pushShort(thread, (short) 1234);
		Assert.assertEquals(size(thread), 1);
		Assert.assertTrue(thread.state.isInteger(-1, Short.MIN_VALUE,
				Short.MAX_VALUE));
		Assert.assertTrue(isShort(thread, -1));
		Assert.assertEquals(getShort(thread, -1), 1234);

		thread.state.pushInteger(Short.MAX_VALUE + 1);
		Assert.assertFalse(isShort(thread, -1));
	}

	@Test
	public void testInt() {
		Assert.assertEquals(size(thread), 0);
		pushInt(thread, 1234567);
		Assert.assertEquals(size(thread), 1);
		Assert.assertTrue(thread.state.isInteger(-1, Integer.MIN_VALUE,
				Integer.MAX_VALUE));
		Assert.assertTrue(isInt(thread, -1));
		Assert.assertEquals(getInt(thread, -1), 1234567);

		thread.state.pushNumber((long) Integer.MAX_VALUE + 1);
		Assert.assertFalse(isInt(thread, -1));
	}

	@Test
	public void testLong() {
		Assert.assertEquals(size(thread), 0);
		pushLong(thread, 12345678901234567L);
		Assert.assertEquals(size(thread), 1);
		Assert.assertTrue(thread.state.isNumber(-1));
		Assert.assertTrue(isLong(thread, -1));
		Assert.assertEquals(getLong(thread, -1), 12345678901234567L);

		thread.state.pushNumber(0.5);
		Assert.assertFalse(isLong(thread, -1));
	}

	@Test
	public void testString() {
		Assert.assertEquals(size(thread), 0);
		pushString(thread, "Hello, world!");
		Assert.assertEquals(size(thread), 1);
		Assert.assertTrue(thread.state.isString(-1));
		Assert.assertTrue(isString(thread, -1));
		Assert.assertEquals(getString(thread, -1), "Hello, world!");
	}

	@Test
	public void testBooleanReference() {
		pushBoolean(thread, true);
		Assert.assertEquals(size(thread), 1);
		LuaObject value = popReference(thread);
		Assert.assertEquals(size(thread), 0);
		pushNil(thread);
		pushNil(thread);
		pushNil(thread);
		Assert.assertTrue(value instanceof LuaBoolean);
		LuaBoolean bvalue = (LuaBoolean) value;
		Assert.assertEquals(size(thread), 3);
		Assert.assertEquals(bvalue.toBoolean(thread), true);
		Assert.assertEquals(size(thread), 3);
	}

	@Test
	public void testNumberReference() {
		pushDouble(thread, 55.3);
		Assert.assertEquals(size(thread), 1);
		LuaObject value = popReference(thread);
		Assert.assertEquals(size(thread), 0);
		pushNil(thread);
		pushNil(thread);
		pushNil(thread);
		Assert.assertTrue(value instanceof LuaNumber);
		LuaNumber nvalue = (LuaNumber) value;
		Assert.assertEquals(size(thread), 3);
		Assert.assertFalse(nvalue.isByte(thread));
		Assert.assertFalse(nvalue.isShort(thread));
		Assert.assertFalse(nvalue.isInt(thread));
		Assert.assertFalse(nvalue.isLong(thread));
		Assert.assertTrue(nvalue.isFloat(thread));
		Assert.assertTrue(nvalue.isDouble(thread));
		Assert.assertEquals(size(thread), 3);
		Assert.assertEquals(nvalue.toDouble(thread), 55.3, 0.00001);
	}

	@Test
	public void testStringReference() {
		pushString(thread, "Hello, world");
		Assert.assertEquals(size(thread), 1);
		LuaObject value = popReference(thread);
		Assert.assertEquals(size(thread), 0);
		pushNil(thread);
		pushNil(thread);
		pushNil(thread);
		Assert.assertTrue(value instanceof LuaString);
		LuaString svalue = (LuaString) value;
		Assert.assertEquals(size(thread), 3);
		Assert.assertEquals(size(thread), 3);
		Assert.assertEquals(svalue.toString(thread), "Hello, world");
	}

	@Test
	public void testFunctionReference() throws LuaException {
		thread.state.doString("function test(a, b)\nreturn a+b\nend");
		thread.state.getGlobal("test");
		Assert.assertEquals(size(thread), 1);
		LuaObject value = popReference(thread);
		Assert.assertEquals(size(thread), 0);
		pushNil(thread);
		pushNil(thread);
		pushNil(thread);
		Assert.assertTrue(value instanceof LuaFunction);
		LuaFunction fvalue = (LuaFunction) value;
		Assert.assertEquals(size(thread), 3);
		Assert.assertEquals(size(thread), 3);
		LuaObject[] args = new LuaObject[2];
		pushInt(thread, 5);
		args[0] = popReference(thread);
		pushInt(thread, 4);
		args[1] = popReference(thread);
		LuaObject[] results = fvalue.call(thread, args, 1);
		Assert.assertEquals(results.length, 1);
		Assert.assertTrue(results[0] instanceof LuaNumber);
		Assert.assertEquals(((LuaNumber) results[0]).toInt(thread), 9);
	}
}
