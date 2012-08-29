package org.lua.commons.impl.nativelua;

import java.io.IOException;

import org.lua.commons.nativeapi.LuaStateApi;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestDump {

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
	public void testDumpAndLoadBuffer() throws IOException {
		state._Lloadfile(peer, "src/test/resources/test.lua");
		byte[] bytes = state._dump(peer);
		state._Lloadbuffer(peer, bytes, "test");
		state._call(peer, 0, 1);
		Assert.assertEquals(state._tostring(peer, -1), "Hello, world!");
	}

}