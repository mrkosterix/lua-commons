package org.lua.commons.webapi.json;

import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.types.LuaObject;

public class JsonTools {

	public static String toJson(LuaThread thread, LuaObject object) {
		return thread.lua.getExtension(JsonTypeCastManager.class).toJson(
				thread, object);
	}

	public static LuaObject toLua(LuaThread thread, String json) {
		return thread.lua.getExtension(JsonTypeCastManager.class).toLua(thread,
				json);
	}

	public static String array(String[] jsons) {
		StringBuilder builder = new StringBuilder();
		builder.append('[');
		for (int i = 0; i < jsons.length; i++) {
			if (i > 0)
				builder.append(',');
			builder.append(jsons[i]);
		}
		builder.append(']');
		return builder.toString();
	}

}
