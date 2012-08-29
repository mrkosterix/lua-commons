package org.lua.commons.customapi.javafunctions.handlers.types;

public class ArrayType implements Type {

	private final Type component;
	private final int dimensions;

	public ArrayType(Type component, int dimensions) {
		this.component = component;
		this.dimensions = dimensions;
	}

	public Type getComponentType() {
		return component;
	}

	public Type getComponentArrayType() {
		if (dimensions < 2)
			return component;
		else
			return new ArrayType(component, dimensions - 1);
	}

	public Class<?> castComponentToJava() {
		if (component instanceof ClassType)
			return ((ClassType) component).getType();
		else
			return Object.class;
	}

	public int getDimensions() {
		return dimensions;
	}

	public String toString() {
		return component.toString() + "[]";
	}
}
