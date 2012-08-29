package org.lua.commons.impl.nativelua;

public class NativeYieldRuntimeException extends RuntimeException {
    private final boolean isK;
    private final int ctx;
    private final int n;

    public NativeYieldRuntimeException(int ctx, int n) {
        this.isK = true;
        this.ctx = ctx;
        this.n = n;
    }

    public NativeYieldRuntimeException(int n) {
        this.isK = false;
        this.ctx = 0;
        this.n = n;
    }

    public int getN() {
        return n;
    }

    public boolean isK() {
        return isK;
    }

    public int getCtx() {
        return ctx;
    }

}
