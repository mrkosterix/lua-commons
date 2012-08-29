package org.lua.commons.baseapi.types;

import static org.lua.commons.baseapi.LuaStack.checkStack;
import static org.lua.commons.baseapi.LuaStack.getReference;
import static org.lua.commons.baseapi.LuaStack.pop;
import static org.lua.commons.baseapi.LuaStack.popReference;
import static org.lua.commons.baseapi.LuaStack.pushReference;
import static org.lua.commons.baseapi.LuaStack.size;
import static org.lua.commons.baseapi.types.LuaObjectTools.tolua;

import org.lua.commons.baseapi.LuaThread;
import org.lua.commons.baseapi.functions.Functions.Function1;
import org.lua.commons.baseapi.functions.Functions.Function2;
import org.lua.commons.baseapi.functions.Functions.Function3;
import org.lua.commons.baseapi.functions.Functions.Function4;

public class LuaMetatable extends LuaTable {

	public static String ADD = "__add";
	public static String SUB = "__sub";
	public static String MUL = "__mul";
	public static String DIV = "__div";
	public static String MOD = "__mod";
	public static String POW = "__pow";
	public static String UNM = "__unm";
	public static String CONCAT = "__concat";
	public static String LEN = "__len";
	public static String EQ = "__eq";
	public static String LT = "__lt";
	public static String LE = "__le";
	public static String INDEX = "__index";
	public static String NEWINDEX = "__newindex";
	public static String CALL = "__call";
	public static String GC = "__gc";

	public LuaMetatable(LuaThread thread, int index) {
		super(thread, index);
	}

	public static LuaMetatable newMetatable(LuaThread thread) {
		checkStack(thread, 1);
		thread.state.newTable();
		LuaMetatable metatable = new LuaMetatable(thread, -1);
		pop(thread);
		return metatable;
	}

	public static LuaMetatable newMetatable(LuaThread thread,
			final LuaMetatableOperations operations) {
		LuaMetatable metatable = newMetatable(thread);
		metatable.setAdd(thread,
				new Function3<LuaThread, LuaObject, LuaObject, LuaObject>() {
					public LuaObject invoke(LuaThread thread, LuaObject a,
							LuaObject b) {
						return operations.add(thread, a, b);
					}
				});
		metatable.setSub(thread,
				new Function3<LuaThread, LuaObject, LuaObject, LuaObject>() {
					public LuaObject invoke(LuaThread thread, LuaObject a,
							LuaObject b) {
						return operations.sub(thread, a, b);
					}
				});
		metatable.setMul(thread,
				new Function3<LuaThread, LuaObject, LuaObject, LuaObject>() {
					public LuaObject invoke(LuaThread thread, LuaObject a,
							LuaObject b) {
						return operations.mul(thread, a, b);
					}
				});
		metatable.setDiv(thread,
				new Function3<LuaThread, LuaObject, LuaObject, LuaObject>() {
					public LuaObject invoke(LuaThread thread, LuaObject a,
							LuaObject b) {
						return operations.div(thread, a, b);
					}
				});
		metatable.setMod(thread,
				new Function3<LuaThread, LuaObject, LuaObject, LuaObject>() {
					public LuaObject invoke(LuaThread thread, LuaObject a,
							LuaObject b) {
						return operations.mod(thread, a, b);
					}
				});
		metatable.setPow(thread,
				new Function3<LuaThread, LuaObject, LuaObject, LuaObject>() {
					public LuaObject invoke(LuaThread thread, LuaObject a,
							LuaObject b) {
						return operations.pow(thread, a, b);
					}
				});
		metatable.setUnm(thread,
				new Function2<LuaThread, LuaObject, LuaObject>() {
					public LuaObject invoke(LuaThread thread, LuaObject a) {
						return operations.unm(thread, a);
					}
				});
		metatable.setConcat(thread,
				new Function3<LuaThread, LuaObject, LuaObject, LuaObject>() {
					public LuaObject invoke(LuaThread thread, LuaObject a,
							LuaObject b) {
						return operations.concat(thread, a, b);
					}
				});
		metatable.setLen(thread,
				new Function2<LuaThread, LuaObject, LuaObject>() {
					public LuaObject invoke(LuaThread thread, LuaObject a) {
						return operations.len(thread, a);
					}
				});
		metatable.setEq(thread,
				new Function3<LuaThread, LuaObject, LuaObject, LuaObject>() {
					public LuaObject invoke(LuaThread thread, LuaObject a,
							LuaObject b) {
						return operations.eq(thread, a, b);
					}
				});
		metatable.setLt(thread,
				new Function3<LuaThread, LuaObject, LuaObject, LuaObject>() {
					public LuaObject invoke(LuaThread thread, LuaObject a,
							LuaObject b) {
						return operations.lt(thread, a, b);
					}
				});
		metatable.setLe(thread,
				new Function3<LuaThread, LuaObject, LuaObject, LuaObject>() {
					public LuaObject invoke(LuaThread thread, LuaObject a,
							LuaObject b) {
						return operations.le(thread, a, b);
					}
				});
		metatable.setIndex(thread,
				new Function3<LuaThread, LuaObject, LuaObject, LuaObject>() {
					public LuaObject invoke(LuaThread thread, LuaObject table,
							LuaObject key) {
						return operations.index(thread, table, key);
					}
				});
		metatable
				.setNewIndex(
						thread,
						new Function4<LuaThread, LuaObject, LuaObject, LuaObject, Void>() {
							public Void invoke(LuaThread thread,
									LuaObject table, LuaObject key,
									LuaObject value) {
								operations.newindex(thread, table, key, value);
								return null;
							}
						});
		metatable.setGC(thread,
				new Function2<LuaThread, LuaObject, LuaObject>() {
					public LuaObject invoke(LuaThread thread, LuaObject object) {
						return operations.gc(thread, object);
					}
				});
		return metatable;
	}

