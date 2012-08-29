package org.lua.commons.customapi.javafunctions;

import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.types.LuaFunction;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class CustomKotlinFunction {

    protected final Object function;

    protected final CustomJavaFunction customFunction;

    public CustomKotlinFunction(Object function, boolean varArgs, boolean varRes) {
        this.function = function;
        Class clazz = function.getClass();
        this.customFunction = new CustomJavaFunction(findMethod(clazz), varArgs, varRes);
    }

    public LuaFunction function(LuaThread thread) {
        return customFunction.function(thread);
    }

    private Method findMethod(Class clazz) {
        Method result = null;
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals("invoke")) {
                result = method;
                for (Type type : method.getGenericParameterTypes()) {
                    if (!type.equals(Object.class)) {
                        break;
                    }
                }
                if (!method.getGenericReturnType().equals(Object.class))
                    break;
            }
        }
        return result;
    }

}
