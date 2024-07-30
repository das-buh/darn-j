package com.darnj.op;

import java.util.ArrayList;

import com.darnj.Error;
import com.darnj.Span;
import com.darnj.interpret.*;

public final class Slice extends Op {
    ArrayList<Op> elems;

    public Slice(Span pos, ArrayList<Op> elems) {
        super(pos);
        this.elems = elems;
    }

    @Override
    public Value eval(Context ctx) throws Error {

    }
}