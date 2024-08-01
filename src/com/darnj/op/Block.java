package com.darnj.op;

import java.util.ArrayList;

import com.darnj.Span;
import com.darnj.interpret.*;
import com.darnj.value.*;

public final class Block extends Op {
    ArrayList<Op> stmts;

    public Block(Span pos, ArrayList<Op> stmts) {
        this.pos = pos;
        this.stmts = stmts;
    }
    
    @Override
    public Value eval(Context ctx) {
        var out = Value.makeUndefined();
        for (var stmt : stmts) {
            out = stmt.eval(ctx);
        }
        return out;
    }
}
