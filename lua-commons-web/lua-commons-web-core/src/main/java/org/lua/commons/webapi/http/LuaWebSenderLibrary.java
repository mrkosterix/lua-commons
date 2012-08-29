package org.lua.commons.webapi.http;

import static org.lua.commons.baseapi.LuaStack.getFunction;
import static org.lua.commons.baseapi.LuaStack.getReference;
import static org.lua.commons.baseapi.LuaStack.isFunction;
import static org.lua.commons.baseapi.LuaStack.pushInt;
import static org.lua.commons.baseapi.LuaStack.pushJavaFunction;
import static org.lua.commons.baseapi.LuaStack.size;

import java.io.IOException;

import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.functions.Functions.Function1;
import org.lua.commons.baseapi.types.LuaFunction;
import org.lua.commons.baseapi.types.LuaObject;
import org.lua.commons.baseapi.types.LuaString;
import org.lua.commons.customapi.container.LuaLibrary;
import org.lua.commons.nativeapi.LuaRuntimeException;
import org.lua.commons.webapi.FunctionDump;
import org.lua.commons.webapi.json.JsonTypeCastManager;

public class LuaWebSenderLibrary implements LuaLibrary {

	protected final LuaWebSender sender;

	public LuaWebSenderLibrary(LuaWebSender sender) {
		this.sender = sender;
	}

	public String getNamespace() {
		return "web";
	}

	public String[] getNames() {
		return new String[] { "send" };
	}

	public void prepare(LuaThread thread) {
		pushJavaFunction(thread, new Function1<LuaThread, Integer>() {

			public Integer invoke(LuaThread thread) {
				if (!isFunction(thread, 2))
					throw new LuaRuntimeException(
							"First parameter of function send must be lua function");
				LuaFunction function = getFunction(thread, 2);
				LuaObject[] arguments = new LuaObject[size(thread) - 2];
				for (int i = 3; i <= size(thread); i++)
					arguments[i - 3] = getReference(thread, i);

				try {
					FunctionDump dump = FunctionDump.dump(thread, function,
							arguments);
					LuaWebResponse response = sender.send(dump.toBytes());
					pushInt(thread, response.statusCode());
					LuaObject body = null;
					if (response.statusCode() == 200) {
						body = thread.lua.getExtension(
								JsonTypeCastManager.class).toLua(thread,
								response.getResponseJson());
					} else {
						body = LuaString.valueOf(thread,
								response.getResponseJson());
					}
					if (body == null)
						thread.state.pushNil();
					else
						body.push(thread);
					return 2;
				} catch (IOException e) {
					throw new LuaRuntimeException(
							"Error while sending dump with message: "
									+ e.getMessage(), e);
				}
			}
		});
		thread.state.setGlobal("send");
	}

}
