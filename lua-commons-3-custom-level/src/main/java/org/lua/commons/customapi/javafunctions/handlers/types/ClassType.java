package org.lua.commons.customapi.javafunctions.handlers.types;

public class ClassType implements Type{

	protected final Class<?> clazz;

	public ClassType(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Class<?> getType() {
		return clazz;
	}

	public String toString() {
		return clazz.getName();
	}

}