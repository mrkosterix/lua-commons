package org.lua.commons.baseapi.functions;

public interface Functions {

	public interface Function {
	}

	public interface Function0<R> extends Function {
		public R invoke();
	}

	public interface Function1<T0, R> extends Function {
		public R invoke(T0 arg0);
	}

	public interface Function2<T0, T1, R> extends Function {
		public R invoke(T0 arg0, T1 arg1);
	}

	public interface Function3<T0, T1, T2, R> extends Function {
		public R invoke(T0 arg0, T1 arg1, T2 arg2);
	}

	public interface Function4<T0, T1, T2, T3, R> extends Function {
		public R invoke(T0 arg0, T1 arg1, T2 arg2, T3 arg3);
	}

	public interface Function5<T0, T1, T2, T3, T4, R> extends Function {
		public R invoke(T0 arg0, T1 arg1, T2 arg2, T3 arg3, T4 arg4);
	}

	public interface Function6<T0, T1, T2, T3, T4, T5, R> extends Function {
		public R invoke(T0 arg0, T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5);
	}

	public interface Function7<T0, T1, T2, T3, T4, T5, T6, R> extends Function {
		public R invoke(T0 arg0, T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5,
				T6 arg6);
	}

	public interface Function8<T0, T1, T2, T3, T4, T5, T6, T7, R> extends
			Function {
		public R invoke(T0 arg0, T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5,
				T6 arg6, T7 arg7);
	}

	public interface Function9<T0, T1, T2, T3, T4, T5, T6, T7, T8, R> extends
			Function {
		public R invoke(T0 arg0, T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5,
				T6 arg6, T7 arg7, T8 arg8);
	}

}
