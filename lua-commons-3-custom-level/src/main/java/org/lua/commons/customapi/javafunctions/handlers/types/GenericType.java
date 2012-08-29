package org.lua.commons.customapi.javafunctions.handlers.types;

public class GenericType extends ClassType {

	private final Type[] generics;

	public GenericType(Class<?> genericClazz, Type[] generics) {
		super(genericClazz);
		this.generics = generics;
	}

	public Type[] getGenericTypes() {
		return generics;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(clazz.getName());
		builder.append('<');
		if (generics.length > 0) {
			builder.append(generics[0]);
			for (int i = 0; i < generics.length; i++) {
				builder.append(',');
				builder.append(generics[i]);
			}
		}
		builder.append('>');
		return builder.toString();
	}

}