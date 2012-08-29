package org.lua.commons.customapi.javafunctions.handlers;

import java.lang.reflect.Array;
import java.util.List;

import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.types.LuaObject;
import org.lua.commons.baseapi.types.LuaTable;
import org.lua.commons.baseapi.types.LuaTable.LuaTableEntry;
import org.lua.commons.customapi.javafunctions.handlers.types.ArrayType;
import org.lua.commons.customapi.javafunctions.handlers.types.Type;

import static org.lua.commons.baseapi.types.LuaObjectTools.tolua;

public class ArrayTypeHandler extends TypeHandler {

	public ArrayTypeHandler(TypeCastManager castManager) {
		super(castManager);
	}

	public Class<?>[] getClasses() {
		return new Class<?>[0];
	}

	public Object handleFrom(LuaThread thread, LuaObject obj, Type expected) {
		if (expected instanceof ArrayType && obj instanceof LuaTable) {
			Type componentType = ((ArrayType) expected).getComponentArrayType();
			LuaTable table = (LuaTable) obj;
			List<LuaTableEntry> entries = table.getEntries(thread);
			int[] dimensions = new int[((ArrayType) expected).getDimensions()];
			dimensions[0] = entries.size();
			Object result = Array.newInstance(
					((ArrayType) expected).castComponentToJava(), dimensions);
			for (int i = 0; i < entries.size(); i++)
				Array.set(result, i, castManager.castFrom(thread, entries
						.get(i).getValue(), componentType));
			return result;
		}
		throw new LuaTypeCastException("Can not to cast lua object "
				+ obj.getClass().getName() + " to java object "
				+ expected.toString());
	}

	public LuaTable handleTo(LuaThread thread, Object obj) {
		Class<?> type = obj.getClass();
		if (type.isArray()) {
			LuaTable table = LuaTable.newTable(thread);
			for (int i = 0; i < Array.getLength(obj); i++) {
				table.set(thread, tolua(thread, i + 1),
						castManager.castTo(thread, Array.get(obj, i)));
			}
			return table;
		}
		throw new LuaTypeCastException("Can not to cast java object "
				+ obj.getClass().getName() + " to lua object");
	}

}
