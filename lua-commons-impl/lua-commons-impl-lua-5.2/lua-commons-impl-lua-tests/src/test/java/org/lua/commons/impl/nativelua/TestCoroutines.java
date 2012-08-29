package org.lua.commons.impl.nativelua;

import org.lua.commons.nativeapi.LuaStateApi;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestCoroutines {

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
    public void testYield() {
        state._pushjclosure(peer, this, "yieldfunc", 0);
        state._setglobal(peer, "yieldfunc");
        state._Ldostring(peer, "function foo(a, b)\nres = yieldfunc(a, b)\nreturn res .. \"!!\"\nend");
        state._getglobal(peer, "foo");
        state._pushstring(peer, "Hello, ");
        state._pushstring(peer, "world!");
        Assert.assertEquals(state._resume(peer, peer, 2), LuaStateApi.LUA_YIELD);
        Assert.assertEquals(state._gettop(peer), 1);
        Assert.assertEquals(state._resume(peer, peer, 1), LuaStateApi.LUA_OK);
        Assert.assertEquals(state._gettop(peer), 1);
        Assert.assertTrue(state._isstring(peer, -1));
        Assert.assertEquals(state._tostring(peer, -1), "Hello, world!!!");
    }

    public int yieldfunc(long peer) {
        LuaStateApi state = new NativeLuaStateApi();
        state._concat(peer, 2);

        state._yield(peer, 1);
        return 0;
    }

    @Test
    public void testYieldk() {
        state._pushjclosure(peer, this, "yieldkfunc", 0);
        state._setglobal(peer, "yieldkfunc");
        state._Ldostring(peer, "function foo(a, b)\nres = yieldkfunc(a, b)\nreturn res\nend");
        state._getglobal(peer, "foo");
        state._pushstring(peer, "Hello, ");
        state._pushstring(peer, "world!");
        Assert.assertEquals(state._resume(peer, peer, 2), LuaStateApi.LUA_YIELD);
        Assert.assertEquals(state._gettop(peer), 1);
        Assert.assertEquals(state._resume(peer, peer, 1), LuaStateApi.LUA_OK);
        Assert.assertEquals(state._gettop(peer), 1);
        Assert.assertTrue(state._isstring(peer, -1));
        Assert.assertEquals(state._tostring(peer, -1), "Hello, world!!!");
    }

    public int yieldkfunc(long peer) {
        LuaStateApi state = new NativeLuaStateApi();
        state._concat(peer, 2);

	state._yieldk(peer, 1, this, "yieldkcontinuation");
        return 0;
    }

    public int yieldkcontinuation(long peer) {
        LuaStateApi state = new NativeLuaStateApi();
        state._pushstring(peer, "!!");
        state._concat(peer, 2);
        return 1;
    }

}
