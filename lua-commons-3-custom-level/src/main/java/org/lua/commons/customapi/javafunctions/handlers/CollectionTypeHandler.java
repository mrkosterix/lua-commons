package org.lua.commons.customapi.javafunctions.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.types.LuaObject;
import org.lua.commons.baseapi.types.LuaTable;
import org.lua.commons.baseapi.types.LuaTable.LuaTableEntry;
import org.lua.commons.customapi.javafunctions.handlers.types.GenericType;
import org.lua.commons.customapi.javafunctions.handlers.types.Type;
import static org.lua.commons.baseapi.types.LuaObjectTools.tolua;

public class CollectionTypeHandler extends TypeHandler {

	public CollectionTypeHandler(TypeCastManager castManager) {
		super(castManager);
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class<?>[] { ArrayList.class, List.class, Collection.class };
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object handleFrom(LuaThread thread, LuaObject obj, Type expected) {
		if (expected instanceof GenericType && obj instanceof LuaTable) {
			Type[] genericTypes = ((GenericType) expected).getGenericTypes();
			if (genericTypes.length == 1) {
				LuaTable table = (LuaTable) obj;
				List<LuaTableEntry> entries = table.getEntries(thread);
				ArrayList result = new ArrayList();
				for (int i = 0; i < entries.size(); i++)
					result.add(castManager.castFrom(thread, entries.get(i)
							.getValue(), genericTypes[0]));
				return result;
			}
		}
		throw new LuaTypeCastException("Can not to cast lua object "
				+ obj.getClass().getName() + " to java object "
				+ expected.toString());
	}

	@Override
	public LuaObject handleTo(LuaThread thread, Object obj) {
		if (obj instanceof Collection) {
			LuaTable table = LuaTable.newTable(thread);
			int index = 1;
			for (Object key : (Collection<?>) obj) {
				table.set(thread, tolua(thread, index),
						castManager.castTo(thread, key));
				index++;
			}
			return table;
		}
		throw new LuaTypeCastException("Can not to cast java object "
				+ obj.getClass().getName() + " to lua object");
	}
}
