package com.darnj.op;

import com.darnj.Span;
import com.darnj.interpret.*;
import com.darnj.value.*;

public final class Continue extends Op {
    public Continue(Span pos) {
        this.pos = pos;
    }

    @Override
    public Value eval(Context ctx) {
        throw new Effect(pos);
    }

    public static class Effect extends ControlFlowEffect {
        Effect(Span pos) {
            super(pos);
        }
    }
    
}