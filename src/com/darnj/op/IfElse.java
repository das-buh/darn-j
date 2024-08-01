package com.darnj.op;

import com.darnj.Span;
import com.darnj.interpret.*;
import com.darnj.value.*;

public final class IfElse extends Op {
    Op cond;
    Op ifBranch;
    Op elseBranch; // Null if no else branch.

    public IfElse(Span pos, Op cond, Op ifBranch, Op elseBranch) {
        this.pos = pos;
        this.cond = cond;
        this.ifBranch = ifBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public Value eval(Context ctx) {
        if (cond.eval(ctx).inner instanceof BoolValue c && c.value()) {
            return ifBranch.eval(ctx);
        } else if (elseBranch != null) {
            return elseBranch.eval(ctx);
        }

        return Value.makeUndefined();
    }
}
