package org.lua.commons.configuration.basic;

public class MathConfigurationApi extends StandartConfigurationApi {

	public String[] getNames() {
		return new String[] { "abs", "acos", "asin", "atan", "atan2", "ceil",
				"cos", "cosh", "deg", "exp", "floor", "fmod", "frexp", "huge",
				"ldexp", "log", "max", "min", "modf", "pi", "pow", "rad",
				"random", "randomseed", "sin", "sinh", "sqrt", "tan", "tanh" };
	}

	@Override
	public String getDefault() {
		return "math";
	}

}
