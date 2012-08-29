package org.lua.commons.configuration;

import java.io.File;

import org.lua.commons.customapi.container.LuaContainer;
import org.lua.commons.impl.nativelua.NativeLuaStateApiFactory;
import org.lua.commons.nativeapi.LuaStateApiProvider;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestLuaConfiguration {
	LuaContainer container;
	LuaConfiguration configuration;

	@BeforeMethod
	public void initLua() {
		LuaStateApiProvider.setFactory(new NativeLuaStateApiFactory());
		container = new LuaContainer();
		configuration = new FileLuaConfiguration(container, new File(
				"src/test/resources/test.conf"));
	}

	@AfterMethod
	public void closeLua() {
		configuration.close();
	}

	@Test
	public void testGetTypes() {
		Assert.assertEquals(configuration.getBoolean("testboolean"), true);
		Assert.assertEquals(configuration.getByte("testbyte"), 122);
		Assert.assertEquals(configuration.getShort("testshort"), 345);
		Assert.assertEquals(configuration.getInt("testint"), 83214);
		Assert.assertEquals(configuration.getLong("testlong"),
				1234567890123456L);
		Assert.assertEquals(configuration.getString("teststring"),
				"Hello, world!");

		String[] array = configuration.getStringArray("teststringarray");
		Assert.assertEquals(array.length, 2);
		Assert.assertEquals(array[0], "value1");
		Assert.assertEquals(array[1], "value2");

		Assert.assertEquals(configuration.getString("testfunction"),
				"Function called 1 times");
		Assert.assertEquals(configuration.getString("testfunction"),
				"Function called 2 times");
	}

}
