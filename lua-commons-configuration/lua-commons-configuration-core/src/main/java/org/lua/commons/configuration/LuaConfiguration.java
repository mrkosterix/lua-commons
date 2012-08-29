package org.lua.commons.configuration;

import static org.lua.commons.baseapi.types.LuaObjectTools.fromlua;
import static org.lua.commons.baseapi.types.LuaObjectTools.tolua;
import static org.lua.commons.customapi.CustomLuaTools.castFrom;
import static org.lua.commons.customapi.javafunctions.handlers.CastUtils.totype;

import org.lua.commons.baseapi.Lua;
import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.extensions.metatables.LuaMetatablesService;
import org.lua.commons.baseapi.extensions.metatables.SimpleLuaMetatablesService;
import org.lua.commons.baseapi.extensions.references.LuaReferencesStorage;
import org.lua.commons.baseapi.extensions.references.WeakLuaReferencesStorage;
import org.lua.commons.baseapi.extensions.threadpool.SimpleLuaContextThreadPool;
import org.lua.commons.baseapi.types.LuaBoolean;
import org.lua.commons.baseapi.types.LuaFunction;
import org.lua.commons.baseapi.types.LuaNumber;
import org.lua.commons.baseapi.types.LuaObject;
import org.lua.commons.baseapi.types.LuaString;
import org.lua.commons.baseapi.types.LuaTable;
import org.lua.commons.customapi.container.LuaContainer;
import org.lua.commons.customapi.javafunctions.handlers.SimpleTypeCastManager;
import org.lua.commons.customapi.javafunctions.handlers.TypeCastManager;
import org.lua.commons.nativeapi.LuaException;
import org.lua.commons.nativeapi.LuaStateImpl;

public abstract class LuaConfiguration {

	protected Lua lua;
	protected LuaTable table;

	public LuaConfiguration(LuaContainer container) {
		this.lua = new Lua(new LuaStateImpl(), new SimpleLuaContextThreadPool());
		addExtensions(lua);
		lua.start();

		table = LuaTable.newTable(lua.contextThread());

		container.prepare(lua, table);
	}

	protected void addExtensions(Lua lua) {
		lua.addExtension(LuaReferencesStorage.class,
				new WeakLuaReferencesStorage(lua));
		lua.addExtension(LuaMetatablesService.class,
				new SimpleLuaMetatablesService());
		lua.addExtension(TypeCastManager.class, new SimpleTypeCastManager());
	}

	public LuaObject get(LuaObject keyObject) {
		if (keyObject instanceof LuaString) {
			LuaThread thread = lua.contextThread();
			String key = ((LuaString) keyObject).toString();
			LuaObject current = table;
			for (String part : key.split("[.]")) {
				if (current == null || !(current instanceof LuaTable)) {
					throw new LuaConfigurationException("Key " + key
							+ " not found");
				}
				current = ((LuaTable) current).get(thread, tolua(thread, part));
			}
			if (current != null && current instanceof LuaFunction) {
				try {
					return ((LuaFunction) current).call(thread,
							new LuaObject[0], 1)[0];
				} catch (LuaException e) {
					throw new LuaConfigurationException("Function with key "
							+ key + " calling failed.", e);
				}
			}
			return current;
		}
		return table.get(lua.contextThread(), keyObject);
	}

	public boolean getBoolean(LuaObject key) {
		LuaObject value = get(key);
		if (value == null) {
			String readablekey = fromlua(key).toString();
			throw new LuaConfigurationException("Key " + readablekey
					+ " not found");
		} else
			return ((LuaBoolean) value).toBoolean();
	}

	public boolean getBoolean(String key) {
		return getBoolean(tolua(lua.contextThread(), key));
	}

	public byte getByte(LuaObject key) {
		LuaObject value = get(key);
		if (value == null) {
			String readablekey = fromlua(key).toString();
			throw new LuaConfigurationException("Key " + readablekey
					+ " not found");
		} else
			return ((LuaNumber) value).toByte();
	}

	public byte getByte(String key) {
		return getByte(tolua(lua.contextThread(), key));
	}

	public short getShort(LuaObject key) {
		LuaObject value = get(key);
		if (value == null) {
			String readablekey = fromlua(key).toString();
			throw new LuaConfigurationException("Key " + readablekey
					+ " not found");
		} else
			return ((LuaNumber) value).toShort();
	}

	public short getShort(String key) {
		return getShort(tolua(lua.contextThread(), key));
	}

	public int getInt(LuaObject key) {
		LuaObject value = get(key);
		if (value == null) {
			String readablekey = fromlua(key).toString();
			throw new LuaConfigurationException("Key " + readablekey
					+ " not found");
		} else
			return ((LuaNumber) value).toInt();
	}

	public int getInt(String key) {
		return getInt(tolua(lua.contextThread(), key));
	}

	public long getLong(LuaObject key) {
		LuaObject value = get(key);
		if (value == null) {
			String readablekey = fromlua(key).toString();
			throw new LuaConfigurationException("Key " + readablekey
					+ " not found");
		} else
			return ((LuaNumber) value).toLong();
	}

	public long getLong(String key) {
		return getLong(tolua(lua.contextThread(), key));
	}

	public float getFloat(LuaObject key) {
		LuaObject value = get(key);
		if (value == null) {
			String readablekey = fromlua(key).toString();
			throw new LuaConfigurationException("Key " + readablekey
					+ " not found");
		} else
			return ((LuaNumber) value).toFloat();
	}

	public float getFloat(String key) {
		return getFloat(tolua(lua.contextThread(), key));
	}

	public double getDouble(LuaObject key) {
		LuaObject value = get(key);
		if (value == null) {
			String readablekey = fromlua(key).toString();
			throw new LuaConfigurationException("Key " + readablekey
					+ " not found");
		} else
			return ((LuaNumber) value).toDouble();
	}

	public double getDouble(String key) {
		return getDouble(tolua(lua.contextThread(), key));
	}

	public String getString(LuaObject key) {
		LuaObject value = get(key);
		if (value == null) {
			String readablekey = fromlua(key).toString();
			throw new LuaConfigurationException("Key " + readablekey
					+ " not found");
		} else {
			if (value instanceof LuaBoolean)
				return "" + ((LuaBoolean) value).toBoolean();
			else if (value instanceof LuaNumber)
				return "" + ((LuaNumber) value).toDouble();
			else
				return ((LuaString) value).toString();
		}
	}

	public String getString(String key) {
		return getString(tolua(lua.contextThread(), key));
	}

	public String[] getStringArray(LuaObject key) {
		LuaObject value = get(key);
		if (value == null) {
			String readablekey = fromlua(key).toString();
			throw new LuaConfigurationException("Key " + readablekey
					+ " not found");
		} else
			return (String[]) castFrom(lua.contextThread(), value,
					totype(String[].class));
	}

	public String[] getStringArray(String key) {
		return getStringArray(tolua(lua.contextThread(), key));
	}

	public void close() {
		lua.close();
	}

}
