package com.darnj.op;

import com.darnj.LangError;
import com.darnj.interpret.*;
import com.darnj.value.*;

public final class Ref extends UnaryOp {
    @Override
    public Value eval(Context ctx) {
        if (operand instanceof Variable o) {
            if (ctx.vars().containsKey(o.id)) {
                return ctx.vars().get(o.id);
            }

            throw new LangError(o.pos(), "variable `%s` is undefined");
        }

        var operand = this.operand.eval(ctx);
        return Value.makeReference(operand);
    }
}
