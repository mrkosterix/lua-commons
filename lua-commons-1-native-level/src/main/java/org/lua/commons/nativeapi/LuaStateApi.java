package org.lua.commons.nativeapi;

public interface LuaStateApi {
	public static final int LUA_OPADD = 0;
	public static final int LUA_OPSUB = 1;
	public static final int LUA_OPMUL = 2;
	public static final int LUA_OPDIV = 3;
	public static final int LUA_OPMOD = 4;
	public static final int LUA_OPPOW = 5;
	public static final int LUA_OPUNM = 6;
	public static final int LUA_OPEQ = 0;
	public static final int LUA_OPLT = 1;
	public static final int LUA_OPLE = 2;
	public static final int LUA_TNONE = -1;
	public static final int LUA_TNIL = 0;
	public static final int LUA_TBOOLEAN = 1;
	public static final int LUA_TLIGHTUSERDATA = 2;
	public static final int LUA_TNUMBER = 3;
	public static final int LUA_TSTRING = 4;
	public static final int LUA_TTABLE = 5;
	public static final int LUA_TFUNCTION = 6;
	public static final int LUA_TUSERDATA = 7;
	public static final int LUA_TTHREAD = 8;
	public static final int LUA_OK = 0;
	public static final int LUA_YIELD = 1;
	public static final int LUA_ERRRUN = 2;
	public static final int LUA_ERRSYNTAX = 3;
	public static final int LUA_ERRMEM = 4;
	public static final int LUA_ERRGCMM = 5;
	public static final int LUA_ERRERR = 6;
	public static final int LUA_ERRFILE = 7;
	public static final int LUA_MULTRET = -1;
	public static final int LUA_REFNIL = -1;
	public static final int LUA_NOREF = -2;

	public int getLUAI_MAXSTACK();

	public int getLUA_REGISTRYINDEX();

	public int getLUA_RIDX_GLOBALS();

	public abstract boolean _isnil(long L, int index);

	public abstract boolean _isnone(long L, int index);

	public abstract boolean _isnoneornil(long L, int index);

	public abstract boolean _isboolean(long L, int index);

	public abstract boolean _isnumber(long L, int index);

	public abstract boolean _isintegerx(long L, int index, int minvalue,
			int maxvalue);

	public abstract boolean _isstring(long L, int index);

	public abstract boolean _isfunction(long L, int index);

	public abstract boolean _istable(long L, int index);

	public abstract boolean _isthread(long L, int index);

	public abstract boolean _isuserdata(long L, int index);

	public abstract boolean _isobject(long L, int index);

	public abstract boolean _isobjectx(long L, int index, String expected);

	public abstract boolean _isjfunction(long L, int index);

	public abstract void _pushnil(long L);

	public abstract void _pushboolean(long L, boolean b);

	public abstract void _pushinteger(long L, int n);

	public abstract void _pushnumber(long L, double n);

	public abstract void _pushstring(long L, String s);

	public abstract void _pushobject(long L, Object object);

	public abstract void _pushjfunction(long L, Object owner, String method);

	public abstract void _pushjclosure(long L, Object owner, String method,
			int n);

	public abstract boolean _pushthread(long L);

	public abstract boolean _toboolean(long L, int index);

	public abstract int _tointeger(long L, int index);

	public abstract double _tonumber(long L, int index);

	public abstract String _tostring(long L, int index);

	public abstract Object _toobject(long L, int index);

	public abstract long _tothread(long L, int index);

	public abstract void _pop(long L, int n);

	public abstract void _pushvalue(long L, int index);

	public abstract int _gettop(long L);

	public abstract void _settop(long L, int index);

	public abstract void _copy(long L, int fromidx, int toidx);

	public abstract void _insert(long L, int index);

	public abstract void _remove(long L, int index);

	public abstract void _replace(long L, int index);

	public abstract int _type(long L, int index);

	public abstract String _typename(long L, int index); // TODO check usage of
															// this method

	public abstract boolean _checkstack(long L, int extra);

	public abstract int _absindex(long L, int index);

	public abstract void _arith(long L, int op);

	public abstract boolean _compare(long L, int index1, int index2, int op);

	public abstract void _concat(long L, int n);

	public abstract long _Lnewstate();

	public abstract int _Ldofile(long L, String fileName);

	public abstract int _Ldostring(long L, String string);

	public abstract void _close(long L);

	public abstract long _newthread(long L);

	public abstract void _Lopenlibs(long L);

	public abstract int _Lloadfile(long L, String file);

	public abstract int _Lloadstring(long L, String script);

	public abstract void _getglobal(long L, String name);

	public abstract void _setglobal(long L, String name);

	public abstract void _createtable(long L, int narr, int nrec);

	public abstract void _newtable(long L);

	public abstract void _gettable(long L, int index);

	public abstract void _settable(long L, int index);

	public abstract void _getfield(long L, int index, String key);

	public abstract void _setfield(long L, int index, String key);

	public abstract boolean _next(long L, int index);

	public abstract void _rawget(long L, int index);

	public abstract void _rawset(long L, int index);

	public abstract void _rawgeti(long L, int index, int n);

	public abstract void _rawseti(long L, int index, int n);

	public abstract int _Lref(long L, int t);

	public abstract void _Lunref(long L, int t, int ref);

	public abstract boolean _rawequal(long L, int index1, int index2);

	public abstract boolean _getmetatable(long L, int index);

	public abstract void _setmetatable(long L, int index);

	public abstract void _getuservalue(long L, int index);

	public abstract void _setuservalue(long L, int index);

	public abstract void _call(long L, int nArgs, int nResults);

	public abstract int _pcall(long L, int nArgs, int nResults, int msgh);

	public abstract void _callk(long L, int nArgs, int nResults, Object owner,
			String method);

	public abstract int _pcallk(long L, int nArgs, int nResults, int errfunc,
			Object owner, String method);

	public abstract void _yield(long L, int nResults);

	public abstract void _yieldk(long L, int nResults, Object owner,
			String method);

	public abstract int _resume(long L, long from, int nArg);

	public abstract int _getctxstate(long L);

	public abstract int _status(long L);

	public abstract int _version(long L);

	public abstract void _len(long L, int index);

	public abstract int _rawlen(long L, int index);

	public abstract int _error(long L);

	public abstract String _Ltypename(long L, int index);

	public abstract void _Lwhere(long L, int lvl);

	public abstract int _upvalueindex(int index);

	public abstract byte[] _dump(long L);

	public abstract int _Lloadbuffer(long L, byte[] dump, String name);

	// DEBUG
	public abstract String _getupvalue(long L, int funcindex, int n);

	public abstract String _setupvalue(long L, int funcindex, int n);
}
