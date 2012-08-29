package org.lua.commons.customapi.javafunctions.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.types.LuaObject;
import org.lua.commons.baseapi.types.LuaTable;
import org.lua.commons.baseapi.types.LuaTable.LuaTableEntry;
import org.lua.commons.customapi.javafunctions.handlers.types.GenericType;
import org.lua.commons.customapi.javafunctions.handlers.types.Type;

public class MapTypeHandler extends TypeHandler {

	public MapTypeHandler(TypeCastManager castManager) {
		super(castManager);
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class<?>[] { HashMap.class, Map.class };
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object handleFrom(LuaThread thread, LuaObject obj, Type expected) {
		if (expected instanceof GenericType && obj instanceof LuaTable) {
			Type[] genericTypes = ((GenericType) expected).getGenericTypes();
			if (genericTypes.length == 2) {
				LuaTable table = (LuaTable) obj;
				List<LuaTableEntry> entries = table.getEntries(thread);
				HashMap result = new HashMap();
				for (int i = 0; i < entries.size(); i++) {
					LuaTableEntry entry = entries.get(i);
					result.put(castManager.castFrom(thread, entry.getKey(),
							genericTypes[0]), castManager.castFrom(thread,
							entry.getValue(), genericTypes[1]));
				}
				return result;
			}
		}
		throw new LuaTypeCastException("Can not to cast lua object "
				+ obj.getClass().getName() + " to java object "
				+ expected.toString());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public LuaObject handleTo(LuaThread thread, Object obj) {
		if (obj instanceof Map) {
			LuaTable table = LuaTable.newTable(thread);
			Set<Map.Entry> entrySet = ((Map) obj).entrySet();
			for (Map.Entry entry : entrySet)
				table.set(thread, castManager.castTo(thread, entry.getKey()),
						castManager.castTo(thread, entry.getValue()));
			return table;
		}
		throw new LuaTypeCastException("Can not to cast java object "
				+ obj.getClass().getName() + " to lua object");
	}
}
