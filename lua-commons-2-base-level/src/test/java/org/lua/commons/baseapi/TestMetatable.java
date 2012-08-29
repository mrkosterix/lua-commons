package org.lua.commons.baseapi;

import static org.lua.commons.baseapi.LuaStack.popBoolean;
import static org.lua.commons.baseapi.LuaStack.popInt;
import static org.lua.commons.baseapi.LuaStack.pushBoolean;
import static org.lua.commons.baseapi.LuaStack.size;
import static org.lua.commons.baseapi.types.LuaObjectTools.tolua;

import org.lua.commons.baseapi.extensions.references.LuaReferencesStorage;
import org.lua.commons.baseapi.extensions.references.WeakLuaReferencesStorage;
import org.lua.commons.baseapi.extensions.threadpool.SimpleLuaContextThreadPool;
import org.lua.commons.baseapi.functions.Functions.Function1;
import org.lua.commons.baseapi.functions.Functions.Function2;
import org.lua.commons.baseapi.functions.Functions.Function3;
import org.lua.commons.baseapi.functions.Functions.Function4;
import org.lua.commons.baseapi.types.LuaJavaObject;
import org.lua.commons.baseapi.types.LuaMetatable;
import org.lua.commons.baseapi.types.LuaNumber;
import org.lua.commons.baseapi.types.LuaObject;
import org.lua.commons.baseapi.types.LuaTable;
import org.lua.commons.impl.nativelua.NativeLuaStateApiFactory;
import org.lua.commons.nativeapi.LuaStateApi;
import org.lua.commons.nativeapi.LuaStateApiProvider;
import org.lua.commons.nativeapi.LuaStateImpl;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestMetatable {

	Lua lua;
	LuaThread thread;

	@BeforeMethod
	public void initLua() {
		LuaStateApiProvider.setFactory(new NativeLuaStateApiFactory());
		lua = new Lua(new LuaStateImpl(), new SimpleLuaContextThreadPool());
		lua.addExtension(LuaReferencesStorage.class,
				new WeakLuaReferencesStorage(lua));
		lua.start();

		thread = lua.contextThread();
	}

	@AfterMethod
	public void closeLua() {
		lua.close();
	}

	@Test
	public void testMetatableForTables() {
		LuaMetatable metatable = LuaMetatable.newMetatable(thread);
		metatable.setAdd(thread,
				new Function3<LuaThread, LuaObject, LuaObject, LuaObject>() {
					public LuaObject invoke(LuaThread thread, LuaObject o1,
							LuaObject o2) {
						int a = ((LuaNumber) ((LuaTable) o1).get(thread,
								tolua(thread, "value"))).toInt(thread);
						int b = ((LuaNumber) ((LuaTable) o2).get(thread,
								tolua(thread, "value"))).toInt(thread);
						return tolua(thread, a * b);
					}
				});
		LuaTable table1 = LuaTable.newTable(thread);
		table1.set(thread, tolua(thread, "value"), tolua(thread, 5));
		table1.setMetatable(thread, metatable);
		LuaTable table2 = LuaTable.newTable(thread);
		table2.set(thread, tolua(thread, "value"), tolua(thread, 7));
		table2.setMetatable(thread, metatable);

		table1.push(thread);
		thread.state.setGlobal("a");
		table2.push(thread);
		thread.state.setGlobal("b");

		Assert.assertEquals(size(thread), 0);
		thread.state.doString("res = a + b");
		Assert.assertEquals(size(thread), 0);

		thread.state.getGlobal("res");
		Assert.assertEquals(popInt(thread), 5 * 7);
	}

	@Test
	public void testMetatableForUserdatas() {
		LuaMetatable metatable = LuaMetatable.newMetatable(thread);
		metatable.setAdd(thread,
				new Function3<LuaThread, LuaObject, LuaObject, LuaObject>() {
					public LuaObject invoke(LuaThread thread, LuaObject o1,
							LuaObject o2) {
						int a = (Integer) ((LuaJavaObject) o1).toObject(thread);
						int b = (Integer) ((LuaJavaObject) o2).toObject(thread);
						return tolua(thread, a * b);
					}
				});
		LuaJavaObject obj1 = tolua(thread, (Object) Integer.valueOf(5));
		obj1.setMetatable(thread, metatable);
		LuaJavaObject obj2 = tolua(thread, (Object) Integer.valueOf(7));
		obj2.setMetatable(thread, metatable);

		obj1.push(thread);
		thread.state.setGlobal("a");
		obj2.push(thread);
		thread.state.setGlobal("b");

		Assert.assertEquals(size(thread), 0);
		thread.state.doString("res = a + b");
		Assert.assertEquals(size(thread), 0);

		thread.state.getGlobal("res");
		Assert.assertEquals(popInt(thread), 5 * 7);
	}

	@Test
	public void testMetatableSub() {
		LuaMetatable metatable = LuaMetatable.newMetatable(thread);
		metatable.setSub(thread,
				new Function3<LuaThread, LuaObject, LuaObject, LuaObject>() {
					public LuaObject invoke(LuaThread thread, LuaObject o1,
							LuaObject o2) {
						int a = (Integer) ((LuaJavaObject) o1).toObject(thread);
						int b = (Integer) ((LuaJavaObject) o2).toObject(thread);
						return tolua(thread, a * b);
					}
				});
		LuaJavaObject obj1 = tolua(thread, (Object) Integer.valueOf(5));
		obj1.setMetatable(thread, metatable);
		LuaJavaObject obj2 = tolua(thread, (Object) Integer.valueOf(7));
		obj2.setMetatable(thread, metatable);

		obj1.push(thread);
		thread.state.setGlobal("a");
		obj2.push(thread);
		thread.state.setGlobal("b");

		Assert.assertEquals(size(thread), 0);
		thread.state.doString("res = a - b");
		Assert.assertEquals(size(thread), 0);

		thread.state.getGlobal("res");
		Assert.assertEquals(popInt(thread), 5 * 7);
	}

	@Test
	public void testMetatableMul() {
		LuaMetatable metatable = LuaMetatable.newMetatable(thread);
		metatable.setMul(thread,
				new Function3<LuaThread, LuaObject, LuaObject, LuaObject>() {
					public LuaObject invoke(LuaThread thread, LuaObject o1,
							LuaObject o2) {
						int a = (Integer) ((LuaJavaObject) o1).toObject(thread);
						int b = (Integer) ((LuaJavaObject) o2).toObject(thread);
						return tolua(thread, a * b);
					}
				});
		LuaJavaObject obj1 = tolua(thread, (Object) Integer.valueOf(5));
		obj1.setMetatable(thread, metatable);
		LuaJavaObject obj2 = tolua(thread, (Object) Integer.valueOf(7));
		obj2.setMetatable(thread, metatable);

		obj1.push(thread);
		thread.state.setGlobal("a");
		obj2.push(thread);
		thread.state.setGlobal("b");

		Assert.assertEquals(size(thread), 0);
		thread.state.doString("res = a * b");
		Assert.assertEquals(size(thread), 0);

		thread.state.getGlobal("res");
		Assert.assertEquals(popInt(thread), 5 * 7);
	}

	@Test
	public void testMetatableDiv() {
		LuaMetatable metatable = LuaMetatable.newMetatable(thread);
		metatable.setDiv(thread,
				new Function3<LuaThread, LuaObject, LuaObject, LuaObject>() {
					public LuaObject invoke(LuaThread thread, LuaObject o1,
							LuaObject o2) {
						int a = (Integer) ((LuaJavaObject) o1).toObject(thread);
						int b = (Integer) ((LuaJavaObject) o2).toObject(thread);
						return tolua(thread, a * b);
					}
				});
		LuaJavaObject obj1 = tolua(thread, (Object) Integer.valueOf(5));
		obj1.setMetatable(thread, metatable);
		LuaJavaObject obj2 = tolua(thread, (Object) Integer.valueOf(7));
		obj2.setMetatable(thread, metatable);

		obj1.push(thread);
		thread.state.setGlobal("a");
		obj2.push(thread);
		thread.state.setGlobal("b");

		Assert.assertEquals(size(thread), 0);
		thread.state.doString("res = a / b");
		Assert.assertEquals(size(thread), 0);

		thread.state.getGlobal("res");
		Assert.assertEquals(popInt(thread), 5 * 7);
	}

	@Test
	public void testMetatableMod() {
		LuaMetatable metatable = LuaMetatable.newMetatable(thread);
		metatable.setMod(thread,
				new Function3<LuaThread, LuaObject, LuaObject, LuaObject>() {
					public LuaObject invoke(LuaThread thread, LuaObject o1,
							LuaObject o2) {
						int a = (Integer) ((LuaJavaObject) o1).toObject(thread);
						int b = (Integer) ((LuaJavaObject) o2).toObject(thread);
						return tolua(thread, a * b);
					}
				});
		LuaJavaObject obj1 = tolua(thread, (Object) Integer.valueOf(5));
		obj1.setMetatable(thread, metatable);
		LuaJavaObject obj2 = tolua(thread, (Object) Integer.valueOf(7));
		obj2.setMetatable(thread, metatable);

		obj1.push(thread);
		thread.state.setGlobal("a");
		obj2.push(thread);
		thread.state.setGlobal("b");

		Assert.assertEquals(size(thread), 0);
		thread.state.doString("res = a % b");
		Assert.assertEquals(size(thread), 0);

		thread.state.getGlobal("res");
		Assert.assertEquals(popInt(thread), 5 * 7);
	}

	@Test
	public void testMetatablePow() {
		LuaMetatable metatable = LuaMetatable.newMetatable(thread);
		metatable.setPow(thread,
				new Function3<LuaThread, LuaObject, LuaObject, LuaObject>() {
					public LuaObject invoke(LuaThread thread, LuaObject o1,
							LuaObject o2) {
						int a = (Integer) ((LuaJavaObject) o1).toObject(thread);
						int b = (Integer) ((LuaJavaObject) o2).toObject(thread);
						return tolua(thread, a * b);
					}
				});
		LuaJavaObject obj1 = tolua(thread, (Object) Integer.valueOf(5));
		obj1.setMetatable(thread, metatable);
		LuaJavaObject obj2 = tolua(thread, (Object) Integer.valueOf(7));
		obj2.setMetatable(thread, metatable);

		obj1.push(thread);
		thread.state.setGlobal("a");
		obj2.push(thread);
		thread.state.setGlobal("b");

		Assert.assertEquals(size(thread), 0);
		thread.state.doString("res = a ^ b");
		Assert.assertEquals(size(thread), 0);

		thread.state.getGlobal("res");
		Assert.assertEquals(popInt(thread), 5 * 7);
	}

	@Test
	public void testMetatableUnm() {
		LuaMetatable metatable = LuaMetatable.newMetatable(thread);
		metatable.setUnm(thread,
				new Function2<LuaThread, LuaObject, LuaObject>() {
					public LuaObject invoke(LuaThread thread, LuaObject o1) {
						int a = (Integer) ((LuaJavaObject) o1).toObject(thread);
						return tolua(thread, a);
					}
				});
		LuaJavaObject obj1 = tolua(thread, (Object) Integer.valueOf(5));
		obj1.setMetatable(thread, metatable);

		obj1.push(thread);
		thread.state.setGlobal("a");

		Assert.assertEquals(size(thread), 0);
		thread.state.doString("res = -a");
		Assert.assertEquals(size(thread), 0);

		thread.state.getGlobal("res");
		Assert.assertEquals(popInt(thread), 5);
	}

	@Test
	public void testMetatableConcat() {
		LuaMetatable metatable = LuaMetatable.newMetatable(thread);
		metatable.setConcat(thread,
				new Function3<LuaThread, LuaObject, LuaObject, LuaObject>() {
					public LuaObject invoke(LuaThread thread, LuaObject o1,
							LuaObject o2) {
						int a = (Integer) ((LuaJavaObject) o1).toObject(thread);
						int b = (Integer) ((LuaJavaObject) o2).toObject(thread);
						return tolua(thread, a * b);
					}
				});
		LuaJavaObject obj1 = tolua(thread, (Object) Integer.valueOf(5));
		obj1.setMetatable(thread, metatable);
		LuaJavaObject obj2 = tolua(thread, (Object) Integer.valueOf(7));
		obj2.setMetatable(thread, metatable);

		obj1.push(thread);
		thread.state.setGlobal("a");
		obj2.push(thread);
		thread.state.setGlobal("b");

		Assert.assertEquals(size(thread), 0);
		thread.state.doString("res = a .. b");
		Assert.assertEquals(size(thread), 0);

		thread.state.getGlobal("res");
		Assert.assertEquals(popInt(thread), 5 * 7);
	}

	@Test
	public void testMetatableLen() {
		LuaMetatable metatable = LuaMetatable.newMetatable(thread);
		metatable.setLen(thread,
				new Function2<LuaThread, LuaObject, LuaObject>() {
					public LuaObject invoke(LuaThread thread, LuaObject o1) {
						int a = (Integer) ((LuaJavaObject) o1).toObject(thread);
						return tolua(thread, a);
					}
				});
		LuaJavaObject obj1 = tolua(thread, (Object) Integer.valueOf(5));
		obj1.setMetatable(thread, metatable);

		obj1.push(thread);
		thread.state.setGlobal("a");

		Assert.assertEquals(size(thread), 0);
		thread.state.doString("res = #a");
		Assert.assertEquals(size(thread), 0);

		thread.state.getGlobal("res");
		Assert.assertEquals(popInt(thread), 5);
	}

	@Test(enabled = false)
	public void testMetatableEq() {
		LuaMetatable metatable = LuaMetatable.newMetatable(thread);
		metatable.setEq(thread, new Function1<LuaThread, Integer>() {
			public Integer invoke(LuaThread thread) {
				pushBoolean(thread, true);
				return 1;
			}
		});
		LuaJavaObject obj1 = tolua(thread, (Object) Integer.valueOf(5));
		obj1.setMetatable(thread, metatable);
		LuaJavaObject obj2 = tolua(thread, (Object) Integer.valueOf(7));
		obj2.setMetatable(thread, metatable);

		obj1.push(thread);
		thread.state.setGlobal("a");
		obj2.push(thread);
		thread.state.setGlobal("b");

		Assert.assertEquals(size(thread), 0);
		thread.state.doString("res = a == b");
		Assert.assertEquals(size(thread), 0);

		thread.state.getGlobal("res");
		Assert.assertEquals(popBoolean(thread), true);
	}

	@Test
	public void testMetatableLt() {
		LuaMetatable metatable = LuaMetatable.newMetatable(thread);
		metatable.setLt(thread,
				new Function3<LuaThread, LuaObject, LuaObject, LuaObject>() {
					public LuaObject invoke(LuaThread thread, LuaObject o1,
							LuaObject o2) {
						return tolua(thread, true);
					}
				});
		LuaJavaObject obj1 = tolua(thread, (Object) Integer.valueOf(5));
		obj1.setMetatable(thread, metatable);
		LuaJavaObject obj2 = tolua(thread, (Object) Integer.valueOf(7));
		obj2.setMetatable(thread, metatable);

		obj1.push(thread);
		thread.state.setGlobal("a");
		obj2.push(thread);
		thread.state.setGlobal("b");

		Assert.assertEquals(size(thread), 0);
		thread.state.doString("res = a < b");
		Assert.assertEquals(size(thread), 0);

		thread.state.getGlobal("res");
		Assert.assertEquals(popBoolean(thread), true);
	}

	@Test
	public void testMetatableLe() {
		LuaMetatable metatable = LuaMetatable.newMetatable(thread);
		metatable.setLe(thread,
				new Function3<LuaThread, LuaObject, LuaObject, LuaObject>() {
					public LuaObject invoke(LuaThread thread, LuaObject o1,
							LuaObject o2) {
						return tolua(thread, true);
					}
				});
		LuaJavaObject obj1 = tolua(thread, (Object) Integer.valueOf(5));
		obj1.setMetatable(thread, metatable);
		LuaJavaObject obj2 = tolua(thread, (Object) Integer.valueOf(7));
		obj2.setMetatable(thread, metatable);

		obj1.push(thread);
		thread.state.setGlobal("a");
		obj2.push(thread);
		thread.state.setGlobal("b");

		Assert.assertEquals(size(thread), 0);
		thread.state.doString("res = a <= b");
		Assert.assertEquals(size(thread), 0);

		thread.state.getGlobal("res");
		Assert.assertEquals(popBoolean(thread), true);
	}

	@Test
	public void testMetatableIndex() {
		LuaMetatable metatable = LuaMetatable.newMetatable(thread);
		metatable.setIndex(thread,
				new Function3<LuaThread, LuaObject, LuaObject, LuaObject>() {
					public LuaObject invoke(LuaThread thread, LuaObject table,
							LuaObject key) {
						return tolua(thread, ((LuaNumber) key).toInt(thread));
					}
				});
		LuaJavaObject obj1 = tolua(thread, (Object) Integer.valueOf(5));
		obj1.setMetatable(thread, metatable);

		obj1.push(thread);
		thread.state.setGlobal("a");

		Assert.assertEquals(size(thread), 0);
		thread.state.doString("res = a[7]");
		Assert.assertEquals(size(thread), 0);

		thread.state.getGlobal("res");
		Assert.assertEquals(popInt(thread), 7);
	}

	@Test
	public void testMetatableNewIndex() {
		LuaMetatable metatable = LuaMetatable.newMetatable(thread);
		metatable
				.setNewIndex(
						thread,
						new Function4<LuaThread, LuaObject, LuaObject, LuaObject, Void>() {
							public Void invoke(LuaThread thread,
									LuaObject table, LuaObject key,
									LuaObject value) {
								key.push(thread);
								value.push(thread);
								thread.state.arith(LuaStateApi.LUA_OPADD);
								thread.state.setGlobal("res");
								return null;
							}
						});
		LuaJavaObject obj1 = tolua(thread, (Object) Integer.valueOf(5));
		obj1.setMetatable(thread, metatable);

		obj1.push(thread);
		thread.state.setGlobal("a");

		Assert.assertEquals(size(thread), 0);
		thread.state.doString("a[7] = 10");
		Assert.assertEquals(size(thread), 0);

		thread.state.getGlobal("res");
		Assert.assertEquals(popInt(thread), 17);
	}

	@Test
	public void testMetatableCall() {
		LuaMetatable metatable = LuaMetatable.newMetatable(thread);
		metatable
				.setCall(
						thread,
						new Function3<LuaThread, LuaObject, LuaObject[], LuaObject[]>() {
							public LuaObject[] invoke(LuaThread thread,
									LuaObject table, LuaObject[] args) {
								return new LuaObject[] { args[1], args[0] };
							}
						});
		LuaJavaObject obj1 = tolua(thread, (Object) Integer.valueOf(9));
		obj1.setMetatable(thread, metatable);

		obj1.push(thread);
		thread.state.setGlobal("a");

		Assert.assertEquals(size(thread), 0);
		thread.state.doString("res1, res2 = a(5, 7)");
		Assert.assertEquals(size(thread), 0);

		thread.state.getGlobal("res1");
		Assert.assertEquals(popInt(thread), 7);
		thread.state.getGlobal("res2");
		Assert.assertEquals(popInt(thread), 5);
	}

}
