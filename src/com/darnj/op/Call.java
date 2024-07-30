package com.darnj.op;

import java.util.ArrayList;

import com.darnj.Error;
import com.darnj.Span;
import com.darnj.interpret.*;

public final class Call extends Op {
    int func;
    ArrayList<Op> args;

    public Call(Span pos, int func, ArrayList<Op> args) {
        super(pos);
        this.func = func;
        this.args = args;
    }

    @Override
    public Value eval(Context ctx) throws Error {
        
    }
}