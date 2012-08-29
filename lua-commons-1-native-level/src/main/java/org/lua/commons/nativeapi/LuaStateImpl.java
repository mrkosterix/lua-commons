package org.lua.commons.nativeapi;

public class LuaStateImpl implements LuaState {

	protected final long peer;
	protected final LuaStateApi stateApi;

	public LuaStateImpl(long peer, LuaStateApi stateApi) {
		this.stateApi = stateApi;
		this.peer = peer;
	}

	public LuaStateImpl(LuaStateApi stateApi) {
		this.stateApi = stateApi;
		this.peer = stateApi._Lnewstate();
	}

	public LuaStateImpl(long peer) {
		this.stateApi = LuaStateApiProvider.getFactory().getLuaStateApi();
		this.peer = peer;
	}

	public LuaStateImpl() {
		this.stateApi = LuaStateApiProvider.getFactory().getLuaStateApi();
		this.peer = stateApi._Lnewstate();
	}

	public int getLUAI_MAXSTACK() {
		return stateApi.getLUAI_MAXSTACK();
	}

	public int getLUA_REGISTRYINDEX() {
		return stateApi.getLUA_REGISTRYINDEX();
	}

	public int getLUA_RIDX_GLOBALS() {
		return stateApi.getLUA_RIDX_GLOBALS();
	}

	public boolean isNil(int index) {
		return stateApi._isnil(peer, index);
	}

	public boolean isNone(int index) {
		return stateApi._isnone(peer, index);
	}

	public boolean isNoneOrNil(int index) {
		return stateApi._isnoneornil(peer, index);
	}

	public boolean isBoolean(int index) {
		return stateApi._isboolean(peer, index);
	}

	public boolean isNumber(int index) {
		return stateApi._isnumber(peer, index);
	}

	public boolean isInteger(int index, int minvalue, int maxvalue) {
		return stateApi._isintegerx(peer, index, minvalue, maxvalue);
	}

	public boolean isString(int index) {
		return stateApi._isstring(peer, index);
	}

	public boolean isFunction(int index) {
		return stateApi._isfunction(peer, index);
	}

	public boolean isTable(int index) {
		return stateApi._istable(peer, index);
	}

	public boolean isThread(int index) {
		return stateApi._isthread(peer, index);
	}

	public boolean isUserdata(int index) {
		return stateApi._isuserdata(peer, index);
	}

	public boolean isObject(int index) {
		return stateApi._isobject(peer, index);
	}

	public boolean isObjectX(int index, String expected) {
		return stateApi._isobjectx(peer, index, expected);
	}

	public boolean isJFunction(int index) {
		return stateApi._isjfunction(peer, index);
	}

	public void pushNil() {
		stateApi._pushnil(peer);
	}

	public void pushBoolean(boolean b) {
		stateApi._pushboolean(peer, b);
	}

	public void pushInteger(int n) {
		stateApi._pushinteger(peer, n);
	}

	public void pushNumber(double n) {
		stateApi._pushnumber(peer, n);
	}

	public void pushString(String s) {
		stateApi._pushstring(peer, s);
	}

	public void pushObject(Object object) {
		stateApi._pushobject(peer, object);
	}

	public void pushJFunction(Object owner, String method) {
		stateApi._pushjfunction(peer, owner, method);
	}

	public void pushJClosure(Object owner, String method, int n) {
		stateApi._pushjclosure(peer, owner, method, n);
	}

	public boolean pushThread() {
		return stateApi._pushthread(peer);
	}

	public boolean toBoolean(int index) {
		return stateApi._toboolean(peer, index);
	}

	public int toInteger(int index) {
		return stateApi._tointeger(peer, index);
	}

	public double toNumber(int index) {
		return stateApi._tonumber(peer, index);
	}

	public String toString(int index) {
		return stateApi._tostring(peer, index);
	}

	public Object toObject(int index) {
		return stateApi._toobject(peer, index);
	}

	public LuaState toThread(int index) {
		return new LuaStateImpl(stateApi._tothread(peer, index));
	}

	public void pop(int n) {
		stateApi._pop(peer, n);
	}

	public void pushValue(int index) {
		stateApi._pushvalue(peer, index);
	}

	public int getTop() {
		return stateApi._gettop(peer);
	}

	public void setTop(int index) {
		stateApi._settop(peer, index);
	}

	public void copy(int fromidx, int toidx) {
		stateApi._copy(peer, fromidx, toidx);
	}

	public void insert(int index) {
		stateApi._insert(peer, index);
	}

	public void remove(int index) {
		stateApi._remove(peer, index);
	}

	public void replace(int index) {
		stateApi._replace(peer, index);
	}

	public int type(int index) {
		return stateApi._type(peer, index);
	}

	public String typeName(int index) {
		return stateApi._typename(peer, index);
	}

	public boolean checkStack(int extra) {
		return stateApi._checkstack(peer, extra);
	}

