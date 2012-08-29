package org.lua.commons.customapi.javafunctions.handlers;

import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.types.LuaObject;
import org.lua.commons.customapi.javafunctions.handlers.types.Type;

public abstract class TypeHandler {

    protected final TypeCastManager castManager;

    public TypeHandler(TypeCastManager castManager) {
        this.castManager = castManager;
    }

    public abstract Class<?>[] getClasses();

    public abstract Object handleFrom(LuaThread thread, LuaObject obj, Type expected);

    public abstract LuaObject handleTo(LuaThread thread, Object obj);

}

