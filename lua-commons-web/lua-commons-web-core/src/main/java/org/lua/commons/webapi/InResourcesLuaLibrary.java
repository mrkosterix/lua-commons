package org.lua.commons.webapi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.lua.commons.baseapi.BaseLuaTools;
import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.customapi.container.LuaLibrary;
import org.lua.commons.nativeapi.LuaException;
import org.lua.commons.nativeapi.LuaRuntimeException;

public abstract class InResourcesLuaLibrary implements LuaLibrary {

	public abstract String getResourceName();

	public void prepare(LuaThread thread) {
		try {
			InputStream stream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(getResourceName());
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			try {
				int length = 0;
				byte[] buf = new byte[10 * 1024];

				while (length >= 0) {
					bout.write(buf, 0, length);
					length = stream.read(buf);
				}
			} finally {
				stream.close();
				bout.close();
			}
			BaseLuaTools.doString(thread, new String(bout.toByteArray()));
		} catch (LuaException e) {
			throw new LuaRuntimeException("In resource library "
					+ getResourceName() + " loading failed", e);
		} catch (IOException e) {
			throw new LuaRuntimeException("In resource library "
					+ getResourceName() + " loading failed", e);
		}
	}
}