	private LuaFunction getOp(LuaThread thread, String op) {
		LuaObject value = get(thread, tolua(thread, op));
		if (value instanceof LuaFunction)
			return (LuaFunction) value;
		return null;
	}

	private void setOp(LuaThread thread, String op, LuaFunction function) {
		set(thread, tolua(thread, op), function);
	}

	private void setOp(LuaThread thread, String op,
			Function1<LuaThread, Integer> function) {
		set(thread, tolua(thread, op), tolua(thread, function));
	}

	private void clearOp(LuaThread thread, String op) {
		set(thread, tolua(thread, op), null);
	}

	public LuaFunction getAdd(LuaThread thread) {
		return getOp(thread, ADD);
	}

	public void setAdd(LuaThread thread, LuaFunction function) {
		setOp(thread, ADD, function);
	}

	public void setAdd(LuaThread thread, Function1<LuaThread, Integer> function) {
		setOp(thread, ADD, function);
	}

	public void clearAdd(LuaThread thread) {
		clearOp(thread, ADD);
	}

	public void setAdd(LuaThread thread,
			final Function3<LuaThread, LuaObject, LuaObject, LuaObject> function) {
		setOp(thread, ADD, new Function1<LuaThread, Integer>() {

			public Integer invoke(LuaThread thread) {
				LuaObject b = popReference(thread);
				LuaObject a = popReference(thread);
				LuaObject res = function.invoke(thread, a, b);
				pushReference(thread, res);
				return 1;
			}

		});
	}

	public LuaFunction getSub(LuaThread thread) {
		return getOp(thread, SUB);
	}

	public void setSub(LuaThread thread, LuaFunction function) {
		setOp(thread, SUB, function);
	}

	public void setSub(LuaThread thread, Function1<LuaThread, Integer> function) {
		setOp(thread, SUB, function);
	}

	public void clearSub(LuaThread thread) {
		clearOp(thread, SUB);
	}

	public void setSub(LuaThread thread,
			final Function3<LuaThread, LuaObject, LuaObject, LuaObject> function) {
		setOp(thread, SUB, new Function1<LuaThread, Integer>() {

			public Integer invoke(LuaThread thread) {
				LuaObject b = popReference(thread);
				LuaObject a = popReference(thread);
				LuaObject res = function.invoke(thread, a, b);
				pushReference(thread, res);
				return 1;
			}

		});
	}

	public LuaFunction getMul(LuaThread thread) {
		return getOp(thread, MUL);
	}

	public void setMul(LuaThread thread, LuaFunction function) {
		setOp(thread, MUL, function);
	}

	public void setMul(LuaThread thread, Function1<LuaThread, Integer> function) {
		setOp(thread, MUL, function);
	}

	public void clearMul(LuaThread thread) {
		clearOp(thread, MUL);
	}

