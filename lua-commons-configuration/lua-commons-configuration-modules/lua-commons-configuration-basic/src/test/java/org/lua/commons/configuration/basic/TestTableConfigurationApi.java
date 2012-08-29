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

public class TestTableConfigurationApi {
	LuaContainer container;
	LuaConfiguration configuration;

	@BeforeMethod
	public void initLua() {
		LuaStateApiProvider.setFactory(new NativeLuaStateApiFactory());
		container = new LuaContainer();
		container.addLib(null, new TableConfigurationApi(), null, null);
		configuration = new FileLuaConfiguration(container, new File(
				"src/test/resources/test_table.conf"));
	}

	@AfterMethod
	public void closeLua() {
		configuration.close();
	}

	@Test
	public void testTableFunctions() {
		Assert.assertEquals(configuration.getString("test_table_concat"),
				"a&b&c&d");
		Assert.assertEquals(configuration.getString("test_table_insert"), "e");
		Assert.assertEquals(configuration.getString("test_table_remove"), "d");
		Assert.assertEquals(configuration.getString("test_table_sort"), "d");
	}
}
