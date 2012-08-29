package org.lua.commons.baseapi;

import static org.lua.commons.baseapi.BaseLuaTools.doFile;
import static org.lua.commons.baseapi.BaseLuaTools.doString;
import static org.lua.commons.baseapi.BaseLuaTools.openLibs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.lua.commons.baseapi.extensions.references.LuaReferencesStorage;
import org.lua.commons.baseapi.extensions.references.WeakLuaReferencesStorage;
import org.lua.commons.baseapi.extensions.threadpool.SimpleLuaContextThreadPool;
import org.lua.commons.impl.nativelua.NativeLuaStateApiFactory;
import org.lua.commons.nativeapi.LuaException;
import org.lua.commons.nativeapi.LuaStateApiProvider;
import org.lua.commons.nativeapi.LuaStateImpl;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestLua {

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
	public void testDoString() throws LuaException {
		doString(lua, "res = 1+2+3");
		thread.state.getGlobal("res");
		Assert.assertEquals(thread.state.getTop(), 1);
		Assert.assertTrue(thread.state.isNumber(-1));
		Assert.assertEquals(thread.state.toInteger(-1), 6);
	}

	@Test(expectedExceptions = LuaException.class)
	public void testDoStringFailed() throws LuaException {
		doString(lua, "avav--vs");
	}

	@Test
	public void testDoFile() throws IOException, LuaException {
		File file = File.createTempFile("test", "lua");
		PrintWriter out = new PrintWriter(new FileOutputStream(file));
		try {
			out.println("res = 1+2+3");
		} finally {
			out.close();
		}
		doFile(thread, file);
		thread.state.getGlobal("res");
		Assert.assertEquals(thread.state.getTop(), 1);
		Assert.assertTrue(thread.state.isNumber(-1));
		Assert.assertEquals(thread.state.toInteger(-1), 6);
	}

	@Test(expectedExceptions = LuaException.class)
	public void testDoFileFailed() throws IOException, LuaException {
		File file = File.createTempFile("test", "lua");
		PrintWriter out = new PrintWriter(new FileOutputStream(file));
		try {
			out.println("avav--vs");
		} finally {
			out.close();
		}
		doFile(thread, file);
	}

	@Test
	public void testOpenLibs() throws LuaException {
		openLibs(lua);
		doString(lua, "res = math.min(5, 3)");
		thread.state.getGlobal("res");
		Assert.assertEquals(thread.state.getTop(), 1);
		Assert.assertTrue(thread.state.isNumber(-1));
		Assert.assertEquals(thread.state.toInteger(-1), 3);
	}

}