	public void setMul(LuaThread thread,
			final Function3<LuaThread, LuaObject, LuaObject, LuaObject> function) {
		setOp(thread, MUL, new Function1<LuaThread, Integer>() {

			public Integer invoke(LuaThread thread) {
				LuaObject b = popReference(thread);
				LuaObject a = popReference(thread);
				LuaObject res = function.invoke(thread, a, b);
				pushReference(thread, res);
				return 1;
			}

		});
	}

	public LuaFunction getDiv(LuaThread thread) {
		return getOp(thread, DIV);
	}

	public void setDiv(LuaThread thread, LuaFunction function) {
		setOp(thread, DIV, function);
	}

	public void setDiv(LuaThread thread, Function1<LuaThread, Integer> function) {
		setOp(thread, DIV, function);
	}

	public void clearDiv(LuaThread thread) {
		clearOp(thread, DIV);
	}

	public void setDiv(LuaThread thread,
			final Function3<LuaThread, LuaObject, LuaObject, LuaObject> function) {
		setOp(thread, DIV, new Function1<LuaThread, Integer>() {

			public Integer invoke(LuaThread thread) {
				LuaObject b = popReference(thread);
				LuaObject a = popReference(thread);
				LuaObject res = function.invoke(thread, a, b);
				pushReference(thread, res);
				return 1;
			}

		});
	}

	public LuaFunction getMod(LuaThread thread) {
		return getOp(thread, MOD);
	}

	public void setMod(LuaThread thread, LuaFunction function) {
		setOp(thread, MOD, function);
	}

	public void setMod(LuaThread thread, Function1<LuaThread, Integer> function) {
		setOp(thread, MOD, function);
	}

	public void clearMod(LuaThread thread) {
		clearOp(thread, MOD);
	}

	public void setMod(LuaThread thread,
			final Function3<LuaThread, LuaObject, LuaObject, LuaObject> function) {
		setOp(thread, MOD, new Function1<LuaThread, Integer>() {

			public Integer invoke(LuaThread thread) {
				LuaObject b = popReference(thread);
				LuaObject a = popReference(thread);
				LuaObject res = function.invoke(thread, a, b);
				pushReference(thread, res);
				return 1;
			}

		});
	}

	public LuaFunction getPow(LuaThread thread) {
		return getOp(thread, POW);
	}

	public void setPow(LuaThread thread, LuaFunction function) {
		setOp(thread, POW, function);
	}

	public void setPow(LuaThread thread, Function1<LuaThread, Integer> function) {
		setOp(thread, POW, function);
	}

	public void clearPow(LuaThread thread) {
		clearOp(thread, POW);
	}

	public void setPow(LuaThread thread,
			final Function3<LuaThread, LuaObject, LuaObject, LuaObject> function) {
		setOp(thread, POW, new Function1<LuaThread, Integer>() {

			public Integer invoke(LuaThread thread) {
				LuaObject b = popReference(thread);
				LuaObject a = popReference(thread);
				LuaObject res = function.invoke(thread, a, b);
				pushReference(thread, res);
				return 1;
			}

		});
	}

	public LuaFunction getUnm(LuaThread thread) {
		return getOp(thread, UNM);
	}

	public void setUnm(LuaThread thread, LuaFunction function) {
		setOp(thread, UNM, function);
	}

	public void setUnm(LuaThread thread, Function1<LuaThread, Integer> function) {
		setOp(thread, UNM, function);
	}

	public void clearUnm(LuaThread thread) {
		clearOp(thread, UNM);
	}

	public void setUnm(LuaThread thread,
			final Function2<LuaThread, LuaObject, LuaObject> function) {
		setOp(thread, UNM, new Function1<LuaThread, Integer>() {

			public Integer invoke(LuaThread thread) {
				LuaObject a = popReference(thread);
				LuaObject res = function.invoke(thread, a);
				pushReference(thread, res);
				return 1;
			}

		});
	}

	public LuaFunction getConcat(LuaThread thread) {
		return getOp(thread, CONCAT);
	}

	public void setConcat(LuaThread thread, LuaFunction function) {
		setOp(thread, CONCAT, function);
	}

	public void setConcat(LuaThread thread,
			Function1<LuaThread, Integer> function) {
		setOp(thread, CONCAT, function);
	}

	public void clearConcat(LuaThread thread) {
		clearOp(thread, CONCAT);
	}

