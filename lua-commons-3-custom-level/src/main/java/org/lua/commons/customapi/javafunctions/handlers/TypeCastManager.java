package org.lua.commons.customapi.javafunctions.handlers;

import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.extensions.LuaExtension;
import org.lua.commons.baseapi.types.LuaObject;
import org.lua.commons.customapi.javafunctions.handlers.types.Type;

public interface TypeCastManager extends LuaExtension {
    public void addHandler(Class<? extends TypeHandler> handler);

    public void add(Class<? extends TypeHandler> handler, Class<?>... keys);

    public void remove(Class<?> key);

    public Object castFrom(LuaThread thread, LuaObject obj, Type expected);

    public LuaObject castTo(LuaThread thread, Object obj);
}

