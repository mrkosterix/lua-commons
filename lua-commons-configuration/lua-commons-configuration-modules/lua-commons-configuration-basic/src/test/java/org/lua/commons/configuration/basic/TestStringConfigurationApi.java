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

public class TestStringConfigurationApi {
    LuaContainer container;
    LuaConfiguration configuration;

    @BeforeMethod
    public void initLua() {
        LuaStateApiProvider.setFactory(new NativeLuaStateApiFactory());
        container = new LuaContainer();
        container.addLib(null, new StringConfigurationApi(), null, null);
        configuration = new FileLuaConfiguration(container, new File("src/test/resources/test_string.conf"));
    }

    @AfterMethod
    public void closeLua() {
        configuration.close();
    }

    @Test
    public void testStringFunctions() {
        Assert.assertEquals(configuration.getString("test_string_char"), "Success");
        Assert.assertEquals(configuration.getInt("test_string_len"), 13);
        Assert.assertEquals(configuration.getInt("test_string_find"), 6);
        Assert.assertEquals(configuration.getString("test_string_sub"), "succeeded");
        Assert.assertEquals(configuration.getInt("test_string_match"), 7);
        Assert.assertEquals(configuration.getString("test_string_gsub"), "world hello Lua from");
        Assert.assertEquals(configuration.getString("test_string_gmatch"), "hello");
        Assert.assertEquals(configuration.getString("test_string_lower"), "hello, from lua!");
        Assert.assertEquals(configuration.getString("test_string_upper"), "HELLO, FROM LUA!");
        Assert.assertEquals(configuration.getString("test_string_reverse"), "!auL morf ,olleH");
        Assert.assertEquals(configuration.getString("test_string_rep"), "Repeat it, Repeat it, ");
        Assert.assertEquals(configuration.getString("test_string_format"), "a string");
    }
}