	public void setConcat(LuaThread thread,
			final Function3<LuaThread, LuaObject, LuaObject, LuaObject> function) {
		setOp(thread, CONCAT, new Function1<LuaThread, Integer>() {

			public Integer invoke(LuaThread thread) {
				LuaObject b = popReference(thread);
				LuaObject a = popReference(thread);
				LuaObject res = function.invoke(thread, a, b);
				pushReference(thread, res);
				return 1;
			}

		});
	}

	public LuaFunction getLen(LuaThread thread) {
		return getOp(thread, LEN);
	}

	public void setLen(LuaThread thread, LuaFunction function) {
		setOp(thread, LEN, function);
	}

	public void setLen(LuaThread thread, Function1<LuaThread, Integer> function) {
		setOp(thread, LEN, function);
	}

	public void clearLen(LuaThread thread) {
		clearOp(thread, LEN);
	}

	public void setLen(LuaThread thread,
			final Function2<LuaThread, LuaObject, LuaObject> function) {
		setOp(thread, LEN, new Function1<LuaThread, Integer>() {

			public Integer invoke(LuaThread thread) {
				LuaObject a = popReference(thread);
				LuaObject res = function.invoke(thread, a);
				pushReference(thread, res);
				return 1;
			}

		});
	}

	public LuaFunction getEq(LuaThread thread) {
		return getOp(thread, EQ);
	}

	public void setEq(LuaThread thread, LuaFunction function) {
		setOp(thread, EQ, function);
	}

	public void setEq(LuaThread thread, Function1<LuaThread, Integer> function) {
		setOp(thread, EQ, function);
	}

	public void clearEq(LuaThread thread) {
		clearOp(thread, EQ);
	}

	public void setEq(LuaThread thread,
			final Function3<LuaThread, LuaObject, LuaObject, LuaObject> function) {
		setOp(thread, EQ, new Function1<LuaThread, Integer>() {

			public Integer invoke(LuaThread thread) {
				LuaObject b = popReference(thread);
				LuaObject a = popReference(thread);
				LuaObject res = function.invoke(thread, a, b);
				pushReference(thread, res);
				return 1;
			}

		});
	}

	public LuaFunction getLt(LuaThread thread) {
		return getOp(thread, LT);
	}

	public void setLt(LuaThread thread, LuaFunction function) {
		setOp(thread, LT, function);
	}

	public void setLt(LuaThread thread, Function1<LuaThread, Integer> function) {
		setOp(thread, LT, function);
	}

	public void clearLt(LuaThread thread) {
		clearOp(thread, LT);
	}

	public void setLt(LuaThread thread,
			final Function3<LuaThread, LuaObject, LuaObject, LuaObject> function) {
		setOp(thread, LT, new Function1<LuaThread, Integer>() {

			public Integer invoke(LuaThread thread) {
				LuaObject b = popReference(thread);
				LuaObject a = popReference(thread);
				LuaObject res = function.invoke(thread, a, b);
				pushReference(thread, res);
				return 1;
			}

		});
	}

	public LuaFunction getLe(LuaThread thread) {
		return getOp(thread, LE);
	}

	public void setLe(LuaThread thread, LuaFunction function) {
		setOp(thread, LE, function);
	}

	public void setLe(LuaThread thread, Function1<LuaThread, Integer> function) {
		setOp(thread, LE, function);
	}

	public void clearLe(LuaThread thread) {
		clearOp(thread, LE);
	}

	public void setLe(LuaThread thread,
			final Function3<LuaThread, LuaObject, LuaObject, LuaObject> function) {
		setOp(thread, LE, new Function1<LuaThread, Integer>() {

			public Integer invoke(LuaThread thread) {
				LuaObject b = popReference(thread);
				LuaObject a = popReference(thread);
				LuaObject res = function.invoke(thread, a, b);
				pushReference(thread, res);
				return 1;
			}

		});
	}

	public LuaFunction getIndex(LuaThread thread) {
		return getOp(thread, INDEX);
	}

	public void setIndex(LuaThread thread, LuaFunction function) {
		setOp(thread, INDEX, function);
	}

	public void setIndex(LuaThread thread,
			Function1<LuaThread, Integer> function) {
		setOp(thread, INDEX, function);
	}

	public void clearIndex(LuaThread thread) {
		clearOp(thread, INDEX);
	}

	public void setIndex(LuaThread thread,
			final Function3<LuaThread, LuaObject, LuaObject, LuaObject> function) {
		setOp(thread, INDEX, new Function1<LuaThread, Integer>() {

			public Integer invoke(LuaThread thread) {
				LuaObject key = popReference(thread);
				LuaObject table = popReference(thread);
				LuaObject res = function.invoke(thread, table, key);
				pushReference(thread, res);
				return 1;
			}

		});
	}

