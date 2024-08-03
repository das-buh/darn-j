package com.darnj.op;

import java.util.Optional;

import com.darnj.Span;
import com.darnj.interpret.*;
import com.darnj.value.*;

public final class IfElse extends Op {
    Op cond;
    Op ifBranch;
    Optional<Op> elseBranch;

    public IfElse(Span pos, Op cond, Op ifBranch) {
        this.pos = pos;
        this.cond = cond;
        this.ifBranch = ifBranch;
        this.elseBranch = Optional.empty();
    }

    public IfElse(Span pos, Op cond, Op ifBranch, Op elseBranch) {
        this.pos = pos;
        this.cond = cond;
        this.ifBranch = ifBranch;
        this.elseBranch = Optional.of(elseBranch);
    }

    @Override
    public Value eval(Context ctx) {
        if (cond.eval(ctx).inner instanceof BoolValue c && c.value()) {
            return ifBranch.eval(ctx);
        } else if (elseBranch.isPresent()) {
            return elseBranch.get().eval(ctx);
        }

        return Value.makeUndefined();
    }
}
