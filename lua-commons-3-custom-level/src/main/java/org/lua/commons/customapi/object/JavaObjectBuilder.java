package org.lua.commons.customapi.object;

import static org.lua.commons.customapi.CustomLuaTools.castFrom;
import static org.lua.commons.customapi.CustomLuaTools.castTo;
import static org.lua.commons.customapi.javafunctions.handlers.CastUtils.totype;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.functions.Functions.Function3;
import org.lua.commons.baseapi.functions.Functions.Function4;
import org.lua.commons.baseapi.types.LuaJavaObject;
import org.lua.commons.baseapi.types.LuaMetatable;
import org.lua.commons.baseapi.types.LuaObject;
import org.lua.commons.baseapi.types.LuaString;
import org.lua.commons.customapi.javafunctions.CustomJavaFunction;
import org.lua.commons.nativeapi.LuaRuntimeException;

public class JavaObjectBuilder {

	protected Map<String, FieldInfo> fields;
	protected Map<String, CustomJavaFunction> methods;

	public JavaObjectBuilder() {
		this.fields = new HashMap<String, FieldInfo>();
		this.methods = new HashMap<String, CustomJavaFunction>();
	}

	public void addField(String name, Field field) {
		addField(name, field, false);
	}

	public void addField(String name, Field field, boolean isReadonly) {
		boolean isRo = isReadonly || Modifier.isFinal(field.getModifiers());
		fields.put(name, new FieldInfo(field, isRo));
		field.setAccessible(true);
	}

	public void addMethod(String name, Method method) {
		methods.put(name, new CustomJavaFunction(method, false, false));
	}

	public LuaMetatable build(LuaThread thread) {
		LuaMetatable metatable = LuaMetatable.newMetatable(thread);

		metatable
				.setNewIndex(
						thread,
						new Function4<LuaThread, LuaObject, LuaObject, LuaObject, Void>() {
							public Void invoke(LuaThread thread,
									LuaObject luaObject, LuaObject keyObj,
									LuaObject value) {
								if (!(keyObj instanceof LuaString))
									throw new LuaRuntimeException(
											"Key "
													+ keyObj
													+ " not allowed for accessing to field.");
								String key = ((LuaString) keyObj)
										.toString(thread);
								if (!fields.containsKey(key))
									throw new LuaRuntimeException(
											"Field with name " + key
													+ " not found.");
								FieldInfo field = fields.get(key);
								if (field.isReadonly)
									throw new LuaRuntimeException(
											"Field with name " + key
													+ " is read only.");
								if (!(luaObject instanceof LuaJavaObject))
									throw new LuaRuntimeException(
											"Fields could be wrote only for java objects");
								Object object = ((LuaJavaObject) luaObject)
										.toObject(thread);
								try {
									field.field.set(
											object,
											castFrom(thread, value,
													totype(field.field
															.getType())));
								} catch (IllegalArgumentException e) {
									throw new LuaRuntimeException(
											"Error happened when trying to set new value "
													+ value
													+ " to field with name "
													+ key, e);
								} catch (IllegalAccessException e) {
									throw new LuaRuntimeException(
											"Error happened when trying to set new value "
													+ value
													+ " to field with name "
													+ key, e);
								}
								return null;
							}
						});

		metatable.setIndex(thread,
				new Function3<LuaThread, LuaObject, LuaObject, LuaObject>() {

					public LuaObject invoke(LuaThread thread,
							LuaObject luaObject, LuaObject keyObj) {
						if (!(keyObj instanceof LuaString))
							throw new LuaRuntimeException("Key " + keyObj
									+ " not allowed for accessing to field.");
						String key = ((LuaString) keyObj).toString(thread);

						if (fields.containsKey(key)) {
							FieldInfo field = fields.get(key);
							if (!(luaObject instanceof LuaJavaObject))
								throw new LuaRuntimeException(
										"Fields could be read only for java objects");
							Object object = ((LuaJavaObject) luaObject)
									.toObject(thread);
							try {
								Object result = field.field.get(object);
								if (result == null)
									return null;
								return castTo(thread, result);
							} catch (IllegalArgumentException e) {
								throw new LuaRuntimeException(
										"Error happened when trying to get value from field with name "
												+ key, e);
							} catch (IllegalAccessException e) {
								throw new LuaRuntimeException(
										"Error happened when trying to get value from field with name "
												+ key, e);
							}
						} else if (methods.containsKey(key)) {
							CustomJavaFunction method = methods.get(key);
							return method.function(thread);
						}
						throw new LuaRuntimeException("Member with name " + key
								+ " not found.");
					}
				});

		return metatable;
	}

	private static class FieldInfo {

		final Field field;
		final boolean isReadonly;

		public FieldInfo(Field field, boolean isReadonly) {
			this.field = field;
			this.isReadonly = isReadonly;
		}

	}

}
