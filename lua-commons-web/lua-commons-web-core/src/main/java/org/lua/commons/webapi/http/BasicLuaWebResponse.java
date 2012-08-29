package org.lua.commons.webapi.http;

public class BasicLuaWebResponse implements LuaWebResponse {

	private final int code;

	private final String json;

	public BasicLuaWebResponse(int code, String json) {
		this.code = code;
		this.json = json;
	}

	public int statusCode() {
		return code;
	}

	public String getResponseJson() {
		return json;
	}

}
