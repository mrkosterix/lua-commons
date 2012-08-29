package org.lua.commons.customapi.javafunctions.handlers;

import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.types.*;
import org.lua.commons.customapi.javafunctions.handlers.types.Type;

public class NopTypeHandler extends TypeHandler {

    public NopTypeHandler(TypeCastManager castManager) {
        super(castManager);
    }

    @Override
    public Class<?>[] getClasses() {
        return new Class<?>[]{LuaObject.class, LuaComposite.class, LuaBoolean.class, LuaNumber.class, LuaString.class,
                LuaJavaObject.class, LuaFunction.class, LuaTable.class, LuaMetatable.class};
    }

    @Override
    public Object handleFrom(LuaThread thread, LuaObject obj, Type expected) {
        return obj;
    }

    @Override
    public LuaObject handleTo(LuaThread thread, Object obj) {
        return (LuaObject) obj;
    }
}
