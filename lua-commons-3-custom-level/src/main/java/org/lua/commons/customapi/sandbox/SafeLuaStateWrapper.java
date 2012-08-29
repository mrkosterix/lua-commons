package org.lua.commons.customapi.sandbox;

import org.lua.commons.nativeapi.LuaState;

public class SafeLuaStateWrapper extends LuaStateWrapper {

	public SafeLuaStateWrapper(LuaState root) {
		super(root);
	}

	public int getLUAI_MAXSTACK() {
		return root.getLUAI_MAXSTACK();
	}

	public int getLUA_REGISTRYINDEX() {
		return root.getLUA_REGISTRYINDEX();
	}

	public int getLUA_RIDX_GLOBALS() {
		throw new LuaOperationPermissionDenied("Permission denied.");
	}

	public boolean isNil(int index) {
		return root.isNil(index);
	}

	public boolean isNone(int index) {
		return root.isNone(index);
	}

	public boolean isNoneOrNil(int index) {
		return root.isNoneOrNil(index);
	}

	public boolean isBoolean(int index) {
		return root.isBoolean(index);
	}

	public boolean isNumber(int index) {
		return root.isNumber(index);
	}

	public boolean isInteger(int index, int minvalue, int maxvalue) {
		return root.isInteger(index, minvalue, maxvalue);
	}

	public boolean isString(int index) {
		return root.isString(index);
	}

	public boolean isFunction(int index) {
		return root.isFunction(index);
	}

	public boolean isTable(int index) {
		return root.isTable(index);
	}

	public boolean isThread(int index) {
		return root.isThread(index);
	}

	public boolean isUserdata(int index) {
		return root.isUserdata(index);
	}

	public boolean isObject(int index) {
		return root.isObject(index);
	}

	public boolean isObjectX(int index, String expected) {
		return root.isObjectX(index, expected);
	}

	public boolean isJFunction(int index) {
		return root.isJFunction(index);
	}

	public void pushNil() {
		root.pushNil();
	}

	public void pushBoolean(boolean b) {
		root.pushBoolean(b);
	}

	public void pushInteger(int n) {
		root.pushInteger(n);
	}

	public void pushNumber(double n) {
		root.pushNumber(n);
	}

	public void pushString(String s) {
		root.pushString(s);
	}

	public void pushObject(Object object) {
		root.pushObject(object);
	}

	public void pushJFunction(Object owner, String method) {
		root.pushJFunction(owner, method);
	}

	public void pushJClosure(Object owner, String method, int n) {
		root.pushJClosure(owner, method, n);
	}

	public boolean pushThread() {
		return root.pushThread();
	}

	public boolean toBoolean(int index) {
		return root.toBoolean(index);
	}

	public int toInteger(int index) {
		return root.toInteger(index);
	}

	public double toNumber(int index) {
		return root.toNumber(index);
	}

	public String toString(int index) {
		return root.toString(index);
	}

	public Object toObject(int index) {
		return root.toObject(index);
	}

	public LuaState toThread(int index) {
		throw new LuaOperationPermissionDenied("Permission denied.");
	}

	public void pop(int n) {
		root.pop(n);
	}

	public void pushValue(int index) {
		root.pushValue(index);
	}

	public int getTop() {
		return root.getTop();
	}

	public void setTop(int index) {
		root.setTop(index);
	}

	public void copy(int fromidx, int toidx) {
		root.copy(fromidx, toidx);
	}

	public void insert(int index) {
		root.insert(index);
	}

	public void remove(int index) {
		root.remove(index);
	}

	public void replace(int index) {
		root.replace(index);
	}

	public int type(int index) {
		return root.type(index);
	}

	public String typeName(int index) {
		return root.typeName(index);
	}

	public boolean checkStack(int extra) {
		return root.checkStack(extra);
	}

	public int absIndex(int index) {
		return root.absIndex(index);
	}

	public void arith(int op) {
		root.arith(op);
	}

	public boolean compare(int index1, int index2, int op) {
		return root.compare(index1, index2, op);
	}

	public void concat(int n) {
		root.concat(n);
	}

	public int doFile(String fileName) {
		return root.doFile(fileName);
	}

	public int doString(String string) {
		return root.doString(string);
	}

