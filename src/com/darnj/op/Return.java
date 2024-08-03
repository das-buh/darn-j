package com.darnj.op;

import java.util.Optional;

import com.darnj.Span;
import com.darnj.interpret.*;
import com.darnj.value.*;

public final class Return extends Op {
    Optional<Op> value;
    
    public Return(Span pos) {
        this.pos = pos;
        this.value = Optional.empty();
    }

    public Return(Span pos, Op value) {
        this.pos = pos;
        this.value = Optional.of(value);
    }

    @Override
    public Value eval(Context ctx) {
        var value = this.value.isPresent() ? this.value.get().eval(ctx) : Value.makeUndefined(); 
        throw new Effect(pos, value);
    }

    public static class Effect extends ControlFlowEffect {
        Value value;
    
        Effect(Span pos, Value value) {
            super(pos);
            this.value = value;
        }
    
        public Value value() {
            return value;
        }
    }
}