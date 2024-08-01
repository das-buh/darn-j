package com.darnj.interpret;

import java.util.HashMap;

import com.darnj.Interner;
import com.darnj.value.*;

public final class Context {
    Interner symbols;
    HashMap<Integer, Function> funcs;
    HashMap<Integer, Value> vars;

    public Context() {
        symbols = new Interner();
        funcs = new HashMap<>();
        vars = new HashMap<>();
    }

    public Interner symbols() {
        return symbols;
    }

    public HashMap<Integer, Function> funcs() {
        return funcs;
    }

    public HashMap<Integer, Value> vars() {
        return vars;
    }
}
