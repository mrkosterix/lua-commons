package org.lua.commons.configuration.basic;

import java.io.File;

import org.lua.commons.configuration.FileLuaConfiguration;
import org.lua.commons.configuration.LuaConfiguration;
import org.lua.commons.customapi.container.LuaContainer;
import org.lua.commons.impl.nativelua.NativeLuaStateApiFactory;
import org.lua.commons.nativeapi.LuaStateApiProvider;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestMathConfigurationApi {
	LuaContainer container;
	LuaConfiguration configuration;

	@BeforeMethod
	public void initLua() {
		LuaStateApiProvider.setFactory(new NativeLuaStateApiFactory());
		container = new LuaContainer();
		container.addLib(null, new MathConfigurationApi(), null, null);
		configuration = new FileLuaConfiguration(container, new File(
				"src/test/resources/test_math.conf"));
	}

	@AfterMethod
	public void closeLua() {
		configuration.close();
	}

	@Test
	public void testMathFunctions() {
		Assert.assertEquals(configuration.getInt("test_math_abs"), 72);
		Assert.assertEquals(configuration.getInt("test_math_ceil"), 18);
		Assert.assertEquals(configuration.getInt("test_math_floor"), 17);
		// TODO ...
	}
}
