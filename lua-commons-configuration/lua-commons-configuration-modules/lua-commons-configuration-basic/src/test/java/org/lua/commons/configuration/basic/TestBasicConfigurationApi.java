package org.lua.commons.configuration.basic;

import java.io.File;

import org.lua.commons.configuration.FileLuaConfiguration;
import org.lua.commons.configuration.LuaConfiguration;
import org.lua.commons.configuration.LuaConfigurationException;
import org.lua.commons.customapi.container.LuaContainer;
import org.lua.commons.impl.nativelua.NativeLuaStateApiFactory;
import org.lua.commons.nativeapi.LuaStateApiProvider;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestBasicConfigurationApi {
	LuaContainer container;
	LuaConfiguration configuration;

	@BeforeMethod
	public void initLua() {
		LuaStateApiProvider.setFactory(new NativeLuaStateApiFactory());
		container = new LuaContainer();
		container.addLib(null, new BasicConfigurationApi(), null, null);
		configuration = new FileLuaConfiguration(container, new File(
				"src/test/resources/test.conf"));
	}

	@AfterMethod
	public void closeLua() {
		configuration.close();
	}

	@Test(expectedExceptions = LuaConfigurationException.class)
	public void testErrorFunction() {
		configuration.getString("test_error");
	}

	@Test
	public void testBasicFunctions() {
		Assert.assertEquals(configuration.getString("test_next"), "key");
		Assert.assertEquals(configuration.getInt("test_tonumber"), 92);
		Assert.assertEquals(configuration.getString("test_tostring"), "124.5");
		Assert.assertEquals(configuration.getString("test_type"), "string");
	}
}
