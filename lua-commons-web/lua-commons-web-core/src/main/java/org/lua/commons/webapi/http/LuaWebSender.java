package org.lua.commons.webapi.http;

public interface LuaWebSender {

	public LuaWebResponse send(byte[] requestBody);

}
