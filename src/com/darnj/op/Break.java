package com.darnj.op;

import com.darnj.Span;
import com.darnj.interpret.*;
import com.darnj.value.*;

public final class Break extends Op {
    public Break(Span pos) {
        this.pos = pos;
    }

    @Override
    public Value eval(Context ctx) {
        throw new Effect(pos);
    }

    public class Effect extends ControlFlowEffect {
        Effect(Span pos) {
            super(pos);
        }
    }
    
}