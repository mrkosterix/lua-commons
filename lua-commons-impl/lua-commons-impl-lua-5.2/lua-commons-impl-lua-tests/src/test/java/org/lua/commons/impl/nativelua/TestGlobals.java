package org.lua.commons.impl.nativelua;

import org.lua.commons.nativeapi.LuaStateApi;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestGlobals {

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
    public void testGetGlobal() {
        Assert.assertEquals(state._gettop(peer), 0);

        state._Ldostring(peer, "test = 5");
        state._getglobal(peer, "test");
        Assert.assertEquals(state._gettop(peer), 1);
        Assert.assertTrue(state._isnumber(peer, -1));
        Assert.assertEquals(state._tointeger(peer, -1), 5);
    }

    @Test
    public void testSetGlobal() {
        Assert.assertEquals(state._gettop(peer), 0);

        state._pushinteger(peer, 5);
        state._setglobal(peer, "test");
        Assert.assertEquals(state._gettop(peer), 0);
        state._getglobal(peer, "test");
        Assert.assertEquals(state._gettop(peer), 1);
        Assert.assertTrue(state._isnumber(peer, -1));
        Assert.assertEquals(state._tointeger(peer, -1), 5);
    }

    @Test
    public void testGetMetatable() {
        Assert.assertEquals(state._gettop(peer), 0);

        state._pushobject(peer, this);
        Assert.assertFalse(state._getmetatable(peer, -1));
        Assert.assertEquals(state._gettop(peer), 1);
    }

    @Test
    public void testSetMetatable() {
        Assert.assertEquals(state._gettop(peer), 0);

        state._pushobject(peer, this);
        state._newtable(peer);
        state._pushstring(peer, "TEST");
        state._pushboolean(peer, true);
        state._settable(peer, -3);
        state._setmetatable(peer, -2);

        Assert.assertEquals(state._gettop(peer), 1);
        Assert.assertTrue(state._getmetatable(peer, -1));
        Assert.assertEquals(state._gettop(peer), 2);
        state._pushstring(peer, "TEST");
        state._gettable(peer, -2);
        Assert.assertTrue(state._isboolean(peer, -1));
        Assert.assertTrue(state._toboolean(peer, -1));
    }

    @Test
    public void testGetUservalue() {
        Assert.assertEquals(state._gettop(peer), 0);

        state._pushobject(peer, this);
        Assert.assertEquals(state._gettop(peer), 1);
        state._getuservalue(peer, -1);
        Assert.assertEquals(state._gettop(peer), 2);
        Assert.assertTrue(state._istable(peer, -1));
        state._pop(peer, 2);

        state._pushobject(peer, this);
        state._pushnil(peer);
        state._setuservalue(peer, -2);
        Assert.assertEquals(state._gettop(peer), 1);
        state._getuservalue(peer, -1);
        Assert.assertEquals(state._gettop(peer), 2);
        Assert.assertTrue(state._isnil(peer, -1));
    }

    @Test
    public void testSetUservalue() {
        state._pushobject(peer, this);
        Assert.assertEquals(state._gettop(peer), 1);
        state._pushnil(peer);
        state._setuservalue(peer, -2);
        state._getuservalue(peer, -1);
        Assert.assertEquals(state._gettop(peer), 2);
        Assert.assertTrue(state._isnil(peer, -1));
    }

}
