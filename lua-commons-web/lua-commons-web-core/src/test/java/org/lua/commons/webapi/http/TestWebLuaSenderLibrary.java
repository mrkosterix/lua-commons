package org.lua.commons.webapi.http;

import static org.lua.commons.baseapi.BaseLuaTools.doString;
import static org.lua.commons.baseapi.LuaStack.popInt;
import static org.lua.commons.baseapi.LuaStack.popString;

import org.lua.commons.baseapi.Lua;
import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.extensions.references.LuaReferencesStorage;
import org.lua.commons.baseapi.extensions.references.WeakLuaReferencesStorage;
import org.lua.commons.baseapi.extensions.threadpool.SimpleLuaContextThreadPool;
import org.lua.commons.customapi.javafunctions.handlers.SimpleTypeCastManager;
import org.lua.commons.customapi.javafunctions.handlers.TypeCastManager;
import org.lua.commons.impl.nativelua.NativeLuaStateApiFactory;
import org.lua.commons.nativeapi.LuaException;
import org.lua.commons.nativeapi.LuaStateApiProvider;
import org.lua.commons.nativeapi.LuaStateImpl;
import org.lua.commons.webapi.json.JsonTypeCastManager;
import org.lua.commons.webapi.json.SimpleJsonTypeCastManager;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestWebLuaSenderLibrary {

	Lua lua;
	LuaThread thread;

	@BeforeMethod
	public void initLua() {
		LuaStateApiProvider.setFactory(new NativeLuaStateApiFactory());
		lua = new Lua(new LuaStateImpl(), new SimpleLuaContextThreadPool());
		lua.addExtension(LuaReferencesStorage.class,
				new WeakLuaReferencesStorage(lua));
		lua.addExtension(TypeCastManager.class, new SimpleTypeCastManager());
		lua.addExtension(JsonTypeCastManager.class,
				new SimpleJsonTypeCastManager());
		lua.start();

		thread = lua.contextThread();
		thread.state.openLibs();
	}

	@AfterMethod
	public void closeLua() {
		lua.close();
	}

	@Test
	public void testWebLuaSenderLibrary() throws LuaException {
		LuaWebSenderLibrary web = new LuaWebSenderLibrary(
				new MockWebHttpSender());
		web.prepare(thread);
		doString(lua, "function test()\nend\n");
		doString(lua, "code, response = send(test)");

		thread.state.getGlobal("code");
		Assert.assertEquals(popInt(thread), 200);
		thread.state.getGlobal("response");
		Assert.assertEquals(popString(thread), "Hello, world!");
	}

	static class MockWebHttpSender implements LuaWebSender {

		public LuaWebResponse send(byte[] requestBody) {
			return new MockWebHttpResponse();
		}

	}

	static class MockWebHttpResponse implements LuaWebResponse {

		public int statusCode() {
			return 200;
		}

		public String getResponseJson() {
			return "'Hello, world!'";
		}
	}

}
