package com.darnj.op;

import com.darnj.Error;
import com.darnj.Span;
import com.darnj.interpret.*;

public final class StrConstant extends Op {
    String val;

    public StrConstant(Span pos, String val) {
        super(pos);
        this.val = val;
    }

    @Override
    public Value eval(Context ctx) throws Error {

    }
}
