package org.lua.commons.webapi;

import static org.lua.commons.baseapi.LuaStack.pop;
import static org.lua.commons.baseapi.LuaStack.popFunction;
import static org.lua.commons.baseapi.types.LuaObjectTools.tolua;
import static org.lua.commons.webapi.json.JsonTools.array;
import static org.lua.commons.webapi.json.JsonTools.toJson;
import static org.lua.commons.webapi.json.JsonTools.toLua;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.types.LuaFunction;
import org.lua.commons.baseapi.types.LuaObject;
import org.lua.commons.baseapi.types.LuaTable;
import org.lua.commons.nativeapi.LuaException;
import org.lua.commons.nativeapi.LuaStateApi;

public class FunctionDump {

	private final byte[] dump;

	private final String jsonUpvalues;

	private final String jsonArguments;

	public FunctionDump(byte[] dump, String jsonUpvalues, String jsonArguments) {
		this.dump = dump;
		this.jsonUpvalues = jsonUpvalues;
		this.jsonArguments = jsonArguments;
	}

	public LuaFunction load(LuaThread thread, String name) throws LuaException {
		int code;
		if ((code = thread.state.loadBuffer(dump, name)) != LuaStateApi.LUA_OK)
			throw new LuaException("Function loading failed with status "
					+ code);
		LuaFunction function = popFunction(thread);

		LuaTable upvalues = (LuaTable) toLua(thread, jsonUpvalues);
		int upvaluesSize = upvalues.getKeys(thread).size();
		if (function.getUpvalueNames(thread).length != upvaluesSize)
			throw new LuaException(
					"Function loading failed, because closures are not compatible.");

		for (int i = 1; i <= upvaluesSize; i++) {
			function.setUpvalue(thread, i,
					upvalues.get(thread, tolua(thread, i)));
		}
		return function;
	}

	public LuaObject[] getArguments(LuaThread thread) {
		LuaTable arguments = (LuaTable) toLua(thread, jsonArguments);
		int argumentsSize = arguments.getKeys(thread).size();

		LuaObject[] result = new LuaObject[argumentsSize];
		for (int i = 1; i <= argumentsSize; i++) {
			result[i - 1] = arguments.get(thread, tolua(thread, i));
		}
		return result;
	}

	public LuaFunction load(LuaThread thread) throws LuaException {
		return load(thread, "");
	}

	public static FunctionDump dump(LuaThread thread, LuaFunction function,
			LuaObject[] argumentObjects) {
		function.push(thread);
		byte[] dump = thread.state.dump();
		pop(thread);
		String[] upvalues = new String[function.getUpvalueNames(thread).length];
		for (int i = 0; i < upvalues.length; i++) {
			upvalues[i] = toJson(thread, function.getUpvalue(thread, i + 1));
		}

		String[] arguments = new String[argumentObjects.length];
		for (int i = 0; i < arguments.length; i++) {
			arguments[i] = toJson(thread, argumentObjects[i]);
		}

		return new FunctionDump(dump, array(upvalues), array(arguments));
	}

	public byte[] toBytes() throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try {
			toStream(bout);
			return bout.toByteArray();
		} finally {
			bout.close();
		}
	}

	public void toStream(OutputStream stream) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(stream);
		out.writeUTF(jsonArguments);
		out.writeInt(dump.length);
		out.write(dump);
		out.writeUTF(jsonUpvalues);
		out.flush();
	}
	
	public static FunctionDump fromBytes(byte[] bytes) throws IOException {
		return fromStream(new ByteArrayInputStream(bytes));
	}

	public static FunctionDump fromStream(InputStream stream)
			throws IOException {
		ObjectInputStream in = new ObjectInputStream(stream);
		String jsonArguments = in.readUTF();

		byte[] dump = new byte[in.readInt()];
		in.readFully(dump);

		String jsonUpvalues = in.readUTF();
		return new FunctionDump(dump, jsonUpvalues, jsonArguments);
	}

}
