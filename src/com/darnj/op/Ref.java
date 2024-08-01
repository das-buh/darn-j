package com.darnj.op;

import com.darnj.LangError;
import com.darnj.interpret.*;
import com.darnj.value.*;

public final class Ref extends UnaryOp {
    @Override
    public Value eval(Context ctx) {
        if (operand instanceof Variable o) {
            if (ctx.vars().containsKey(o.id)) {
                return Value.makeReference(ctx.vars().get(o.id));
            }

            var format = "variable `%s` is undefined";
            throw new LangError(o.pos(), String.format(format, ctx.symbols().resolve(o.id)));
        }

        var operand = this.operand.eval(ctx);
        return Value.makeReference(operand);
    }
}