	public int absIndex(int index) {
		return stateApi._absindex(peer, index);
	}

	public void arith(int op) {
		stateApi._arith(peer, op);
	}

	public boolean compare(int index1, int index2, int op) {
		return stateApi._compare(peer, index1, index2, op);
	}

	public void concat(int n) {
		stateApi._concat(peer, n);
	}

	public int doFile(String fileName) {
		return stateApi._Ldofile(peer, fileName);
	}

	public int doString(String string) {
		return stateApi._Ldostring(peer, string);
	}

	public void close() {
		stateApi._close(peer);
	}

	public LuaState newThread() {
		return new LuaStateImpl(stateApi._newthread(peer));
	}

	public void openLibs() {
		stateApi._Lopenlibs(peer);
	}

	public int loadFile(String file) {
		return stateApi._Lloadfile(peer, file);
	}

	public int loadString(String script) {
		return stateApi._Lloadstring(peer, script);
	}

	public void getGlobal(String name) {
		stateApi._getglobal(peer, name);
	}

	public void setGlobal(String name) {
		stateApi._setglobal(peer, name);
	}

	public void createTable(int narr, int nrec) {
		stateApi._createtable(peer, narr, nrec);
	}

	public void newTable() {
		stateApi._newtable(peer);
	}

	public void getTable(int index) {
		stateApi._gettable(peer, index);
	}

	public void setTable(int index) {
		stateApi._settable(peer, index);
	}

	public void getField(int index, String key) {
		stateApi._getfield(peer, index, key);
	}

	public void setField(int index, String key) {
		stateApi._setfield(peer, index, key);
	}

	public boolean next(int index) {
		return stateApi._next(peer, index);
	}

	public void rawGet(int index) {
		stateApi._rawget(peer, index);
	}

	public void rawSet(int index) {
		stateApi._rawset(peer, index);
	}

	public void rawGetI(int index, int n) {
		stateApi._rawgeti(peer, index, n);
	}

	public void rawSetI(int index, int n) {
		stateApi._rawseti(peer, index, n);
	}

	public int ref(int t) {
		return stateApi._Lref(peer, t);
	}

	public void unref(int t, int ref) {
		stateApi._Lunref(peer, t, ref);
	}

	public boolean rawequal(int index1, int index2) {
		return stateApi._rawequal(peer, index1, index2);
	}

	public boolean getMetatable(int index) {
		return stateApi._getmetatable(peer, index);
	}

	public void setMetatable(int index) {
		stateApi._setmetatable(peer, index);
	}

	public void getUservalue(int index) {
		stateApi._getuservalue(peer, index);
	}

	public void setUservalue(int index) {
		stateApi._setuservalue(peer, index);
	}

	public void call(int nArgs, int nResults) {
		stateApi._call(peer, nArgs, nResults);
	}

	public int pcall(int nArgs, int nResults, int msgh) {
		return stateApi._pcall(peer, nArgs, nResults, msgh);
	}

	public void callk(int nArgs, int nResults, Object owner, String method) {
		stateApi._callk(peer, nArgs, nResults, owner, method);
	}

	public int pcallk(int nArgs, int nResults, int errfunc, Object owner,
			String method) {
		return stateApi._pcallk(peer, nArgs, nResults, errfunc, owner, method);
	}

	public void yield(int nResults) {
		stateApi._yield(peer, nResults);
	}

	public void yieldk(int nResults, Object owner, String method) {
		stateApi._yieldk(peer, nResults, owner, method);
	}

	public int resume(long from, int nArg) {
		return stateApi._resume(peer, from, nArg);
	}

	public int getctxstate() {
		return stateApi._getctxstate(peer);
	}

	public int status() {
		return stateApi._status(peer);
	}

	public int version() {
		return stateApi._version(peer);
	}

	public void len(int index) {
		stateApi._len(peer, index);
	}

	public int rawlen(int index) {
		return stateApi._rawlen(peer, index);
	}

	public int error() {
		return stateApi._error(peer);
	}

	public void LcheckStack(int sz, String msg) {
		if (!stateApi._checkstack(peer, sz)) {
			stateApi._pushstring(peer, msg);
			stateApi._error(peer);
		}
	}

	public String Ltypename(int index) {
		return stateApi._Ltypename(peer, index);
	}

	public void Lwhere(int lvl) {
		stateApi._Lwhere(peer, lvl);
	}

	public int upvalueIndex(int index) {
		return stateApi._upvalueindex(index);
	}

	public byte[] dump() {
		return stateApi._dump(peer);
	}

	public int loadBuffer(byte[] buf, String name) {
		return stateApi._Lloadbuffer(peer, buf, name);
	}

	// DEBUG
	public String getUpValue(int funcindex, int n) {
		return stateApi._getupvalue(peer, funcindex, n);
	}

	public String setUpValue(int funcindex, int n) {
		return stateApi._setupvalue(peer, funcindex, n);
	}
}
