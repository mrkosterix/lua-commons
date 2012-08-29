package org.lua.commons.impl.nativelua;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.lua.commons.nativeapi.LuaRuntimeException;
import org.lua.commons.nativeapi.LuaStateApi;

public class NativeLuaStateApi implements LuaStateApi {

	private static final String API_MODIFICATIONS_NAME = "api-modifications.lua";

	private final NativeLuaEnvironment env = _getenvironment();

	public int getLUAI_MAXSTACK() {
		return env.LUAI_MAXSTACK;
	}

	public int getLUA_REGISTRYINDEX() {
		return env.LUA_REGISTRYINDEX;
	}

	public int getLUA_RIDX_GLOBALS() {
		return env.LUA_RIDX_GLOBALS;
	}

	public synchronized native NativeLuaEnvironment _getenvironment();

	public synchronized native boolean _isnil(long L, int index);

	public synchronized native boolean _isnone(long L, int index);

	public synchronized native boolean _isnoneornil(long L, int index);

	public synchronized native boolean _isboolean(long L, int index);

	public synchronized native boolean _isnumber(long L, int index);

	public synchronized native boolean _isintegerx(long L, int index,
			int minvalue, int maxvalue);

	public synchronized native boolean _isstring(long L, int index);

	public synchronized native boolean _isfunction(long L, int index);

	public synchronized native boolean _istable(long L, int index);

	public synchronized native boolean _isthread(long L, int index);

	public synchronized native boolean _isuserdata(long L, int index);

	public synchronized boolean _isobject(long L, int index) {
		return _isobjectx(L, index, null);
	}

	public synchronized native boolean _isjfunction(long L, int index);

	public synchronized native boolean _isobjectx(long L, int index,
			String expected);

	public synchronized native void _pushnil(long L);

	public synchronized native void _pushboolean(long L, boolean b);

	public synchronized native void _pushinteger(long L, int n);

	public synchronized native void _pushnumber(long L, double n);

	public synchronized native void _pushstring(long L, String s);

	public synchronized native void _pushobject(long L, Object object);

	public synchronized void _pushjfunction(long L, Object owner, String method) {
		_pushjclosure(L, owner, method, 0);
	}

	public synchronized native void _pushjclosure(long L, Object owner,
			String method, int n);

	public synchronized native boolean _pushthread(long L);

	public synchronized native boolean _toboolean(long L, int index);

	public synchronized native int _tointeger(long L, int index);

	public synchronized native double _tonumber(long L, int index);

	public synchronized native String _tostring(long L, int index);

	public synchronized native Object _toobject(long L, int index);

	public synchronized native long _tothread(long L, int index);

	public synchronized native void _pop(long L, int n);

	public synchronized native void _pushvalue(long L, int index);

	public synchronized native int _gettop(long L);

	public synchronized native void _settop(long L, int index);

	public synchronized native void _copy(long L, int fromidx, int toidx);

	public synchronized native void _insert(long L, int index);

	public synchronized native void _remove(long L, int index);

	public synchronized native void _replace(long L, int index);

	public synchronized native int _type(long L, int index);

	public synchronized native String _typename(long L, int index);

	public synchronized native boolean _checkstack(long L, int extra);

	public synchronized native int _absindex(long L, int index);

	public synchronized native void _arith(long L, int op);

	public synchronized native boolean _compare(long L, int index1, int index2,
			int op);

	public synchronized native void _concat(long L, int n);

	public synchronized native long _Lnewstate();

	public synchronized native int _Ldofile(long L, String fileName);

	public synchronized native int _Ldostring(long L, String string);

	public synchronized native void _close(long L);

	public synchronized native long _newthread(long L);

	public synchronized void _Lopenlibs(long L) {
		_Lopenlibs0(L);
		try {
			InputStream in = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(API_MODIFICATIONS_NAME);
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			int length = 0;
			byte[] buf = new byte[10 * 1024];
			while (length >= 0) {
				bout.write(buf, 0, length);
				length = in.read(buf);
			}
			String modifications = bout.toString("utf8");
			_Ldostring(L, modifications);
		} catch (IOException e) {
			throw new LuaRuntimeException("Could not open standart libraries",
					e);
		}
	}

	public synchronized native void _Lopenlibs0(long L);

	public synchronized native int _Lloadfile(long L, String file);

	public synchronized native int _Lloadstring(long L, String string);

	public synchronized native void _getglobal(long L, String name);

	public synchronized native void _setglobal(long L, String name);

	public synchronized native void _createtable(long L, int narr, int nrec);

	public synchronized void _newtable(long L) {
		_createtable(L, 0, 0);
	}

	public synchronized native void _gettable(long L, int index);

	public synchronized native void _settable(long L, int index);

	public synchronized native void _getfield(long L, int index, String key);

	public synchronized native void _setfield(long L, int index, String key);

	public synchronized native boolean _next(long L, int index);

	public synchronized native void _rawget(long L, int index);

	public synchronized native void _rawset(long L, int index);

	public synchronized native void _rawgeti(long L, int index, int n);

	public synchronized native void _rawseti(long L, int index, int n);

	public synchronized native int _Lref(long L, int t);

	public synchronized native void _Lunref(long L, int t, int ref);

	public synchronized native boolean _rawequal(long L, int index1, int index2);

	public synchronized native boolean _getmetatable(long L, int index);

	public synchronized native void _setmetatable(long L, int index);

	public synchronized native void _getuservalue(long L, int index);

	public synchronized native void _setuservalue(long L, int index);

	public synchronized native void _call(long L, int nArgs, int nResults);

	public synchronized native int _pcall(long L, int nArgs, int nResults,
			int msgh);

	public synchronized native void _callk(long L, int nArgs, int nResults,
			Object owner, String method);

	public synchronized native int _pcallk(long L, int nArgs, int nResults,
			int errfunc, Object owner, String method);

	public synchronized native void _yield(long L, int nResults);

	public synchronized native void _yieldk(long L, int nResults, Object owner,
			String method);

	public synchronized native int _resume(long L, long from, int nArg);

	public synchronized native int _getctxstate(long L);

	public synchronized native int _status(long L);

	public synchronized native int _version(long L);

	public synchronized native void _len(long L, int index);

	public synchronized native int _rawlen(long L, int index);

	public synchronized native int _error(long L);

	public synchronized native String _Ltypename(long L, int index);

	public synchronized native void _Lwhere(long L, int lvl);

	public synchronized native int _upvalueindex(int index);

	public synchronized native byte[] _dump(long L);

	public synchronized native int _Lloadbuffer(long L, byte[] dump, String name);

	// DEBUG
	public synchronized native String _getupvalue(long L, int funcindex, int n);

	public synchronized native String _setupvalue(long L, int funcindex, int n);

}
