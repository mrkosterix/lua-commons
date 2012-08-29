package org.lua.commons.impl.nativelua;

import org.lua.commons.nativeapi.LuaStateApi;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestStack {

	private LuaStateApi state;

	private long peer;

	@BeforeMethod
	public void initLua() {
		state = new NativeLuaStateApiFactory().getLuaStateApi();
		peer = state._Lnewstate();
		state._Lopenlibs(peer);
	}

	@AfterMethod
	public void closeLua() {
		state._close(peer);
	}

	@Test
	public void testNil() {
		Assert.assertEquals(state._gettop(peer), 0);

		state._pushnil(peer);
		Assert.assertTrue(state._isnil(peer, -1));

		Assert.assertEquals(state._gettop(peer), 1);
	}

	@Test
	public void testNone() {
		Assert.assertEquals(state._gettop(peer), 0);

		Assert.assertTrue(state._isnone(peer, 1));
		Assert.assertTrue(state._isnoneornil(peer, 1));
		state._pushnil(peer);
		Assert.assertTrue(state._isnil(peer, 1));
		Assert.assertFalse(state._isnone(peer, 1));
		Assert.assertTrue(state._isnoneornil(peer, 1));

		Assert.assertEquals(state._gettop(peer), 1);
	}

	@Test
	public void testBoolean() {
		Assert.assertEquals(state._gettop(peer), 0);

		state._pushboolean(peer, true);
		Assert.assertTrue(state._isboolean(peer, -1));
		Assert.assertTrue(state._toboolean(peer, -1));
		state._pop(peer, 1);
		state._pushboolean(peer, false);
		Assert.assertTrue(state._isboolean(peer, -1));
		Assert.assertFalse(state._toboolean(peer, -1));

		Assert.assertEquals(state._gettop(peer), 1);
	}

	@Test
	public void testNumber() {
		Assert.assertEquals(state._gettop(peer), 0);

		state._pushnumber(peer, 12345);
		Assert.assertTrue(state._isnumber(peer, -1));
		Assert.assertEquals(state._tonumber(peer, -1), 12345, 0.0001);

		Assert.assertEquals(state._gettop(peer), 1);
	}

	@Test
	public void testIntegerX() {
		Assert.assertEquals(state._gettop(peer), 0);

		state._pushinteger(peer, 12345);
		Assert.assertTrue(state._isintegerx(peer, -1, 0, 20000));
		Assert.assertFalse(state._isintegerx(peer, -1, 0, 200));
		Assert.assertFalse(state._isintegerx(peer, -1, 20000, 40000));
		Assert.assertEquals(state._tointeger(peer, -1), 12345);

		Assert.assertEquals(state._gettop(peer), 1);
	}

	@Test
	public void testString() {
		Assert.assertEquals(state._gettop(peer), 0);

		state._pushstring(peer, "Hello, world");
		Assert.assertTrue(state._isstring(peer, -1));
		Assert.assertEquals(state._tostring(peer, -1), "Hello, world");

		Assert.assertEquals(state._gettop(peer), 1);
	}

	@Test
	public void testFunction() {
		Assert.assertEquals(state._gettop(peer), 0);

		Assert.assertEquals(0, state._Ldostring(peer, "function func()\nend"));
		Assert.assertEquals(state._gettop(peer), 0);
		state._getglobal(peer, "func");
		Assert.assertEquals(state._gettop(peer), 1);
		Assert.assertTrue(state._isfunction(peer, -1));

		Assert.assertEquals(state._gettop(peer), 1);
	}

	@Test
	public void testTable() {
		Assert.assertEquals(state._gettop(peer), 0);

		state._newtable(peer);
		Assert.assertTrue(state._istable(peer, -1));

		Assert.assertEquals(state._gettop(peer), 1);
	}

	@Test
	public void testThread() {
		Assert.assertEquals(state._gettop(peer), 0);

		state._pushthread(peer);
		Assert.assertTrue(state._isthread(peer, -1));
		Assert.assertEquals(state._tothread(peer, -1), peer);

		Assert.assertEquals(state._gettop(peer), 1);
	}

	@Test
	public void testUserdata() {
		Assert.assertEquals(state._gettop(peer), 0);

		state._pushobject(peer, this);
		Assert.assertTrue(state._isuserdata(peer, -1));

		Assert.assertEquals(state._gettop(peer), 1);
	}

	@Test
	public void testObject() {
		Assert.assertEquals(state._gettop(peer), 0);

		state._pushobject(peer, this);
		Assert.assertEquals(state._gettop(peer), 1);
		Assert.assertTrue(state._isobject(peer, -1));
		Assert.assertTrue(state._isobjectx(peer, -1,
				"org.lua.commons.impl.nativelua.TestStack"));
		Assert.assertTrue(state._isobjectx(peer, -1, "java.lang.Object"));
		Assert.assertEquals(state._toobject(peer, -1), this);

		Assert.assertEquals(state._gettop(peer), 1);
	}

	@Test
	public void testJFunction() {
		Assert.assertEquals(state._gettop(peer), 0);

		state._pushstring(peer, "Hello, ");
		state._pushjclosure(peer, this, "jfunction", 1);
		Assert.assertEquals(state._gettop(peer), 1);
		state._pushstring(peer, "from ");
		state._call(peer, 1, 1);
		Assert.assertEquals(state._gettop(peer), 1);
		Assert.assertTrue(state._isstring(peer, -1));
		Assert.assertEquals(state._tostring(peer, -1),
				"Hello, from java function");

		Assert.assertEquals(state._gettop(peer), 1);
	}

	public int jfunction(long peer) {
		LuaStateApi state = new NativeLuaStateApi();
		state._pushstring(peer, "java function");
		state._concat(peer, 3);
		return 1;
	}

	@Test
	public void testPop() {
		Assert.assertEquals(state._gettop(peer), 0);

		state._pushinteger(peer, 56);
		state._pushinteger(peer, 92);
		Assert.assertEquals(state._gettop(peer), 2);
		state._pop(peer, 2);
		Assert.assertEquals(state._gettop(peer), 0);
	}

	@Test
	public void testPushValue() {
		Assert.assertEquals(state._gettop(peer), 0);

		state._pushinteger(peer, 56);
		state._pushnil(peer);
		Assert.assertEquals(state._gettop(peer), 2);
		Assert.assertFalse(state._isnumber(peer, -1));
		state._pushvalue(peer, -2);
		Assert.assertEquals(state._gettop(peer), 3);
		Assert.assertTrue(state._isnumber(peer, -1));
		Assert.assertEquals(state._tointeger(peer, -1), 56);

		Assert.assertEquals(state._gettop(peer), 3);
	}

	@Test
	public void testGetTop() {
		Assert.assertEquals(state._gettop(peer), 0);

		state._pushnil(peer);
		Assert.assertEquals(state._gettop(peer), 1);
		state._pushnil(peer);
		Assert.assertEquals(state._gettop(peer), 2);
		state._pop(peer, 1);
		Assert.assertEquals(state._gettop(peer), 1);
		state._pop(peer, 1);
		Assert.assertEquals(state._gettop(peer), 0);
	}

	@Test
	public void testSetTop() {
		Assert.assertEquals(state._gettop(peer), 0);

		state._pushinteger(peer, 5);
		Assert.assertEquals(state._gettop(peer), 1);
		Assert.assertTrue(state._isnone(peer, 2));
		state._settop(peer, 2);
		Assert.assertEquals(state._gettop(peer), 2);
		Assert.assertFalse(state._isnone(peer, 2));
		Assert.assertTrue(state._isnil(peer, -1));
		state._settop(peer, 1);
		Assert.assertEquals(state._gettop(peer), 1);
		Assert.assertTrue(state._isnumber(peer, -1));
		Assert.assertEquals(state._tointeger(peer, -1), 5);
		state._settop(peer, 0);
		Assert.assertEquals(state._gettop(peer), 0);
	}

	@Test
	public void testCopy() {
		Assert.assertEquals(state._gettop(peer), 0);

		state._pushinteger(peer, 5);
		state._pushnil(peer);
		Assert.assertEquals(state._gettop(peer), 2);
		Assert.assertTrue(state._isnumber(peer, -2));
		Assert.assertTrue(state._isnil(peer, -1));
		state._copy(peer, -2, -1);
		Assert.assertEquals(state._gettop(peer), 2);
		Assert.assertTrue(state._isnumber(peer, -2));
		Assert.assertTrue(state._isnumber(peer, -1));
		Assert.assertEquals(state._tointeger(peer, -1), 5);
		Assert.assertEquals(state._tointeger(peer, -2), 5);
	}

	@Test
	public void testInsert() {
		Assert.assertEquals(state._gettop(peer), 0);

		state._pushinteger(peer, 5);
		state._pushnil(peer);
		Assert.assertEquals(state._gettop(peer), 2);
		Assert.assertTrue(state._isnumber(peer, -2));
		Assert.assertTrue(state._isnil(peer, -1));
		state._insert(peer, -2);
		Assert.assertEquals(state._gettop(peer), 2);
		Assert.assertTrue(state._isnil(peer, -2));
		Assert.assertTrue(state._isnumber(peer, -1));
		Assert.assertEquals(state._tointeger(peer, -1), 5);
	}

	@Test
	public void testRemove() {
		Assert.assertEquals(state._gettop(peer), 0);

		state._pushinteger(peer, 5);
		state._pushnil(peer);
		state._pushinteger(peer, 7);
		Assert.assertEquals(state._gettop(peer), 3);
		Assert.assertTrue(state._isnumber(peer, -3));
		Assert.assertTrue(state._isnil(peer, -2));
		Assert.assertTrue(state._isnumber(peer, -1));
		state._remove(peer, -2);
		Assert.assertEquals(state._gettop(peer), 2);
		Assert.assertTrue(state._isnumber(peer, -2));
		Assert.assertTrue(state._isnumber(peer, -1));
		Assert.assertEquals(state._tointeger(peer, -2), 5);
		Assert.assertEquals(state._tointeger(peer, -1), 7);
	}

	@Test
	public void testReplace() {
		Assert.assertEquals(state._gettop(peer), 0);

		state._pushinteger(peer, 5);
		state._pushnil(peer);
		state._pushinteger(peer, 7);
		Assert.assertEquals(state._gettop(peer), 3);
		Assert.assertTrue(state._isnumber(peer, -3));
		Assert.assertTrue(state._isnil(peer, -2));
		Assert.assertTrue(state._isnumber(peer, -1));
		state._replace(peer, -3);
		Assert.assertEquals(state._gettop(peer), 2);
		Assert.assertTrue(state._isnumber(peer, -2));
		Assert.assertTrue(state._isnil(peer, -1));
		Assert.assertEquals(state._tointeger(peer, -2), 7);
	}

	@Test
	public void testType() {
		Assert.assertEquals(state._gettop(peer), 0);

		Assert.assertEquals(state._type(peer, 1), LuaStateApi.LUA_TNONE);

		state._pushnil(peer);
		Assert.assertEquals(state._type(peer, -1), LuaStateApi.LUA_TNIL);
		state._pop(peer, 1);

		state._pushstring(peer, "String");
		Assert.assertEquals(state._type(peer, -1), LuaStateApi.LUA_TSTRING);
		state._pop(peer, 1);
	}

	@Test
	public void testLTypeName() {
		Assert.assertEquals(state._gettop(peer), 0);

		state._pushstring(peer, "String");
		Assert.assertEquals(state._Ltypename(peer, -1), "string");
		state._pop(peer, 1);
	}

	@Test
	public void testCheckStack() {
		Assert.assertEquals(state._gettop(peer), 0);
		state._checkstack(peer, 100);
		Assert.assertEquals(state._gettop(peer), 0);
	}

}