	public LuaFunction getNewIndex(LuaThread thread) {
		return getOp(thread, NEWINDEX);
	}

	public void setNewIndex(LuaThread thread, LuaFunction function) {
		setOp(thread, NEWINDEX, function);
	}

	public void setNewIndex(LuaThread thread,
			Function1<LuaThread, Integer> function) {
		setOp(thread, NEWINDEX, function);
	}

	public void clearNewIndex(LuaThread thread) {
		clearOp(thread, NEWINDEX);
	}

	public void setNewIndex(
			LuaThread thread,
			final Function4<LuaThread, LuaObject, LuaObject, LuaObject, Void> function) {
		setOp(thread, NEWINDEX, new Function1<LuaThread, Integer>() {

			public Integer invoke(LuaThread thread) {
				LuaObject value = popReference(thread);
				LuaObject key = popReference(thread);
				LuaObject table = popReference(thread);
				function.invoke(thread, table, key, value);
				return 0;
			}

		});
	}

	public LuaFunction getCall(LuaThread thread) {
		return getOp(thread, CALL);
	}

	public void setCall(LuaThread thread, LuaFunction function) {
		setOp(thread, CALL, function);
	}

	public void setCall(LuaThread thread, Function1<LuaThread, Integer> function) {
		setOp(thread, CALL, function);
	}

	public void clearCall(LuaThread thread) {
		clearOp(thread, CALL);
	}

	public void setCall(
			LuaThread thread,
			final Function3<LuaThread, LuaObject, LuaObject[], LuaObject[]> function) {
		setOp(thread, CALL, new Function1<LuaThread, Integer>() {
			public Integer invoke(LuaThread thread) {
				LuaObject[] args = new LuaObject[size(thread) - 2];
				for (int i = 0; i < args.length; i++) {
					args[i] = getReference(thread, i + 3);
				}
				LuaObject func = getReference(thread, 1);
				LuaObject[] results = function.invoke(thread, func, args);
				for (LuaObject result : results)
					result.push(thread);
				return results.length;
			}

		});
	}

	public LuaFunction getGC(LuaThread thread) {
		return getOp(thread, GC);
	}

	public void setGC(LuaThread thread, LuaFunction function) {
		setOp(thread, GC, function);
	}

	public void setGC(LuaThread thread, Function1<LuaThread, Integer> function) {
		setOp(thread, GC, function);
	}

	public void clearGC(LuaThread thread) {
		clearOp(thread, GC);
	}

	public void setGC(LuaThread thread,
			final Function2<LuaThread, LuaObject, LuaObject> function) {
		setOp(thread, GC, new Function1<LuaThread, Integer>() {

			public Integer invoke(LuaThread thread) {
				LuaObject a = popReference(thread);
				LuaObject res = function.invoke(thread, a);
				pushReference(thread, res);
				return 1;
			}

		});
	}

	public static interface LuaMetatableOperations {

		public LuaObject add(LuaThread thread, LuaObject a, LuaObject b);

		public LuaObject sub(LuaThread thread, LuaObject a, LuaObject b);

		public LuaObject mul(LuaThread thread, LuaObject a, LuaObject b);

		public LuaObject div(LuaThread thread, LuaObject a, LuaObject b);

		public LuaObject mod(LuaThread thread, LuaObject a, LuaObject b);

		public LuaObject pow(LuaThread thread, LuaObject a, LuaObject b);

		public LuaObject unm(LuaThread thread, LuaObject a);

		public LuaObject concat(LuaThread thread, LuaObject a, LuaObject b);

		public LuaObject len(LuaThread thread, LuaObject a);

		public LuaObject eq(LuaThread thread, LuaObject a, LuaObject b);

		public LuaObject lt(LuaThread thread, LuaObject a, LuaObject b);

		public LuaObject le(LuaThread thread, LuaObject a, LuaObject b);

		public LuaObject index(LuaThread thread, LuaObject table, LuaObject key);

		public void newindex(LuaThread thread, LuaObject table, LuaObject key,
				LuaObject value);

		public LuaObject[] call(LuaThread thread, LuaObject func,
				LuaObject[] args);

		public LuaObject gc(LuaThread thread, LuaObject object);
	}

}
