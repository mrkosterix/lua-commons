package org.lua.commons.baseapi.types;

import org.lua.commons.baseapi.LuaThread;
import static org.lua.commons.baseapi.LuaStack.*;

public class LuaNumber extends LuaObject {

	public LuaNumber(LuaThread thread, int index) {
		super(thread, index);
	}

	public static LuaNumber valueOf(LuaThread thread, double value) {
		checkStack(thread, 1);
		pushDouble(thread, value);
		return popDoubleRef(thread);
	}

	public static LuaNumber valueOf(LuaThread thread, int value) {
		checkStack(thread, 1);
		pushInt(thread, value);
		return popIntRef(thread);
	}

	public static LuaNumber valueOf(LuaThread thread, long value) {
		checkStack(thread, 1);
		pushLong(thread, value);
		return popLongRef(thread);
	}

	public boolean isByte() {
		return isByte(lua.contextThread());
	}

	public boolean isByte(LuaThread thread) {
		checkStack(thread, 1);
		push(thread);
		boolean result = isTopByte(thread);
		pop(thread);
		return result;
	}

	public byte toByte() {
		return toByte(lua.contextThread());
	}

	public byte toByte(LuaThread thread) {
		checkStack(thread, 1);
		push(thread);
		return popByte(thread);
	}

	public boolean isShort() {
		return isShort(lua.contextThread());
	}

	public boolean isShort(LuaThread thread) {
		checkStack(thread, 1);
		push(thread);
		boolean result = isTopShort(thread);
		pop(thread);
		return result;
	}

	public short toShort() {
		return toShort(lua.contextThread());
	}

	public short toShort(LuaThread thread) {
		checkStack(thread, 1);
		push(thread);
		return popShort(thread);
	}

	public boolean isInt() {
		return isInt(lua.contextThread());
	}

	public boolean isInt(LuaThread thread) {
		checkStack(thread, 1);
		push(thread);
		boolean result = isTopInt(thread);
		pop(thread);
		return result;
	}

	public int toInt() {
		return toInt(lua.contextThread());
	}

	public int toInt(LuaThread thread) {
		checkStack(thread, 1);
		push(thread);
		return popInt(thread);
	}

	public boolean isLong() {
		return isLong(lua.contextThread());
	}

	public boolean isLong(LuaThread thread) {
		checkStack(thread, 1);
		push(thread);
		boolean result = isTopLong(thread);
		pop(thread);
		return result;
	}

	public long toLong() {
		return toLong(lua.contextThread());
	}

	public long toLong(LuaThread thread) {
		checkStack(thread, 1);
		push(thread);
		return popLong(thread);
	}

	public boolean isFloat() {
		return isFloat(lua.contextThread());
	}

	public boolean isFloat(LuaThread thread) {
		checkStack(thread, 1);
		push(thread);
		boolean result = isTopFloat(thread);
		pop(thread);
		return result;
	}

	public float toFloat() {
		return toFloat(lua.contextThread());
	}

	public float toFloat(LuaThread thread) {
		checkStack(thread, 1);
		push(thread);
		return popFloat(thread);
	}

	public boolean isDouble() {
		return isDouble(lua.contextThread());
	}

	public boolean isDouble(LuaThread thread) {
		checkStack(thread, 1);
		push(thread);
		boolean result = isTopDouble(thread);
		pop(thread);
		return result;
	}

	public double toDouble() {
		return toDouble(lua.contextThread());
	}

	public double toDouble(LuaThread thread) {
		checkStack(thread, 1);
		push(thread);
		return popDouble(thread);
	}

}
