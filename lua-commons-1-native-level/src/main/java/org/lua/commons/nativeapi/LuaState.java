package org.lua.commons.nativeapi;

public interface LuaState {

	public int getLUAI_MAXSTACK();

	public int getLUA_REGISTRYINDEX();

	public int getLUA_RIDX_GLOBALS();

	public boolean isNil(int index);

	public boolean isNone(int index);

	public boolean isNoneOrNil(int index);

	public boolean isBoolean(int index);

	public boolean isNumber(int index);

	public boolean isInteger(int index, int minvalue, int maxvalue);

	public boolean isString(int index);

	public boolean isFunction(int index);

	public boolean isTable(int index);

	public boolean isThread(int index);

	public boolean isUserdata(int index);

	public boolean isObject(int index);

	public boolean isObjectX(int index, String expected);

	public boolean isJFunction(int index);

	public void pushNil();

	public void pushBoolean(boolean b);

	public void pushInteger(int n);

	public void pushNumber(double n);

	public void pushString(String s);

	public void pushObject(Object object);

	public void pushJFunction(Object owner, String method);

	public void pushJClosure(Object owner, String method, int n);

	public boolean pushThread();

	public boolean toBoolean(int index);

	public int toInteger(int index);

	public double toNumber(int index);

	public String toString(int index);

	public Object toObject(int index);

	public LuaState toThread(int index);

	public void pop(int n);

	public void pushValue(int index);

	public int getTop();

	public void setTop(int index);

	public void copy(int fromidx, int toidx);

	public void insert(int index);

	public void remove(int index);

	public void replace(int index);

	public int type(int index);

	public String typeName(int index);

	public boolean checkStack(int extra);

	public int absIndex(int index);

	public void arith(int op);

	public boolean compare(int index1, int index2, int op);

	public void concat(int n);

	public int doFile(String fileName);

	public int doString(String string);

	public void close();

	public LuaState newThread();

	public void openLibs();

	public int loadFile(String file);

	public int loadString(String script);

	public void getGlobal(String name);

	public void setGlobal(String name);

	public void createTable(int narr, int nrec);

	public void newTable();

	public void getTable(int index);

	public void setTable(int index);

	public void getField(int index, String key);

	public void setField(int index, String key);

	public boolean next(int index);

	public void rawGet(int index);

	public void rawSet(int index);

	public void rawGetI(int index, int n);

	public void rawSetI(int index, int n);

	public int ref(int t);

	public void unref(int t, int ref);

	public boolean rawequal(int index1, int index2);

	public boolean getMetatable(int index);

	public void setMetatable(int index);

	public void getUservalue(int index);

	public void setUservalue(int index);

	public void call(int nArgs, int nResults);

	public int pcall(int nArgs, int nResults, int msgh);

	public void callk(int nArgs, int nResults, Object owner, String method);

	public int pcallk(int nArgs, int nResults, int errfunc, Object owner,
			String method);

	public void yield(int nResults);

	public void yieldk(int nResults, Object owner, String method);

	public int resume(long from, int nArg);

	public int getctxstate();

	public int status();

	public int version();

	public void len(int index);

	public int rawlen(int index);

	public int error();

	public void LcheckStack(int sz, String msg);

	public String Ltypename(int index);

	public void Lwhere(int lvl);

	public int upvalueIndex(int index);

	public byte[] dump();

	public int loadBuffer(byte[] buf, String name);

	// DEBUG
	public String getUpValue(int funcindex, int n);

	public String setUpValue(int funcindex, int n);
}
