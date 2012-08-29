package org.lua.commons.impl.nativelua;

import org.lua.commons.nativeapi.LuaStateApi;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class TestSystem {

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
    public void testDoString() {
        state._Ldostring(peer, "test = 5+6");
        state._getglobal(peer, "test");
        Assert.assertEquals(state._gettop(peer), 1);
        Assert.assertTrue(state._isnumber(peer, -1));
        Assert.assertEquals(state._tointeger(peer, -1), 11);
    }

    @Test
    public void testLoadString() {
        state._Lloadstring(peer, "test=9");
        Assert.assertEquals(state._gettop(peer), 1);
        state._getglobal(peer, "test");
        Assert.assertTrue(state._isnil(peer, -1));
        state._pop(peer, 1);
        state._call(peer, 0, 0);
        Assert.assertEquals(state._gettop(peer), 0);
        state._getglobal(peer, "test");
        Assert.assertEquals(state._gettop(peer), 1);
        Assert.assertTrue(state._isnumber(peer, -1));
        Assert.assertEquals(state._tointeger(peer, -1), 9);
    }

    @Test(enabled = false)
    public void testDoFile() throws FileNotFoundException {
        // TODO fix
        PrintWriter out = new PrintWriter(new File("target/test-classes/test.lua"));
        out.println("test = 5+6");
        out.close();

        state._Ldofile(peer, "target/test-classes/test.lua");
        state._getglobal(peer, "test");
        Assert.assertEquals(state._gettop(peer), 1);
        Assert.assertTrue(state._isnumber(peer, -1));
        Assert.assertEquals(state._tointeger(peer, -1), 11);
    }

    @Test(enabled = false)
    public void testLoadFile() throws FileNotFoundException {
        PrintWriter out = new PrintWriter(new File("target/test-classes/test.lua"));
        out.println("test = 9");
        out.close();

        state._Lloadfile(peer, "target/test-classes/test.lua");
        Assert.assertEquals(state._gettop(peer), 1);
        state._getglobal(peer, "test");
        Assert.assertTrue(state._isnil(peer, -1));
        state._pop(peer, 1);
        state._call(peer, 0, 0);
        Assert.assertEquals(state._gettop(peer), 0);
        state._getglobal(peer, "test");
        Assert.assertEquals(state._gettop(peer), 1);
        Assert.assertTrue(state._isnumber(peer, -1));
        Assert.assertEquals(state._tointeger(peer, -1), 9);
    }

    @Test
    public void testNewThread() {
        long thread = state._newthread(peer);
        Assert.assertEquals(state._gettop(peer), 1);
        Assert.assertEquals(state._gettop(thread), 0);
        state._pushnil(peer);
        Assert.assertEquals(state._gettop(peer), 2);
        Assert.assertEquals(state._gettop(thread), 0);
        state._pushnil(thread);
        state._pushnil(thread);
        state._pushnil(thread);
        Assert.assertEquals(state._gettop(peer), 2);
        Assert.assertEquals(state._gettop(thread), 3);
    }

    @Test
    public void testStatus() {
        Assert.assertEquals(state._status(peer), LuaStateApi.LUA_OK);
    }

    @Test
    public void testVersion() {
        Assert.assertEquals(state._version(peer), 502);
    }

    @Test
    public void testLen() {
        state._pushstring(peer, "Hello, world!");
        state._len(peer, -1);
        Assert.assertTrue(state._isnumber(peer, -1));
        Assert.assertEquals(state._tointeger(peer, -1), 13);
    }

    @Test
    public void testRawLen() {
        state._pushstring(peer, "Hello, world!");
        Assert.assertEquals(state._rawlen(peer, -1), 13);
    }

    @Test
    public void testWhere() {
        state._pushjfunction(peer, this, "function");
        state._setglobal(peer, "fun");
        state._Ldostring(peer, "fun()");
    }

    public int function(long peer) {
        LuaStateApi state = new NativeLuaStateApi();
        state._Lwhere(peer, 1);
        Assert.assertEquals(state._tostring(peer, -1), "[string \"fun()\"]:1: ");
        return 0;
    }

}