	public void close() {
		throw new LuaOperationPermissionDenied("Permission denied.");
	}

	public LuaState newThread() {
		throw new LuaOperationPermissionDenied("Permission denied.");
	}

	public void openLibs() {
		throw new LuaOperationPermissionDenied("Permission denied.");
	}

	public int loadFile(String file) {
		return root.loadFile(file);
	}

	public int loadString(String script) {
		return root.loadString(script);
	}

	public void getGlobal(String name) {
		root.getGlobal(name);
	}

	public void setGlobal(String name) {
		root.setGlobal(name);
	}

	public void createTable(int narr, int nrec) {
		root.createTable(narr, nrec);
	}

	public void newTable() {
		root.newTable();
	}

	public void getTable(int index) {
		root.getTable(index);
	}

	public void setTable(int index) {
		root.setTable(index);
	}

	public void getField(int index, String key) {
		root.getField(index, key);
	}

	public void setField(int index, String key) {
		root.setField(index, key);
	}

	public boolean next(int index) {
		return root.next(index);
	}

	public void rawGet(int index) {
		root.rawGet(index);
	}

	public void rawSet(int index) {
		root.rawSet(index);
	}

	public void rawGetI(int index, int n) {
		root.rawGetI(index, n);
	}

	public void rawSetI(int index, int n) {
		root.rawSetI(index, n);
	}

	public int ref(int t) {
		return root.ref(t);
	}

	public void unref(int t, int ref) {
		root.unref(t, ref);
	}

	public boolean rawequal(int index1, int index2) {
		return root.rawequal(index1, index2);
	}

	public boolean getMetatable(int index) {
		return root.getMetatable(index);
		//throw new LuaOperationPermissionDenied("Permission denied.");
	}

	public void setMetatable(int index) {
		root.setMetatable(index);
		//throw new LuaOperationPermissionDenied("Permission denied.");
	}

	public void getUservalue(int index) {
		throw new LuaOperationPermissionDenied("Permission denied.");
	}

	public void setUservalue(int index) {
		throw new LuaOperationPermissionDenied("Permission denied.");
	}

	public void call(int nArgs, int nResults) {
		root.call(nArgs, nResults);
	}

	public int pcall(int nArgs, int nResults, int msgh) {
		return root.pcall(nArgs, nResults, msgh);
	}

	public void callk(int nArgs, int nResults, Object owner, String method) {
		root.callk(nArgs, nResults, owner, method);
	}

	public int pcallk(int nArgs, int nResults, int errfunc, Object owner,
			String method) {
		return root.pcallk(nArgs, nResults, errfunc, owner, method);
	}

	public void yield(int nResults) {
		throw new LuaOperationPermissionDenied("Permission denied.");
	}

	public void yieldk(int nResults, Object owner, String method) {
		throw new LuaOperationPermissionDenied("Permission denied.");
	}

	public int resume(long from, int nArg) {
		throw new LuaOperationPermissionDenied("Permission denied.");
	}

	public int getctxstate() {
		throw new LuaOperationPermissionDenied("Permission denied.");
	}

	public int status() {
		throw new LuaOperationPermissionDenied("Permission denied.");
	}

	public int version() {
		return root.version();
	}

	public void len(int index) {
		root.len(index);
	}

	public int rawlen(int index) {
		return root.rawlen(index);
	}

	public int error() {
		return root.error();
	}

	public void LcheckStack(int sz, String msg) {
		root.LcheckStack(sz, msg);
	}

	public String Ltypename(int index) {
		return root.Ltypename(index);
	}

	public void Lwhere(int lvl) {
		throw new LuaOperationPermissionDenied("Permission denied.");
	}

	public int upvalueIndex(int index) {
		return root.upvalueIndex(index);
	}

	public byte[] dump() {
		throw new LuaOperationPermissionDenied("Permission denied.");
	}

	public int loadBuffer(byte[] buf, String name) {
		throw new LuaOperationPermissionDenied("Permission denied.");
	}

	// DEBUG
	public String getUpValue(int funcindex, int n) {
		throw new LuaOperationPermissionDenied("Permission denied.");
	}

	public String setUpValue(int funcindex, int n) {
		throw new LuaOperationPermissionDenied("Permission denied.");
	}

}
