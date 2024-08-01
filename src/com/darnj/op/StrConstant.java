package com.darnj.op;

import com.darnj.Span;
import com.darnj.interpret.*;
import com.darnj.value.*;

public final class StrConstant extends Op {
    String val;

    public StrConstant(Span pos, String val) {
        this.pos = pos;
        this.val = val;
    }

    @Override
    public Value eval(Context ctx) {
        return Value.makeStr(val);
    }
}
