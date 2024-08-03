package com.darnj.op;

import com.darnj.Span;
import com.darnj.interpret.*;
import com.darnj.value.*;

public final class While extends Op {
    Op cond;
    Op body;

    public While(Span pos, Op cond, Op body) {
        this.pos = pos;
        this.cond = cond;
        this.body = body;
    }

    @Override
    public Value eval(Context ctx) {
        while (cond.eval(ctx).inner instanceof BoolValue c && c.value()) {
            try {
                body.eval(ctx);
            } catch (Continue.Effect _) {
                continue;
            } catch (Break.Effect _) {
                break;
            }
        } 

        return Value.makeUndefined();
    }
}
