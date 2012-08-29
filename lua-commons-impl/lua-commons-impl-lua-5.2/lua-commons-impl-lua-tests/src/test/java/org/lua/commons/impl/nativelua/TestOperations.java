package org.lua.commons.impl.nativelua;

import org.lua.commons.nativeapi.LuaStateApi;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestOperations {

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
    public void testAbsIndex() {
        Assert.assertEquals(state._gettop(peer), 0);

        state._pushinteger(peer, 5478);
        Assert.assertTrue(state._isnumber(peer, -1));
        Assert.assertTrue(state._isnumber(peer, state._absindex(peer, -1)));

        Assert.assertEquals(state._gettop(peer), 1);
    }

    @Test
    public void testArith() {
        Assert.assertEquals(state._gettop(peer), 0);

        state._pushinteger(peer, 123);
        state._pushinteger(peer, 456);
        state._arith(peer, LuaStateApi.LUA_OPADD);
        Assert.assertTrue(state._isnumber(peer, -1));
        Assert.assertEquals(state._tointeger(peer, -1), 123 + 456);

        Assert.assertEquals(state._gettop(peer), 1);
    }

    @Test
    public void testCompare() {
        Assert.assertEquals(state._gettop(peer), 0);

        state._pushstring(peer, "First string");
        state._pushstring(peer, "Second string");
        Assert.assertFalse(state._compare(peer, -2, -1, LuaStateApi.LUA_OPEQ));
        Assert.assertTrue(state._compare(peer, -2, -1, LuaStateApi.LUA_OPLE));
        Assert.assertTrue(state._compare(peer, -2, -1, LuaStateApi.LUA_OPLT));
        state._pop(peer, 2);
        state._pushstring(peer, "Equal string");
        state._pushstring(peer, "Equal string");
        Assert.assertTrue(state._compare(peer, -2, -1, LuaStateApi.LUA_OPEQ));
        Assert.assertTrue(state._compare(peer, -2, -1, LuaStateApi.LUA_OPLE));
        Assert.assertFalse(state._compare(peer, -2, -1, LuaStateApi.LUA_OPLT));

        Assert.assertEquals(state._gettop(peer), 2);
    }

    @Test
    public void testConcat() {
        Assert.assertEquals(state._gettop(peer), 0);

        state._pushstring(peer, "Hello, ");
        state._pushstring(peer, "world");
        state._pushstring(peer, "!!!");
        state._concat(peer, 3);
        Assert.assertEquals(state._gettop(peer), 1);
        Assert.assertTrue(state._isstring(peer, -1));
        Assert.assertEquals(state._tostring(peer, -1), "Hello, world!!!");

        Assert.assertEquals(state._gettop(peer), 1);
    }

}
