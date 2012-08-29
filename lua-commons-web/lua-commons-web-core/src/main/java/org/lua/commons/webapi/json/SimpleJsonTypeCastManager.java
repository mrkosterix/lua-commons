package org.lua.commons.webapi.json;

import static org.lua.commons.baseapi.types.LuaObjectTools.tolua;

import java.util.ArrayList;

import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.types.LuaBoolean;
import org.lua.commons.baseapi.types.LuaNumber;
import org.lua.commons.baseapi.types.LuaObject;
import org.lua.commons.baseapi.types.LuaString;
import org.lua.commons.baseapi.types.LuaTable;
import org.lua.commons.baseapi.types.LuaTable.LuaTableEntry;

public class SimpleJsonTypeCastManager implements JsonTypeCastManager {

	public SimpleJsonTypeCastManager() {
	}

	public void start() {
		// do nothing
	}

	public void close() {
		// do nothing
	}

	public String toJson(LuaThread thread, LuaObject obj) {
		if (obj == null)
			return "null";
		if (obj instanceof LuaBoolean) {
			return String.valueOf(((LuaBoolean) obj).toBoolean());
		}
		if (obj instanceof LuaNumber) {
			LuaNumber num = (LuaNumber) obj;
			if (num.isInt())
				return String.valueOf(num.toInt());
			if (num.isLong())
				return String.valueOf(num.toLong());
			return String.valueOf(num.toDouble());
		}
		if (obj instanceof LuaString) {
			return "\"" + ((LuaString) obj).toString() + "\"";
		}
		if (obj instanceof LuaTable) {
			LuaTable table = (LuaTable) obj;
			StringBuilder builder = new StringBuilder();
			builder.append('{');
			boolean z = false;
			for (LuaTableEntry entry : table.getEntries(thread)) {
				if (z)
					builder.append(',');
				LuaObject key = entry.getKey();
				LuaObject value = entry.getValue();
				builder.append(toJson(thread, key));
				builder.append(':');
				builder.append(toJson(thread, value));
				z = true;
			}
			builder.append('}');
			return builder.toString();
		}
		throw new LuaJsonCastException("Couldn't cast object with type "
				+ obj.toString());
	}

	public LuaObject toLua(LuaThread thread, String json) {
		return parse(thread, removeTrailSpaces(json));
	}

	protected LuaObject parse(LuaThread thread, String json) {
		if (json.startsWith("{"))
			return parseTable(thread, json);
		if (json.startsWith("["))
			return parseArray(thread, json);
		return parseSimple(thread, json);
	}

	protected LuaObject parseTable(LuaThread thread, String json) {
		String[] keyvalues = split(json.substring(1, json.length() - 1), ',');
		LuaTable table = LuaTable.newTable(thread);
		for (int i = 0; i < keyvalues.length; i++) {
			String[] strs = split(keyvalues[i], ':');
			String key = strs[0];
			String value = strs[1];
			table.set(thread, parseSimple(thread, key), parse(thread, value));
		}
		return table;
	}

	protected LuaObject parseArray(LuaThread thread, String json) {
		String[] values = split(json.substring(1, json.length() - 1), ',');
		LuaTable table = LuaTable.newTable(thread);
		for (int i = 0; i < values.length; i++) {
			table.set(thread, tolua(thread, i + 1), parse(thread, values[i]));
		}
		return table;
	}

	protected LuaObject parseSimple(LuaThread thread, String json) {
		if (json.equalsIgnoreCase("null"))
			return null;
		if (json.equalsIgnoreCase("true"))
			return LuaBoolean.valueOf(thread, true);
		if (json.equalsIgnoreCase("false"))
			return LuaBoolean.valueOf(thread, false);
		if (json.startsWith("\"") || json.startsWith("'"))
			return LuaString.valueOf(thread,
					json.substring(1, json.length() - 1));
		try {
			return LuaNumber.valueOf(thread, Double.parseDouble(json));
		} catch (NumberFormatException e) {
			throw new LuaJsonCastException(
					"Couldn't cast json string to lua object \"" + json + "\"");
		}
	}

	protected String[] split(String json, char e) {
		ArrayList<String> result = new ArrayList<String>();
		StringBuilder current = new StringBuilder();
		int lvldq = 0;
		int lvlq = 0;
		int lvls = 0;
		int lvlf = 0;
		for (int i = 0; i < json.length(); i++) {
			char c = json.charAt(i);
			if (c == '\'')
				lvlq = (lvlq + 1) % 2;
			if (c == '\"')
				lvldq = (lvldq + 1) % 2;
			if (c == '[')
				lvls++;
			if (c == ']')
				lvls--;
			if (c == '{')
				lvlf++;
			if (c == '}')
				lvlf--;
			if (lvldq == 0 && lvlq == 0 && lvls == 0 && lvlf == 0 && c == e) {
				result.add(current.toString());
				current = new StringBuilder();
			} else {
				current.append(c);
			}
		}
		result.add(current.toString());
		ArrayList<String> r = new ArrayList<String>();
		for (String v : result) {
			if (!v.isEmpty())
				r.add(v);
		}
		return r.toArray(new String[r.size()]);
	}

	protected String removeTrailSpaces(String json) {
		StringBuilder builder = new StringBuilder();
		int lvlq = 0;
		int lvldq = 0;
		for (int i = 0; i < json.length(); i++) {
			char c = json.charAt(i);
			if (c == '\'')
				lvlq = (lvlq + 1) % 2;
			if (c == '"')
				lvldq = (lvldq + 1) % 2;

			if (lvlq != 0 || lvldq != 0 || !(c == ' ' || c == '\t'))
				builder.append(c);
		}
		return builder.toString();
	}

}
